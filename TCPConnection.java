
import java.io.*;
import java.net.*;
import java.util.*;

public class TCPConnection {

	transient private Socket network;
	String host;
	int port;
	
	BufferedOutputStream out;
	BufferedInputStream in;
	
	private boolean  debug = false;

	
	public TCPConnection() { ; } 
	
	public TCPConnection(String s, int i)  {
		host = s;
		port = i;
	}
	
	public void enableDebug() { 
		this.debug = true; 
		//System.out.println ("Debug enabled.");
	}
	public void disableDebug() { this.debug = false; }

	
	public void setHost(String s)  { this.host = s; }
	public void setPort(int i)  { this.port = i; }

	public String getHost() { return host; }
	public int getPort() { return port; }

	public BufferedOutputStream getOutputStream() {return out;}
	public BufferedInputStream getInputStream() {return in;}

	public void connect() throws IOException {
		network = new Socket(host,port);
		
		out =new BufferedOutputStream(network.getOutputStream());
		in =new BufferedInputStream(network.getInputStream());
	}

	public void disconnect() throws IOException {
		out.close();
		in.close();
		network.close();
	}
	
	public void sendCommand (String comm) throws IOException {
		try {
			sendCommand(comm.getBytes("US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			; // US-ASCII is a known fixed value this should never happen
		}
	}
	
	public void sendCommand (byte[] comm) throws IOException {
	
		if (this.debug)
			System.out.print(">> " + (new String(comm)));
		
		out.write(comm);
		out.flush();
	}
	
	
	public int receiveResponse (byte[] comm,int off,int len) throws IOException {
		int i = in.read(comm,off,len);
		if (this.debug)
			System.out.print ("<< " + (new String (comm)));
		return i;
	}
	
	
	public int receiveResponse (byte[] comm) throws IOException {
		int i = in.read(comm);
		if (this.debug)
			System.out.print ("<< " + (new String (comm)));
		return i;
	}
	
	
	
}