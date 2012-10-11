import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;


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
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ");
			Date startDate = sdf.parse(args[0] + " 00:00:00 GMT",new ParsePosition(0));
			Date endDate = sdf.parse(args[1] + " 00:00:00 GMT",new ParsePosition(0));

			
			//nntp.enableDebug();
			try {
				nntp.connect();
				
				String group = fileProperties.getProperty("NewsServerGroup");
				Integer threads = new Integer(fileProperties.getProperty("Threads"));

				nntp.setGroup(group);
				
				Integer start = nntp.getGroupStart();
				Integer end = nntp.getGroupEnd();

				System.out.println("Group: " + group + " " + start +"-"+end);

				
				int articleStart = huntDate(start,end,startDate,nntp);
				int articleEnd = huntDate(start,end,endDate,nntp);
				int articleNumber = articleEnd-articleStart;
				System.out.println("Range " + articleStart + "-" + articleEnd);
				System.out.println("Articles " + articleNumber);
				
				int found = huntSubject(articleStart,articleEnd,args[2],nntp);
				
				// hunt backwards and forwards until the nzb file is found.
				
				AtomicCounter forward = new AtomicCounter(found, found + 10000, 1);
				AtomicCounter backward = new AtomicCounter(found-1, found - 10000, -1);
				
				NNTPMatchedArticle matchedArticle = new decodeArticle();
				
				Thread allThreads[];
				allThreads = new Thread[threads];
				
				String match = new String(args[2]).concat(".*\\.nzb.*");
				
				System.out.println ("Searching for: " + match);
				
				for (int i=0; i<threads; i+=2) {
					
					NNTPConnection nntpthread = new NNTPConnection(host,port);
					// nntpthread.enableDebug();
					nntpthread.connect();
					nntpthread.setGroup(group);
					allThreads[i] = new Thread (new NzbCollectorThread(matchedArticle,forward,nntpthread,match));
					allThreads[i].start();
					
					nntpthread = new NNTPConnection(host,port);
					// nntpthread.enableDebug();
					nntpthread.connect();
					nntpthread.setGroup(group);
					allThreads[i+1] = new Thread (new NzbCollectorThread(matchedArticle,backward,nntpthread,match));
					allThreads[i+1].start();
					
					
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
	
	protected static int huntDate (int start, int end, Date searchDate, NNTPConnection nntp) throws IOException {
		
		Integer get;
		do {
			
			// What's the difference
			//				Integer get = (start + end) / 2;
			 get = start + (end - start) / 2;
			
			
			try {
				nntp.headArticle(get.toString());
				// should start recording this.
				System.out.println (get + " : " + nntp.getArticleDateAsString() + " : " + nntp.getArticleSubject() );
			} catch (NNTPException e) {
				System.out.println("Couldn't read article "+ get);
			}
			
			if ( searchDate.compareTo(nntp.getArticleDate()) == 1) { start = get; }
			if ( searchDate.compareTo(nntp.getArticleDate()) <= 0) { end = get; }
			
		//	System.out.println ("distance: " + (end-start));
			
		} while (end-start > 1);
		return start;
	}		
	
	protected static int huntSubject (int articleStart, int articleEnd, String match, NNTPConnection nntp) throws IOException
	{
		int articleNumber = articleEnd-articleStart;
		
		for (int i=0; 1<<i < articleNumber; i ++)
		{
			
			
			int step = articleNumber / (1 << i);
		//	System.out.println ("search: " + (1<<i) + " : " + step); 
			
			for (int j=0; j * step < articleNumber; j++) {
				
				int get = (j * step) + (step / 2) + articleStart;
				
				try {
					nntp.headArticle(""+get);
					System.out.println (""+get + " : " + nntp.getArticleDateAsString() + " : " + nntp.getArticleSubject() );
					
					if (nntp.getArticleSubject().matches(match)) {
						return get;
						
					}
					
				} catch (NNTPException e) {
					System.out.println("Couldn't read article "+ get);
				}
				
				
			}
			
		}
	
		return -1;
	}
	
	
}