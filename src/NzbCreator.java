import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;


public class NzbCreator {
	
	public static void main(String[] args) {
		
		NNTPConnection nntp = null;
		
//        561430118 - 562059827
        
        
//        561709547
        
  // 151811
        
        
		try {
			
			// check that all arguments are present...
			
			String group = args[0];
			Long articleStart = new Long(args[1]);
			Long articleEnd = new Long(args[2]);
			String articleMatchArgs = args[3];
            
            NZBfile nzbfile = new NZBfile();
			
			Properties fileProperties = new Properties();
			
			FileInputStream fis = new FileInputStream(new File("nntp.properties"));
			fileProperties.load (fis);
			fis.close();
			
			Integer port = new Integer(fileProperties.getProperty("NewsServerPort"));
			String host = fileProperties.getProperty("NewsServerHost");
			nntp = new NNTPConnection(host,port);
			
			//nntp.enableDebug();
			try {
				nntp.connect();
				
				// fileProperties.getProperty("NewsServerGroup");
				Integer threads = new Integer(fileProperties.getProperty("Threads"));

				Long articleNumber = articleEnd-articleStart;
				System.out.println("Range " + articleStart + "-" + articleEnd);
				System.out.println("Articles " + articleNumber);
				
				
				AtomicCounter forward = new AtomicCounter(articleStart, articleEnd, 1);
				
				NNTPMatchedArticle matchedArticle = new createNZB(group,nzbfile);
				
				Thread allThreads[];
				allThreads = new Thread[threads];
				
				//String match = new String(args[2]).concat("nzb.*");
				
			//	System.out.println ("Searching for: " + match);
				
				for (int i=0; i<threads; i++) {
					
					NNTPConnection nntpthread = new NNTPConnection(host,port);
					// nntpthread.enableDebug();
					nntpthread.connect();
					nntpthread.setGroup(group);
					allThreads[i] = new Thread (new NzbCollectorThread(matchedArticle,forward,nntpthread,group,articleMatchArgs,true));
					allThreads[i].start();
					
				}
				
				
				for (int i=0; i<threads; i++) {
					try {
						allThreads[i].join();
					} catch (InterruptedException e) {
						System.out.println("What interrupted us? " + e.getMessage());
					}
				}
				
				System.out.println (nzbfile);
				
			} catch (NumberFormatException e) {
				System.out.println("Could not read the group articles range: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("Problem reading from NNTP server: " + e.getMessage());
			} catch (NNTPConnectionResponseException e) {
				System.out.println("NNTP server didn't respond properly: " + e.getMessage());
			} catch (NNTPNoSuchGroupException e) {
				System.out.println("Couldn't find group: " + e.getMessage());
			} catch (NNTPGroupResponseException e) {
				System.out.println("Problem getting group from NNTP server: " + e.getMessage());
			} catch (javax.xml.parsers.ParserConfigurationException e) {
                System.out.println("XML parsing error: " + e.getMessage());

            }
			
			
			try {
				nntp.disconnect();
			} catch (IOException e) {
				System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
			} catch (NNTPUnexpectedResponseException e) {
				System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
			}
			
			
		} catch (IOException e) {
			System.out.println("Problem connecting to NNTP server: " + e.getMessage());
		} catch (javax.xml.parsers.ParserConfigurationException e) {
            			System.out.println("XML parsing error: " + e.getMessage());
        }
		
	}
	
}
