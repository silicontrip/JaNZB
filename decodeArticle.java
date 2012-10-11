class decodeArticle implements NNTPMatchedArticle  {
	public  void processArticle (NNTPConnection n) {
		System.out.println(n.getArticleName() + " : " + n.getArticleSubject() + " : " + n.getArticleMessageID());
		try {
			NNTPyDecoder ydec = new NNTPyDecoder(n);
			ydec.decodeParts();
		} catch (Exception e) {
			System.out.println ("Problem decoding article: " + e);
		}
	}
}
