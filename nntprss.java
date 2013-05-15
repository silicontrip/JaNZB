import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: d332027
 * Date: 15/05/13
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class nntprss {

    public static void main(String[] args) {

        NNTPConnection nntp;

        try {

            Properties fileProperties = new Properties();


            FileInputStream fis = new FileInputStream(new File("nntp.properties"));
            fileProperties.load (fis);
            fis.close();

            Integer port = new Integer(fileProperties.getProperty("NewsServerPort"));
            String host = fileProperties.getProperty("NewsServerHost");

            try {


                nntp = new NNTPConnection(host,port);
                nntp.connect();

                for (String artID : args)
                {

                    nntp.bodyArticle(artID);

                    NNTPMatchedArticle nntpma = new printRSS();

                    nntpma.processArticle(nntp);

                }
            }    catch (Exception e) {

                System.out.println("Unexpected error: (actually it was expected but I'm not sure what to do with it)");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            }

            nntp.disconnect();
            nntp.close();

        } catch (NNTPUnexpectedResponseException e) {

            System.out.println("Server did not return expected response: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NNTPConnectionResponseException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnknownHostException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FileNotFoundException e) {
            System.out.println("Could not find config file: " + e.getMessage());

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            System.out.println("Could not read from config file: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NNTPNoSuchArticleException e) {

            System.out.println("Could not read article: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
