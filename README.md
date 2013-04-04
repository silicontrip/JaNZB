JaNZB
=====

Java based NZB and NNTP tools.

Uses the yenc library from Ktulu. http://ktulu.com.ar/blog/projects/jyenc/

This is a collection of tools that originally started out as another NZB downloader.
The NZB downloader was never completed but the NNTP class was used as the basis for other NNTP and NZB tools.

NzbCollector
------------

Scans a news group looking for NZB files.  It stores it's current article position in the news group and searches from 
that point to the newest article.  This is designed to be run periodically out of cron.

It writes it's current location to a configuration file, which also contains the list of groups to scan and news server details.

    #Thu Apr 04 22:27:22 EST 2013
    NewsServerPort=119
    Groups=alt.binaries.teevee
    Threads=8
    alt.binaries.teevee.currentArticle=489972152
    NewsServerHost=news.netspace.net.au

SAMPLE OUTPUT
-------------

    Group: alt.binaries.teevee 489972152-489979638
    489972155 : (bf1) [00/31] - "The.Biggest.Loser.Australia.S08E09.WS.PDTV.XviD.BF1.nzb" yEnc (2/2) : <YyB6t.86467$JR.16197@en-nntp-16.dc1.easynews.com>
    489972174 : (bf1) [00/31] - "The.Biggest.Loser.Australia.S08E09.WS.PDTV.XviD.BF1.nzb" yEnc (1/2) : <WyB6t.86466$JR.74528@en-nntp-16.dc1.easynews.com>
    489974599 : [133579]-[FULL]-[#a.b.teevee]-[ Kristen.Schaal.Live.At.The.Fillmore.2013.HDTV.x264-YesTV ]-[1/1] - "Kristen.Schaal.Live.At.The.Fillmore.2013.HDTV.x264-YesTV.nzb" yEnc (1/1) : <1364914122.81467.1@eu.news.astraweb.com>
    489975351 : [133580]-[FULL]-[#a.b.teevee]-[ Secret.Life.Of.Money.HDTV.x264-YesTV ]-[1/1] - "Secret.Life.Of.Money.HDTV.x264-YesTV.nzb" yEnc (1/1) : <1364914236.28143.1@eu.news.astraweb.com>
    489977281 : [133581]-[FULL]-[#a.b.teevee]-[ Secret.Life.Of.Money.720p.HDTV.x264-YesTV ]-[1/1] - "Secret.Life.Of.Money.720p.HDTV.x264-YesTV.nzb" yEnc (1/1) : <1364914339.46100.1@eu.news.astraweb.com>
    489978966 : [133582]-[FULL]-[#a.b.teevee]-[ Kristen.Schaal.Live.At.The.Fillmore.2013.720p.HDTV.x264-YesTV ]-[1/1] - "Kristen.Schaal.Live.At.The.Fillmore.2013.720p.HDTV.x264-YesTV.nzb" yEnc (1/1) : <1364914362.16322.1@eu.news.astraweb.com>
    7486 articles in 194 seconds (38.58762886597938 a/s)


NNTPget
-------

Takes a list of article IDs on the command line as multiple parts to a single yEncoded file to download and decode.


    java -cp janzb.jar:jyenc-0.5.jar NNTPget '<1364914339.46100.1@eu.news.astraweb.com>'
    decoding file "Secret.Life.Of.Money.720p.HDTV.x264-YesTV.nzb" [176458 bytes] (1/1)
    Done.


NzbCheck
--------

Checks that all the articles listed in the NZB file are available on the news server.

    java -cp janzb.jar NzbCheck Secret.Life.Of.Money.720p.HDTV.x264-YesTV.nzb 
    Could not read nzb file: www.newzbin.com

There is a problem with NZB files and the Java SAX XML parser which doesn't like the DOCTYPE entry.  
Removing this line resolves the issue.

    <?xml version="1.0" encoding="iso-8859-1" ?>
    <!DOCTYPE nzb PUBLIC "-//newzBin//DTD NZB 1.0//EN" "http://www.newzbin.com/DTD/nzb/nzb-1.0.dtd">
    <nzb xmlns="http://www.newzbin.com/DTD/2003/nzb">


    java -cp janzb.jar NzbCheck Secret.Life.Of.Money.720p.HDTV.x264-YesTV.nzb           
    <1364914327.95892.1@eu.news.astraweb.com> : [133581]-[FULL]-[#a.b.teevee]-[ Secret.Life.Of.Money.720p.HDTV.x264-YesTV ]-[26/43] - "secret.life.of.money.720p-yestv.r14" yEnc (01/66) : <1364914327.95892.1@eu.news.astraweb.com>
    <1364914327.98889.3@eu.news.astraweb.com> : [133581]-[FULL]-[#a.b.teevee]-[ Secret.Life.Of.Money.720p.HDTV.x264-YesTV ]-[26/43] - "secret.life.of.money.720p-yestv.r14" yEnc (03/66) : <1364914327.98889.3@eu.news.astraweb.com>
    <1364914327.97217.2@eu.news.astraweb.com> : [133581]-[FULL]-[#a.b.teevee]-[ Secret.Life.Of.Money.720p.HDTV.x264-YesTV ]-[26/43] - "secret.life.of.money.720p-yestv.r14" yEnc (02/66) : <1364914327.97217.2@eu.news.astraweb.com>
    <1364914327.99740.4@eu.news.astraweb.com> : [133581]-[FULL]-[#a.b.teevee]-[ Secret.Life.Of.Money.720p.HDTV.x264-YesTV ]-[26/43] - "secret.life.of.money.720p-yestv.r14" yEnc (04/66) : <1364914327.99740.4@eu.news.astraweb.com>
    ...
    Couldn't find article: <1364914327.95893.1@eu.news.astraweb.com>: 430 No such article
    1750 articles in 71 seconds (24.64788732394366 a/s)

A complete archive should not contain any errors.  However there may be enough PAR2 files to recover from any missing articles.

NNTPindex
---------

Is a tool for searching for a specific file in a news group based on date posted.

    java -cp janzb.jar:jyenc-0.5.jar NNTPindex alt.binaries.teevee 2011-12-18 2011-12-19 '.*Black.Mirror.S01E03.HDTV.*'

It first performs a binary search, to locate the article at the start an end of the date range.
It then performs a search over the range for an article matching the search REGEX.
Then it performs a forwards and backwards article by article search for the NZB file. 
The nzb file is downloaded and decoded.
