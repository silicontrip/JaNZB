import java.io.*;
import java.util.*;
import java.net.*;
import ar.com.ktulu.yenc.*;

public class NNTPget {
	
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
				//nntp.enableDebug();

				nntp.connect();
				
				//String group = fileProperties.getProperty("NewsServerGroup");
				
				//nntp.setGroup(group);
								
				//nntp.bodyArticle(args[0]);
				//nntp.writeArticleToFile(new File(args[0]));
				
				NNTPyDecoder ydec = new NNTPyDecoder(nntp,args);
				ydec.decodeParts();

							
				try {
					nntp.disconnect();
				} catch (IOException e) {
					System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
				} catch (NNTPUnexpectedResponseException e) {
					System.out.println("Problem disconnecting from NNTP server: " + e.getMessage());
				}
			

			} catch (NNTPUnexpectedResponseException e) {
				System.out.println("NNTP server didn't respond properly: " + e.getMessage());
			} catch (YEncException e) {
				System.out.println("Could not decode the articles: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("Problem reading from NNTP server: " + e.getMessage());
			} catch (NNTPConnectionResponseException e) {
				System.out.println("NNTP server didn't respond properly: " + e.getMessage());
			} catch (NNTPNoSuchArticleException e) {
				System.out.println("Couldn't find the articles: " + e.getMessage());
				e.printStackTrace();
			}
						
		} catch (IOException e) {
			System.out.println("Problem connecting to NNTP server: " + e.getMessage());
		} 
		
	}
	
}