
public class NNTPNoSuchArticleException extends NNTPException { 
	protected String article;
	public NNTPNoSuchArticleException (String message) { super(message); }
	public NNTPNoSuchArticleException (String message, String missingArticle) { this(message); article = missingArticle;  }

	public String getMissingArticleName() { return article; }
}
