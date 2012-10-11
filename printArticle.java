	class printArticle implements NNTPMatchedArticle  {
		public  void processArticle (NNTPConnection n) {
			System.out.println(n.getArticleName() + " : " + n.getArticleSubject() + " : " + n.getArticleMessageID());
		}
	}

