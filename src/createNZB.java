	class createNZB implements NNTPMatchedArticle  {

		NZBfile nzb;
		String group;

		public createNZB (String g)
		{
			nzb= new NZBfile();
			group = g;
		}
		
		public  void processArticle (NNTPConnection n) {

			String fileSubject = nzb.getFileFromSubject( n.getArticleSubject());			
			String fileSegmentNumber = nzb.getSegmentNumberFromSubject( n.getArticleSubject());			

			synchronized (this) {
				Node file = nzb.getFileForSubject(fileSubject);
				if (file == null) {
					nzb.addFile(fileSubject,group);
				}
				nzb.addSegmentToFile(fileSubject,n.getMessageID(),fileSegmentNumber);
			}
		}
	}

