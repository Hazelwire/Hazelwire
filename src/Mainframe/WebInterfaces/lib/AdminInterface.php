<?php

/**
 * Description of AdminInterface
 *
 * @author Daniel
 */
class AdminInterface extends WebInterface {

    private $state;
    private $created_db = false;

    public function show() {
        $smarty = &$this->getSmarty();

        /*
         * Error handling
         */
        $smarty->assign("num_errors", count($this->errors));
        if ($this->fatal_error) {
            $smarty->assignByRef("errors", $this->errors);
            $smarty->display("fatal_error.tpl");
            return;
        } else if (count($this->errors) > 0) {
            $errors_by_name = array();
            for ($i = 0; $i < count($this->errors); $i++) {
                /* @var $error Error */
                $error = $this->errors[$i];
                $smarty->assign($error->getType(), $error->getMessage());
                $errors_by_name[$error->getType()] = $error;
            }
            $smarty->assign("errors", $this->errors);
        }

        /*
         * Actual showing the pages
         */
        if (($this->state = $this->getCurrentState()) == PRECONFIG) {
            $smarty->assign("db_created", ($this->created_db ? 1 : 0));
            if ($this->created_db) {
                $smarty->assign("db_file_name", $this->config['database_file_name']);
                $smarty->assign("site_path", $this->config['site_folder']);
            }
            return $smarty->fetch("config.tpl");
            
        } elseif (($this->state = $this->getCurrentState()) == POSTCONFIG) {
            
            $smarty->assign("contestants", $this->contestant_list);
            return $smarty->fetch("add_contestants.tpl");
        } elseif ($this->getCurrentState() == PREVPN) {
            $smarty->assign("contestants", $this->contestant_list);
            return $smarty->fetch("vpn_start.tpl");
        } elseif ($this->getCurrentState() == PREGAMESTART) {
            return $smarty->fetch("game_start.tpl");
        } elseif ($this->getCurrentState() == GAMEINPROGRESS) {
            $db = &$this->database;
            $q = $db->query("SELECT value FROM config WHERE config_name = 'gamename'");
            $res = $q->fetch();
            $smarty->assign("title",$res[0]);
            if(!isset($_GET['aaction'])){
                $q = $db->query("SELECT value FROM config WHERE config_name = 'gamename'");
                $res = $q->fetch();
                $smarty->assign("title",$res[0]);

                $q = $db->query("SELECT teams.id as team_id, ifnull(sum(scores.points),0) as points FROM " . /* @var $q PDOStatement */
                             "teams LEFT OUTER JOIN scores ON teams.id = scores.team_id ".
                             "GROUP BY teams.id ORDER BY ifnull(sum(scores.points),0) DESC;");

                $contestants = array();
                while (($res = $q->fetch()) !== false){
                    $c = Contestant::getById($res['team_id'], $db);
                    array_push($contestants, $c);
                }
                $smarty->assign("contestants",$contestants);

                $q  = $db->query("SELECT * FROM announcements");
                $announcements = array();
                foreach ($q as $announce){
                    $announcement = new stdClass();
                    $announcement->id = $announce['id'];
                    $announcement->title = $announce['title'];
                    $announcement->content = $announce['announcement'];
                    array_push($announcements, $announcement);
                }
                $smarty->assign("announcements",$announcements);

            return $smarty->fetch("admin.tpl");
            }else if(startsWith ($_GET['aaction'],"cadd")){
                if (isset($_POST['cadd']) && strtolower($_POST['cadd']) == 'add'){
                    if(count($this->errors) == 0){
                        $smarty->assign("caddsuccess","1");
                    }
                    
                }else{
                    
                }
                return $smarty->fetch("admincadd.tpl");
            }else if(startsWith ($_GET['aaction'],"cedit")){
                if (isset($_POST['cedit']) && strtolower($_POST['cedit']) == 'save'){
                    if(count($this->errors) == 0){
                        $smarty->assign("ceditsuccess","1");
                    }
                }else if(isset($_POST['contestant'])){
                    $id = intval($_POST['contestant']);
                    $contestant = Contestant::getById($id, $db);
                    $boom = explode(".", $contestant->getSubnet());
                    $subnet = $boom[1];
                    $boom = explode(".", $contestant->getVm_ip());
                    $vmip = $boom[2];
                    $smarty->assign("contestant",$contestant);
                    $smarty->assign("vmip",$vmip);
                    $smarty->assign("subnet",$subnet);
                }
                return $smarty->fetch("admincedit.tpl");
            }

        } elseif ($this->getCurrentState() == POSTGAME) {
            return $smarty->fetch("game_end.tpl");
        }
    }

