	class printArticle implements NNTPMatchedArticle  {
		public  void processArticle (NNTPConnection n) {
			System.out.println(n.getArticleName() + " : " + n.getArticleSubject() + " : https://silicontrip.net/~mark/nntpget.php?move=" + n.getArticleMessageID());
		}
	}

