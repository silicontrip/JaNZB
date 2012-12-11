import java.io.*;

import java.util.Date;
import java.util.ArrayList;

public class NzbCheckThread implements Runnable {
	
	
	private NNTPConnection nntp;
	private NNTPMatchedArticle callback;
	private AtomicCounter ac;
	private ArrayList<String> articleList;
	
	
	
	public NzbCheckThread (NNTPMatchedArticle nma, AtomicCounter atom, NNTPConnection nn, ArrayList<String> al) {
		//setStart(st);
		//setEnd(en);
		//setIncrement(in);
		setAtomicCounter(atom);
		setNNTP(nn);
		setArticleList(al);
		setNNTPMatchedArticle(nma);
	}
	
	public void setAtomicCounter(AtomicCounter atom) { ac = atom; }
	public void setNNTP(NNTPConnection i) { nntp = i; }
	public void setNNTPMatchedArticle(NNTPMatchedArticle nma) { callback = nma; }
	public void setArticleList(ArrayList<String> al) { articleList = al ; }
	
	public void run() {
		
		try{
			long starttime = System.currentTimeMillis();
			long i;
			while ((i = ac.getNext() ) != -1) {
				
				
				String is = articleList.get((int)i);
				
				// String is = Long.toString(i);
				
				try {
					
					nntp.headArticle("<" + is + ">" );
										
					//System.out.println ("" + i + ": Subject: " + subject + "match: " + match);
					
					//	long currenttime = System.currentTimeMillis();
					//	long currentarticle = i;
						
						// in theory there are increment number of concurrent threads, they should all be doing about the same a/s
					//	Double aps = 1.0*(currentarticle - startarticle) / (currenttime - starttime)*1.0;
						
						callback.processArticle(nntp);
						
						
					//	starttime =currenttime;
					//	startarticle = currentarticle;
						
					
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