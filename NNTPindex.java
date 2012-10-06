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
			
			//nntp.enableDebug();
			try {
				nntp.connect();
				
				String group = fileProperties.getProperty("NewsServerGroup");
				
				nntp.setGroup(group);
				
				Integer start = nntp.getGroupStart();
				Integer end = nntp.getGroupEnd();

				for (Integer i=start; i<end; i++) 
				{
				
					try {
						nntp.headArticle(i.toString());
					
						System.out.println (nntp.getArticleDate() + " : " + nntp.getArticleBytes() + " : " + nntp.getArticleSubject());
					} catch (NNTPException e) {
						System.out.println("Couldn't read article "+ i);
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