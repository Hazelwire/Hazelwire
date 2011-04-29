<?php

function myip2long($ip) {
   if (is_numeric($ip)) {
       return sprintf("%u", floatval($ip));
   } else {
       return sprintf("%u", floatval(ip2long($ip)));
   }
}

########### function to chek ip if it in one of denyied/allowed networks =)

function ip_in_range($ip,$range) {
   $match = false;

   $ip_addr = decbin(myip2long($ip));

   $network = explode("/", $range);
   $net_addr = decbin(myip2long($network[0]));
   $cidr = $network[1];

   if (substr($net_addr, 0, $cidr) == substr($ip_addr, 0, $cidr)) {
       $match = true;
   }
   return $match;
}
?>
