import java.sql.*;
import java.text.SimpleDateFormat;

public class mysqlRSS implements NNTPMatchedArticle {

    protected static final String url = "jdbc:mysql://localhost:3306/newzearch";
    
    
public  void processArticle (NNTPConnection n)  {

    
    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

   
    System.out.println("article date: " + df.format(n.getArticleDate()));
    
    java.sql.Date sqlDate = new java.sql.Date(n.getArticleDate().getTime());
    System.out.println("sql date: " + df.format(sqlDate));
    
    
    
    String description =
    "Bytes: "+ n.getArticleBytes() +
    " Lines: "+ n.getArticleLines() +
    " Path: "+ n.getArticlePath() +
    " From: "+ n.getArticleFrom() +
    " Newsgroups: "+ n.getArticleNewsgroups() +
    " Message-ID: "+ n.getArticleMessageID() +
    " Organization: "+ n.getArticleOrganization() +
    " NNTP-Posting-Host: "+ n.getArticleNNTPPostingHost() +
    " Xref: "+ n.getArticleXref();

    try {
    
    
        Statement stmt;
        
        Class.forName("com.mysql.jdbc.Driver");

        
        Connection con = DriverManager.getConnection(url,"nzb", "nznzb");
        
        PreparedStatement query = con.prepareStatement("INSERT INTO NNTPArticle (message_id,creation_time,title,description) VALUES (?,?,?,?)");
        
        query.setString(1, n.getArticleMessageID());
        query.setDate(2,sqlDate);        
        query.setString(3,  n.getArticleSubject());
        query.setString(4,description);

        query.executeUpdate();
        
    
        con.close();

    } catch (java.sql.SQLException sqe) {
        
        System.out.println("SQL Error: " + sqe.getMessage());
    } catch ( ClassNotFoundException cnfe) {
        System.out.println("Class not found: " + cnfe.getMessage());

    }
}


}
