
all: janzb.jar classes
	jar -cf janzb.jar *.class ar

classes: janzb.class  NNTPindex.class NzbMake.class  NzbCollector.class  NNTPget.class \
	NzbCollectorThread.class NNTPyDecoder.class \
	NNTPNetwork.class NNTPConnection.class NZBfile.class NNTPThread.class NNTPindex.class TCPConnection.class \
	AtomicCounter.class printArticle.class \
	NNTPConnectionResponseException.class NNTPGroupResponseException.class NNTPNoSuchArticleException.class NNTPNoSuchGroupException.class NNTPUnexpectedResponseException.class

%.class: %.java
	javac  -Xlint:deprecation -Xlint:unchecked -target 1.5 $<

clean:
	rm *.class
