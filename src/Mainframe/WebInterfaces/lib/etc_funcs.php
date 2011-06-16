<?php
function ping($host) {
    exec(sprintf('ping -c 1 -W 1 %s', escapeshellarg($host)), $res, $rval);
    return $rval === 0;
  }
function myip2long($ip) {
   if (is_numeric($ip)) {
       return sprintf("%u", floatval($ip));
   } else {
       return sprintf("%u", floatval(ip2long($ip)));
   }
}

########### function to chek ip if it in one of denyied/allowed networks

function ip_in_range($ip,$range) {
   $match = false;
   
   $ip_addr = decbin(myip2long($ip));

   $network = explode("/", $range);
   $net_addr = decbin(myip2long($network[0]));
   $cidr = $network[1];

   if (strcmp(substr($net_addr, 0, $cidr),substr($ip_addr, 0, $cidr)) == 0) {
       $match = true;
   }
   return $match;
}

function startsWith($haystack, $needle)
{
    $length = strlen($needle);
    return (substr($haystack, 0, $length) === $needle);
}


   function run_in_background($Command, $Priority = 0)
   {
       if($Priority)
           $PID = shell_exec("nohup nice -n $Priority $Command 2> /dev/null & echo $!");
       else
           $PID = shell_exec("nohup $Command 2> /dev/null & echo $!");
       return($PID);
   }

   function is_process_running($PID)
   {
       exec("ps $PID", $ProcessState);
       return(count($ProcessState) >= 2);
   }

function checkValidIp($cidr,$range = false) {

    // Checks for a valid IP address or optionally a cidr notation range
    // e.g. 1.2.3.4 or 1.2.3.0/24
    
    $reg = ($range)?"/^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}(/[0-9]{1,2}){0,1}$/":"/^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$/";
   if(!preg_match($reg, $cidr)) {
       $return = FALSE;
   } else {
       $return = TRUE;
   }
   
    if ( $return == TRUE ) {

        $parts = explode("/", $cidr);
        $ip = $parts[0];
        $netmask = count($parts)>1?$parts[1]:"";
        $octets = explode(".", $ip);

        foreach ( $octets AS $octet ) {
            if ( $octet > 255 ) {
                $return = FALSE;
            }
        }

        if ( ( $netmask != "" ) && ( $netmask > 32 ) ) {
            $return = FALSE;
        }

    }

    return $return;

}

function deleteAll($directory, $empty = false) {
    if(substr($directory,-1) == "/") {
        $directory = substr($directory,0,-1);
    }

    if(!file_exists($directory) || !is_dir($directory)) {
        return false;
    } elseif(!is_readable($directory)) {
        return false;
    } else {
        $directoryHandle = opendir($directory);

        while ($contents = readdir($directoryHandle)) {
            if($contents != '.' && $contents != '..') {
                $path = $directory . "/" . $contents;

                if(is_dir($path)) {
                    deleteAll($path);
                } else {
                    unlink($path);
                }
            }
        }

        closedir($directoryHandle);

        if($empty == false) {
            if(!rmdir($directory)) {
                return false;
            }
        }

        return true;
    }
}


// Unify line breaks of different operating systems
function convertlinebreaks ($text) {
    return preg_replace ("/\015\012|\015|\012/", "\n", $text);
}

// Remove everything but the newline charachter
function bbcode_stripcontents ($text) {
    return preg_replace ("/[^\n]/", '', $text);
}

function do_bbcode_url ($action, $attributes, $content, $params, $node_object) {
    if (!isset ($attributes['default'])) {
        $url = $content;
        $text = htmlspecialchars ($content);
    } else {
        $url = $attributes['default'];
        $text = $content;
    }
    if ($action == 'validate') {
        if (substr ($url, 0, 5) == 'data:' || substr ($url, 0, 5) == 'file:'
          || substr ($url, 0, 11) == 'javascript:' || substr ($url, 0, 4) == 'jar:') {
            return false;
        }
        return true;
    }
    return '<a href="'.htmlspecialchars ($url).'">'.$text.'</a>';
}

// Function to include images
function do_bbcode_img ($action, $attributes, $content, $params, $node_object) {
    if ($action == 'validate') {
        if (substr ($content, 0, 5) == 'data:' || substr ($content, 0, 5) == 'file:'
          || substr ($content, 0, 11) == 'javascript:' || substr ($content, 0, 4) == 'jar:') {
            return false;
        }
        return true;
    }else{
        $src = htmlspecialchars($content);
        $height =   isset($attributes['height'])    ?"height=\"".$attributes['height']  . "\" ":"";
        $width =    isset($attributes['width'])     ?"width=\"" .$attributes['width']   . "\" ":"";
        $alt =      isset($attributes['alt'])       ?"alt=\""   .$attributes['alt']     ."\" ":"";
    }
    return '<img src="'.$src.'" '.$height. $width . $alt. ' />';
}
function parse_links  ( $m )
{
    $href = $name = html_entity_decode($m[0]);

    if ( strpos( $href, '://' ) === false ) {
        $href = 'http://' . $href;
    }
   /* if( strpos($href,"[img") !== false ||
    	strpos($href,"[url") !== false ||
    	strpos($href,"[/img]") !== false ||
    	strpos($href,"[/url]") !== false)
    	return $href;*/

    if( strlen($name) > 50 ) {
        $k = ( 50 - 3 ) >> 1;
        $name = substr( $name, 0, $k ) . '...' . substr( $name, -$k );
    }

    return sprintf( '<a href="%s">%s</a>', htmlentities($href), htmlentities($name) );
}

function autoParseLinks($text){
    $regex = '/(((https?):\/\/|www\d?\.)((([a-z0-9][a-z0-9-]*[a-z0-9]\.)*[a-z][a-z0-9-]*[a-z0-9]|((\d|[1-9]\d|1\d{2}|2[0-4][0-9]|25[0-5])\.){3}(\d|[1-9]\d|1\d{2}|2[0-4][0-9]|25[0-5]))(:\d+)?)(((\/+([a-z0-9$_\.\+!\*\'\(\),;:@&=-]|%[0-9a-f]{2})*)*(\?([a-z0-9$_\.\+!\*\'\(\),;:@&=-]|%[0-9a-f]{2})*)?)?)?(#([a-z0-9$_\.\+!\*\'\(\),;:@&=-]|%[0-9a-f]{2})*)?)/i';
    $text = preg_replace_callback( $regex, 'parse_links', $text );
    return $text;
}

?>
