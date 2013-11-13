<?php
$error = "";
function hex_dump($data, $newline = "\n") {
	static $from = '';
	static $to = '';
	
	static $width = 16; // number of bytes per line
	
	static $pad = '.'; // padding for non-visible characters
	
	if ($from === '') {
		for($i = 0; $i <= 0xFF; $i ++) {
			$from .= chr ( $i );
			$to .= ($i >= 0x20 && $i <= 0x7E) ? chr ( $i ) : $pad;
		}
	}
	
	$hex = str_split ( bin2hex ( $data ), $width * 2 );
	$chars = str_split ( strtr ( $data, $from, $to ), $width );
	
	$offset = 0;
	foreach ( $hex as $i => $line ) {
		echo sprintf ( '%6X', $offset ) . ' : ' . implode ( ' ', str_split ( $line, 2 ) ) . ' [' . htmlentities ( $chars [$i] ) . ']' . $newline;
		$offset += $width;
	}
}

/**
 * yDecodes an encoded string and either writes the result to a file
 * or returns it as a string.
 *
 * @param
 *        	string yEncoded string to decode.
 * @param
 *        	destination Destination directory where the decoded file will
 *        	be written. This must be a valid directory <b>with no trailing
 *        	slash</b> to which PHP has write access. If <i>destination</i> is
 *        	not specified, the decoded file will be returned rather than
 *        	written to the disk.
 * @return If <i>destination</i> is not set, the decoded file will be
 *         returned as a string. Otherwise, <i>true</i> will be returned on
 *         success. In either case, <i>false</i> will be returned on error.
 * @see encode()
 */
function print_decode($string) {
	$encoded = array ();
	$header = array ();
	$trailer = array ();
	$decoded = '';
	$c = '';
	
	global $error;
	
	// Extract the yEnc string itself.
	preg_match ( "/^(=ybegin.*=yend[^$]*)$/ims", $string, $encoded );
	$encoded = $encoded [1];
	
	// Extract the file size from the header.
	preg_match ( "/^=ybegin.*size=([^ $]+)/im", $encoded, $header );
	$headersize = $header [1];
	
	// Extract the file name from the header.
	preg_match ( "/^=ybegin.*name=([^\\r\\n]+)/im", $encoded, $header );
	$filename = trim ( $header [1] );
	
	// Extract the file size from the trailer.
	preg_match ( "/^=yend.*size=([^ $\\r\\n]+)/im", $encoded, $trailer );
	$trailersize = $trailer [1];
	
	// Extract the CRC32 checksum from the trailer (if any).
	preg_match ( "/^=yend.*crc32=([^ $\\r\\n]+)/im", $encoded, $trailer );
	$crc = @trim ( @$trailer [1] );
	
	// remove anything before the =ybegin header.
	$encoded = preg_replace ( "/.*=ybegin/im", "=ybegin", $encoded, 1 );
	
	// Remove the header and trailer from the string before parsing it.
	$encoded = preg_replace ( "/(^=ybegin.*\\r\\n)/im", "", $encoded, 1 );
	$encoded = preg_replace ( "/(^=ypart.*\\r\\n)/im", "", $encoded, 1 );
	$encoded = preg_replace ( "/(^=yend.*)/im", "", $encoded, 1 );
	
	// Remove linebreaks from the string.
	$encoded = trim ( str_replace ( "\r\n", "", $encoded ) );
	
	// Make sure the header and trailer filesizes match up.
	if ($headersize != $trailersize) {
		$error = "Header and trailer file sizes do not match. This is a violation of the yEnc specification.";
		return false;
	}
	
	// Decode
	$strLength = strlen ( $encoded );
	
	for($i = 0; $i < $strLength; $i ++) {
		$c = $encoded {$i};
		
		if ($c == '=') {
			$i ++;
			$decoded .= chr ( (ord ( $encoded {$i} ) - 64) - 42 );
		} else {
			$decoded .= chr ( ord ( $c ) - 42 );
		}
	}
	
	// Make sure the decoded filesize is the same as the size specified in the header.
	if (strlen ( $decoded ) != $headersize) {
		$error = "Header file size ($headersize) and actual file size " . strlen ( $decoded ) . " do not match. The file is probably corrupt.";
		return false;
	}
	
	// Check the CRC value
	if ($crc != "" && strtolower ( $crc ) != strtolower ( sprintf ( "%08X", crc32 ( $decoded ) ) )) {
		$error = "CRC32 checksums do not match. The file is probably corrupt.";
		print "<pre>";
		print "$crc !=" . sprintf ( "%04X", crc32 ( $decoded ) );
		print "</pre>";
		return false;
	}
	
	// Should we write to a file or spit back a string?
	header ( "Content-type: application/x-nzb; name=\"$filename\"; charset=utf-8" );
	header ( "Cache-Control: no-cache, must-revalidate" );
	header ( "Content-Disposition: attachment; filename=\"$filename\"" );
	
	// Spit back a string.
	print $decoded;
}

global $error;

$scriptname = $_SERVER ['SCRIPT_NAME'];
$value = ltrim ( $_SERVER ['PATH_INFO'], '/' );
$uri = $_SERVER ['REQUEST_URI'];

if ($value) {
	
	$fp = fsockopen ( "news.netspace.net.au", 119, $errno, $errstr, 30 );
	
	if ($fp) {
		fgets ( $fp, 128 );
		$out = "body $value\r\n";
		fwrite ( $fp, $out );
		fgets ( $fp, 128 );
		$art = "";
		$r = fgets ( $fp, 128 );
		while ( $r != ".\r\n" ) {
			$art .= $r;
			$r = fgets ( $fp, 128 );
		}
		fwrite ( $fp, "QUIT\r\n" );
		fgets ( $fp, 128 );
		fclose ( $fp );
		
		print_decode ( $art );
		
		if ($error)
			echo "<h2>$error</h2>";
	}
}

?>
