import java.io.*;
import java.util.*;
import java.net.*;

public class NNTPindex {
	
	public static void main(String[] args) {
		
		NNTPConnection nntp = null;
		
		try {
			
			Properties fileProperties = new Properties();
			
			
			FileInputStream fis = new FileInputStream(new File("nntp.properties"));
			fileProperties.load (fis);
			fis.close();
			
			Integer port = new Integer(fileProperties.getProperty("NewsServerPort"));
			String host = fileProperties.getProperty("NewsServerHost");
			nntp = new NNTPConnection(host,port);
			
			try {
				nntp.connect();
				
				String group = fileProperties.getProperty("NewsServerGroup");
				
				nntp.setGroup(group);
				
				Integer start = nntp.getGroupStart();
				Integer end = nntp.getGroupEnd();
				Integer threads = new Integer(fileProperties.getProperty("Threads"));

				Thread allThreads[];
				
				allThreads = new Thread[threads];
				
				for (int i=0; i<threads; i++) {
					
					NNTPConnection nntpthread = new NNTPConnection(host,port);
					nntpthread.connect();
					nntpthread.setGroup(group);
					allThreads[i] = new Thread (new NzbCollectorThread(start+i, end,threads,nntpthread,args[0]));
				}
				
				for (int i=0; i<threads; i++) {
					allThreads[i].start();
				}
				
				for (int i=0; i<threads; i++) {
					try {
						allThreads[i].join();
					} catch (InterruptedException e) {
						System.out.println("What interrupted us? " + e.getMessage());
					}
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
			
			
			try {
				nntp.disconnect();
			} catch (IOException e) {
				System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
			} catch (NNTPUnexpectedResponseException e) {
				System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
			}
			
			
		} catch (IOException e) {
			System.out.println("Problem connecting to NNTP server: " + e.getMessage());
		} 
		
	}
	
}