import java.io.*;
import java.net.*;
import java.util.*;


public class NNTPConnection extends InputStream {
	
	transient private TCPConnection network;
	private static final byte[] dot = {0xd, 0xa, 0x2e, 0xd, 0xa};
	private static final byte[] newline = {0xd, 0xa};
	
	private static final int BUFFER_SIZE=8192;
	private  String endMarkerString;
	transient private boolean lock=false;
	transient private int total = 0;
	transient private long start;
	transient private long split;
	transient private int splitBytes;
	
	// internal reader
	private byte[] buffer;
	private int buffer_pointer=0;
	private int buffer_size=0;
	private boolean end_of_data;
	private int buffer2_size=0;
	private byte[] buffer2;
	
	
	String groupName;
	Integer groupStart;
	Integer groupEnd;
	Integer groupLength;
	
	HashMap<String, String> articleHeader;
	
	private String articleName;
	private String article;
	private int articleMarker=0;
	private int articlePointer=0;
	
	public NNTPConnection () {
		//	endMarkerString = new String (endmarker);
		//printHex(endMarkerString);
		
		buffer = new byte[BUFFER_SIZE];
		buffer2 = new byte[BUFFER_SIZE];
		
		network = new TCPConnection();
		
	}
	
	public NNTPConnection(String s, int i) throws UnknownHostException, IOException {		
		this();
		network = new TCPConnection(s,i);
	}
	
	public void enableDebug() { network.enableDebug(); }
	public void disableDebug() { network.disableDebug(); }
	
	public String getHost() { return network.getHost(); }
	public int getPort() { return network.getPort(); }
	
	public void setHost(String s) { network.setHost(s); }
	
	public void setPort(int i)  { network.setPort(i); }
	
	public void lock() { lock = true; }
	public void unlock() { lock = false; }
	public boolean isLocked() { return lock; }
	
	//	public BufferedOutputStream getOutputStream() {return network.getOutputStream();}
	//	public BufferedInputStream getInputStream() {return network.getInputStream();}
	
	public void connect() throws IOException, NNTPConnectionResponseException {
		
		// check for 200 ok
		this.setEndCommand(newline);
		network.connect();
		
		try{
			checkResponse("200");
		} catch (NNTPUnexpectedResponseException e) {
			throw new NNTPConnectionResponseException(e.getMessage());
		}
		
	}
	
	public void disconnect() throws IOException, NNTPUnexpectedResponseException {
		
		this.setEndCommand(newline);
		sendCommand("QUIT\r\n");
		
		// check for success, but what are we going to do if it fails? close connection?
		checkResponse("205");
		
		network.disconnect();
	}
	
	public void reset() { buffer_size = 0; buffer_pointer = 0; buffer2_size = 0; end_of_data=false; }
	
	public void sendCommand (String comm) throws IOException { reset(); network.sendCommand(comm); }
	public void sendCommand (byte[] comm) throws IOException { reset(); network.sendCommand(comm); }
	
	public boolean markSupported() { return false; }
	
	public int available() { return buffer_size - buffer_pointer; }
	
	public String readAsString() throws IOException {
		String s = new String();
		int i=0;
		
		byte[] b = new byte[1];
		i = this.read();
		
		while (i != -1) {
			b[0] = (byte)i;
			s = s.concat(new String(b));
			i = this.read();
		}
		return s;
		
	}
	
	public String readLine () throws IOException {
		
		int i=0;
		
		byte[] b = new byte[1];
		
		String s = new String();

		while (s.indexOf("\r\n") == -1 && i != -1) {
			i = this.read();
			if (i != -1) {
				b[0] = (byte)i;
				s = s.concat(new String(b));
			}
		}
		s = s.replaceAll("(\\r|\\n)", "");
		return s;
	}
	
	//	public int read (byte[] b) throws IOException {
	//
	//	}
	
	// TODO: read entire article into string buffer and read from that.
	
