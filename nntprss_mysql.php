<?php

    if ($_SERVER['PATH_INFO']) {
        $download_url = 'http://' . $_SERVER['PATH_INFO'];
    } else {
        $download_url='http://silicontrip.net/~mark/nzb.php/';
    }
    
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

	print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";


?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom"> <channel>
<title>alt.binaries.teevee</title>
<link>
http://silicontrip.net/~mark/nntpget.php
</link>
<description>Collection of nzb articles.</description> <language>en-us</language>
<atom:link href="<?=$_SERVER['SCRIPT_NAME']?>" rel="self"
	type="application/rss+xml" />
<?

	print "<lastBuildDate>$builddate</lastBuildDate>\n";

        while ($row = mysqli_fetch_assoc($result)) {
	$link = $download_url . htmlentities($row['message_id']);
	$description=htmlentities($row['description']);
?>
<item>
<title><?=$row['title']?></title>
<link><?=$link?></link>
<pubDate><?=$row['creation_time']?></pubDate>
<guid><?=$link?></guid>
<description><?=$description?>
</description>
</item>
<?php

        }


        mysqli_free_result($result);
        mysqli_close($dbcon);


?>


</channel> </rss>

