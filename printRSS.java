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
        System.out.println("<title>" + n.getArticleName() .replace("<", "&lt;").replace("&", "&amp;"); +"</title>");
        System.out.println("<link>http://silicontrip.net/~mark/nntpget.php/" + n.getArticleMessageID() .replace("<", "&lt;").replace("&", "&amp;"); +"</link>");
        System.out.println("<pubDate>"+ n.getArticleDateAsString() +"</pubDate>");
        System.out.println("<description>"+ n.getArticleSubject().replace("<", "&lt;").replace("&", "&amp;"); +"</description>") ;
        System.out.println("<content>");
        System.out.println("Bytes: "+ n.getArticleBytes());
        System.out.println("Lines: "+ n.getArticleLines());
        System.out.println("Path: "+ n.getArticlePath().replace("<", "&lt;").replace("&", "&amp;"););
        System.out.println("From: "+ n.getArticleFrom().replace("<", "&lt;").replace("&", "&amp;"););
        System.out.println("Newsgroups: "+ n.getArticleNewsgroups());
        System.out.println("Message-ID: "+ n.getArticleMessageID().replace("<", "&lt;").replace("&", "&amp;"););
        System.out.println("Organization: "+ n.getArticleOrganization());
        System.out.println("NNTP-Posting-Host: "+ n.getArticleNNTPPostingHost());
        System.out.println("Xref: "+ n.getArticleXref());

        System.out.println("</content>");

        System.out.println("</item>");

    }
   }
