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
    //@TODO remove this BS when done
    $res = $database->exec("BEGIN TRANSACTION;
                CREATE TABLE flagpoints (flag_id INTEGER, mod_id INTEGER, points INTEGER);
                INSERT INTO flagpoints VALUES(1,1,15);
                INSERT INTO flagpoints VALUES(2,1,23);
                INSERT INTO flagpoints VALUES(3,1,41);
                INSERT INTO flagpoints VALUES(1,2,20);
                INSERT INTO flagpoints VALUES(2,2,15);
                INSERT INTO flagpoints VALUES(3,2,10);
                INSERT INTO flagpoints VALUES(4,2,50);
                INSERT INTO flagpoints VALUES(5,2,25);
                INSERT INTO flagpoints VALUES(6,2,31);
                INSERT INTO flagpoints VALUES(1,3,10);
                INSERT INTO flagpoints VALUES(2,3,25);
                CREATE TABLE flags (flag_id INTEGER, mod_id INTEGER, team_id INTEGER, flag VARCHAR ( 64 ));
                INSERT INTO flags VALUES(1,2,1,'FLGZ0sRrUGvTLLaHqv8dUwy3HPgDo8y5ZCweGvCt9VyuXCus4UmUhzqeB9FFr6c7');
                INSERT INTO flags VALUES(1,1,3,'FLGGMenl1YVh67ciCW1tUPnvB9D5aolTraVXsQ772oMf0pWKkmLOibeTEkEeGjFN');
                INSERT INTO flags VALUES(2,1,3,'FLG8oC9iJ0ltt758w8DzmZkBDeoHDmXYk0uKwwz1RLnE9VbEkzSLBtMSIv43o4zY');
                INSERT INTO flags VALUES(3,1,3,'FLGlwQQi972y6RlFVeaANZL2ehaIPaUV4rR6jOS3jPqz3pRcFL5WE8wUSCg5ljpC');
                INSERT INTO flags VALUES(1,2,3,'FLGHJNoY0sZuzNsN7TGTUSH3wnvFBfHWHm313Y2cFFl5sVUohqyrcruVegOADgFN');
                INSERT INTO flags VALUES(2,2,3,'FLGd2LPMs95ptUDuLcaMogdToVqmgiwHC9eRtQrkqRLQ7ogMLhYee9DuiERDlSHz');
                INSERT INTO flags VALUES(3,2,3,'FLG7mWl5bn2doY2ZnQnTcABC61kwxREb10LYqQEi6NW6TPzjU7Onas4uXoI85ACi');
                INSERT INTO flags VALUES(4,2,3,'FLGDuIey7AV4xFRx6VPBPXRVrFrrFED7wvPuivgmEYJiq9Oy7M1bxsZgRLXJsApH');
                INSERT INTO flags VALUES(5,2,3,'FLGe4JuSqc4Q2h1nok0ZXYt50j1lWLbNJ3ni0512AK75qtauoUXlYvy5hm2Xa9Eb');
                INSERT INTO flags VALUES(6,2,3,'FLGfd2jCzV9itkoerUfb5nvn58oQV6vdrzBEQ3RnNB9hISwSIC4kvEg0huCCYym3');
                INSERT INTO flags VALUES(1,3,3,'FLGbfWQlGZs1kgz5ynaINfHBzSLW24xD8yaUZidZsbUwErEjtwCxeYTPVaGWqgq7');
                INSERT INTO flags VALUES(2,3,3,'FLGyslt7FAVJQlukFc0jkz3eBIj0M1FeG9Y3qnzWuIwY2Iw9uXxkmPmVKgmmLZTE');
                CREATE TABLE modules (id INTEGER PRIMARY KEY, name VARCHAR ( 50 ), numFlags INTEGER, basepath VARCHAR ( 255 ), deployscript VARCHAR ( 255 ));
                INSERT INTO modules VALUES(1,'pwnjebox',3,'/exploit1/','deploy/install.py');
                INSERT INTO modules VALUES(2,'g0tr00t?',6,'/exploit2/','deploy/derp.py');
                INSERT INTO modules VALUES(3,'Revenge of ZeroCool',2,'/exploit3/','deploy/foobaar.py');
                CREATE TABLE teams (id INTEGER PRIMARY KEY, name VARCHAR ( 50 ), VMip VARCHAR (15), subnet VARCHAR ( 15 ));
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