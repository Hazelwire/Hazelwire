<?php
define("preConfig",0,true);
define("postConfig",1,true);
define("preVPN",2,true);
define("preGameStart",3,true);
define("GameInProgress",4,true);
define("PostGame",5,true);
//includes
include_once 'lib/etc_funcs.php';
include_once 'config.php';
include_once 'lib/WebInterface.php';
include_once 'lib/AdminInterface.php';
include_once 'lib/ContestantInterface.php';
include_once 'lib/Smarty-3.0.6/libs/Smarty.class.php';
include_once 'lib/Error.php';


$interface;
if(strcmp($_SERVER['REMOTE_ADDR'], '127.0.0.1') === 0 || ip_in_range($_SERVER['REMOTE_ADDR'], $config['admin_ip_range'])){
      $interface = new AdminInterface();
}
else {
    $interface = new ContestantInterface();
}
$interface->doWork();
echo $interface->show();
?>