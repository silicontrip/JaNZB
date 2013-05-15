
all: classes
	jar -cf janzb.jar *.class 

classes: janzb.class  NNTPindex.class NzbCollector.class  NNTPget.class NzbCheck.class \
	NzbCollectorThread.class NzbCheckThread.class NNTPyDecoder.class \
	NNTPNetwork.class NNTPConnection.class NZBfile.class NNTPThread.class NNTPindex.class TCPConnection.class \
	AtomicCounter.class printArticle.class decodeArticle.class \
	NNTPConnectionResponseException.class NNTPGroupResponseException.class NNTPNoSuchArticleException.class NNTPNoSuchGroupException.class NNTPUnexpectedResponseException.class \
	printRSS.class nntprss.class writeRSS.class

%.class: %.java
	javac  -Xlint:deprecation -Xlint:unchecked -cp .:jyenc-0.5.jar  -target 1.5 $<

clean:
	rm *.class
