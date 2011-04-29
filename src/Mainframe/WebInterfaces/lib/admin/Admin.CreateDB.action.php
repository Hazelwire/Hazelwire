<?php
function create_database(){
    global $interface;
    $config = $interface->getConfig();
    //open_db
    $database = new PDO ("sqlite:" . $config['database_file_name']);
    if($database === false)
        return false;
    //create tables
    $res = $database->exec("CREATE TABLE 'state' ( 'state' SMALLINT );");
    if($res === false)return false;
    $res = $database->exec("CREATE TABLE 'config' ( 'name' VARCHAR ( 60 ) , 'p2p_interval' INT , 's2p_interval' INT ) ; ");
    if($res === false)return false;
    
    //insert some data alreay?
    
    
    return true;
}
?>
