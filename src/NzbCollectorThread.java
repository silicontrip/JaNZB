import java.io.*;

public class NzbCollectorThread implements Runnable {
	
	
	private int start;
	private int end;
	private int increment;
	private NNTPConnection nntp;
	private String match;
	private NNTPMatchedArticle callback;
	private String group;
	private Boolean verbose;
	
	private AtomicCounter ac;
	
	public NzbCollectorThread (NNTPMatchedArticle nma, AtomicCounter atom, String grp,NNTPConnection nn) { this(nma,atom,nn,grp,".*"); }
	public NzbCollectorThread (NNTPMatchedArticle nma, AtomicCounter atom, NNTPConnection nn, String grp,  String ma) { this(nma,atom,nn,grp,ma,false); }
	
	public NzbCollectorThread (NNTPMatchedArticle nma, AtomicCounter atom, NNTPConnection nn, String grp,  String ma, Boolean ver) {
		//setStart(st);
		//setEnd(en);
		//setIncrement(in);
		setAtomicCounter(atom);
		setNNTP(nn);
		setMatch(ma);
		setNNTPMatchedArticle(nma);
		setGroup(grp);
		setVerbose(ver);
	}
	
	//	public void setStart(int i) { start = i; }
	//	public void setEnd(int i) { end = i; }
	//	public void setIncrement(int i) { increment = i; }
	public void setAtomicCounter(AtomicCounter atom) { ac = atom; }
	public void setNNTP(NNTPConnection i) { nntp = i; }
	public void setNNTPMatchedArticle(NNTPMatchedArticle nma) { callback = nma; }
	public void setGroup(String g) { group = g; }
	public void setVerbose(Boolean v) { verbose = v; }
	public Boolean isVerbose() { return verbose; }
	
	public void setMatch(String m) { 
		if (m!=null) {
			match = m; 
		} else {
			match = ".*";
		}
	}
	
	public void run() {
		
		long i = 0 ;
		
			
			do {
				
				try {
					if (isVerbose()) {
						System.out.println("Thread connecting to NNTP server.");
					}

					nntp.connect();
					nntp.setGroup(group);
					
					while ((i = ac.getNext() ) != -1) {
						
						String is = Long.toString(i);
						
						try {
							
							nntp.headArticle(is);
							
							if (isVerbose()) {
								System.out.println ("" + i + ": " + nntp.getArticleDate() + " " + nntp.getArticleSubject());
							}
							
							if (nntp.getArticleSubject().matches(match)) { callback.processArticle(nntp); }
						} catch (NNTPNoSuchArticleException e) {
							;
							if (isVerbose()) {
							 System.out.println("Couldn't find article: " + e.getMessage());
							// e.printStackTrace();
							}
						} 
						
					}
					
					
				} catch (IOException e) {
					System.out.println("Problem reading from NNTP server: " + e.getMessage());
				} catch (NNTPConnectionResponseException e) {
					System.out.println("NNTP server didn't respond properly: " + e.getMessage());
				} catch (NNTPNoSuchGroupException e) {
					System.out.println("Couldn't find group: " + e.getMessage());
				} catch (NNTPGroupResponseException e) {
					System.out.println("Problem getting group from NNTP server: " + e.getMessage());
				}
				
				
			} while (i!=-1);
			
			try {
				nntp.disconnect();
			} catch (Exception e) {
				System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
			} 
			
			
		}
	}
