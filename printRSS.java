/**
 * Created with IntelliJ IDEA.
 * User: d332027
 * Date: 15/05/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class printRSS implements NNTPMatchedArticle {

    private static String articleDir = "/Users/mark/Sites/nntp";

    public  void processArticle (NNTPConnection n) {



        System.out.println("<item>");
        System.out.println("<title>" + n.getArticleSubject() .replace("&", "&amp;").replace("<", "&lt;") +"</title>");
        System.out.println("<link>http://silicontrip.net/~mark/nzb.php/" + n.getArticleMessageID() .replace("&", "&amp;").replace("<", "&lt;") +"</link>");
        System.out.println("<pubDate>"+ n.getArticleDateAsString() +"</pubDate>");
        System.out.println("<description>");
        //System.out.println.getArticleSubject().replace("&", "&amp;").replace("<", "&lt;") );

      //  System.out.println("<content>");
        System.out.println("Bytes: "+ n.getArticleBytes() );
        System.out.println("Lines: "+ n.getArticleLines() );
        System.out.println("Path: "+ n.getArticlePath().replace("&", "&amp;").replace("<", "&lt;") );
        System.out.println("From: "+ n.getArticleFrom().replace("&", "&amp;").replace("<", "&lt;") );
        System.out.println("Newsgroups: "+ n.getArticleNewsgroups());
        System.out.println("Message-ID: "+ n.getArticleMessageID().replace("&", "&amp;").replace("<", "&lt;") );
        System.out.println("Organization: "+ n.getArticleOrganization());
        System.out.println("NNTP-Posting-Host: "+ n.getArticleNNTPPostingHost());
        System.out.println("Xref: "+ n.getArticleXref());
        System.out.println("</description>") ;

        //System.out.println("</content>");

        System.out.println("</item>");

    }
   }
