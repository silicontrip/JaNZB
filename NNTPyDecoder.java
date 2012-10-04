import java.io.*;
import java.net.*;
import java.util.*;
import ar.com.ktulu.yenc.*;

public class NNTPyDecoder {

	private String[] articleList;
	Part[] parts;
	YEncDecoder decoder;
	//NNTPConnection[] nntp;
	Integer nntpPort;
	String nntpHost;
	
	public NNTPyDecoder () throws IOException {
		decoder = new YEncDecoder();
		Properties fileProperties = new Properties();
		
		
		FileInputStream fis = new FileInputStream(new File("nntp.properties"));
		fileProperties.load (fis);
		fis.close();
		
		nntpPort = new Integer(fileProperties.getProperty("NewsServerPort"));
		nntpHost = fileProperties.getProperty("NewsServerHost");
		
	}

	public NNTPyDecoder (String[] a) throws IOException, YEncException, NNTPNoSuchArticleException, NNTPConnectionResponseException
	{
		this();
		setArticleList(a);
	}
	
	public NNTPyDecoder (String a) throws IOException, YEncException, NNTPNoSuchArticleException, NNTPConnectionResponseException
	{
		this();
		setArticle(a);
	}
	
	
	
	// public void setNNTPConnection (NNTPConnection n) { nntp = n; }
	
	/**
	 * Creates a connection to the News server and issues a Body command on the chosen article.
	 *
	 * @param articleName the name of the article to be fetched.
	 * @return an NNTPConnection to the News Server.
	 * @throws IOException if a network error occurs.
	 * @throws NNTPNoSuchArticleException if the chosen article does not exist.
	 * @throws NNTPConnectionResponseException if news server does not respond with the expected connection response.
	 */
	protected NNTPConnection nntpFactory(String articleName) throws IOException, NNTPNoSuchArticleException, NNTPConnectionResponseException
	{
		NNTPConnection nntp = new NNTPConnection(nntpHost,nntpPort);
		nntp.connect();

		nntp.bodyArticle(articleName);
		
		return nntp;
	}
	
	/**
	 * Set a single article for decode.
	 *
	 * @param article the name of the article for decoding.
	 * @throws IOException if a network error occurs while trying to read the article from the news server
	 * @throws NNTPNoSuchArticleException if the article doesn't exist on the news server
	 * @throws NNTPConnectionResponseException if the news server doesn't respond with the expected connection response
	 */
	
	public void setArticle(String article) throws IOException,  NNTPNoSuchArticleException, NNTPConnectionResponseException
	{
		parts = new Part[1];
		
		parts[0] = new Part();
		parts[0].file = article;
		parts[0].nntp = nntpFactory(parts[0].file);
		
	}
	/**
	 * Sets the article list.  Sorts the articles in part order. Validates all the parts exist and are from the same archive.
	 *
	 * @param articleList a list of the article name to be validated.
	 * @throws IOException if there was a network error reading from the news server
	 * @throws YEncException if the articles cannot be decoded. If there are parts missing or if the parts are from different archives.
	 * @throws NNTPNoSuchArticleException if the article doesn't exist on the news server
	 * @throws NNTPConnectionResponseException if the news server doesn't respond with the expected connection response
	 */
	
	public void setArticleList(String[] articleList) throws IOException, YEncException, NNTPNoSuchArticleException, NNTPConnectionResponseException
	{ 		
		parts = new Part[articleList.length];
		
		//nntp = new NNTPConnection[articleList.length];
		
		// get info on all parts
		for (int i = 0; i < articleList.length; i++) {
			decoder.reset();
			parts[i] = new Part();
			parts[i].file = articleList[i];
			parts[i].nntp = nntpFactory(parts[i].file);
			decoder.setInputStream( parts[i].nntp );
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
			// nntp.setArticleName(parts[i].file);
			parts[i].nntp.reset();
			decoder.setInputStream(parts[i].nntp);
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
			
			parts[i].nntp.disconnect();
			
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