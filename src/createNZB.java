	class createNZB implements NNTPMatchedArticle  {

		NZBfile nzb;
		String group;

		public createNZB (String g, NZBfile f) throws javax.xml.parsers.ParserConfigurationException
		{
			nzb= f;
			group = g;
		}
		
		public  void processArticle (NNTPConnection n) {

			String fileSubject = nzb.getFileNameFromSubject( n.getArticleSubject());
			String fileSegmentNumber = nzb.getSegmentNumberFromSubject( n.getArticleSubject());			

			synchronized (this) {
				if (!nzb.hasFileForSubject(fileSubject)) {
					nzb.addFile(fileSubject,group);
				}
				nzb.addSegmentToFile(fileSubject,n.getArticleMessageID(),fileSegmentNumber);
			}
		}
	}

