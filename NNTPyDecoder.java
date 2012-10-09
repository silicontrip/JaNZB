import java.io.*;
import java.net.*;
import java.util.*;
import ar.com.ktulu.yenc.*;

public class NNTPyDecoder {
	
	private String[] articleList;
	Part[] parts;
	YEncDecoder decoder;
	NNTPConnection nntp;
	
	public NNTPyDecoder (NNTPConnection n) throws IOException {
		decoder = new YEncDecoder();
		setNNTPConnection(n);
		//Properties fileProperties = new Properties();
		
		
	//	FileInputStream fis = new FileInputStream(new File("nntp.properties"));
	//	fileProperties.load (fis);
	//	fis.close();
		
	//	nntpPort = new Integer(fileProperties.getProperty("NewsServerPort"));
	//	nntpHost = fileProperties.getProperty("NewsServerHost");
		
	}
	
	public NNTPyDecoder (NNTPConnection n, String[] a) throws IOException, YEncException, NNTPNoSuchArticleException, NNTPConnectionResponseException
	{
		this(n);
		setArticleList(a);
	}
	
	public NNTPyDecoder (NNTPConnection n, String a) throws IOException, YEncException, NNTPNoSuchArticleException, NNTPConnectionResponseException
	{
		this(n);
		setArticle(a);
	}
	
	
	/**
	 * Set the NNTP connection to read from.
	 *
	 * @param n the NNTPConnection.
	 */
	
	
	public void setNNTPConnection (NNTPConnection n) { nntp = n; }
	

	
	/**
	 * Set a single article for decode.
	 *
	 * @param article the name of the article for decoding.
	 */
	
	public void setArticle(String article) 
	{
		parts = new Part[1];
		
		parts[0] = new Part();
		parts[0].file = article;
		//parts[0].nntp = nntpFactory(parts[0].file);
		
	}
	/**
	 * Sets the article list for decoding. 
	 *
	 * @param articleList a list of article names for decoding.
	 */
	
	public void setArticleList(String[] articleList) 
	{ 		
		parts = new Part[articleList.length];
				
		for (int i = 0; i < articleList.length; i++) {
			parts[i] = new Part();
			parts[i].file = articleList[i];
		}
		
		
	}
	/**
	 * Sorts the articles in part order. Validates all the parts exist and are from the same archive.
	 * Do not run this method if reading for a network stream.  Oh hang on this is for an NNTP server.
	 * what am I thinking? Do not run this method, unless you don't care about network bandwidth and time.
	 * Or you are lazy and cannot be bothered sorting the yEnc parts. 
	 *
	 * @throws IOException if there was a network error reading from the news server
	 * @throws YEncException if the articles cannot be decoded. If there are parts missing or if the parts are from different archives.
	 * @throws NNTPNoSuchArticleException if the article doesn't exist on the news server
	 * @throws NNTPConnectionResponseException if the news server doesn't respond with the expected connection response
	 */
	public void validateArticleList() throws IOException, YEncException, NNTPNoSuchArticleException, NNTPConnectionResponseException
	{
		for (int i = 0; i < articleList.length; i++) {
			decoder.reset();
			nntp.bodyArticle(parts[i].file);
			decoder.setInputStream( nntp );
			parts[i].name = decoder.getFileName();
			parts[i].part = decoder.getPartNumber();
			parts[i].pbegin = decoder.getPartBegin();
			parts[i].pend = decoder.getPartEnd();
		}
		
		Arrays.sort(parts); 
		
		for (int i = 0; i < parts.length-1; i++) {
			if (parts[i].part+1 != parts[i+1].part)
				throw new MissingPartsException("Parts are " +
												"not consecutive (expecting=" +
												(parts[i].part + 1) + "/read=" +
												parts[i+1].part + ")");
			if (!parts[i].name.equals(parts[i+1].name))
				throw new YEncException("Parts are from " +
										"different archives");
			if (parts[i].pend+1 != parts[i+1].pbegin)
				throw new YEncException("Missing data");
		}
	}
	
	public void decodeParts() throws IOException, YEncException, NNTPNoSuchArticleException, NNTPUnexpectedResponseException
	{
		OutputStream out = null;
		for (int i = 0; i < parts.length; i++) {
			nntp.bodyArticle(parts[i].file);
			decoder.setInputStream( nntp );
			
			parts[i].name = decoder.getFileName();
			parts[i].part = decoder.getPartNumber();
			parts[i].pbegin = decoder.getPartBegin();
			parts[i].pend = decoder.getPartEnd();

			// validate as we go
			if (i>0) {
				if (parts[i].part != parts[i-1].part+1)
					throw new MissingPartsException("Parts are " +
													"not consecutive (expecting=" +
													(parts[i-1].part + 1) + "/found=" +
													parts[i].part + ")");
				
				if (!parts[i].name.equals(parts[i-1].name))
					throw new YEncException("Parts are from " +
											"different archives");
				
				
				if (parts[i-1].pend+1 != parts[i].pbegin)
					throw new YEncException("Missing data");
				
			}
			
			if (out == null) {
				out = new BufferedOutputStream(
											   new FileOutputStream(
																	decoder.getFileName()));
				decoder.setOutputStream(out);
			}
			
			System.out.print("decoding file \"" +
							 decoder.getFileName() +
							 "\" [" + decoder.getSize() + " bytes]");
			if (decoder.getPartNumber() != -1)
				System.out.print(" (" +
								 decoder.getPartNumber() + "/" +
								 decoder.getTotalParts() + ")");
			System.out.println();
			
			
			decoder.decode();
			
			System.out.println("Done.");
						
		}
	}		
	
	class Part implements Comparable {
		String file, name;
		int part;
		NNTPConnection nntp;
		long pbegin, pend;
		
		/**
		 * Note: this class has a natural ordering that is inconsistent
		 * with equals.
		 */
		public int compareTo(Object o) {
			if (!(o instanceof Part))
				throw new ClassCastException("object is not " +
											 "instance of Part");
			Part po = ((Part)o);
			
			if (part < po.part) return -1;
			if (part > po.part) return 1;
			return 0;
		}
	}
}