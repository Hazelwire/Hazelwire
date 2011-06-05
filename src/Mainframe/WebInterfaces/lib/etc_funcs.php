<?php

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
?>
