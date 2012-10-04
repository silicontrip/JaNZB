import java.io.*;
import java.net.*;
import java.util.*;
import ar.com.ktulu.yenc.*;

public class NNTPyDecoder {

	private String[] articleList;
	Part[] parts;
	YEncDecoder decoder;
	NNTPConnection nntp;
	
	public NNTPyDecoder (NNTPConnection n) {
		 decoder = new YEncDecoder();
	}

	public NNTPyDecoder (NNTPConnection n, String[] a) throws IOException, YEncException, NNTPNoSuchArticleException
	{
		decoder = new YEncDecoder();
		setNNTPConnection(n);
		setArticleList(a);
	}
	
	public void setNNTPConnection (NNTPConnection n) { nntp = n; }
	
	public void setArticleList(String[] articleList) throws IOException, YEncException, NNTPNoSuchArticleException
	{ 		
		parts = new Part[articleList.length];
		
		// get info on all parts
		for (int i = 0; i < articleList.length; i++) {
			decoder.reset();
			nntp.setArticleName(articleList[i]);
			decoder.setInputStream( nntp );
			parts[i] = new Part();
			parts[i].file = articleList[i];
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

	public void decodeParts() throws IOException, YEncException, NNTPNoSuchArticleException
	{
		OutputStream out = null;
		for (int i = 0; i < parts.length; i++) {
			nntp.setArticleName(parts[i].file);
			decoder.setInputStream(nntp);
			System.out.print("decoding file \"" +
							 decoder.getFileName() +
							 "\" [" + decoder.getSize() + " bytes]");
			if (decoder.getPartNumber() != -1)
				System.out.print(" (" +
								 decoder.getPartNumber() + "/" +
								 decoder.getTotalParts() + ")");
			System.out.println();
			if (out == null) {
				out = new BufferedOutputStream(
											   new FileOutputStream(
																	decoder.getFileName()));
				decoder.setOutputStream(out);
			}
			decoder.decode();
		}
	}		
	
class Part implements Comparable {
	String file, name;
	int part;
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