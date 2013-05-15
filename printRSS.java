/**
 * Created with IntelliJ IDEA.
 * User: d332027
 * Date: 15/05/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class printRSS implements NNTPMatchedArticle {
    public  void processArticle (NNTPConnection n) {

        System.out.println("<item>");
        System.out.println("<title>" + n.getArticleName() .replace("&", "&amp;").replace("<", "&lt;") +"</title>");
        System.out.println("<link>http://silicontrip.net/~mark/nzb.php/" + n.getArticleMessageID() .replace("&", "&amp;").replace("<", "&lt;") +"</link>");
        System.out.println("<pubDate>"+ n.getArticleDateAsString() +"</pubDate>");
        System.out.println("<description>"+ n.getArticleSubject().replace("&", "&amp;").replace("<", "&lt;")  +"<br/>");

      //  System.out.println("<content>");
        System.out.println("Bytes: "+ n.getArticleBytes() +"<br/>");
        System.out.println("Lines: "+ n.getArticleLines() +"<br/>");
        System.out.println("Path: "+ n.getArticlePath().replace("&", "&amp;").replace("<", "&lt;")+"<br/>" );
        System.out.println("From: "+ n.getArticleFrom().replace("&", "&amp;").replace("<", "&lt;")+"<br/>" );
        System.out.println("Newsgroups: "+ n.getArticleNewsgroups()+"<br/>");
        System.out.println("Message-ID: "+ n.getArticleMessageID().replace("&", "&amp;").replace("<", "&lt;")+"<br/>" );
        System.out.println("Organization: "+ n.getArticleOrganization()+"<br/>");
        System.out.println("NNTP-Posting-Host: "+ n.getArticleNNTPPostingHost()+"<br/>");
        System.out.println("Xref: "+ n.getArticleXref()+"<br/>");
        System.out.println("</description>") ;

        //System.out.println("</content>");

        System.out.println("</item>");

    }
   }