	public int read () throws IOException {
		
		if (buffer_pointer >= buffer_size) {
			if (!end_of_data) {
				
				
				if(buffer2_size == 0 ) {
					
					buffer_size=network.receiveResponse(buffer,0,BUFFER_SIZE);
					buffer_pointer = 0;
					
					
					//	printHex(buffer,buffer_size);
					
				} else {
					
					byte[] temp;
					
					temp = buffer2;
					buffer2 = buffer;
					buffer = temp;
					
					buffer_size = buffer2_size;
					buffer2_size = 0;
					buffer_pointer = 0;
				}
				
				if (NNTPendofcommand(buffer,buffer_size)) {
					
					// horible hack
					buffer_size -= (endMarkerString.length()-2) ;
					end_of_data = true;
				} else {
					
					buffer2_size=network.receiveResponse(buffer2,0,BUFFER_SIZE);
					//printHex(buffer2,buffer2_size);
					
					//  check that the end of data marker doesn't cross the boundary.
					if (buffer2_size < endMarkerString.length()) {
						if (NNTPendofcommand(buffer,buffer_size,buffer2,buffer2_size)) {
							
							
							end_of_data = true;
							// horible hack

							buffer_size -= (endMarkerString.length() - buffer2_size - 2);
						}
					}
				}
				
				
			} else {
				return -1;
			}
		}
		
		return (int) buffer[buffer_pointer++];
	}
	
	public void close () throws IOException
	{
		while (this.read() != -1);
		this.reset();
	}
	
	private String checkResponse (String result) throws IOException, NNTPUnexpectedResponseException {
		String s = this.readLine();
		
		if (!s.startsWith(result)) {
			throw new NNTPUnexpectedResponseException(s);
		}
		return s;
	}
	
	public boolean NNTPendofcommand(byte[] comm, int l) {
		if (l > 0) 
		return NNTPendofcommand ( new String (comm,0,l));
		return false;
	}
	
	public boolean NNTPendofcommand (byte[] comm, int l,byte[] comm2, int l2) {
		return NNTPendofcommand ( new String (comm,0,l) , new String (comm2,0,l2));
	}
	
	public boolean NNTPendofcommand (String s1, String s2)
	{
		
		if (s1 != null) {
			if (s2 != null) {
				return NNTPendofcommand(s1.concat(s2));
			} else {
				return NNTPendofcommand(s1);
			}
		} else {
			if (s2 != null) {
				return NNTPendofcommand(s2);
			} 
		}
		return false;
	}
	
	public boolean NNTPendofcommand (String contents) {
		return contents.indexOf(endMarkerString) != -1;
	}
	
	private void setEndCommand(byte[] b) 
	{
		endMarkerString = new String (b);
	}
	
	public static void printHex (byte[]b , int s) 
	{
		int width  = 16;
		
		
		for (int i=0; i < s; i+= width )
		{
			
			System.out.printf("%04x  ",i);
			
			int j;
			for (j=0; j<width && i+j <s; j++) 
				System.out.printf("%02x ",b[i+j]);
			
			for (int k=j; k < width; k++)
				System.out.printf("   ");
			
			for ( j=0; j<width && i+j <s; j++) 
				if (b[i+j] >= 32 && b[i+j] <127) 
					System.out.printf("%c",b[i+j]);
				else 
					System.out.printf(".");
			
			for (int k=j; k < width; k++)
				System.out.printf(" ");
			
			
			System.out.println();
		}
	}
	
	public long getAveBPS() {
		long sec = System.currentTimeMillis() - start;
		if (sec>0)
			return 1000*total / sec; 
		return 0;
	}
	
	public long getSplitBPS() {
		long sec = System.currentTimeMillis() - split;
		int splitTotal = total - splitBytes;
		splitBytes = total;
		split = System.currentTimeMillis();
		if (sec >0)
			return 1000*splitTotal / sec; 
		return 0;
	}
	
	public int getTotal() { return total; }
	
