import java.io.*;
import java.util.*;
import java.net.*;

public class NzbCollector  {
	
	public static void main(String[] args) {
		
		NNTPConnection nntp = null;
		
		try {
			
			Properties fileProperties = new Properties();
			
			
			FileInputStream fis = new FileInputStream(new File("nntp.properties"));
			fileProperties.load (fis);
			fis.close();
			
			Integer port = new Integer(fileProperties.getProperty("NewsServerPort"));
			String host = fileProperties.getProperty("NewsServerHost");
			
			// maybe should put this in the properties file.
			// nntp.enableDebug();
			
			for (String group : fileProperties.getProperty("Groups").split(",")) {
				
				
				
				try {
					nntp = new NNTPConnection(host,port);
					nntp.connect();
					
					
					nntp.setGroup(group);
					Integer end = nntp.getGroupEnd();
					
					try {
						nntp.disconnect();
					} catch (IOException e) {
						System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
					} catch (NNTPUnexpectedResponseException e) {
						System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
					}
					
					
					
					if (fileProperties.getProperty(group + ".currentArticle") != null)
					{
						try {
							Integer threads = new Integer(fileProperties.getProperty("Threads"));
							Integer start = new Integer(fileProperties.getProperty(group + ".currentArticle"));
							
							
							if (args.length ==2 ) {
								start = new Integer(args[0]);
								end = new Integer(args[1]);
							}
							
							System.out.println("Group: " + group + " " + start +"-"+end);
							
							Thread allThreads[];
							
							allThreads = new Thread[threads];
							
							long starttime = System.currentTimeMillis();
							
							AtomicCounter ac = new AtomicCounter(start,end);
							
							for (int i=0; i<threads; i++) {
								
								NNTPConnection nntpthread = new NNTPConnection(host,port);
								//nntpthread.enableDebug();
								nntpthread.connect();
								nntpthread.setGroup(group);
								allThreads[i] = new Thread (new NzbCollectorThread(new printArticle(),ac,nntpthread,".*nzb.*"));
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
							
							System.out.println(end-start + " articles in " + totaltime + " seconds (" + 1.0* (end-start)/totaltime + " a/s)");
						} catch (NumberFormatException e) {
							System.out.println("Could not read the properties file: " + e.getMessage());
						}
						
						
					} 
					if (args.length != 2) {
						fileProperties.setProperty(group + ".currentArticle",end.toString());
						FileOutputStream fos = new FileOutputStream(new File("nntp.properties"));
						fileProperties.store(fos,null);
						fos.close();
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
				
				
			}
			
		} catch (IOException e) {
			System.out.println("Problem connecting to NNTP server: " + e.getMessage());
		} 
		
	}
	
	
}