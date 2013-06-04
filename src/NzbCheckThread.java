import java.io.*;

import java.util.Date;
import java.util.ArrayList;

public class NzbCheckThread implements Runnable {
	
	private NNTPConnection nntp;
	private NNTPMatchedArticle callback;
	private AtomicCounter ac;
	private ArrayList<String> articleList;
	
	public NzbCheckThread (NNTPMatchedArticle nma, AtomicCounter atom, NNTPConnection nn, ArrayList<String> al) {
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
								
				try {
					nntp.headArticle("<" + is + ">" );
					callback.processArticle(nntp);
				} catch (NNTPNoSuchArticleException e) {
					System.out.println("Couldn't find article: " + e.getMessage());
				} 
			}
		} catch (IOException e) {
			System.out.println ("Exiting due to Network Error: " + e.getMessage());
		}
		try {
			nntp.disconnect();
		} catch (Exception e) {
			// like we really care if there is a problem disconnecting.
			System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
		} 
	}
}