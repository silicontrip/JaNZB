import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: d332027
 * Date: 15/05/13
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class writeRSS implements NNTPMatchedArticle {

    private static String articleDir = "/Users/mark/Sites/nntp";

    public  void processArticle (NNTPConnection n)  {


        String fileName = articleDir + "/" + n.getArticleMessageID();


        try {
            FileWriter fstream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fstream);


            out.write("<item>\n");
            out.write("<title>" + n.getArticleSubject() .replace("&", "&amp;").replace("<", "&lt;") +"</title>\n");
            out.write("<link>http://silicontrip.net/~mark/nzb.php/" + n.getArticleMessageID() .replace("<", "%3C").replace(">" , "%3E").replace("@","%40") +"</link>\n");
            out.write("<pubDate>"+ n.getArticleDateAsString() +"</pubDate>\n");
            out.write("<guid>http://silicontrip.net/~mark/nzb.php/" + n.getArticleMessageID() .replace("<", "%3C").replace(">" , "%3E").replace("@","%40") +"</guid>\n");

            out.write("<description>\n");
            //System.out.println.getArticleSubject().replace("&", "&amp;").replace("<", "&lt;") );

            //  System.out.println("<content>");
            out.write("Bytes: "+ n.getArticleBytes() + "\n");
            out.write("Lines: "+ n.getArticleLines()+ "\n" );
            out.write("Path: "+ n.getArticlePath().replace("&", "&amp;").replace("<", "&lt;") + "\n");
            out.write("From: "+ n.getArticleFrom().replace("&", "&amp;").replace("<", "&lt;")+ "\n" );
            out.write("Newsgroups: "+ n.getArticleNewsgroups()+ "\n");
            out.write("Message-ID: "+ n.getArticleMessageID().replace("&", "&amp;").replace("<", "&lt;") .replace(">" , "&gt;") + "\n" );
            out.write("Organization: "+ n.getArticleOrganization()+ "\n");
            out.write("NNTP-Posting-Host: "+ n.getArticleNNTPPostingHost()+ "\n");
            out.write("Xref: "+ n.getArticleXref()+ "\n");
            out.write("</description>\n") ;

            //System.out.println("</content>");

            out.write("</item>\n");

            out.close();
        } catch (IOException exception) {
            System.out.println("Cannot write article file: " + exception.getMessage());
        }

    }
}


