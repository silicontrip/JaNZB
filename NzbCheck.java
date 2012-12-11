import java.io.*;
import java.util.*;
import java.net.*;
import java.util.ArrayList;


public class NzbCheck {
	
	public static void main(String[] args) {
		
		NNTPConnection nntp = null;
		NZBfile nzb;
		
		try {
			
			Properties fileProperties = new Properties();
			
			
			FileInputStream fis = new FileInputStream(new File("nntp.properties"));
			fileProperties.load (fis);
			fis.close();
			
			Integer port = new Integer(fileProperties.getProperty("NewsServerPort"));
			String host = fileProperties.getProperty("NewsServerHost");
			
			// nntp = new NNTPConnection(fileProperties.getProperty("NewsServerHost"),port);
			
			try {
				// nntp.connect();
				
				
				nzb = new NZBfile(args[0]);
				
				ArrayList<String> segments = nzb.getAllSegmentsAsStrings();
				
				int total = nzb.getNumberSegments();
				int counter = 1;
				
				AtomicCounter ac = new AtomicCounter(0,total-1);
				
				NNTPMatchedArticle nntpma = new printArticle();
				
				
				Integer threads = new Integer(fileProperties.getProperty("Threads"));
				
				Thread allThreads[];
				allThreads = new Thread[threads];
				
				long starttime = System.currentTimeMillis();
				
				for (int i=0; i<threads; i++) {
					
					NNTPConnection nntpthread = new NNTPConnection(host,port);
					//nntpthread.enableDebug();
					nntpthread.connect();
					allThreads[i] = new Thread (new NzbCheckThread(nntpma ,ac,nntpthread,nzb.getAllSegmentsAsStrings()));
					allThreads[i].start();
				}
				
				for (int i=0; i<threads; i++) {
					try {
						allThreads[i].join();
					} catch (InterruptedException e) {
						System.out.println("What interrupted us? " + e.getMessage());
					}
				}
				
				long totaltime = (System.currentTimeMillis() - starttime) / 1000;
				
				System.out.println(total + " articles in " + totaltime + " seconds (" + 1.0* total/totaltime + " a/s)");
				
				
			} catch (Exception e) {
				System.out.println("Could not read nzb file: " + e.getMessage());
			}
			
			
			
			
		} catch (IOException e) {
			System.out.println("Problem connecting to NNTP server: " + e.getMessage());
		} 
		
	}
	
}