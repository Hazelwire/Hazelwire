<?php

/**
 * Description of AdminInterface
 *
 * @author Daniel
 */
class AdminInterface extends WebInterface {

    private $state;
    private $created_db = false;
    private $cadd_success = false;
    private $cedit_success = false;
    private $sancheckSuccess = false;
    private $cban_success = false;
    private $cdel_success = false;

    private $startvpn_success = false;
    private $endgame_success = false;

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
            $smarty->assignByRef("errors", $this->errors);
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

        } elseif ($this->getCurrentState() == POSTGAME && !$this->endgame_success) {
            return $smarty->fetch("game_end.tpl");
        } else {
            $db = &$this->database;
            $q = $db->query("SELECT value FROM config WHERE config_name = 'gamename'");
            $res = $q->fetch();
            $smarty->assign("title",$res[0]);
            if(!isset($_GET['aaction'])){

                $allow_startvpn = OpenVPNManager::getBaseVPNStatus();
                $allow_stopvpn = OpenVPNManager::getBaseVPNStatus();

                $q = $db->query("SELECT value FROM config WHERE config_name = 'gamename'");
                $res = $q->fetch();
                $smarty->assign("title",$res[0]);

                $q = $db->query("SELECT teams.id as team_id, ifnull(sum(scores.points),0) as points FROM " . /* @var $q PDOStatement */
                             "teams LEFT OUTER JOIN scores ON teams.id = scores.team_id ".
                             "GROUP BY teams.id ORDER BY ifnull(sum(scores.points),0) DESC;");

                $contestants = array();
                while (($res = $q->fetch()) !== false){
                    $c = Contestant::getById($res['team_id'], $db);
                    $allow_startvpn = $allow_startvpn && OpenVPNManager::getVPNStatus($c);
                    $allow_stopvpn = $allow_stopvpn || OpenVPNManager::getVPNStatus($c);
                    array_push($contestants, $c);
                }
                $smarty->assign("contestants",$contestants);
                $smarty->assign("allow_startvpn",!$allow_startvpn);
                $smarty->assign("allow_stopvpn",$allow_startvpn);
                $smarty->assign("allow_endgame",$this->getCurrentState() == GAMEINPROGRESS);
                $smarty->assign("allow_startgame",$this->getCurrentState() == PREGAMESTART);

                $q  = $db->query("SELECT * FROM announcements ORDER BY timestamp");
                $announcements = array();
                foreach ($q as $announce){
                    $announcement = new stdClass();
                    $announcement->id = $announce['id'];
                    $announcement->title = htmlspecialchars($announce['title']);
                    $announcement->content = $this->parseBB($announce['announcement']);
                    array_push($announcements, $announcement);
                }
                $smarty->assign("announcements",$announcements);

                return $smarty->fetch("admin.tpl");
            } else if(startsWith ($_GET['aaction'],"cadd")){
                if (isset($_POST['cadd']) && strtolower($_POST['cadd']) == 'add'){
                    if($this->cadd_success){
                        $smarty->assign("caddsuccess","1");
                    }

                }else{

                }
                return $smarty->fetch("admincadd.tpl");
            } else if(startsWith ($_GET['aaction'],"cedit")){
                if (isset($_POST['cedit']) && (strtolower($_POST['cedit']) == 'save' || strtolower($_POST['cedit']) == 'forcesancheck')){
                    if(isset($_POST['contestant']))
                        $id = Contestant::getById($_POST['contestant'], $db);
                    else if(isset($_POST['cid']))
                        $c = Contestant::getById($_POST['cid'], $db);
                    if($c != false){
                        $smarty->assign("contestant",$c);
                        $boom = explode(".", $c->getSubnet());
                        $subnet = $boom[1];
                        $boom = explode(".", $c->getVm_ip());
                        $vmip = $boom[2];
                        $smarty->assign("vmip",$vmip);
                        $smarty->assign("subnet",$subnet);
                    }
                    if($this->cedit_success){
                        $smarty->assign("ceditsuccess","1");
                    }else if($this->sancheckSuccess){
                        $smarty->assign("sanchecksuccess","1");
                    }
                } else if(isset($_POST['contestant'])){
                    if(isset($_POST['contestant']))
                        $id = intval($_POST['contestant']);
                    else if(isset($_POST['cid']))
                        $id = intval($_POST['cid']);
                    else
                        $id = -1;
                    if($id > 0){
                        $contestant = Contestant::getById($id, $db);
                        $boom = explode(".", $contestant->getSubnet());
                        $subnet = $boom[1];
                        $boom = explode(".", $contestant->getVm_ip());
                        $vmip = $boom[2];
                        $smarty->assign("contestant",$contestant);
                        $smarty->assign("vmip",$vmip);
                        $smarty->assign("subnet",$subnet);
                    }
                }
                return $smarty->fetch("admincedit.tpl");
            }else if(startsWith ($_GET['aaction'],"cban")){
                $c = Contestant::getById($_POST['contestant'], $this->database);
                if($c == false)
                	$c = Contestant::getById($_POST['cid'], $this->database);
                $smarty->assign("contestant",$c);
                if (isset($_POST['cid'])){
                    if($this->cban_success){
                        $smarty->assign("cbansuccess","1");
                    }
                }
                return $smarty->fetch("admincban.tpl");
            }else if(startsWith ($_GET['aaction'],"cdel")){
                $c = Contestant::getById($_POST['contestant'], $this->database);
                if($c == false)
                	$c = Contestant::getById(intval($_POST['cid']), $this->database);
                $smarty->assign("contestant",$c);
                if (isset($_POST['cid'])){
                    if($this->cdel_success){
                        $smarty->assign("cdelsuccess","1");
                    }
                }
                return $smarty->fetch("admincdel.tpl");
            }else if(startsWith ($_GET['aaction'],"aadd")){
                if (isset($_POST['submitted'])){
                    if(count($this->errors) == 0){
                        $smarty->assign("aaddsuccess","1");
                    }
                }
                return $smarty->fetch("adminapost.tpl");
            }else if(startsWith ($_GET['aaction'],"aedit")){
                if (isset($_POST['submitted'])){
                    if(count($this->errors) == 0){
                        $smarty->assign("aeditsuccess","1");
                    }
                }
                $q  = $db->prepare("SELECT * FROM announcements WHERE id = ?");
                $q->execute(array(isset($_POST['announcement'])?$_POST['announcement']:(isset($_POST['aid'])?$_POST['aid']:-1)));
                $announce = $q->fetch();
                if($announce == false){
                    $this->handleError (new Error ("aedit_error", "Selected no valid announcement.", false));
                    $announcement = new stdClass();
                    $announcement->id = -1;
                    $announcement->title = "null";
                    $announcement->content = "null";
                }else{
                    $announcement = new stdClass();
                    $announcement->id = $announce['id'];
                    $announcement->title = $announce['title'];
                    $announcement->content = $announce['announcement'];
                }
                $smarty->assign("announcement",$announcement);
                return $smarty->fetch("adminaedit.tpl");
            }else if(startsWith ($_GET['aaction'],"adel")){
                if (isset($_POST['submitted'])){
                    if(count($this->errors) == 0){
                        $smarty->assign("adelsuccess","1");
                    }
                }
                $q  = $db->prepare("SELECT * FROM announcements WHERE id = ?");
                $q->execute(array(isset($_POST['announcement'])?$_POST['announcement']:(isset($_POST['aid'])?$_POST['aid']:-1)));
                $announce = $q->fetch();
                if($announce == false){
                    $this->handleError (new Error ("adel_error", "(#1) Selected no valid announcement for deletion.", false));
                    $announcement = new stdClass();
                    $announcement->id = -1;
                    $announcement->title = "null";
                }else{
                    $announcement = new stdClass();
                    $announcement->id = $announce['id'];
                    $announcement->title = $announce['title'];
                }
                $smarty->assign("announcement",$announcement);
                return $smarty->fetch("adminadel.tpl");

            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"startvpn")==0){
                if($this->startvpn_success){
                    $string = "VPN servers started".count($this->errors) > 0?"(but with errrors)":"".".<br />".PHP_EOL."<pre style=\"font-size:1.1em;\">Status:".PHP_EOL;

                    foreach ($this->contestant_list as $c){
                        $string .= sprintf("%'.-30s: %s",$c->getTeamname(),OpenVPNManager::getVPNStatus($c)?"Online ":"Offline").PHP_EOL;
                    }
                    $string .= sprintf("%'.-30s: %s","Base VM",OpenVPNManager::getBaseVPNStatus()?"Online ":"Offline").PHP_EOL;

                    $string .="</pre>";

                    $smarty->assignByRef("success", $string);
                }
                $retval = new stdClass();
                $retval->action = "startvpn";
                $retval->reply = $smarty->fetch("admin_ajax_startvpn.tpl");

                return json_encode($retval);
            } else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"stopvpn")==0){
                if(count($this->errors) == 0){
                    $string = "VPN servers stopped.<br />".PHP_EOL."<pre style=\"font-size:1.1em;\">Status:".PHP_EOL;

                    foreach ($this->contestant_list as $c){
                        $string .= sprintf("%'.-30s: %s",$c->getTeamname(),OpenVPNManager::getVPNStatus($c)?"Online ":"Offline").PHP_EOL;
                    }
                    $string .= sprintf("%'.-30s: %s","Base VM",OpenVPNManager::getBaseVPNStatus()?"Online ":"Offline").PHP_EOL;

                    $string .="</pre>";

                    $smarty->assignByRef("success", $string);
                }
                $retval = new stdClass();
                $retval->action = "stopvpn";
                $retval->reply = $smarty->fetch("admin_ajax_startvpn.tpl");

                return json_encode($retval);
            } else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"startgame")==0){
                if(count($this->errors) == 0){
                    $string = "The wargame has started! To the bunkers!";

                    $smarty->assignByRef("success", $string);
                }
                $retval = new stdClass();
                $retval->action = "startgame";
                $retval->reply = $smarty->fetch("admin_ajax_startvpn.tpl");

                return json_encode($retval);
            } else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"endgame")==0){
                if(count($this->errors) == 0){
                    $string = "The wargame has ended! Hail the champions!!";

                    $smarty->assignByRef("success", $string);
                }
                $retval = new stdClass();
                $retval->action = "endgame";
                $retval->reply = $smarty->fetch("admin_ajax_startvpn.tpl");

                return json_encode($retval);
            }

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
        if($this->getCurrentState() == GAMEINPROGRESS)
                $this->unban();
        if($_SERVER['REQUEST_METHOD'] == "POST"){
            if(isset($_POST['configsubmit'])){
                 $this->configure();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"cadd")==0 && isset($_POST['cadd']) && strtolower($_POST['cadd']) == 'add'){
                $this->addContestant();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"cedit")==0 && isset($_POST['cedit']) && strtolower($_POST['cedit']) == 'save'){
                $this->editContestant();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"cedit")==0 && isset($_POST['cedit']) && strtolower($_POST['cedit']) == 'forcesancheck'){
                $this->forceSanityCheck();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"cban")==0 ){
                $this->banContestant();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"cdel")==0 ){
                $this->deleteContestant();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"aadd")==0 && isset($_POST['submitted'])){
                $this->addAnnouncement();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"aedit")==0 && isset($_POST['submitted'])){
                $this->editAnnouncement();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"adel")==0 && isset($_POST['submitted'])){
                $this->deleteAnnouncement();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"startvpn")==0){
                $this->startVPN();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"stopvpn")==0){
                $this->stopVPN();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"startgame")==0){
                $this->startGame();
            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"endgame")==0){
                $this->endGame();
            }
        }

    }

    public function configure(){
        if($this->getCurrentState() != PRECONFIG ) {
            $this->handleError(new Error("illegal_action", "You can't configure the wargame now.", false));
            return;
        }

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

    public function addContestant(){
        if($this->getCurrentState() != GAMEINPROGRESS &&
            $this->getCurrentState() != PREVPN &&
            $this->getCurrentState() != PREGAMESTART &&
            $this->getCurrentState() != POSTCONFIG      ) {
            $this->handleError(new Error("illegal_action", "You're not allowed to add contestants during this stage of the wargame.", false));
            return;
        }
       /**
        * Check for correct input
        * Check for overlapping IPs | @TODO Make it possible for Admin to have more control over subnets
        * Create a VPN config file and a Client Config Dir.
        * Create keys and a CA / DH if necessary (i.e. if this is the first)
        */
        $this->cadd_success = false;
        $db = &$this->database;
        $smarty = &$this->getSmarty();
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
                    $this->handleError(new Error("team_input_error", "Duplicate name!", false));
                elseif($subnet == $res['subnet'])
                    $this->handleError(new Error("team_input_error", "Duplicate subnet!", false));
                elseif($vmip == $res['VMip'])
                    $this->handleError(new Error("team_input_error", "Duplicate server ip!", false));
            }

            //check for errors and return
            if(count($this->errors) > 0){
                return;
            }else{
                //assume all is right, insert into database
                $c = new Contestant($_POST['cname'], $subnet, $vmip);

                array_push($this->contestant_list,$c);

                $c->save($db);
                $c = Contestant::getById($c->getId(), $db);

                if($num_teams == 0){

                    OpenVPNManager::buildInitKeys();
                    sleep(5);
                    OpenVPNManager::createBaseVPNServer();

                    $smarty = &$this->getSmarty();
                    $tpl = $smarty->createTemplate("server.conf"); /* @var $tpl Smarty_Internal_Template */
                    $tpl->assign("filename", "basevpn");
                    $tpl->assign("path_to_rsa", $this->config['site_folder'] . $this->config['RSA_location']);
                    $tpl->assign("path_to_openvpn", $this->config['site_folder'] . $this->config['openvpn_location']);
                    $tpl->assign("server_ip_range",  "10.0.0.0");
                    $tpl->assign("man_port",$this->config['management_port_base']);
                    $tpl->assign("port",$this->config['base_port']);
                    $tpl->assign("ccd",$this->config['site_folder'] . $this->config['openvpn_location'] . "/ccd" );
                    $config_file_data = $tpl->fetch();

                    $config_file_loc = $this->config['openvpn_location'] . "basevpn.conf";
                    $handle = @fopen($config_file_loc, 'w');
                    if($handle === false){
                        $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                        return;
                    }
                    fwrite($handle, $config_file_data);
                    fclose($handle);
                    if($this->getCurrentState() == POSTCONFIG)
                        $this->setState(PREVPN);
                }

                OpenVPNManager::buildServerKeys("Team".$c->getId());
                OpenVPNManager::buildClientKeys("Team".$c->getId());

                // @TODO We moeten ook Apache configs aanpassen enzo om Limit te allowen voor .htaccess
                // create the CCD file
                OpenVPNManager::createClientConfigFile("Team".$c->getId(), $vmip, $vmip_endpoint);

                $this->createVPNServerConf($c);


                $gc = &$this->gameConfig; /* @var $gc GameConfig */

                // VPN client config for the team players
                $tpl = $smarty->createTemplate("client.conf"); /* @var $tpl Smarty_Internal_Template */
                $tpl->assign("teamname", "Team".$c->getId());
                $tpl->assign("port",$this->config['base_port'] + $c->getId());
                $tpl->assign("server_ip", $gc->server_ip);
                $config_file_data = $tpl->fetch();

                $config_file_loc = $this->config['openvpn_location'] ."Team".$c->getId() . "_client.conf";
                $handle = @fopen($config_file_loc, 'w');
                if($handle === false){
                    $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                    return;
                }
                fwrite($handle, $config_file_data);
                fclose($handle);

                // VPN client config for the Virtual Machine
                $tpl = $smarty->createTemplate("client.conf"); /* @var $tpl Smarty_Internal_Template */
                $tpl->assign("teamname", "Team".$c->getId()."_vm");
                $tpl->assign("port",$this->config['base_port'] + $c->getId());
                $tpl->assign("server_ip", $gc->server_ip);
                $config_file_data = $tpl->fetch();

                $config_file_loc = $this->config['openvpn_location'] ."Team".$c->getId() . "_vm_client.conf";
                $handle = @fopen($config_file_loc, 'w');
                if($handle === false){
                    $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                    return;
                }
                fwrite($handle, $config_file_data);
                fclose($handle);

                if($this->getCurrentState() == GAMEINPROGRESS){
                    sleep(2);
                    OpenVPNManager::startVPN($c);
                }
            }
            $this->cadd_success = true;
        }
        

    }

    public function editContestant(){
        $state = $this->getCurrentState();
        if($state != GAMEINPROGRESS &&
            $state != PREVPN &&
            $state != PREGAMESTART &&
            $state != POSTCONFIG      ) {
            $this->handleError(new Error("illegal_action", "You're not allowed to edit contestants during this stage of the wargame.", false));
            return;
        }
        $this->cedit_success = false;
        $db = &$this->database;
        $smarty = &$this->getSmarty();
        //return;

        $c = Contestant::getById($_POST['cid'], $db);
        if($c == false){
            $this->handleError(new Error("team_input_error", "A wrong contestant ID was entered.", false));
            return;
        }

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


            $result = $db->prepare("SELECT * FROM teams where id <> ?");
            $result->execute(array(intval($_POST['cid'])));

            foreach($result as $res){
                if($_POST['cname'] == $res['name'])
                    $this->handleError(new Error("team_input_error", "Duplicate name!", false));
                elseif($subnet == $res['subnet'])
                    $this->handleError(new Error("team_input_error", "Duplicate subnet!", false));
                elseif($vmip == $res['VMip'])
                    $this->handleError(new Error("team_input_error", "Duplicate server ip!", false));
            }

            //check for errors and return
            if(count($this->errors) > 0){
                return;
            }else{
                //assume all is right, insert into database
                $c->setTeamname($_POST['cname']);
                $c->setSubnet($subnet);
                $c->setVm_ip($vmip);

                $c->save($db);


                OpenVPNManager::createClientConfigFile("Team".$c->getId(), $vmip, $vmip_endpoint);

                $this->createVPNServerConf($c);

                if(OpenVPNManager::getVPNStatus($c)){
                    OpenVPNManager::diconnectVPN($c);
                    OpenVPNManager::stopVPN($c);
                    sleep(5);
                }
                OpenVPNManager::startVPN($c);
            }
            $this->cedit_success = true;
        }
        
    }

    public function banContestant(){
        if($this->getCurrentState() != GAMEINPROGRESS ) {
            $this->handleError(new Error("illegal_action", "You're not allowed to add contestants during this stage of the wargame.", false));
            return;
        }
        $this->cban_success = false;

        if(isset($_POST['cid']) && intval($_POST['cid']) != 0){
            $time = $_POST['cbantime'];

            if(!ctype_digit($time) && intval($time)>=0){
                $this->handleError(new Error("ban_error", "Invalid ban input.", false));
                return;
            }
            $time=intval($time);

            $now = time();
            $id = $_POST['cid'];
            $db =& $this->database; /* @var $db PDO */
            $c = Contestant::getById($id, $db);

            if($time == 0){
                $q = $db->prepare("DELETE FROM bans WHERE team_id = ?");
                $q->execute(array(intval($id)));
                if(OpenVPNManager::getVPNStatus($c))
                    OpenVPNManager::diconnectVPN($c);
                return;
            }

            exec("mv  ".$this->config['site_folder']."lib/admin/openvpn/ccd/Team".$c->getId(). "_vm ".$this->config['site_folder']."lib/admin/openvpn/ccd/_Team".$c->getId()."_vm");

            $smarty = &$this->getSmarty();
            $tpl = $smarty->createTemplate("server.conf"); /* @var $tpl Smarty_Internal_Template */
            $tpl->assign("filename", "server_Team".$c->getId());
            $tpl->assign("path_to_rsa", $this->config['site_folder'] . $this->config['RSA_location']);
            $tpl->assign("path_to_openvpn", $this->config['site_folder'] . $this->config['openvpn_location']);
            $tpl->assign("server_ip_range",  $c->getSubnet());
            $tpl->assign("man_port",$this->config['management_port_base'] + $id);
            $tpl->assign("port",$this->config['base_port'] + $id);
            $tpl->assign("ccd",$this->config['site_folder'] . $this->config['openvpn_location'] . "/ccd/Team" .$c->getId() );
            $tpl->assign("banned",1);
            $config_file_data = $tpl->fetch();

            $config_file_loc = $this->config['openvpn_location'] . "Team" . $c->getId() . ".conf";
            $handle = @fopen($config_file_loc, 'w');
            if($handle === false){
                $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
                return;
            }
            fwrite($handle, $config_file_data);
            fclose($handle);

            if(OpenVPNManager::getVPNStatus($c)){
                OpenVPNManager::diconnectVPN($c);
                OpenVPNManager::stopVPN($c);
                sleep(5);
            }
            OpenVPNManager::startVPN($c);

            $q = $db->prepare("DELETE FROM bans WHERE team_id = ?");
            $q->execute(array(intval($id)));
            if($time != 0 && $time!=-1){
                $q = $db->prepare("INSERT INTO bans values(?,?,?,?)");
                $q->execute(array(intval($id),$now+($time*60),"Pwned",0));
            }else if($time == -1){
                $q = $db->prepare("INSERT INTO bans values(?,?,?,?)");
                $q->execute(array(intval($id),-1,"Pwned",0));
            }
            
            $this->cban_success = true;
        }
        
    }

    public function deleteContestant(){
        if(isset($_POST['cid']) && intval($_POST['cid']) != 0){

            /*
             * @FIXME How should this be implemented? Should the keys be deleted as well? Should the scores be deleted?
             * The latter would affect other players as well if it happened during the game.
             *
             * Currently only basic deletion is imeplemented, i.e. the team will not show up in the listing of teams
             * nor can the OpenVPN server be started for that team making sure that it's not an unwanted backdoor.
             */

            $this->cdel_success = false;
            $db =& $this->database; /* @var $db PDO */
            $c = Contestant::getById(intval($_POST['cid']), $db);
            if($c == false){
                $this->handleError(new Error("cdell_error", "Invalid contestant.", false));
                return;
            }

            if(OpenVPNManager::getVPNStatus($c)){
                OpenVPNManager::diconnectVPN($c);
                OpenVPNManager::stopVPN($c);
                sleep(3);
            }

            if(!deleteAll($this->config['site_folder']."lib/admin/openvpn/ccd/Team".$c->getId())){
                $this->handleError(new Error("cdell_error", "(#1) Could not properly delete files asscociated with this contestant.", false));
            }

            if(!unlink($this->config['openvpn_location'] . "Team" . $c->getId() . ".conf")){
                $this->handleError(new Error("cdell_error", "(#2) Could not properly delete files asscociated with this contestant.", false));
            }

            $q = $db->prepare("DELETE FROM teams WHERE id = ?");
            $q->execute(array($c->getId()));

            $this->cdel_success = true;
        }
    }

    public function addAnnouncement(){
        if($this->getCurrentState() == PRECONFIG ) {
            $this->handleError(new Error("illegal_action", "You're not allowed to add announcements during this stage of the wargame.", false));
            return;
        }

        if(!isset($_POST['atitle']) || $_POST['atitle'] == "Announcement title"  || !isset($_POST['abody']) || $_POST['abody'] == "Announcement body"){
            $this->handleError(new Error("announce_add_error", "(#1) Please fill out the full form.", false));
            return;
        }

        $q = $this->database->prepare("INSERT INTO announcements VALUES (null,?,?,?)");
        $q->execute(array($_POST['atitle'],$_POST['abody'], time()));
    }

    public function editAnnouncement(){
        if($this->getCurrentState() == PRECONFIG ) {
            $this->handleError(new Error("illegal_action", "You're not allowed to edit announcements during this stage of the wargame.", false));
            return;
        }

        if(!isset($_POST['atitle']) || $_POST['atitle'] == "Announcement title"  || !isset($_POST['abody']) || $_POST['abody'] == "Announcement body"){
            $this->handleError(new Error("announce_edit_error", "(#2) Please fill out the full form.", false));
            return;
        }

        $q = $this->database->prepare("SELECT * FROM announcements WHERE id = ?");
        $q->execute(array($_POST['aid']));
        if($q->fetch() == false){
            $this->handleError(new Error("announce_edit_error", "Invalid announcement selected for editing.", false));
            return;
        }

        $q = $this->database->prepare("UPDATE announcements SET title = ?, announcement = ? WHERE id = ?");
        $q->execute(array($_POST['atitle'],$_POST['abody'],$_POST['aid']));
    }

    public function deleteAnnouncement(){
        if($this->getCurrentState() == PRECONFIG ) {
            $this->handleError(new Error("illegal_action", "You're not allowed to delete announcements during this stage of the wargame.", false));
            return;
        }

        if(!isset($_POST['aid']) || intval($_POST['aid']) == -1 ){
            $this->handleError(new Error("announce_del_error", "(#2) No valid announcement selected for deletion.", false));
            return;
        }

        $q = $this->database->prepare("SELECT * FROM announcements WHERE id = ?");
        $q->execute(array($_POST['aid']));
        if($q->fetch() == false){
            $this->handleError(new Error("announce_del_error", "(#3) No valid announcement selected for deletion.", false));
            return;
        }

        $q = $this->database->prepare("DELETE FROM announcements WHERE id = ?");
        $q->execute(array($_POST['aid']));
    }

    public function startVPN(){
        $state = $this->getCurrentState();
        if($state != PREVPN &&
                $state != PREGAMESTART &&
                $state != GAMEINPROGRESS) {
            $this->handleError(new Error("illegal_action", "You can't start the VPNs in this stage.", false));
            return;
        }
        $this->startvpn_success = false;

        foreach ($this->contestant_list as $c){
            if(!OpenVPNManager::getVPNStatus($c))
                OpenVPNManager::startVPN($c);
        }
        if(!OpenVPNManager::getBaseVPNStatus())
            OpenVPNManager::startBaseVPN();
        if($state == PREVPN)
            $this->setState(PREGAMESTART);
        sleep(3);
        $this->startvpn_success = true;
    }

    public function stopVPN(){
        $state = $this->getCurrentState();
        if($state != PREGAMESTART &&
                $state != GAMEINPROGRESS &&
                $state != POSTGAME) {
            $this->handleError(new Error("illegal_action", "You can't stop the VPNs in this stage.", false));
            return;
        }
        foreach ($this->contestant_list as $c){
            if(OpenVPNManager::getVPNStatus($c))
                OpenVPNManager::stopVPN($c);
        }
        if(OpenVPNManager::getBaseVPNStatus())
            OpenVPNManager::stopBaseVPN();
        sleep(3);
    }

    public function forceSanityCheck(){
        $this->sancheckSuccess = false;
        if($this->getCurrentState() != GAMEINPROGRESS ) {
            $this->handleError(new Error("illegal_action", "You can't request a sanity check during this stage.", false));
            return;
        }
        $cid = $_POST['cid'];
        if(false==($c = Contestant::getById($cid, $this->database))){
            $this->handleError(new Error("sanity_input_error", "You must specify a team to be checked!", false));
            return;
        }

        $fp = @fsockopen("127.0.0.1", 9997, $errno, $errstr, 5);
        if(!$fp){
            $this->handleError(new Error("vpn_error", "Error #42: Cannot connect to Sanity Check Service! (".$errno.")", false));
            return false;
        }else{
            fwrite($fp, "CHECK TYPE NORMAL ".$c->getVm_ip()."\n");
            fclose($fp);
        }
        
        $fp = @fsockopen("127.0.0.1", 9997, $errno, $errstr, 5);
        if(!$fp){
            $this->handleError(new Error("vpn_error", "Error #42+1: Cannot connect to Sanity Check Service! (".$errno.")", false));
            return false;
        }else{
            fwrite($fp, "CHECK TYPE P2P ".$c->getVm_ip()."\n");
            fclose($fp);
        }

        $this->sancheckSuccess = true;
    }

    public function startGame(){
        if($this->getCurrentState() != PREGAMESTART ) {
            $this->handleError(new Error("illegal_action", "You can't start a wargame in this stage.", false));
            return;
        }

        OpenVPNManager::setKernelRouting(true);
        exec("python ". $this->config['ch_location'] . "FlagAdministration.py " . $this->config['site_folder'].$this->config['database_file_name'] . " > /dev/null 2>/dev/null &");
        exec("python ". $this->config['ch_location'] . "SanityCheckService.py " . $this->config['site_folder'].$this->config['database_file_name'] . " > /dev/null 2>/dev/null &");
        $db =&$this->database; /* @var $db PDO */
        $q = $db->prepare("INSERT INTO config VALUES (?,?)");
        $q->execute(array("start_time",time()));

        $this->setState(GAMEINPROGRESS);
    }

    public function endGame(){
        if($this->getCurrentState() != GAMEINPROGRESS ) {
            $this->handleError(new Error("illegal_action", "You can't stop a wargame which is not in progress.", false));
            return;
        }
        $this->endgame_success = false;

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
        $this->endgame_success = true;
    }
    /**
     *
     * @param Contestant $c The contestant which needs a new VPN server config file
     */
    private function createVPNServerConf(&$c){
        $smarty = &$this->getSmarty();
        $tpl = $smarty->createTemplate("server.conf"); /* @var $tpl Smarty_Internal_Template */
        $tpl->assign("filename", "server_"."Team".$c->getId());
        $tpl->assign("path_to_rsa", $this->config['site_folder'] . $this->config['RSA_location']);
        $tpl->assign("path_to_openvpn", $this->config['site_folder'] . $this->config['openvpn_location']);
        $tpl->assign("server_ip_range",  substr($c->getSubnet(), 0, -3));
        $tpl->assign("man_port",$this->config['management_port_base'] + $c->getId());
        $tpl->assign("port",$this->config['base_port'] + $c->getId());
        $tpl->assign("ccd",$this->config['site_folder'] . $this->config['openvpn_location'] . "/ccd/Team" .$c->getId() );
        $config_file_data = $tpl->fetch();

        $config_file_loc = $this->config['openvpn_location'] . "Team".$c->getId() . ".conf";
        $handle = @fopen($config_file_loc, 'w');
        if($handle === false){
            $this->handleError(new Error("fatal_error", "Cannot write to file " .$config_file_loc. "!" , true));
            return;
        }
        fwrite($handle, $config_file_data);
        fclose($handle);
    }

    /**
     * Fetches the last n Sanity Check results for the given Contestant
     * @param Integer $n
     * @param Contestant $c
     * @global WebInterface interface
     */
    public static function getSanityResultsForContestant($n,&$c){
        global $interface;

        $db =& $interface->database;
        
        // evil_teams ( ip TEXT, port INTEGER, timestamp INTEGER, reporter TEXT, seen INTEGER);
        // modules (id INTEGER PRIMARY KEY, name TEXT, numFlags INTEGER, deployscript TEXT, serviceport INTEGER);
        // teams (id INTEGER PRIMARY KEY, name TEXT, VMip TEXT, subnet TEXT);
        $q = $db->prepare("SELECT et.port as port, et.timestamp as timestamp, m.name as modulename, ifnull(t.name,'System') as reporter
                            FROM evil_teams et
                            INNER JOIN modules m ON et.port = m.serviceport
                            LEFT OUTER JOIN teams t ON et.reporter = t.VMip
                            WHERE et.ip = ?
                            ORDER BY et.timestamp DESC");
        $q->execute(array($c->getVm_ip()));

        $retval = array();
        while($n>0 && ($res = $q->fetch())){
            $sanres = new stdClass();
            $sanres->port = $res['port'];
            $sanres->service = $res['modulename'];
            $sanres->timestamp = $res['timestamp'];
            $sanres->reporter = $res['reporter'];
            array_push($retval, $sanres);
            $n--;
        }
        return $retval;
    }
}

?>

