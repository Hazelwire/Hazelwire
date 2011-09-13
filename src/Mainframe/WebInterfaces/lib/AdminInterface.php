<?php

/**
 * AdminInterface handles all the interaction between the administrator of the wargame and the Mainframe.
 * It is responsible for showing the interface to the user and of handling the user's input.
 *
 * @author Daniel
 */
class AdminInterface extends WebInterface {

    /**
     *
     * @var int The state of the wargame, as defined in index.php
     */
    private $state;
    private $created_db = false;
    private $cadd_success = false;
    private $cedit_success = false;
    private $sancheckSuccess = false;
    private $cban_success = false;
    private $cdel_success = false;
    private $aadd_success = false;
    private $aedit_success = false;
    private $adel_success = false;

    private $startvpn_success = false;
    private $startgame_success = false;
    private $stopvpn_success = false;
    private $endgame_success = false;

    /**
     * This function returns the string that will be returned to the user.
     * This can either be full fledged HTML or JSON encoded data, depending on the request type.
     * Internal it handles the errors (i.e. makes them ready for display) and then generates the
     * output which corresponds to the request made. For this purpose it makes use of Smarty.
     *
     * @return string The string that will be displayed to the user. 
     */
    public function show() {
        $smarty = &$this->getSmarty();
        /*
         * Error handling. It adds all the reported errors to Smarty for displaying.
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
         * Actual showing the pages.
         *
         * First: On PRECONFIG show the config page, with a message if the DB has just been created
         */
        if (($this->state = $this->getCurrentState()) == PRECONFIG) {
            $smarty->assign("db_created", ($this->created_db ? 1 : 0));
            if ($this->created_db) {
                $smarty->assign("db_file_name", $this->config['database_file_name']);
                $smarty->assign("site_path", $this->config['site_folder']);
            }
            return $smarty->fetch("config.tpl");
            /* If there's no game, and the has not been just stopped, show the End Game page*/
        } /*elseif ($this->getCurrentState() == POSTGAME && !$this->endgame_success) {
            return $smarty->fetch("game_end.tpl");
        } */else {
            // Get the name of the game
            $db = &$this->database;
            $q = $db->query("SELECT value FROM config WHERE config_name = 'gamename'");
            $res = $q->fetch();
            $smarty->assign("title",$res[0]);

            /*
             * If there's not a specified action, assume it is a full page load
             * and show everything.
             */
            if(!isset($_GET['aaction'])){
                
                $allow_startvpn = OpenVPNManager::getBaseVPNStatus();
                $allow_stopvpn = OpenVPNManager::getBaseVPNStatus();

                $q = $db->query("SELECT value FROM config WHERE config_name = 'gamename'");
                $res = $q->fetch();
                $smarty->assign("title",$res[0]);

                /*
                 * Get the contestants ordered by their points
                 */
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

                /* assign the vlaue of 'disabled' to the corrent buttons */
                $smarty->assign("allow_startvpn",!$allow_startvpn);
                $smarty->assign("allow_stopvpn",$allow_startvpn);
                $smarty->assign("allow_endgame",$this->getCurrentState() == GAMEINPROGRESS);
                $smarty->assign("allow_startgame",$this->getCurrentState() == PREGAMESTART);

                /*
                 * Get all the announcements with the newest first.
                 */
                $q  = $db->query("SELECT * FROM announcements ORDER BY timestamp DESC");
                $announcements = array();
                foreach ($q as $announce){
                    $announcement = new stdClass();
                    $announcement->id = $announce['id'];
                    $announcement->timestamp = $announce['timestamp'];
                    $announcement->title = htmlspecialchars($announce['title']);
                    $announcement->content = $this->parseBB($announce['announcement']);
                    array_push($announcements, $announcement);
                }
                $smarty->assign("announcements",$announcements);

                return $smarty->fetch("admin.tpl");
            } else if(startsWith ($_GET['aaction'],"getsanity")){
                /*
                 * Ajax request for sanity list
                 */
                $csq = $db->query("select id from teams");
                $q = $db->prepare("SELECT et.port as port, et.timestamp as timestamp, ifnull(m.name,'ClientBot') as modulename, ifnull(t.name,'System') as reporter
                                        FROM evil_teams et
                                        LEFT OUTER JOIN modules m ON et.modulename = m.name
                                        LEFT OUTER JOIN teams t ON et.reporter = t.VMip
                                        WHERE et.ip = ?
                                        ORDER BY et.timestamp DESC");
                $retval = array();
                foreach($csq as $cdata){
                    $c = Contestant::getById($cdata['id'], $db);
                    $q->execute(array($c->getVm_ip()));

                    while($res = $q->fetch()){
                        $sanres = array();
                        array_push($sanres, $c->getTeamname());
                        array_push($sanres, date("d/m/Y H:i:s",$res['timestamp']));
                        array_push($sanres, $c->getVm_ip());
                        array_push($sanres, $res['port']);
                        array_push($sanres, $res['modulename']);
                        array_push($sanres, $res['reporter']);

                        array_push($retval, $sanres);
                    }
                }

                $trueretval = new stdClass();
                $trueretval->aaData = $retval;
                return json_encode($trueretval);

            } else if(startsWith ($_GET['aaction'],"getcs")){
                /*
                 * Ajax request for update of contestant list
                 */
                /*
                 * Get the contestants ordered by their points
                 */
                $q = $db->query("SELECT teams.id as team_id, ifnull(sum(scores.points),0) as points FROM " . /* @var $q PDOStatement */
                             "teams LEFT OUTER JOIN scores ON teams.id = scores.team_id ".
                             "GROUP BY teams.id ORDER BY ifnull(sum(scores.points),0) DESC;");

                $contestants = array();
                while (($res = $q->fetch()) !== false){
                    $c = Contestant::getById($res['team_id'], $db);
                    array_push($contestants, $c);
                }
                $smarty->assign("contestants",$contestants);

                $retval = new stdClass();
                $retval->action="getcs";
                $retval->reply = $smarty->fetch("admin_ajax_clist.tpl");
                
                return json_encode($retval);
            } else if(startsWith ($_GET['aaction'],"getas")){
                /*
                 * Ajax request for update of announcement list
                 */
                /*
                 * Get all the announcements with the newest first.
                 */
                $q  = $db->query("SELECT * FROM announcements ORDER BY timestamp DESC");
                $announcements = array();
                foreach ($q as $announce){
                    $announcement = new stdClass();
                    $announcement->id = $announce['id'];
                    $announcement->timestamp = $announce['timestamp'];
                    $announcement->title = htmlspecialchars($announce['title']);
                    $announcement->content = $this->parseBB($announce['announcement']);
                    array_push($announcements, $announcement);
                }
                $smarty->assign("announcements",$announcements);

                $retval = new stdClass();
                $retval->action="getas";
                $retval->reply = $smarty->fetch("admin_ajax_alist.tpl");

                return json_encode($retval);

            } else if(startsWith ($_GET['aaction'],"cadd")){
                /*
                 * Show the page to add a Contestant.
                 * On success it also shows a success message.
                 */
                if (isset($_POST['cadd']) && strtolower($_POST['cadd']) == 'add'){
                    $retval = new stdClass();
                    $retval->errorcount = count($this->errors);
                    $errorArray = array();
                    foreach($this->errors as $error){
                        array_push($errorArray, $error->getMessage());
                    }
                    $retval->errors = $errorArray;
                    $retval->success = $this->cadd_success;
                    $retval->action = "caddReply";
                    $retval->reply = "Contestand added.";
                    return json_encode($retval);
                }
                else{
                    $retval = new stdClass();
                    $retval->action = "cadd";
                    $retval->reply = $smarty->fetch("admincadd.tpl");
                    return json_encode($retval);
                }
            } else if(startsWith ($_GET['aaction'],"cedit")){
                /*
                 * Show the page to edit a contestant, with preloaded values if a contestant ID has been given.
                 * This can either be from the form itself ('cedit') or from the mainpage form ('contestant')
                 * On success it also shows a success message.
                 */

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

                    $retval = new stdClass();
                    $retval->errorcount = count($this->errors);
                    $errorArray = array();
                    foreach($this->errors as $error){
                        array_push($errorArray, $error->getMessage());
                    }
                    $retval->errors = $errorArray;
                    $retval->success = $this->cedit_success || $this->sancheckSuccess;
                    $retval->action = "ceditReply";
                    $retval->reply = "Contestant edited.";
                    return json_encode($retval);

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
                $retval = new stdClass();
                $retval->action = "cedit";
                $retval->reply = $smarty->fetch("admincedit.tpl");
                return json_encode($retval);
            }else if(startsWith ($_GET['aaction'],"cban")){
                /*
                 * Show the banning page for contestant, with preloaded name. On success it also shows a success message.
                 */
                $c = Contestant::getById($_POST['contestant'], $this->database);
                if($c == false)
                	$c = Contestant::getById($_POST['cid'], $this->database);
                $smarty->assign("contestant",$c);
                
                if (isset($_POST['cid'])){
                    $retval = new stdClass();
                    $retval->errorcount = count($this->errors);
                    $errorArray = array();
                    foreach($this->errors as $error){
                        array_push($errorArray, $error->getMessage());
                    }
                    $retval->errors = $errorArray;
                    $retval->success = $this->cban_success;
                    $retval->action = "cbanReply";
                    $retval->reply = "Contestand banned.";
                    return json_encode($retval);
                }
                $retval = new stdClass();
                $retval->action = "cban";
                $retval->reply = $smarty->fetch("admincban.tpl");
                return json_encode($retval);
            }else if(startsWith ($_GET['aaction'],"cdel")){
                /*
                 * Show the Contestant deletion page, with a preloaded name if it exists. On success it also shows a success message.
                 */
                $c = Contestant::getById($_POST['contestant'], $this->database);
                if($c == false)
                	$c = Contestant::getById(intval($_POST['cid']), $this->database);
                $smarty->assign("contestant",$c);
                if (isset($_POST['cid'])){
                    $retval = new stdClass();
                    $retval->errorcount = count($this->errors);
                    $errorArray = array();
                    foreach($this->errors as $error){
                        array_push($errorArray, $error->getMessage());
                    }
                    $retval->errors = $errorArray;
                    $retval->success = $this->cdel_success;
                    $retval->action = "cdelReply";
                    $retval->reply = "Contestand deleted.";
                    return json_encode($retval);
                }
                $retval = new stdClass();
                $retval->action = "cdel";
                $retval->reply = $smarty->fetch("admincdel.tpl");
                return json_encode($retval);
            }else if(startsWith ($_GET['aaction'],"aadd")){
                /*
                 * Show the page to add announcement. This includes a message if an announcement has just been added.
                 */
                if (isset($_POST['submitted'])){
                    $retval = new stdClass();
                    $retval->errorcount = count($this->errors);
                    $errorArray = array();
                    foreach($this->errors as $error){
                        array_push($errorArray, $error->getMessage());
                    }
                    $retval->errors = $errorArray;
                    $retval->success = $this->aadd_success;
                    $retval->action = "aaddReply";
                    $retval->reply = "Announcement added.";
                    return json_encode($retval);
                }
                $retval = new stdClass();
                $retval->action = "aadd";
                $retval->reply = $smarty->fetch("adminapost.tpl");
                return json_encode($retval);
            }else if(startsWith ($_GET['aaction'],"aedit")){
                /*
                 * Show the page for editing an announcement with preloaded values.
                 * On success it also shows a success message.
                 */
                if (isset($_POST['submitted'])){
                    $retval = new stdClass();
                    $retval->errorcount = count($this->errors);
                    $errorArray = array();
                    foreach($this->errors as $error){
                        array_push($errorArray, $error->getMessage());
                    }
                    $retval->errors = $errorArray;
                    $retval->success = $this->aedit_success;
                    $retval->action = "aeditReply";
                    $retval->reply = "Announcement edited.";
                    return json_encode($retval);
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
                
                $retval = new stdClass();
                $retval->action = "aedit";
                $retval->reply = $smarty->fetch("adminaedit.tpl");
                return json_encode($retval);
            }else if(startsWith ($_GET['aaction'],"adel")){
                /*
                 * Show the page to delete an announcement, with preloaded title.
                 * On success it also shows a success message.
                 */
                if (isset($_POST['submitted'])){
                    $retval = new stdClass();
                    $retval->errorcount = count($this->errors);
                    $errorArray = array();
                    foreach($this->errors as $error){
                        array_push($errorArray, $error->getMessage());
                    }
                    $retval->errors = $errorArray;
                    $retval->success = $this->adel_success;
                    $retval->action = "adelReply";
                    $retval->reply = "Announcement deleted.";
                    return json_encode($retval);
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
                $retval = new stdClass();
                $retval->action = "adel";
                $retval->reply = $smarty->fetch("adminadel.tpl");
                return json_encode($retval);

            }else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"startvpn")==0){
                /*
                 * Show the result for the startvpn action. It consists of a success message or a list of errors and possibly both.
                 * The success message includes a list of all the VPNs running and their status.
                 * The result is JSON encoded and returned.
                 */
                $string= "";
                if($this->startvpn_success){
                    $string = "VPN servers started".((count($this->errors) > 0)?"(but with errrors)":"").".<br />".PHP_EOL."<pre style=\"font-size:1.1em;\">Status:".PHP_EOL;

                    foreach ($this->contestant_list as $c){
                        $string .= sprintf("%'.-30s: %s",$c->getTeamname(),OpenVPNManager::getVPNStatus($c)?"Online ":"Offline").PHP_EOL;
                    }
                    $string .= sprintf("%'.-30s: %s","Base VM",OpenVPNManager::getBaseVPNStatus()?"Online ":"Offline").PHP_EOL;

                    $string .="</pre>";

                    //$smarty->assignByRef("success", $string);
                }
                $retval = new stdClass();
                $retval->errorcount = count($this->errors);
                $errorArray = array();
                foreach($this->errors as $error){
                    array_push($errorArray, $error->getMessage());
                }
                $retval->errors = $errorArray;
                $retval->success = $this->startvpn_success;
                $retval->action = "startvpn";
                $retval->reply = $string;
                        //$smarty->fetch("admin_ajax_startvpn.tpl");

                return json_encode($retval);
            } else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"stopvpn")==0){
                /*
                 * Show the result for the stopvpn action. It consists of a success message or a list of errors and possibly both.
                 * The success message includes a list of all the VPNs running and their status.
                 * The result is JSON encoded and returned.
                 */
                $string="";
                if(count($this->errors) == 0){
                    $string = "VPN servers stopped.<br />".PHP_EOL."<pre style=\"font-size:1.1em;\">Status:".PHP_EOL;

                    foreach ($this->contestant_list as $c){
                        $string .= sprintf("%'.-30s: %s",$c->getTeamname(),OpenVPNManager::getVPNStatus($c)?"Online ":"Offline").PHP_EOL;
                    }
                    $string .= sprintf("%'.-30s: %s","Base VM",OpenVPNManager::getBaseVPNStatus()?"Online ":"Offline").PHP_EOL;

                    $string .="</pre>";

                    //$smarty->assignByRef("success", $string);
                }
                $retval = new stdClass();
                $retval->errorcount = count($this->errors);
                $errorArray = array();
                foreach($this->errors as $error){
                    array_push($errorArray, $error->getMessage());
                }
                $retval->errors = $errorArray;
                $retval->success = $this->stopvpn_success;
                $retval->action = "stopvpn";
                $retval->reply = $string;
                        //$smarty->fetch("admin_ajax_startvpn.tpl");

                return json_encode($retval);
            } else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"startgame")==0){
                /*
                 * Shows the result of the startgame action. This consists of a list of error when an error has occured, or a success message
                 * when there was no error.
                 *
                 * @todo Create more accurate result by using a field
                 */
                $string="";
                if($this->startgame_success){
                    $string = "The wargame has started! To the bunkers!";

                    //$smarty->assignByRef("success", $string);
                }
                $retval = new stdClass();
                $retval->errorcount = count($this->errors);
                $errorArray = array();
                foreach($this->errors as $error){
                    array_push($errorArray, $error->getMessage());
                }
                $retval->errors = $errorArray;
                $retval->success = $this->startgame_success;
                $retval->action = "startgame";
                $retval->reply = $string;
                        //$smarty->fetch("admin_ajax_startvpn.tpl");

                return json_encode($retval);
            } else if(isset($_GET['aaction']) && strcmp ($_GET['aaction'],"endgame")==0){
                /*
                 * Shows the result of the endgame action. This consists of This consists of a list of error when an error has occured, or a success message
                 * when there was no error.
                 *
                 * @todo make more accurate return message
                 */
                $string="";
                if($this->endgame_success){
                    $string = "The wargame has ended! Hail the champions!!";

                    //$smarty->assignByRef("success", $string);
                }
                $retval = new stdClass();
                $retval->errorcount = count($this->errors);
                $errorArray = array();
                foreach($this->errors as $error){
                    array_push($errorArray, $error->getMessage());
                }
                $retval->errors = $errorArray;
                $retval->success = $this->endgame_success;
                $retval->action = "endgame";
                $retval->reply = $string;
                        //$smarty->fetch("admin_ajax_startvpn.tpl");

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
     * This function handles all the 'work' (i.e. user input). <br />
     * It creates a database if there isn't any yet. Then it does the unban job to unban people who are again allowed to play.
     * Finally it tests the input of the user to see what action it should do, and calls the method to perform this action.
     * @return void Nothing ...
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
                $this->unban(); //@todo make scheduled?
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

    /**
     * Configure handles the configuration of the wargame, i.e. it handles the input from the config page.
     * @return void Nothing ...
     */
    public function configure(){
        if($this->getCurrentState() != PRECONFIG ) {
            $this->handleError(new Error("illegal_action", "You can't configure the wargame now.", false));
            return;
        }

        /* @var $db PDO */
        $db = &$this->database;

        /*
         * fetch all the values from the POST array
         */
        $gamename = htmlspecialchars($_POST['name']);
        $auto_p2p_interval = intval($_POST['p2p_interval']);
        $auto_s2p_interval = intval($_POST['s2p_interval']);
        $server_ip = $_POST['server_ip'];

        $decay_mod = floatval($_POST['points_decay_mod']);
        $points_min = intval($_POST['points_min']);
        $point_penalty_mod = floatval($_POST['point_penalty_mod']);
        $penalty_offline = 0;//intval($_POST['penalty_offline']);

        $manifest = $_FILES['manifest'];
        $temp = explode(".", $manifest['name']);
        $ext = $temp[count($temp) - 1];

        if (strcmp($ext, "xml") != 0) {
            $this->handleError(new Error("config_input_error", "You can only upload XML files!", false));
            return;
        } elseif (  $auto_p2p_interval > 0 &&
                    $auto_s2p_interval > 0 &&
                    $manifest['error'] === 0 &&
                    checkValidIp($server_ip) &&
                    preg_match("/[-]?([0-9]*\.)?[0-9]+/", $_POST['points_decay_mod']) &&
                    preg_match("/[-]?([0-9]*\.)?[0-9]+/", $_POST['point_penalty_mod']) &&
                    preg_match("/[-]?[0-9]+/", $_POST['points_min'])) {

            /*
             * Assume the input is correct. Only a few things are checked, mainly because we should assume the administrator is not
             * trying to mess with his own game.
             */

            move_uploaded_file($manifest['tmp_name'], "manifest.xml");

            $res = exec("python ". $this->config['ch_location'] . "ManifestParser.py " . $this->config['site_folder']."manifest.xml " . $this->config['site_folder'].$this->config['database_file_name'], $cmd_result);
            if(strrpos($res,"False") !== false){
                $this->handleError(new Error("config_input_error", "Invalid manifest XML!", true));
                return;
            }

            /*
             * Save all the configs in the database
             */
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

    /**
     * This function handles adding a Contestant based on user input. If it is
     * the first Contestant being made (more specific: if the number of
     * contestants in the database equals 0) also some initial keys are created
     * and the configuration for the initial VPN is created (10.0.0.1). <br>
     * Finally, if the game is already started, the new VPN will automatically
     * be started as well.
     * @return void Nothing ...
     */
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
                $tpl->clearAllAssign();
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

    /**
     * This function handling editing a Contestant by using the input given in
     * the POST array. It checks the validity of the input, stores it and
     * restarts the VPN server (thus disconnecting the Contestant for a while).
     * @return void Nothing ...
     */
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
                    //OpenVPNManager::diconnectVPN($c);
                    OpenVPNManager::stopVPN($c);
                    sleep(5);
                }
                OpenVPNManager::startVPN($c);
            }
            $this->cedit_success = true;
        }
        
    }

    /**
     * Handles banning a Contestant identified by user input. The bantime is
     * taken from the POST array, and verfied. <br>0 only disconnects the client
     * (but with auto reconnect this does not matter) and deletes a prevous ban.
     * <br>Negative values asside from -1 disconnect the Contestant and create a ban
     * for a time in the past, thus unbanning as well. <br>A value of -1 bans the
     * Contestant in such a way that he can only be unbanned by an Administrator.
     * <br>Any positive value bans the Contestant for the given number of minutes.
     * @return void Nothing ...
     */
    public function banContestant(){
        if($this->getCurrentState() != GAMEINPROGRESS ) {
            $this->handleError(new Error("illegal_action", "You're not allowed to ban contestants during this stage of the wargame.", false));
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
                $q = $db->prepare("INSERT INTO bans values(?,?,?,?)");
                $q->execute(array(intval($id),0,"Pwned",0));
                if(OpenVPNManager::getVPNStatus($c))
                    OpenVPNManager::diconnectVPN($c);
                $this->cban_success =  true;
                return;
            }
            
            if(OpenVPNManager::getVPNStatus($c)){
                OpenVPNManager::diconnectVPN($c);
                OpenVPNManager::stopVPN($c);
                sleep(5);
            }
            exec("mv ".$this->config['site_folder']."lib/admin/openvpn/ccd/Team".$c->getId(). "/Team".$c->getId(). "_vm ".$this->config['site_folder']."lib/admin/openvpn/ccd/Team".$c->getId(). "/_Team".$c->getId()."_vm");

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

    /**
     * Handles deleting a contestant, identied by $_POST['cid']. The VPN is
     * stopped, then the Client Config Directory file (which is needed for a
     * client to log in) is removed along with the VPNs config file. Finally the
     * entry in the database is removed, altough all references do still exist
     * (if any).
     * @return void Nothing ...
     */
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

            //if(OpenVPNManager::getVPNStatus($c)){
                OpenVPNManager::diconnectVPN($c);
                OpenVPNManager::stopVPN($c);
                sleep(3);
            //}

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

    /**
     * Handles adding an Announcement based on user input.
     * @return void Nothing ...
     */
    public function addAnnouncement(){
        $this->aadd_success = false;
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

        $this->aadd_success = true;
    }

    /**
     * Handles editting an Announcement, if it exists.
     * @return void Nothing ...
     */
    public function editAnnouncement(){
        $this->aedit_success = false;
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

        $this->aedit_success = true;
    }

    /**
     * Handles deleting an Announcement identified by the given ID.
     * @return void Nothing ...
     */
    public function deleteAnnouncement(){
        $this->adel_success = false;
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

        $this->adel_success = true;
    }

    /**
     * Handles starting the VPN servers. <br>
     * It only starts the VPN servers of the Contestants whose VPN is currently
     * not running. It also starts the base VPN if necessary.
     * 
     * @return void Nothing ...
     */
    public function startVPN(){
        $state = $this->getCurrentState();
        if($state != PREVPN &&
                $state != PREGAMESTART &&
                $state != GAMEINPROGRESS) {
            $this->handleError(new Error("illegal_action", "You can't start the VPNs in this stage.", false));
            return;
        }
        $q = $this->database->query("select count(*) as count from teams");
        $qre = $q->fetch();
        if($qre['count'] < 1){
            $this->handleError(new Error("start_vpn_error", "You need at least one contestant to do this.", false));
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

    /**
     * Handles stopping the VPN servers. <br>
     * It only stops the VPN servers of the Contestants whose VPN is currently
     * running. It also stops the base VPN if necessary.
     *
     * @return void Nothing ...
     */
    public function stopVPN(){
        $state = $this->getCurrentState();
        if($state != PREGAMESTART &&
                $state != GAMEINPROGRESS &&
                $state != POSTGAME) {
            $this->handleError(new Error("illegal_action", "You can't stop the VPNs in this stage.", false));
            return;
        }
        $this->stopvpn_success = false;
        foreach ($this->contestant_list as $c){
            if(OpenVPNManager::getVPNStatus($c))
                OpenVPNManager::stopVPN($c);
        }
        if(OpenVPNManager::getBaseVPNStatus())
            OpenVPNManager::stopBaseVPN();
        sleep(3);

        $this->stopvpn_success=true;
    }

    /**
     * Handles requesting both types of Sanity Check for the specified Contestant.
     * 
     * @return void Nothing ...
     */
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
        sleep(5);
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

    /**
     * Handles starting the game. <br>
     * It starts the kernel routing, Flag Administration and Sanity Check Service.
     * It also stores the start time of the game, used to  create plot the graph 
     * for the Contestants.
     * 
     * @return void Nothing ...
     */
    public function startGame(){
        $this->startgame_success = false;
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
        $this->startgame_success = true;
    }

    /**
     * Handles stopping the game. <br> It stops kernel routing, Flag Administration,
     * OpenVPN Service, Sanity Check Service and all VPNs. Lastly it changes the
     * game state.
     * @return void Nothing ...
     */
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
            fwrite($fp, "REQSHUTDOWN\n");
            fclose($fp);
        }

        $fp = @fsockopen("127.0.0.1", 9996, $errno, $errstr, 5);
        if(!$fp){
            $this->handleError(new Error("game_administration_error", "Cannot stop Sanity Check Service! (".$errno.")", false));
        }else{
            fwrite($fp, "STOPSANITYSERVICE\n");
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
            fwrite($fp, "STOPSERVICE\n");
            fclose($fp);
        }

        $this->setState(POSTGAME);
        $this->endgame_success = true;
    }

    /**
     * Generates VPN server configuration file for the given Contestant.
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
     * 
     * @param int $n
     * @param Contestant $c
     * @global WebInterface interface
     */
    public static function getSanityResultsForContestant($n,&$c){
        global $interface;

        $db =& $interface->database;
        
        // evil_teams ( ip TEXT, port INTEGER, timestamp INTEGER, reporter TEXT, seen INTEGER);
        // modules (id INTEGER PRIMARY KEY, name TEXT, numFlags INTEGER, deployscript TEXT, serviceport INTEGER);
        // teams (id INTEGER PRIMARY KEY, name TEXT, VMip TEXT, subnet TEXT);
        $q = $db->prepare("SELECT et.port as port, et.timestamp as timestamp, ifnull(m.name,'ClientBot') as modulename, ifnull(t.name,'System') as reporter
                            FROM evil_teams et
                            LEFT OUTER JOIN modules m ON et.modulename = m.name
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

