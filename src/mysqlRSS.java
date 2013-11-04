import java.sql.*;


public class mysqlRSS implements NNTPMatchedArticle {

    protected static final String url = "jdbc:mysql://localhost:3306/newzearch";
    
    
public  void processArticle (NNTPConnection n)  {

   
    
    
    String query = "INSERT INTO NNTPArticle (message_id,creation_time,title,description) VALUES ('" +
    n.getArticleMessageID() + "', '" +
    n.getArticleDateAsString() + "', '" +
    n.getArticleSubject() + "', '" +
    
    "Bytes: "+ n.getArticleBytes() +
    " Lines: "+ n.getArticleLines() +
    " Path: "+ n.getArticlePath() +
    " From: "+ n.getArticleFrom() +
    " Newsgroups: "+ n.getArticleNewsgroups() +
    " Message-ID: "+ n.getArticleMessageID() +
    " Organization: "+ n.getArticleOrganization() +
    " NNTP-Posting-Host: "+ n.getArticleNNTPPostingHost() +
    " Xref: "+ n.getArticleXref()+
    "')";

    try {
    
    
        Statement stmt;
        
        Class.forName("com.mysql.jdbc.Driver");

        
        Connection con = DriverManager.getConnection(url,"nzb", "nznzb");
        stmt = con.createStatement();

    
        stmt.executeUpdate(query);
    
        con.close();

    } catch (java.sql.SQLException sqe) {
        
        System.out.println("SQL Error: " + sqe.getMessage());
    } catch ( ClassNotFoundException cnfe) {
        System.out.println("Class not found: " + cnfe.getMessage());

    }
}


}