    /**
     * This sets the Mainserver's state to the given state. It is used to differentiate between
     * different states in the setup stage and the game stage.<br> The main reason for this is making
     * it possible to ask certain info from the Admin at the correct moments and preventing things
     * from happening in the wrong momens.
     * 
     * @param integer $state The state the system is in.
     */
    private function setState($state) {
        $db = & $this->database;
        /* @var $db PDO */
        if ($this->getCurrentState() == -1) {
            $q = $db->prepare("INSERT INTO 'state' VALUES (?);");
            $q->execute(array($state));
        } else {
            $q = $db->prepare("UPDATE state SET state = ?;");
            $q->execute(array($state));
        }
        $this->state = $state;
    }

    /**
     * Does all the work. It interprets form data, etc.
     */
    public function doWork() {
        if (!$this->db_ready) {
            include_once 'admin/Admin.CreateDB.action.php';
            $db_create_result = create_database();
            $this->created_db = true;
            if ($db_create_result === false || !$this->connectDB()) {
                $this->handleError(new Error("db_error", "An error occured while trying to create and connect to the database!", true));
                return;
            }
            $this->setState(PRECONFIG);
        }
        if ($this->fatal_error)
            return;

        //Assuming everything went OK, we are now in the PRECONFIG state
        //Going to do the actual work

        switch ($this->getCurrentState()) {
            case PRECONFIG:
                //if something was submitted we need to handle it
                if ($_SERVER['REQUEST_METHOD'] == "POST") {
                    /* @var $db PDO */
                    $db = &$this->database;

                    $gamename = $_POST['name'];
                    $auto_p2p_interval = intval($_POST['p2p_interval']);
                    $auto_s2p_interval = intval($_POST['s2p_interval']);
                    $server_ip = $_POST['server_ip'];
                    
                    $decay_mod = $_POST['points_decay_mod'];
                    $points_min = $_POST['points_min'];
                    $point_penalty_mod = $_POST['point_penalty_mod'];
                    $penalty_offline = $_POST['penalty_offline'];
                    
                    $manifest = $_FILES['manifest'];
                    $temp = explode(".", $manifest['name']);
                    $ext = $temp[count($temp) - 1];

                    if (strcmp($ext, "xml") != 0) {
                        $this->handleError(new Error("config_input_error", "You can only upload XML files!", false));
                        return;
                    } elseif ($auto_p2p_interval > 0 && $auto_s2p_interval > 0 && $manifest['error'] === 0 && checkValidIp($server_ip)) {

                        
                        move_uploaded_file($manifest['tmp_name'], "manifest.xml");
                        
                        $res = exec("python ". $this->config['ch_location'] . "ManifestParser.py " . $this->config['site_folder']."manifest.xml " . $this->config['site_folder'].$this->config['database_file_name'], $cmd_result);
                        if(strrpos($res,"False") !== false){
                            $this->handleError(new Error("config_input_error", "Invalid manifest XML!", true));
                            return;
                        }

                        $update = $db->prepare("INSERT INTO config (value,config_name) VALUES (?,?);");
                        /* @var $update PDOStatement */
                        $result = $update->execute(array($gamename, 'gamename'));
                        $result = $result && $update->execute(array($server_ip, 'server_ip'));


                        $result = $result && $update->execute(array($auto_p2p_interval, 'p2p_interval'));
                        $result = $result && $update->execute(array($auto_s2p_interval, 'normal_interval'));
                        
                        $result = $result && $update->execute(array($decay_mod , 'points_decay_mod'));
                        $result = $result && $update->execute(array($points_min , 'points_min'));
                        $result = $result && $update->execute(array($point_penalty_mod , 'point_penalty_mod'));
                        $result = $result && $update->execute(array($penalty_offline , 'penalty_offline'));
                        

                        if ($result !== false) {
                            $this->setState(POSTCONFIG);
                        } else {
                            $this->handleError(new Error("config_input_error", "An error occured while trying to insert something into the database", true));
                            return;
                        }
                    } elseif ($manifest['error'] === 0) {
                        $this->handleError(new Error("config_input_error", "Please supply valid configuration inputs!", false));
                    } else {
                        $this->handleError(new Error("config_input_error", "Something went wrong with uploading the manifest file. Please try again.", false));
                    }
                }

                break;
            case POSTCONFIG:
                if (strtolower($_SERVER['REQUEST_METHOD']) == "post") {
                    $db = &$this->database;
                    
                    /* @var $result PDOStatement */
                    $result = $db->query("SELECT COUNT(*) FROM teams");
                    $num_teams = $result->fetchColumn();
                    if (isset($_POST['button']) && strtolower($_POST['button']) == 'next') {
                        if ($num_teams > 0) {
                            $this->setState(PREVPN);
                        } else {
                            $this->handleError(new Error("team_input_error", "You must atleast have 1 contestant to start a wargame. Sorry! xD", false));
                        }
                    } elseif (isset($_POST['button']) && strtolower($_POST['button']) == 'add') {
                        /**
                         * Check for correct input
                         * Check for overlapping IPs | @TODO Make it possible for Admin to have more control over subnets
                         * Create a VPN config file and a Client Config Dir.
                         * Create keys and a CA / DH if necessary (i.e. if this is the first)
                         */
                        
                        if(!ctype_alnum($_POST['name']))
                            $this->handleError(new Error("team_input_error", "Illegal name. Only alphanumeric allowed!", false));
                        elseif((!(intval($_POST['server_ip']) > 0 && intval($_POST['server_ip']) < 255)) || !((intval($_POST['ip_range']) > 0 && intval($_POST['ip_range']) < 255)))
                            $this->handleError(new Error("team_input_error", "IP input has to be between 0 and 255, excluding.", false));
                        else {
                            $subnet = "10." . $_POST['ip_range'] . ".0.0/24";
                            $vmip =   "10." . $_POST['ip_range'] . "." . $_POST['server_ip'] . ".1";
                            $vmip_endpoint =   "10." . $_POST['ip_range'] . "." . $_POST['server_ip'] . ".2";
                            
                            if(!checkValidIp($vmip)){
                                $this->handleError(new Error("team_input_error", "Illegal IP address input", false));
                                return;
                            }
                            
                            
                            $result = $db->query("SELECT * FROM teams");
                            
                            foreach($result as $res){
                                if($_POST['name'] == $res['name'])
                                    $this->handleError(new Error("team_input_error", "Duplicate name much!", false));
                                elseif($subnet == $res['subnet'])
                                    $this->handleError(new Error("team_input_error", "Duplicate subnet much!", false));
                                elseif($vmip == $res['VMip'])
                                    $this->handleError(new Error("team_input_error", "Duplicate server ip much!", false));
                            }
                            
                            //check for errors and return
                            if(count($this->errors) > 0){
                                return;
                            }else{
                                //assume all is right, insert into database
                                $c = new Contestant($_POST['name'], $subnet, $vmip);
                                
                                array_push($this->contestant_list,$c);
                                
                                $c->save($db);
                                
                                if($num_teams == 0){

                                    OpenVPNManager::buildInitKeys();

                                    OpenVPNManager::createBaseVPNServer();
                                    
                                    $smarty = &$this->getSmarty();
                                    $tpl = $smarty->createTemplate("server.conf"); /* @var $tpl Smarty_Internal_Template */
                                    $tpl->assign("filename", "basevpn");
                                    $tpl->assign("path_to_rsa", $this->config['site_folder'] . $this->config['RSA_location']);
                                    $tpl->assign("path_to_openvpn", $this->config['site_folder'] . $this->config['openvpn_location']);
                                    $tpl->assign("server_ip_range",  "10.0.0.0");
                                    $tpl->assign("man_port",$this->config['management_port_base']);
                                    $tpl->assign("port",$this->config['base_port']);
                                    $config_file_data = $tpl->fetch();

                                    $config_file_loc = $this->config['openvpn_location'] . "basevpn.conf";
                                    $handle = @fopen($config_file_loc, 'w');
                                    if($handle === false){
                                        $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                                        return;
                                    }
                                    fwrite($handle, $config_file_data);
                                    fclose($handle);
                                }
                                
                                OpenVPNManager::buildServerKeys($_POST['name']);
                                OpenVPNManager::buildClientKeys($_POST['name']);
                                
                                // @TODO We moeten ook Apache configs aanpassen enzo om Limit te allowen voor .htaccess
                                // create the CCD file
                                OpenVPNManager::createClientConfigFile($_POST['name'], $vmip, $vmip_endpoint);
                                
                                $smarty = &$this->getSmarty();
                                $tpl = $smarty->createTemplate("server.conf"); /* @var $tpl Smarty_Internal_Template */
                                $tpl->assign("filename", "server_".$_POST['name']);
                                $tpl->assign("path_to_rsa", $this->config['site_folder'] . $this->config['RSA_location']);
                                $tpl->assign("path_to_openvpn", $this->config['site_folder'] . $this->config['openvpn_location']);
                                $tpl->assign("server_ip_range",  substr($subnet, 0, -3));
                                $tpl->assign("man_port",$this->config['management_port_base'] + $c->getId());
                                $tpl->assign("port",$this->config['base_port'] + $c->getId());
                                $config_file_data = $tpl->fetch();
                                
                                $config_file_loc = $this->config['openvpn_location'] . $_POST['name'] . ".conf";
                                $handle = @fopen($config_file_loc, 'w');
                                if($handle === false){
                                    $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                                    return;
                                }
                                fwrite($handle, $config_file_data);
                                fclose($handle);



                                $gc = &$this->gameConfig; /* @var $gc GameConfig */
                                
                                $tpl = $smarty->createTemplate("client.conf"); /* @var $tpl Smarty_Internal_Template */
                                $tpl->assign("teamname", $_POST['name']);
                                $tpl->assign("port",$this->config['base_port'] + $c->getId());
                                $tpl->assign("server_ip", $gc->server_ip);
                                $config_file_data = $tpl->fetch();

                                $config_file_loc = $this->config['openvpn_location'] . $_POST['name'] . "_client.conf";
                                $handle = @fopen($config_file_loc, 'w');
                                if($handle === false){
                                    $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                                    return;
                                }
                                fwrite($handle, $config_file_data);
                                fclose($handle);
                                    
                            }
                        }
                        
                    }
                }
                break;
            case PREVPN:
                $db = &$this->database;
                if (strtolower($_SERVER['REQUEST_METHOD']) == "post") {
                    
                    if (isset($_POST['start'])) {
                        $id = intval(key($_POST['start']));
                        if(($c = Contestant::getById($id, $db)) !== false){
                            OpenVPNManager::startVPN($c);
                        }else
                            $this->handleError(new Error("vpn_error", "Such a contestant does not exist!", false));
                    }elseif (isset($_POST['stop'])) {
                        $id = intval(key($_POST['stop']));
                        if(($c = Contestant::getById($id, $db)) !== false){
                            OpenVPNManager::stopVPN($c);
                        }else
                            $this->handleError(new Error("vpn_error", "Such a contestant does not exist!", false));
                    } elseif (isset($_POST['startall'])) {
                        foreach ($this->contestant_list as $c){
                            if(!OpenVPNManager::getVPNStatus($c))
                                OpenVPNManager::startVPN($c);
                        }
                    }elseif (isset($_POST['stopall'])) {
                        foreach ($this->contestant_list as $c){
                            if(OpenVPNManager::getVPNStatus($c))
                                OpenVPNManager::stopVPN($c);
                        }
                    }elseif (isset($_POST['next'])) {
                        foreach ($this->contestant_list as $c){
                            if(!OpenVPNManager::getVPNStatus($c))
                                OpenVPNManager::startVPN($c);
                        }
                        OpenVPNManager::startBaseVPN();
                        $this->setState(PREGAMESTART);
                    }
                }

                break;
            case PREGAMESTART:
                if (strtolower($_SERVER['REQUEST_METHOD']) == "post") {
                    if (isset($_POST['next'])) {
                        
                        OpenVPNManager::setKernelRouting(true);
                        exec("python ". $this->config['ch_location'] . "FlagAdministration.py " . $this->config['site_folder'].$this->config['database_file_name'] . " > /dev/null 2>/dev/null &");
                        $db =&$this->database; /* @var $db PDO */
                        $q = $db->prepare("INSERT INTO config VALUES (?,?)");
                        $q->execute(array("start_time",time()));
                        
                        $this->setState(GAMEINPROGRESS);
                    }
                }

                break;
            case GAMEINPROGRESS:
                if (strtolower($_SERVER['REQUEST_METHOD']) == "post") {
                    if (isset($_POST['next'])) {
                        
                        OpenVPNManager::setKernelRouting(false);
                        
                        $fp = @fsockopen("127.0.0.1", 9999, $errno, $errstr, 5);
                        if(!$fp){
                            $this->handleError(new Error("game_administration_error", "Cannot stop Flag Administration! (".$errno.")", false));
                        }else{
                            fwrite($fp, "REQSHUTDOWN");
                            fclose($fp);
                        }
                        
                        foreach ($this->contestant_list as $c){
                            if(OpenVPNManager::getVPNStatus($c))
                                OpenVPNManager::stopVPN($c);
                        }
                        OpenVPNManager::stopBaseVPN();
                        
                        $fp = @fsockopen("127.0.0.1", 10000, $errno, $errstr, 5);
                        if(!$fp){
                            $this->handleError(new Error("game_administration_error", "Cannot stop OpenVPNService.py! (".$errno.")", false));
                        }else{
                            fwrite($fp, "STOPSERVICE");
                            fclose($fp);
                        }
                        
                        $this->setState(POSTGAME);
                    } else if(isset($_POST['cadd']) && strtolower($_POST['cadd']) == 'add'){
                        $db = &$this->database;

                        /* @var $result PDOStatement */
                        $result = $db->query("SELECT COUNT(*) FROM teams");
                        $num_teams = $result->fetchColumn();

                        
                        // @FIXME doe dubbele code weghalen?
                        if(!ctype_alnum($_POST['cname']))
                            $this->handleError(new Error("team_input_error", "Illegal name. Only alphanumeric allowed!", false));
                        elseif((!(intval($_POST['cvmip']) > 0 && intval($_POST['cvmip']) < 255)) || !((intval($_POST['csubnet']) > 0 && intval($_POST['csubnet']) < 255)))
                            $this->handleError(new Error("team_input_error", "IP input has to be between 0 and 255, excluding.", false));
                        else {
                            $subnet = "10." . $_POST['csubnet'] . ".0.0/24";
                            $vmip =   "10." . $_POST['csubnet'] . "." . $_POST['cvmip'] . ".1";
                            $vmip_endpoint =   "10." . $_POST['csubnet'] . "." . $_POST['cvmip'] . ".2";

                            if(!checkValidIp($vmip)){
                                $this->handleError(new Error("team_input_error", "Illegal IP address input", false));
                                return;
                            }


                            $result = $db->query("SELECT * FROM teams");

                            foreach($result as $res){
                                if($_POST['cname'] == $res['name'])
                                    $this->handleError(new Error("team_input_error", "Duplicate name much!", false));
                                elseif($subnet == $res['subnet'])
                                    $this->handleError(new Error("team_input_error", "Duplicate subnet much!", false));
                                elseif($vmip == $res['VMip'])
                                    $this->handleError(new Error("team_input_error", "Duplicate server ip much!", false));
                            }

                            //check for errors and return
                            if(count($this->errors) > 0){
                                return;
                            }else{
                                //assume all is right, insert into database
                                $c = new Contestant($_POST['cname'], $subnet, $vmip);

                                array_push($this->contestant_list,$c);

                                $c->save($db);

                                if($num_teams == 0){

                                    OpenVPNManager::buildInitKeys();

                                    OpenVPNManager::createBaseVPNServer();

                                    $smarty = &$this->getSmarty();
                                    $tpl = $smarty->createTemplate("server.conf"); /* @var $tpl Smarty_Internal_Template */
                                    $tpl->assign("filename", "basevpn");
                                    $tpl->assign("path_to_rsa", $this->config['site_folder'] . $this->config['RSA_location']);
                                    $tpl->assign("path_to_openvpn", $this->config['site_folder'] . $this->config['openvpn_location']);
                                    $tpl->assign("server_ip_range",  "10.0.0.0");
                                    $tpl->assign("man_port",$this->config['management_port_base']);
                                    $tpl->assign("port",$this->config['base_port']);
                                    $config_file_data = $tpl->fetch();

                                    $config_file_loc = $this->config['openvpn_location'] . "basevpn.conf";
                                    $handle = @fopen($config_file_loc, 'w');
                                    if($handle === false){
                                        $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                                        return;
                                    }
                                    fwrite($handle, $config_file_data);
                                    fclose($handle);
                                }

                                OpenVPNManager::buildServerKeys($_POST['cname']);
                                OpenVPNManager::buildClientKeys($_POST['cname']);

                                // @TODO We moeten ook Apache configs aanpassen enzo om Limit te allowen voor .htaccess
                                // create the CCD file
                                OpenVPNManager::createClientConfigFile($_POST['cname'], $vmip, $vmip_endpoint);

                                $smarty = &$this->getSmarty();
                                $tpl = $smarty->createTemplate("server.conf"); /* @var $tpl Smarty_Internal_Template */
                                $tpl->assign("filename", "server_".$_POST['cname']);
                                $tpl->assign("path_to_rsa", $this->config['site_folder'] . $this->config['RSA_location']);
                                $tpl->assign("path_to_openvpn", $this->config['site_folder'] . $this->config['openvpn_location']);
                                $tpl->assign("server_ip_range",  substr($subnet, 0, -3));
                                $tpl->assign("man_port",$this->config['management_port_base'] + $c->getId());
                                $tpl->assign("port",$this->config['base_port'] + $c->getId());
                                $config_file_data = $tpl->fetch();

                                $config_file_loc = $this->config['openvpn_location'] . $_POST['cname'] . ".conf";
                                $handle = @fopen($config_file_loc, 'w');
                                if($handle === false){
                                    $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                                    return;
                                }
                                fwrite($handle, $config_file_data);
                                fclose($handle);



                                $gc = &$this->gameConfig; /* @var $gc GameConfig */

                                $tpl = $smarty->createTemplate("client.conf"); /* @var $tpl Smarty_Internal_Template */
                                $tpl->assign("teamname", $_POST['cname']);
                                $tpl->assign("port",$this->config['base_port'] + $c->getId());
                                $tpl->assign("server_ip", $gc->server_ip);
                                $config_file_data = $tpl->fetch();

                                $config_file_loc = $this->config['openvpn_location'] . $_POST['cname'] . "_client.conf";
                                $handle = @fopen($config_file_loc, 'w');
                                if($handle === false){
                                    $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                                    return;
                                }
                                fwrite($handle, $config_file_data);
                                fclose($handle);

                            }
                        }

                    } else if (isset($_POST['cedit']) && strtolower($_POST['cedit']) == 'save') {
                        // @TODO make name usage into ID usage to prevent problems with renaming
                        $db = &$this->database;

                        /* @var $result PDOStatement */
                        $result = $db->query("SELECT COUNT(*) FROM teams");
                        $num_teams = $result->fetchColumn();


                        // @FIXME doe dubbele code weghalen?
                        if(!ctype_alnum($_POST['cname']))
                            $this->handleError(new Error("team_input_error", "Illegal name. Only alphanumeric allowed!", false));
                        elseif((!(intval($_POST['cvmip']) > 0 && intval($_POST['cvmip']) < 255)) || !((intval($_POST['csubnet']) > 0 && intval($_POST['csubnet']) < 255)))
                            $this->handleError(new Error("team_input_error", "IP input has to be between 0 and 255, excluding.", false));
                        else {
                            $subnet = "10." . $_POST['csubnet'] . ".0.0/24";
                            $vmip =   "10." . $_POST['csubnet'] . "." . $_POST['cvmip'] . ".1";
                            $vmip_endpoint =   "10." . $_POST['csubnet'] . "." . $_POST['cvmip'] . ".2";

                            if(!checkValidIp($vmip)){
                                $this->handleError(new Error("team_input_error", "Illegal IP address input", false));
                                return;
                            }


                            $result = $db->query("SELECT * FROM teams");

                            foreach($result as $res){
                                if($_POST['cname'] == $res['name'])
                                    $this->handleError(new Error("team_input_error", "Duplicate name much!", false));
                                elseif($subnet == $res['subnet'])
                                    $this->handleError(new Error("team_input_error", "Duplicate subnet much!", false));
                                elseif($vmip == $res['VMip'])
                                    $this->handleError(new Error("team_input_error", "Duplicate server ip much!", false));
                            }

                            //check for errors and return
                            if(count($this->errors) > 0){
                                return;
                            }else{
                                //assume all is right, insert into database
                                $c = new Contestant($_POST['cname'], $subnet, $vmip);



                                $c->save($db);


                                OpenVPNManager::createClientConfigFile($_POST['cname'], $vmip, $vmip_endpoint);

                                $smarty = &$this->getSmarty();
                                $tpl = $smarty->createTemplate("server.conf"); /* @var $tpl Smarty_Internal_Template */
                                $tpl->assign("filename", "server_".$_POST['cname']);
                                $tpl->assign("path_to_rsa", $this->config['site_folder'] . $this->config['RSA_location']);
                                $tpl->assign("path_to_openvpn", $this->config['site_folder'] . $this->config['openvpn_location']);
                                $tpl->assign("server_ip_range",  substr($subnet, 0, -3));
                                $tpl->assign("man_port",$this->config['management_port_base'] + $c->getId());
                                $tpl->assign("port",$this->config['base_port'] + $c->getId());
                                $config_file_data = $tpl->fetch();

                                $config_file_loc = $this->config['openvpn_location'] . $_POST['cname'] . ".conf";
                                $handle = @fopen($config_file_loc, 'w');
                                if($handle === false){
                                    $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                                    return;
                                }
                                fwrite($handle, $config_file_data);
                                fclose($handle);



                                $gc = &$this->gameConfig; /* @var $gc GameConfig */

                                $tpl = $smarty->createTemplate("client.conf"); /* @var $tpl Smarty_Internal_Template */
                                $tpl->assign("teamname", $_POST['cname']);
                                $tpl->assign("port",$this->config['base_port'] + $c->getId());
                                $tpl->assign("server_ip", $gc->server_ip);
                                $config_file_data = $tpl->fetch();

                                $config_file_loc = $this->config['openvpn_location'] . $_POST['cname'] . "_client.conf";
                                $handle = @fopen($config_file_loc, 'w');
                                if($handle === false){
                                    $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                                    return;
                                }
                                fwrite($handle, $config_file_data);
                                fclose($handle);

                            }
                        }
                    }
                }

                break;
            case POSTGAME:


                break;
            default:
                break;
        }
    }

}

?>
