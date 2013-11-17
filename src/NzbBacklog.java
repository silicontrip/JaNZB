import java.io.*;
import java.util.*;

public class NzbBacklog  {
	
	public static void main(String[] args) {
		
		NNTPConnection nntp;
		
		try {
			
			Properties fileProperties = new Properties();
			
			
			FileInputStream fis = new FileInputStream(new File("nntp.properties"));
			fileProperties.load (fis);
			fis.close();
			
			Integer port = new Integer(fileProperties.getProperty("NewsServerPort"));
			String host = fileProperties.getProperty("NewsServerHost");
			
			// maybe should put this in the properties file.
			// nntp.enableDebug();
			
			String group = args[2];
				try {
					nntp = new NNTPConnection(host,port);
                    nntp.connect();


				Long end, start;

                    nntp.setGroup(group);
					
					try {
						nntp.disconnect();
					} catch (Exception e) {
						System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
					} 
					
					
					try {
							
							//NNTPMatchedArticle nntpma = new printArticle();
                            NNTPMatchedArticle nntpma = new mysqlRSS();

							start = new Long(args[0]);
							end = new Long(args[1]);
							
							System.out.println("Group: " + group + " " + start +"-"+end);
							
							Thread allThread;
							
							allThread = new Thread();
							
							long starttime = System.currentTimeMillis();
							
							AtomicCounter ac = new AtomicCounter(start,end,-1);
							
							NNTPConnection nntpthread = new NNTPConnection(host,port);
							allThread = new Thread (new NzbCollectorThread(nntpma ,ac,nntpthread,group,".*\\.nzb.*",true));
							allThread.start();
							
							try {
								allThread.join();
							} catch (InterruptedException e) {
								System.out.println("What interrupted us? " + e.getMessage());
							}
							long totaltime = (System.currentTimeMillis() - starttime) / 1000;
							
							// System.out.println(end-start + " articles in " + totaltime + " seconds (" + 1.0* (end-start)/totaltime + " a/s)");
						} catch (NumberFormatException e) {
							System.out.println("Could not read the properties file: " + e.getMessage());
						}
						
						
					
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
				}
				
				
			
		} catch (IOException e) {
			System.out.println("Problem connecting to NNTP server: " + e.getMessage());
		} 
		
	}
	
	
}
