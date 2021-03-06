import java.sql.*;
import java.text.SimpleDateFormat;

/*
 mysql> desc nntparticle;
 +---------------+--------------+------+-----+---------+----------------+
 | Field         | Type         | Null | Key | Default | Extra          |
 +---------------+--------------+------+-----+---------+----------------+
 | id            | int(11)      | NO   | PRI | NULL    | auto_increment |
 | message_id    | varchar(255) | YES  |     | NULL    |                |
 | creation_time | datetime     | YES  |     | NULL    |                |
 | title         | varchar(255) | YES  |     | NULL    |                |
 | description   | text         | YES  |     | NULL    |                |
 +---------------+--------------+------+-----+---------+----------------+
 */

public class mysqlRSS implements NNTPMatchedArticle {
    
    protected static final String url = "jdbc:mysql://localhost:3306/newzearch";
    
    
    public  void processArticle (NNTPConnection n)  {
        
        
	// System.out.println(">>> mysqlRSS processArticle");
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        
        
        //System.out.println("article date: " + df.format(n.getArticleDate()));
        
        java.sql.Timestamp sqlDate = new java.sql.Timestamp(n.getArticleDate().getTime());
        //System.out.println("sql date: " + df.format(sqlDate));
        
        
        
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
            query.setTimestamp(2,sqlDate);
            query.setString(3,  n.getArticleSubject());
            query.setString(4,description);
            
            query.executeUpdate();
            
            
            con.close();
            
        } catch (java.sql.SQLException sqe) {
            System.out.println("SQL Error: " + sqe.getMessage());
	    System.out.println("INSERT INTO NNTPArticle (message_id,creation_time,title,description) VALUES ('" + n.getArticleMessageID() + "','" + n.getArticleDate() + "','" +  n.getArticleSubject() + "','" + description + "')" ) ;
        } catch ( ClassNotFoundException cnfe) {
            System.out.println("Class not found: " + cnfe.getMessage());
            
        }
    }
    
    
}
