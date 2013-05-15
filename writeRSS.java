import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created with IntelliJ IDEA.
 * User: d332027
 * Date: 15/05/13
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class writeRSS {

    private static String articleDir = "/Users/mark/Sites/nntp";

    public  void processArticle (NNTPConnection n) implements NNTPMatchedArticle {


        String fileName = articleDir + "/" + n.getArticleMessageID();

        FileWriter fstream = new FileWriter(fileName, true);
        BufferedWriter out = new BufferedWriter(fstream);


        out.write("<item>");
        out.write("<title>" + n.getArticleSubject() .replace("&", "&amp;").replace("<", "&lt;") +"</title>");
        out.write("<link>http://silicontrip.net/~mark/nzb.php/" + n.getArticleMessageID() .replace("&", "&amp;").replace("<", "&lt;") +"</link>");
        out.write("<pubDate>"+ n.getArticleDateAsString() +"</pubDate>");
        out.write("<description>");
        //System.out.println.getArticleSubject().replace("&", "&amp;").replace("<", "&lt;") );

        //  System.out.println("<content>");
        out.write("Bytes: "+ n.getArticleBytes() );
        out.write("Lines: "+ n.getArticleLines() );
        out.write("Path: "+ n.getArticlePath().replace("&", "&amp;").replace("<", "&lt;") );
        out.write("From: "+ n.getArticleFrom().replace("&", "&amp;").replace("<", "&lt;") );
        out.write("Newsgroups: "+ n.getArticleNewsgroups());
        out.write("Message-ID: "+ n.getArticleMessageID().replace("&", "&amp;").replace("<", "&lt;") );
        out.write("Organization: "+ n.getArticleOrganization());
        out.write("NNTP-Posting-Host: "+ n.getArticleNNTPPostingHost());
        out.write("Xref: "+ n.getArticleXref());
        out.write("</description>") ;

        //System.out.println("</content>");

        out.write("</item>");

        out.close();

    }
}


