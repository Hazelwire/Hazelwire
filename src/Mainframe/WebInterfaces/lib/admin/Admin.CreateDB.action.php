<?php
function create_database(){
    global $interface;
    $config = $interface->getConfig();
    //open_db
    $database = new PDO ("sqlite:" . $config['database_file_name']);
    if($database === false)
        return false;
    //create tables
    $res = $database->exec("CREATE TABLE 'state' ( 'state' INTEGER );");
    if($res === false)return false;
    $res = $database->exec("CREATE TABLE 'config' ( 'config_name' TEXT , 'value' TEXT) ; ");
    if($res === false)return false;
    //@TODO remove this BS when done
    $res = $database->exec("BEGIN TRANSACTION;
                CREATE TABLE team_id (id INTEGER);
                INSERT INTO team_id VALUES(1);
                CREATE TABLE flagpoints (flag_id INTEGER, mod_id INTEGER, points INTEGER);
                CREATE TABLE flags (flag_id INTEGER, mod_id INTEGER, team_id INTEGER, flag TEXT);
                CREATE TABLE modules (id INTEGER PRIMARY KEY, name TEXT, numFlags INTEGER, deployscript TEXT, serviceport INTEGER);
                CREATE TABLE teams (id INTEGER PRIMARY KEY, name TEXT, VMip TEXT, subnet TEXT);
                CREATE TABLE scores (team_id INTEGER, flag TEXT, timestamp INTEGER, points INTEGER);
                CREATE TABLE submission_block (team_id INTEGER, try_timestamp INTEGER, block_timestamp INTEGER);
                CREATE TABLE evil_teams ( ip TEXT, port INTEGER, timestamp INTEGER, reporter TEXT, seen INTEGER, modulename TEXT);
                CREATE TABLE announcements ( id INTEGER PRIMARY KEY, title TEXT, announcement TEXT, timestamp INTEGER);
                CREATE TABLE bans ( team_id INTEGER, end_timestamp INTEGER, reason TEXT, jobid INTEGER );
                COMMIT;
    ");
    if($res === false)return false;
    //insert some data alreay?
    
    /*
     * INSERT INTO teams VALUES(1,'Henkies','10.8.1.1','10.8.0.0/24');
                INSERT INTO teams VALUES(2,'Sjakies','10.10.10.1', '10.10.9.0/24');
                INSERT INTO teams VALUES(3,'lokale lutsers','127.0.0.1', '127.0.0.0/24');
     */
    return true;
}
?>
