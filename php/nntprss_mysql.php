<?php

    if ($_SERVER['PATH_INFO']) {
        $download_url = 'http:/' . $_SERVER['PATH_INFO'];
    } else {
        $download_url='http://silicontrip.net/~mark/nzb.php/';
    }

libxml_use_internal_errors(true);

    
    $dbcon=mysqli_connect('127.0.0.1','nzb','nznzb','newzearch');


        if (mysqli_connect_errno($dbcon))
        {
                echo "Failed to connect to MySQL: " . mysqli_connect_error();
                exit;
        }

        $result = mysqli_query($dbcon,'select max(creation_time) as creation_time from nntparticle');
        while ($row = mysqli_fetch_assoc($result)) {
		$builddate = $row['creation_time'];
	}
        mysqli_free_result($result);

	$query="SELECT message_id,creation_time,title,description FROM NNTPArticle WHERE creation_time > DATE_SUB('". $builddate . "', INTERVAL 1 DAY) order by creation_time";
        $result = mysqli_query($dbcon,$query);

	header ( 'Content-type: application/xml' );
	set_time_limit ( 300 );

	$doc =  new DOMDocument("1.0");
	$doc->formatOutput = true;
	$docrss = $doc->createElement( "rss" );
	$doc->appendChild( $docrss );
	$docchannel = $doc->createElement( "channel" );
	$docrss->appendChild($docchannel);
	$doctitle = $doc->createElement( "title" ); $doctitle->appendChild($doc->createTextNode("alt.binaries.teevee"));
	$doclink = $doc->createElement( "link" ); $doclink->appendChild($doc->createTextNode("http://silicontrip.net/~mark/nntprss_mysql.php"));
	$docdescription = $doc->createElement( "description" ); $docdescription->appendChild($doc->createTextNode("collection of nzb articles."));
	$doclanguage = $doc->createElement( "language" ); $doclanguage->appendChild($doc->createTextNode("en"));
	$docbuilddate = $doc->createElement( "lastBuildDate" ); $docbuilddate->appendChild($doc->createTextNode($builddate));

	$docchannel->appendChild($doctitle);
	$docchannel->appendChild($doclink);
	$docchannel->appendChild($docdescription);
	$docchannel->appendChild($doclanguage);
	$docchannel->appendChild($docbuilddate);

	$rss = new SimpleXMLElement('<?xml version="1.0" encoding="UTF-8"' . '?' . '>' . "\r\n" . ' <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom"/>');

	$rss->addChild('channel');
	$rss->channel->addChild('title','alt.binaries.teevee');
	$rss->channel->addChild('link','http://silicontrip.net/~mark/nntpget.php');
	$rss->channel->addChild('description','Collection of nzb articles.');
	$rss->channel->addChild('language','en');
#	$rss->channel->children('atom',true)->addChild('link');
#	$rss->channel->children('atom',true)->link->addAttribute('href',$_SERVER['SCRIPT_NAME']);
#	$rss->channel->children('atom',true)->link->addAttribute('rel','self');
#	$rss->channel->children('atom',true)->link->addAttribute('type','application/rss+xml');

	$rss->channel->addChild('lastBuildDate',$builddate);

	$count = 0;
        while ($row = mysqli_fetch_assoc($result)) {

		$docitem = $doc->createElement( "item" );
		$rss->channel->addChild('item');

		$link = $download_url . $row['message_id'];
		#$striptitle = preg_replace ('^[^"]*"([^"]*)" yEnc \(\d/\d\)$','$1',$row['title']);
		$item = explode('"',$row['title']);

		$rss->channel->item[$count]->addChild('title',$item[1]);
		$rss->channel->item[$count]->addChild('link',$link);
		$rss->channel->item[$count]->addChild('pubDate',$row['creation_time']);
		$rss->channel->item[$count]->addChild('guid',$link);
		$rss->channel->item[$count]->addChild('description',$row['title'] . " " . $row['description']);

		$itemtitle = $doc->createElement( "title" ); $itemtitle->appendChild($doc->createTextNode($item[1]));
		$itemlink = $doc->createElement( "link" ); $itemlink->appendChild($doc->createTextNode($link));
		$itempubdate = $doc->createElement( "pubDate" ); $itempubdate->appendChild($doc->createTextNode($row['creation_time']));
		$itemguid = $doc->createElement( "guid" ); $itemguid->appendChild($doc->createTextNode($link));
		$itemdescription = $doc->createElement( "description" ); $itemdescription->appendChild($doc->createTextNode($row['title'] . " " . $row['description']));

		$docitem->appendChild($itemtitle);
		$docitem->appendChild($itemlink);
		$docitem->appendChild($itempubdate);
		$docitem->appendChild($itemguid);
		$docitem->appendChild($itemdescription);
		$docchannel->appendChild($docitem);
	$count++;
        }

        mysqli_free_result($result);
        mysqli_close($dbcon);

#echo $rss->asXML();
echo $doc->saveXML();


?>



