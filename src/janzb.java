import java.io.*;
import java.net.*;
import org.w3c.dom.*;
import java.beans.*;
import javax.xml.parsers.*;
import java.lang.InterruptedException;

public class janzb {
	
	public static void main(String[] args) {
		
		NNTPNetwork nntp = new NNTPNetwork();
		NNTPConnection nc;
		NZBfile nzb;
		NodeList segments;
		String subDir;
		
		
		try {
			nzb = new NZBfile(args[0]);
			
			try { 
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				
                try {
					DocumentBuilder db =  dbf.newDocumentBuilder();
					URL xmlURL = janzb.class.getResource("NNTPNetwork.xml");
					FileInputStream fis = new FileInputStream("NNTPNetwork.xml");
					
					if (xmlURL == null) { 
						System.out.println("Configuration: xmlURL is null"); 
					} else {
						//						XMLDecoder d = new XMLDecoder(new BufferedInputStream(xmlURL.openStream()));
						XMLDecoder d = new XMLDecoder(new BufferedInputStream(fis));
						
						nntp  = (NNTPNetwork) d.readObject();
						d.close();
					}
					
                } catch (ParserConfigurationException pce) {
					System.out.println("Configuration: Parser Configuration Error " + pce);
                } catch (IOException ioe) {
					System.out.println("Configuration: IO Error " + ioe);
					ioe.printStackTrace();
                }
				
				
				
				
				nntp.connectAll();
				
				if (args.length>1) { nzb.setFilter(args[1]); }
				
				System.out.println ("Getting " + nzb.getFilesLength() + " of " + nzb.getTotalFilesLength());
				
				
				for (int f=0; f < nzb.getFilesLength(); f++) {
					subDir = nzb.getName().replace('/','_');
					System.out.println ("FILE (" + f + "/" + nzb.getFilesLength() + ") "  + nzb.getFileSubject(f));
					
					new File(subDir).mkdir();
					segments = nzb.getFileSegment(f);
					//	System.out.println ("Number of segments: " + nzb.getFileSegmentsLength(f) + " "  + nzb.getFileSubject(f));
					
					for (int n=0; n < segments.getLength(); n++) {
						String articleName = segments.item(n).getFirstChild().getNodeValue();
						
						// file exists? and same size.
						File af = new File(subDir + '/' + articleName);
						if (af.exists()) {
							// && nzb.getFileSegmentsSize(f,n) <= af.length()) {
							System.out.println ("SKIPPING (" + n + "/" + segments.getLength() + ") "  + articleName);
							
						} else {
							while ((nc=nntp.getAvailableConnection())==null) { 
								try {
									int ttbps = 0;
									int c = 0;
									for (NNTPConnection cnc  : nntp) {
										ttbps +=cnc.getAveBPS();
										//	System.out.println("connection: " + c + " bps: " + cnc.getSplitBPS());
										c++;
									}
									System.out.print( "BPS: " + ttbps + "   \r");
									
									Thread.sleep(1000); 
								} catch (InterruptedException ie) {
									// like anything going to interrupt us?
									;
								}
							}
							
							System.out.println ("GETTING (" + n + "/" + segments.getLength() + ") "  + articleName);
							NNTPThread getThread = new NNTPThread(articleName,nc,af);
							getThread.start();
						}
						
					}
					waitCompleted(nntp);
					// perform yDec
					
				}
				
				// wait for all threads to finish
				
				waitCompleted(nntp);				
				nntp.disconnectAll();
				
			} catch (UnknownHostException e) {
				System.out.println("Unknown host " + e.toString());
			} catch (IOException e) {
				System.out.println("IO Error " + e.toString());
			}
		} catch (Exception e) {
			System.out.println("NZB file " + e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	
	
	static void waitCompleted(NNTPNetwork nntp) {
		while (nntp.connectionActive()) { 
			try {
				Thread.sleep(1000); 
			} catch (InterruptedException ie) {
				// like anything going to interrupt us?
				;
			}
		}
	}
}
