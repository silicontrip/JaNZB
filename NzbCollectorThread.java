import java.io.*;

import java.util.HashMap;
import java.util.Date;
import ar.com.ktulu.yenc.*;


public class NzbCollectorThread implements Runnable {
	
	
	private int start;
	private int end;
	private int increment;
	private NNTPConnection nntp;
	private String match;
	
	public NzbCollectorThread (int st, int en, int in, NNTPConnection nn, String ma) {
		setStart(st);
		setEnd(en);
		setIncrement(in);
		setNNTP(nn);
		setMatch(ma);
	}
	
	public void setStart(int i) { start = i; }
	public void setEnd(int i) { end = i; }
	public void setIncrement(int i) { increment = i; }
	public void setNNTP(NNTPConnection i) { nntp = i; }
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
			int startarticle = start;
			for (int i=start; i<end; i+=increment) {
				
				String is = Integer.toString(i);
				
				try {
					HashMap<String,String> articleHeader;
					
					nntp.headArticle(is);
					//articleHeader = nntp.getHeader(is);
					
					String subject = nntp.getArticleSubject();
					
					//	System.out.println ("Subject: " + subject + "match: " + match);
					
					if (subject.matches(match)) {
						long currenttime = System.currentTimeMillis();
						int currentarticle = i;
						
						// in theory there are increment number of concurrent threads, they should all be doing about the same a/s
						Double aps = 1.0*(currentarticle - startarticle) / (currenttime - starttime)*1.0;
						Double remain = (end - i) / aps ;
						Date d = new Date (remain.longValue() + currenttime);
						System.out.println("" + i + " : " +  subject + " : " + nntp.getArticleName()  + " : " +  d.toString() );
						
					//	Thread decodeThread = new Thread (new Runnable() {
					//		public void run() 
					//		{
						/*
								try {
									// NNTPyDecoder ydec = new NNTPyDecoder(nntp.getArticleName());
									// ydec.decodeParts();
									
									YEncDecoder decoder = new YEncDecoder();
									nntp.bodyArticle(nntp.getArticleName());
									decoder.setInputStream(nntp);
									OutputStream out = new BufferedOutputStream(new FileOutputStream(decoder.getFileName()));
									decoder.setOutputStream(out);
									decoder.decode();
									System.out.println("decoded file \"" +
													 decoder.getFileName() +
													 "\" [" + decoder.getSize() + " bytes]");
									
								} catch (IOException e) {
									System.out.println("Couldn't connect to news server.");
								} catch (NNTPNoSuchArticleException e) {
									// don't want to know if the article isn't there.
									System.out.println("Couldn't find article: " + e.getMessage());
								} catch (ar.com.ktulu.yenc.YEncException e) {
									;
									System.out.println("Couldn't decode article: " + e.getMessage());
								} 
						 */
					//		}
					//	});
					//	decodeThread.start();
						
						//	NNTPyDecoder ydec = new NNTPyDecoder(nntp.getArticleName());
						//	ydec.decodeParts();
						
						starttime =currenttime;
						startarticle = currentarticle;
						
					}
				} catch (NNTPNoSuchArticleException e) {
					;
					// don't want to know if the article isn't there.
					System.out.println("Couldn't find article: " + e.getMessage());
					e.printStackTrace();
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