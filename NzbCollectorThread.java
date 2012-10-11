import java.io.*;

import java.util.Date;
import ar.com.ktulu.yenc.*;


public class NzbCollectorThread implements Runnable {
	
	
	private int start;
	private int end;
	private int increment;
	private NNTPConnection nntp;
	private String match;
	private NNTPMatchedArticle callback;
	
	private AtomicCounter ac;
	
	public NzbCollectorThread (NNTPMatchedArticle nma, AtomicCounter atom, NNTPConnection nn) {
		this(nma,atom,nn,".*");
	}
	
	public NzbCollectorThread (NNTPMatchedArticle nma, AtomicCounter atom, NNTPConnection nn, String ma) {
		//setStart(st);
		//setEnd(en);
		//setIncrement(in);
		setAtomicCounter(atom);
		setNNTP(nn);
		setMatch(ma);
		setNNTPMatchedArticle(nma);
	}
	
//	public void setStart(int i) { start = i; }
//	public void setEnd(int i) { end = i; }
//	public void setIncrement(int i) { increment = i; }
	public void setAtomicCounter(AtomicCounter atom) { ac = atom; }
	public void setNNTP(NNTPConnection i) { nntp = i; }
	public void setNNTPMatchedArticle(NNTPMatchedArticle nma) { callback = nma; }
	public void setMatch(String m) { 
		if (m!=null) {
			match = m; 
		} else {
			match = ".*";
		}
	}
	
	public void run() {
		
		try{
			long starttime = System.currentTimeMillis();
			long startarticle = start;
			long i;
			while ((i = ac.getNext() ) != -1) {
				
				String is = Long.toString(i);
				
				try {
					
					nntp.headArticle(is);
										
					//System.out.println ("" + i + ": Subject: " + subject + "match: " + match);
					
					if (nntp.getArticleSubject().matches(match)) {
					//	long currenttime = System.currentTimeMillis();
					//	long currentarticle = i;
						
						// in theory there are increment number of concurrent threads, they should all be doing about the same a/s
					//	Double aps = 1.0*(currentarticle - startarticle) / (currenttime - starttime)*1.0;
						
						callback.processArticle(nntp);
						
						
					//	starttime =currenttime;
					//	startarticle = currentarticle;
						
					}
				} catch (NNTPNoSuchArticleException e) {
					// don't want to know if the article isn't there.
					System.out.println("Couldn't find article: " + e.getMessage());
					// e.printStackTrace();
				} 
			}
		} catch (IOException e) {
			System.out.println ("Exiting due to Network Error: " + e.getMessage());
		}
		try {
			nntp.disconnect();
		} catch (IOException e) {
			System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
		} catch (NNTPUnexpectedResponseException e) {
			System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
		}
	}
}