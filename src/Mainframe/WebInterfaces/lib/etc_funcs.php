<?php
/*******************************************************************************
 * Copyright (c) 2011 The Hazelwire Team.
 *     
 * This file is part of Hazelwire.
 * 
 * Hazelwire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Hazelwire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/**
 * Pings a host to test if it is online
 * @param string $host The host to ping
 * @return boolean true if 1 or more pings returned successfuly
 */
function ping($host) {
    exec(sprintf('ping -c 1 -W 1 %s', escapeshellarg($host)), $res, $rval);
    return $rval === 0;
  }

  /**
   * Converts a given dotted notated IP address to it long form
   * @param string $ip The ip to convert
   * @return string the long notation for the given IP address
   */
function myip2long($ip) {
   if (is_numeric($ip)) {
       return sprintf("%u", floatval($ip));
   } else {
       return sprintf("%u", floatval(ip2long($ip)));
   }
}


/**
 * Tests whether the given IP falls within the given network range
 * @param string $ip The dotted notated IP to check
 * @param array $range The array of CIDR notated IP ranges
 * @return boolean true if it falls within the range, false otherwise
 */
function ip_in_range($ip,$ranges) {
   $match = false;
   if(!is_array($ranges))
        $ranges = array($ranges);
   
   for ($i = 0; $i<count($ranges) && !$match; $i++){
       $ip_addr = decbin(myip2long($ip));

       $network = explode("/", $ranges);
       $net_addr = decbin(myip2long($network[0]));
       $cidr = $network[1];

       if (strcmp(substr($net_addr, 0, $cidr),substr($ip_addr, 0, $cidr)) == 0) {
           $match = true;
       }
   }
   return $match;
}

/**
 * Tests if a given string starts with a certain other string
 * @param string $haystack The string to test the start of
 * @param string $needle The possible start of the haystack
 * @return boolean true if $haystack starts with $needle
 */
function startsWith($haystack, $needle)
{
    $length = strlen($needle);
    return (substr($haystack, 0, $length) === $needle);
}

/**
 * Runs a program or script in the background, in order to let PHP continue its business
 * @param string $Command The command to execute in the background
 * @param int $Priority The priority (i.e. nice value) of the to be made process
 * @return <type>
 */
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

   /**
    * Checks the given string to check if it is a valid IP address or CIDR address range
    * @param string $cidr The IP address (range) which needs to be checked
    * @param boolean $range whether or not the given IP is a CIDR range
    * @return boolean true if the address is valid, false otherwise
    */
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

/**
 * Deletes (or empties) a directory recursively, removing everything thats inside
 * 
 * @param <type> $directory the path to the directory that needs to be deleted or emptied
 * @param <type> $empty Whether to empty the given directory or delete it
 * @return boolean true if the deletion was successful, false if it ran into a problem
 */
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


/**
 * Converts linebreaks to work on most platforms
 * @param string $text Text to convert linebreaks in
 * @return string Text with converted linebreaks
 */
function convertlinebreaks ($text) {
    return preg_replace ("/\015\012|\015|\012/", "\n", $text);
}

/**
 * Strips all the non-newline characters from the string
 * @param string $text Text which needs stripping
 * @return string Only the newline characters
 */
function bbcode_stripcontents ($text) {
    return preg_replace ("/[^\n]/", '', $text);
}

/**
 * Function for BB parser, handles [size]
 */
function do_bbcode_size ($action, $attributes, $content, $params, $node_object) {
    if ($action == 'validate') {
        return isset($attributes['default']) && preg_match('/(^xx-small|x-small|small|medium|large|x-large|xx-large$)|^([0-9]*\.)?[0-9]+(px|em|pt|%)$/', $attributes['default']);
    }

    return '<span style="font-size:'.$attributes['default'].';">'.$content.'</span>';
}

/**
 * Function for BB parser, handles [color]
 */
function do_bbcode_color ($action, $attributes, $content, $params, $node_object) {
    if (!isset ($attributes['default'])) {
        $color = "#000000";
    } else {
        $color = $attributes['default'];
    }
    if ($action == 'validate') {
        return preg_match('/^#[a-f0-9]{6}|\w$/i', $color);
    }
    return '<span style="color:'.$color.';">'.$content.'</span>';
}

/**
 * Function for BB parser, handles [url]
 */
function do_bbcode_url ($action, $attributes, $content, $params, $node_object) {
    if (!isset ($attributes['default'])) {
        $url = $content;
        $text = htmlspecialchars ($content);
    } else {
        $url = $attributes['default'];
        $text = $content;
    }
    if ( strpos( $url, '://' ) === false ) {
        $url = 'http://' . $url;
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

/**
 * Function for BB parser, handles [img]
 */
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

/**
 * Function for BB parser, creates links from given text
 */
function parse_links  ( $m )
{
    $href = $name = html_entity_decode($m[0]);

    if ( strpos( $href, '://' ) === false ) {
        $href = 'http://' . $href;
    }

    if( strlen($name) > 50 ) {
        $k = ( 50 - 3 ) >> 1;
        $name = substr( $name, 0, $k ) . '...' . substr( $name, -$k );
    }

    return sprintf( '<a href="%s">%s</a>', htmlentities($href), htmlentities($name) );
}

/**
 * Function for BB parser, gets all non-[url]'d links from the text to link them
 */
function autoParseLinks($text){
    $regex = '/(((https?):\/\/|www\d?\.)((([a-z0-9][a-z0-9-]*[a-z0-9]\.)*[a-z][a-z0-9-]*[a-z0-9]|((\d|[1-9]\d|1\d{2}|2[0-4][0-9]|25[0-5])\.){3}(\d|[1-9]\d|1\d{2}|2[0-4][0-9]|25[0-5]))(:\d+)?)(((\/+([a-z0-9$_\.\+!\*\'\(\),;:@&=-]|%[0-9a-f]{2})*)*(\?([a-z0-9$_\.\+!\*\'\(\),;:@&=-]|%[0-9a-f]{2})*)?)?)?(#([a-z0-9$_\.\+!\*\'\(\),;:@&=-]|%[0-9a-f]{2})*)?)/i';
    $text = preg_replace_callback( $regex, 'parse_links', $text );
    return $text;
}

?>