	public void setGroup (String g) throws IOException, NNTPNoSuchGroupException, NNTPGroupResponseException
	{
		setEndCommand(newline);
		sendCommand("GROUP " + g + "\r\n");
		
		try {
			String s = checkResponse("211");
			
			String param[] = s.split(" ");
			
			if (param.length <= 4) {
				throw new NNTPGroupResponseException(s);	
			}
			
			groupLength=new Integer(param[1]);
			groupStart=new Integer(param[2]);
			groupEnd=new Integer(param[3]);
			groupName=param[4];
			
		} catch (NNTPUnexpectedResponseException e) {
			throw new NNTPNoSuchGroupException(e.getMessage());
		} catch (NumberFormatException e) {
			throw new NNTPGroupResponseException(e.getMessage());	
		}
		
	}
	
	public Integer getGroupLength() {return groupLength;}
	public Integer getGroupStart() {return groupStart;}
	public Integer getGroupEnd() {return groupEnd;}
	public String getGroupName() {return groupName;}
	
	public String statArticle (String articleNumber) throws IOException, NNTPNoSuchArticleException
	{
		String s;
		
		// System.out.println("STAT " + articleNumber );
		setEndCommand(newline);
		sendCommand("STAT " + articleNumber + "\r\n");
		try {
			s = checkResponse("223");
		} catch (NNTPUnexpectedResponseException e) {
			throw new NNTPNoSuchArticleException(e.getMessage());
		}
		s = s.replaceAll("(\\r|\\n)", "");
		String param[] = s.split(" ");
		
		if (param.length > 2) {
			this.articleName = param[2];
			return param[2];
		}
		
		throw new IOException("NNTP Error stat response: " + s);	
		
	}
	
	public void headArticle() throws IOException,NNTPNoSuchArticleException {
		headArticle(this.articleName);
	}
	
	public void headArticle(String article) throws IOException,NNTPNoSuchArticleException {
		
		articleHeader = new HashMap<String,String>();
		String r ;
		String q=null;
		
		setEndCommand(dot);
		sendCommand("HEAD " + article + "\r\n");
		
		try {
			r= checkResponse("221");		
		} catch (NNTPUnexpectedResponseException e) {
			throw new NNTPNoSuchArticleException(e.getMessage());
		}
		

		while (r.length() > 0) {
			r = readLine();

			String[] s = splitHeader(r);
			
			articleHeader.put(s[0],s[1]);

		}
		
		this.articleName = articleHeader.get("Message-ID");
	}
	
	private String[] splitHeader(String s)
	{
		int colon = s.indexOf(": ");
		String r[] = new String[2];
		
		if (colon != -1) {
			r[0]  = new String(s.getBytes(),0,colon);
			r[1] = new String(s.getBytes(),colon+2,s.length()-colon-2);
		}
		return r;
	}
	
	public String getArticleName() { return this.articleName; }
	public String getArticleSubject() { return this.articleHeader.get("Subject"); }
	
	public void setArticleName (String articleName) throws IOException , NNTPNoSuchArticleException { 
		
		this.close();
		
		this.articleName = articleName; 
		
		setEndCommand(dot);
		sendCommand("ARTICLE " + articleName + "\r\n");
		try {
			checkResponse("220");
		} catch (NNTPUnexpectedResponseException e) {
			throw new NNTPNoSuchArticleException(e.getMessage());
		}
		
		start = System.currentTimeMillis();
		split = start;
		
		
	}
	
	public String getArticleAsString() throws IOException,NNTPNoSuchArticleException {
		return this.readAsString();
	}
	
	
	public int writeArticleToFile(File f) throws IOException,NNTPNoSuchArticleException {
		
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
		
		String a = getArticleAsString();
		
		out.write (a.getBytes(),0,a.length());
		out.close();
		
		return a.length();
		
	}
	
	
	public void interupt() throws IOException { network.disconnect(); }
	
	
}