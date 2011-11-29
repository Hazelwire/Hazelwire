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
 
define("preConfig",0,true);
define("postConfig",1,true);
define("preVPN",2,true);
define("preGameStart",3,true);
define("GameInProgress",4,true);
define("PostGame",5,true);
//includes
include_once 'lib/stringparser_bbcode.class.php';
include_once 'lib/etc_funcs.php';
include_once 'config.php';
include_once 'lib/GameConfig.php';
include_once 'lib/WebInterface.php';
include_once 'lib/AdminInterface.php';
include_once 'lib/ContestantInterface.php';
include_once 'lib/Smarty-3.0.6/libs/Smarty.class.php';
include_once 'lib/Error.php';
include_once 'lib/Contestant.php';
include_once 'lib/OpenVPNManager.php';

$interface;
if(ip_in_range($_SERVER['REMOTE_ADDR'], explode(",",$config['admin_ip_range']))){
      $interface = new AdminInterface();
}
else {
    $interface = new ContestantInterface();
}
$interface->doWork();
echo $interface->show();

?>
