import org.xml.sax.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.ArrayList;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


public class NZBfile implements java.io.Serializable {
	
	Document nzbdoc;
	NodeList nzbFiles;
	int[] filterMapping;
	
	protected static final String ROOT_NODE_NAME = "nzb";
	protected static final String FILE_NODE_NAME = "file";
	protected static final String GROUP_LIST_NAME = "groups";
	protected static final String GROUP_NODE_NAME = "group";
	protected static final String SEGMENT_LIST_NAME = "segments";
	protected static final String SEGMENT_NODE_NAME = "segment";

	
	public NZBfile(String file) throws ParserConfigurationException, SAXException, IOException  {
		
		// Step 1: create a DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		// Step 2: create a DocumentBuilder
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		// Step 3: parse the input file to get a Document object
		nzbdoc = db.parse(new File(file));
		
		// check NZB file is nzb
		
		if (nzbdoc.getFirstChild().getNodeName().equals(ROOT_NODE_NAME)) {
			
			// 			System.out.println("NZB -> " + nzbdoc.getFirstChild().getNodeName());
			nzbFiles = nzbdoc.getElementsByTagName(FILE_NODE_NAME);
			
		} else {
			throw new SAXException("not NZB file");
		}
		
		this.unsetFilter();
	}
	
	public NZBfile() throws ParserConfigurationException  {
	
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		nzbdoc = db.newDocument();
		
		Element root = nzbdoc.createElement(ROOT_NODE_NAME);
		nzbdoc.appendChild(root);
		
	}
	
	public void addFile (String subject, String group)
	{
		
		Element nzbfile = nzbdoc.createElement(FILE_NODE_NAME);
		// Add subject

		Element nzbgroups = nzbdoc.createElement(GROUP_LIST_NAME);
		Element nzbsegments = nzbdoc.createElement(SEGMENT_LIST_NAME);

	
		Element nzbgroup = nzbdoc.createElement(GROUP_NODE_NAME);
		nzbgroup.appendChild(nzbdoc.createTextNode(group));
		nzbgroups.appendChild(nzbgroup);
	
		nzbfile.appendChild(nzbgroups);


	}

	public static String getFileNameFromSubject(String subject)
	{
		String[] ele = subject.split('"');
		// should validate
		return ele[1];
	}

	public static String getSegmentNumberFromSubject(String subject)
	{
		String[] ele = subject.split('yEnc');

		String[] ele2 = ele[1].split("/");
		String[] ele3 = ele2[0].split("(");

		return ele3[1];

	}

	protected Element getFileForSubject(String subject)
	{
		// get all files
		// check subject
		// return match

	}

	public void addSegmentToFile(String subject, String segmentArticle)
	{

		// get file for subject
		// get segments
		// create segment
		// add segment

	}


	public void appendFile (ArrayList<String> groups, ArrayList<String> segments) {
	
		Element nzbfile = nzbdoc.createElement(FILE_NODE_NAME);
		// Add subject

		Element nzbgroups = nzbdoc.createElement(GROUP_LIST_NAME);
		Element nzbsegments = nzbdoc.createElement(SEGMENT_LIST_NAME);

		for (String s : groups)
		{
			Element nzbgroup = nzbdoc.createElement(GROUP_NODE_NAME);
			nzbgroup.appendChild(nzbdoc.createTextNode(s));
			nzbgroups.appendChild(nzbgroup);
		}
		
		nzbfile.appendChild(nzbgroups);
		
		for (String s : segments)
		{
			Element nzbsegment = nzbdoc.createElement(SEGMENT_NODE_NAME);
			nzbsegment.appendChild(nzbdoc.createTextNode(s));
			nzbsegments.appendChild(nzbsegment);
		}
		
		nzbfile.appendChild(nzbsegments);
		
		nzbdoc.appendChild(nzbfile);
		
	}
	
	public String toString() {
		
		
		try {
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			
			DOMSource source = new DOMSource(nzbdoc);
			transformer.transform(source, result);
			return sw.toString();
		} catch (Exception e) {
			// there *should* be no error here
			return "";
		}
		
	}
	
	public NodeList getAllSegments()
	{
		return nzbdoc.getElementsByTagName(SEGMENT_NODE_NAME);
	}
	
	public int getNumberSegments() 
	{
		return getAllSegments().getLength();
	}
	
	public ArrayList<String> getAllSegmentsAsStrings()
	{
		NodeList nl = getAllSegments();
		
		ArrayList<String> segments = new ArrayList<String>();
		
		for (int i =0; i < getNumberSegments(); i++) 
		{
			Node n = nl.item(i);
			segments.add(n.getFirstChild().getNodeValue());
		}
		return segments;
	}
		
	public void setFilter(String filter) {
		
		int m=0;
		
		for (int n=0; n < getTotalFilesLength(); n++) 
			if (((Attr)nzbFiles.item(n).getAttributes().getNamedItem("subject")).getValue().matches(filter)) 
				m++;
		
		filterMapping = new int[m];
		m=0;
		
		for (int n=0; n < getTotalFilesLength(); n++) 
			if (((Attr)nzbFiles.item(n).getAttributes().getNamedItem("subject")).getValue().matches(filter)) 
				filterMapping[m++]=n;
		
		
	}

	public void unsetFilter() {
		setFilter(".*"); // there might be an easier way to do this.
	}

	public  int mapFilter(int i) {
	
		if (i > filterMapping.length) {
			return i;
		}
		return filterMapping[i];
	}
	
//	public Document getDocument() { return nzbdoc; }
//	public NodeList getFiles() { return nzbFiles; }
	
	public int getTotalFilesLength() { 
		return nzbFiles.getLength();
	}
	
	public int getFilesLength() { 
		return filterMapping.length;
	}
	
	public String getFileSubjectTotal(int i) {
		return ((Attr)nzbFiles.item(i).getAttributes().getNamedItem("subject")).getValue();
	}
	
	
	public String getFileSubject(int i) {
		return ((Attr)nzbFiles.item(this.mapFilter(i)).getAttributes().getNamedItem("subject")).getValue();
	}
	
	public int getFileSegmentsSize(int f, int s) {
		NodeList seg;	
		seg = getFileSegments(f);
		try {
			return Integer.parseInt(((Attr)seg.item(s).getAttributes().getNamedItem("bytes")).getValue());
		} catch (NumberFormatException nfe) { return 0; }
	}
	
	public int getFileSegmentsLength(int i) { return getFileSegments(i).getLength();}
	
	public NodeList getFileSegments(int i) {
		if (nzbFiles.item(this.mapFilter(i)).getNodeName().equals("file")) {
			return ((Element)nzbFiles.item(this.mapFilter(i))).getElementsByTagName("segment");
		}
		return null;
	}
	
	public void  printFiles() {
		for (int n=0; n < getFilesLength(); n++) {
			System.out.println(getFileSubject(n));
		}
	}
	
	public String getName() {
	
		String s = 	getFileSubject(0);

		for (int n=1; n < getTotalFilesLength(); n++) 
			s=mungString (s,getFileSubjectTotal(n));
		
		return s.trim();
		
	}
	
	public String mungString(String s, String t) {
		
		String r = new String();
		int l = s.length();
		
		if (t.length() < l) { l = t.length(); }
		
		
		for (int n=0; n<l; n++) {
			if (s.charAt(n) == t.charAt(n)) {
				r = r + s.charAt(n);
			} else {
				r=r +" ";
			}
		}
		
		return r;
		
	}
	
}