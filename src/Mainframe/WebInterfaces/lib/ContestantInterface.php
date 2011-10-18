<?php

/**
 * The interface which handles the interaction between the Contestants and the Mainframe.
 *
 * @author Daniel
 */
class ContestantInterface extends WebInterface{
    
    private $contestant;
    private $flag_success;
    
    function __construct() {
        parent::WebInterface();
        $this->flag_success = false;
        $this->contestant = false;
        foreach($this->contestant_list as $c){ /* @var $c Contestant */
            if(ip_in_range($_SERVER['REMOTE_ADDR'], $c->getSubnet())){
                $this->contestant = $c;
            }
        }
    }
    
    /**
     * Generates the HTML / JSON which should be send back to the user.
     * 
     * @return void Nothing ...
     */
    public function show(){
        if($this->contestant === false)
            die("Not allowed to access this webpage. Shoo!");
        if($this->contestant->getBanned()){
             $smarty = &$this->getSmarty();
             $smarty->assign("ban_time",$this->contestant->getBantime_full());
             return $smarty->fetch("banpage.tpl");
        }
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
        
        if ($this->getCurrentState() < GAMEINPROGRESS) {
            return $smarty->fetch("game_not_started.tpl");
        } else {
            if(!isset($_POST['ajax'])){
                if($this->getCurrentState() == POSTGAME)
                        $smarty->assign ("endgame", true);
                $db = &$this->database;
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
               
                if((!isset($_GET['winsort']) && !isset($_POST['winsort'])) || strcmp($_GET['winsort'],"flagtype") ==0 || strcmp($_POST['winsort'],"flagtype")==0) {
                    //CREATE TABLE flagpoints (flag_id INTEGER, mod_id INTEGER, points INTEGER);
                    //CREATE TABLE flags (flag_id INTEGER, mod_id INTEGER, team_id INTEGER, flag TEXT);
                    //CREATE TABLE teams (id INTEGER PRIMARY KEY, name TEXT, VMip TEXT, subnet TEXT);
                    //CREATE TABLE scores (team_id INTEGER, flag TEXT, timestamp INTEGER, points INTEGER);
                    $q = $db->query("SELECT * FROM flagpoints");
                    $q2 = $db->prepare("SELECT DISTINCT t.VMip as ip FROM scores s INNER JOIN teams t ON s.team_id = t.id INNER JOIN flags f ON s.flag = f.flag AND s.team_id = f.team_id INNER JOIN scores s2 ON s.timestamp = s2.timestamp AND s.flag = s2.flag WHERE s.points >=0 AND f.flag_id = ? AND f.mod_id = ? AND s2.team_id = ?"); /* @var $q2 PDOStatement */

                    $flags = array();$id = 0;
                    foreach ($q as $challenge) {
                        
                        $flag = new stdClass();
                        $flag->id = $id;//$challenge['flag_id'].",".$challenge['mod_id'];
                        $flag->name = "Flag for " . $challenge['points'] . " pts.";
                        $flag->wins = array();
                        $q2->execute(array($challenge['flag_id'],$challenge['mod_id'],$this->contestant->getId()));
                        while(($res = $q2->fetch()) != false){
                            $win = new stdClass();
                            $win->submitted = true;
                            $win->name = $res['ip'];
                            array_push($flag->wins, $win);
                        }
                        /*if(count($flag->wins)==0){
                            $win = new stdClass();
                            $win->submitted = false;
                            $win->name = "None";
                            array_push ($flag->wins, $win);
                        }*/
                        if(count($flag->wins)>0){
                            array_push($flags, $flag);
                            $id++;
                        }
                    }
                    $smarty->assign("flags",$flags);
                    $smarty->assign("winsort","flagtype");
                }else{

                    $q  = $db->prepare("SELECT DISTINCT t.VMip as ip, t.id as id FROM scores s 
                                        INNER JOIN teams t ON t.id = s.team_id 
                                        INNER JOIN scores s2 ON s2.flag=s.flag AND s.timestamp=s2.timestamp AND s.team_id <> s2.team_id
                                        WHERE s.points >= 0 AND s2.team_id=?");
                    $q->execute(array($this->contestant->getId()));
                    $q2 = $db->prepare("SELECT fp.points as pts, ifnull(s.timestamp, 'fail') as win FROM flagpoints fp 
                                        LEFT OUTER JOIN  flags f ON fp.flag_id = f.flag_id AND fp.mod_id = f.mod_id 
                                        LEFT OUTER JOIN scores s ON s.flag = f.flag 
                                        WHERE (ifnull(s.team_id,0)=0 OR s.team_id = ?) AND f.team_id = ?;"); /* @var $q2 PDOStatement */

                    $flags = array();$id = 0;
                    foreach ($q as $team) {
                        $flag = new stdClass();
                        $flag->id = $id;//$team['id'];
                        $flag->name = $team['ip'];
                        $flag->wins = array();
                        $q2->execute(array($this->contestant->getId(),intval($team['id'])));
                        while(($res = $q2->fetch()) != false){
                            $win = new stdClass();
                            $win->submitted = ($res['win'] != 'fail');
                            $win->name = "Flag for " . $res['pts'] . " pts.";
                            if($win->submitted)
                                array_push($flag->wins, $win);
                        }
                        array_push($flags, $flag);
                        $id++;
                    }
                    $smarty->assign("flags",$flags);
                    $smarty->assign("winsort","warserver");
                }
                
                
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
                /* @var $db PDO */ /* @var $q PDOStatement */
                //CREATE TABLE flagpoints (flag_id INTEGER, mod_id INTEGER, points INTEGER);
                //CREATE TABLE flags (flag_id INTEGER, mod_id INTEGER, team_id INTEGER, flag TEXT);
                //CREATE TABLE teams (id INTEGER PRIMARY KEY, name TEXT, VMip TEXT, subnet TEXT);
                //CREATE TABLE scores (team_id INTEGER, flag TEXT, timestamp INTEGER, points INTEGER);
                $q = $db->query("SELECT value FROM config WHERE config_name = 'start_time'");
                $res = $q->fetch();
                $start_time = $res['value']*1000;
                $smarty->assign("start_time",$start_time);
                $now = time();
                $point_fetch = $db->prepare("SELECT points,timestamp FROM scores WHERE team_id=? ORDER BY timestamp"); /* @var $point_fetch PDOStatement */
                $q = $db->prepare("SELECT id, name FROM teams");
                $q->execute();
                $series = new stdClass();
                $series->series = array();
                $series->seriesString = "";
                foreach($q as $contest){
                    $serie = new stdClass();
                    $serie->id   = "line".$contest['id'];
                    $series->seriesString .= $serie->id.",";
                    $serie->name = $contest['name'];
                    $serie->string = "[";

                    $serie->string .= "[".($start_time).",0],";
                    $total = 0;

                    $point_fetch->execute(array($contest['id']));
                    foreach($point_fetch as $points){
                        $total += intval($points['points']);
                        $serie->string .= "[".($points['timestamp']*1000).",".$total."],";
                    }
                    $serie->string .= "[".($now*1000).",".$total."]]";
                    
                    array_push($series->series, $serie);
                }
                
                $series->seriesString = substr($series->seriesString, 0, strlen($series->seriesString)-1);
                
                $smarty->assign("series",$series);
                
                return $smarty->fetch("contestant.tpl");

            }else if(strcmp($_POST['ajax'],"leaderboard")==0){
                $db = &$this->database;
                $retval = new stdClass();
                $q = $db->query("SELECT teams.id as team_id, ifnull(sum(scores.points),0) as points FROM " . /* @var $q PDOStatement */
                             "teams LEFT OUTER JOIN scores ON teams.id = scores.team_id ".
                             "GROUP BY teams.id ORDER BY ifnull(sum(scores.points),0) DESC;");

                $contestants = array();
                while (($res = $q->fetch()) !== false){
                    $c = Contestant::getById($res['team_id'], $db);
                    array_push($contestants, $c);
                }
                $smarty->assign("contestants",$contestants);
                $retval->action = "leaderboard";
                if($this->getCurrentState() == POSTGAME)
                        $retval->action .="_final";
                $retval->reply = $smarty->fetch("contestant_ajax_leaderboard.tpl");
                return json_encode($retval);
            }else if(strcmp($_POST['ajax'],"plotdata")==0){
                $db = &$this->database;
                $retval = new stdClass();
                $retval->action="plotdata";
                if($this->getCurrentState() == POSTGAME)
                        $retval->action .="_final";

                $q = $db->query("SELECT value FROM config WHERE config_name = 'start_time'");
                $res = $q->fetch();
                $retval->starttime = $res['value']*1000 - 60000;
                
                $now = time();
                $point_fetch = $db->prepare("SELECT points,timestamp FROM scores WHERE team_id=? ORDER BY timestamp"); /* @var $point_fetch PDOStatement */
                $q = $db->prepare("SELECT id, name FROM teams");
                $q->execute();
                
                $retval->plotdata = array();
                $retval->series = array();
                foreach($q as $contest){
                    $serie = array();

                    array_push($serie, array(($retval->starttime+60000),0));

                    $total = 0;

                    $point_fetch->execute(array($contest['id']));
                    foreach($point_fetch as $points){
                        $total += intval($points['points']);
                        array_push($serie,array(($points['timestamp']*1000),$total));
                    }
                    array_push($serie,array(($now*1000),$total));
                    array_push($retval->plotdata, $serie);
                    array_push($retval->series, $contest['name']);
                }

                return json_encode($retval);
            }else if(strcmp($_POST['ajax'],"flagsub")==0){
                if($this->getCurrentState() == POSTGAME){
                    $retval = new stdClass();
                    $retval->action = "endgame";
                    $retval->reply = "The game has ended.";
                    return json_encode($retval);
                }

                if($this->flag_success){
                    $smarty->assign("flag_success", 1);
                }
                $retval = new stdClass();
                $retval->action = "flagsub";
                $retval->reply = $smarty->fetch("contestant_ajax_flagsub.tpl");
                return json_encode($retval);
                
                //return $smarty->fetch("contestant_ajax_flagsub.tpl");
            }else if(strcmp($_POST['ajax'],"announcements")==0){
                $db = &$this->database;
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
                $retval->action = "announcements";
                if($this->getCurrentState() == POSTGAME)
                        $retval->action .="_final";
                $retval->reply = $smarty->fetch("contestant_ajax_announcements.tpl");
                return json_encode($retval);
            }
        }
    }

    /**
     * Handles the user input and executes the requested actions
     * @return void Nothing..
     */
    public function doWork(){
        if($this->contestant === false || !$this->db_ready)
                return;
        // @TODO check for db_ready.
        if($this->getCurrentState() == GAMEINPROGRESS){
            $this->unban();
            if($this->contestant->getBanned())
                return;
            if (strtolower($_SERVER['REQUEST_METHOD']) == "post") {
                if(isset($_POST['sub_flag'])){
                    $now = time(); // take a timestamp.
                    $db = &$this->database; /* @var $db PDO */

                    /*
                     * If a flag is submitted it should handle this
                     */
                    if(isset($_POST['flag'])){
                        // But only if the contestant can actually submit flags
                        if(!$this->contestant->isFlagSubmissionBlocked()){
                            $flag = $_POST['flag'];
                            
                            // Check for basic validity
                            if((startsWith($flag, "FLG") && strlen($flag) && ctype_alnum($flag))){

                                // Check for duplicate flag
                                //$q = $db->prepare("SELECT * FROM scores WHERE flag = ? AND team_id = ?"); /* @var $q PDOStatement */
                                //$q->execute(array($flag,$this->contestant->getId()));
                                //if($q->fetch()){
                                //    $this->handleError(new Error("flag_error", "You already submitted that flag!"));
                                //    $this->addFlagFailure($now);
                                //    return;
                                //}

                                /*
                                 * Select all the flags from the database (along with relevant information) which is the same as the submitted flag.
                                 */
                                $q = $db->prepare("SELECT flags.team_id as team_id, flags.flag as flag, flagpoints.points as points FROM flags INNER JOIN flagpoints ON flagpoints.flag_id = flags.flag_id AND flagpoints.mod_id = flags.mod_id WHERE flag = ?");
                                $q->execute(array($flag));
                                $result = $q->fetch();
                                if($result === false){
                                    $this->handleError(new Error("flag_error", "Unknown flag!"));
                                    $this->addFlagFailure($now);
                                }  elseif ($result['team_id'] == $this->contestant->getId()) {
                                    $this->handleError(new Error("flag_error", "Rooting your own box is not cool man!"));
                                    $this->addFlagFailure($now);
                                } else {
                                    // Check for duplicate flag
                                    $q = $db->prepare("SELECT * FROM scores WHERE flag = ? AND team_id = ?"); /* @var $q PDOStatement */
                                    $q->execute(array($flag,$this->contestant->getId()));
                                    if($q->fetch()){
                                        $this->handleError(new Error("flag_error", "You already submitted that flag!"));
                                        $this->addFlagFailure($now);
                                        return;
                                    }
                                    
                                    /*
                                     * Calculate points
                                     *
                                     * First select the already scored flags of this Contestant which have the same type
                                     */
                                    $q = $db->prepare("SELECT f.flag_id FROM scores s INNER JOIN flags f ON s.flag = f.flag WHERE s.team_id = ? AND " . 
                                                        "f.flag_id = (SELECT flag_id FROM flags WHERE flag = ?) AND f.mod_id = (SELECT mod_id FROM flags WHERE flag = ?)");
                                    $q->execute(array($this->contestant->getId(), $flag, $flag));
                                    $exp = count($q->fetchAll(PDO::FETCH_COLUMN, 0));
                                    $gc = &$this->gameConfig; /* @var $gc GameConfig */
                                    
                                    /* Decrease the amount of points using the points decay modifier */
                                    $mult = pow($gc->points_decay_mod, $exp);
                                    $points = intval(intval($result['points']) * $mult);
                                    
                                    //Give points to scoring team and substract from pwned team
                                    $timestamp = time();
                                    $q = $db->prepare("INSERT INTO scores VALUES (?, ? ,? ,?);");
                                    $q->execute(array($this->contestant->getId(), $flag, $timestamp, ($points > $gc->points_min)?$points:$gc->points_min));
                                    $q->execute(array($result['team_id'], $flag, $timestamp, -1 *intval(intval($result['points']) * $gc->points_penalty_mod)));
                                    $this->flag_success = true;
                                }

                            }else{
                                $this->handleError(new Error("flag_error", "The flag you submitted is incorrect!"));
                                $this->addFlagFailure($now);
                            }
                        }else{
                            //fetch old block time
                            $q = $db->prepare("SELECT team_id, try_timestamp, block_timestamp FROM submission_block WHERE team_id = ? AND block_timestamp >= ? ORDER BY try_timestamp DESC");
                            $q->execute(array($this->contestant->getId(),$now));
                            $res = $q->fetch();
                            if($res != false){
                                $time_blocked = $res['block_timestamp'] - $res['try_timestamp'];

                                $time_left = $res['block_timestamp'] - $now;

                                // double block time
                                $q = $db->prepare("SELECT count(*) as count FROM submission_block WHERE team_id = ? AND try_timestamp >= ?"); /* @var $q PDOStatement */
                                $q->execute(array($this->contestant->getId(), $now - 60 ));
                                $res = $q->fetch();

                                if($res['count'] >= 9){
                                    $new_block_time = min(array(($time_blocked * 2),3600)) + $now; // @todo make configurable
                                    $q = $db->prepare("INSERT INTO submission_block VALUES (?,?,?)");
                                    $q->execute(array($this->contestant->getId(), $now, $new_block_time));
                                }

                                $this->handleError(new Error("flag_error", "You are temporarily blocked from submitting flags due to spam! (" . $time_left . "s left)"));
                            }
                        }
                    }
                    
                     //clear block info that is too long ago now
                    $q = $db->prepare("DELETE FROM submission_block WHERE team_id = ? AND try_timestamp NOT IN (SELECT try_timestamp FROM submission_block WHERE team_id = ? ORDER BY try_timestamp DESC LIMIT 0,10)");
                    $q->execute(array($this->contestant->getId(), $this->contestant->getId()));
                }
            }
        }
    }
    
    /**
     * Adds a flag failure entry in the database and if necessary temporarily blocks the
     * player from future submissions to prevent spamming.
     * @param integer $timestamp The time the failure ocurred.
     */
    public function addFlagFailure($timestamp){
        $db = &$this->database; /* @var $db PDO */
        $q = $db->prepare("SELECT count(*) as count FROM submission_block WHERE team_id = ? AND try_timestamp >= ?"); /* @var $q PDOStatement */
        $q->execute(array($this->contestant->getId(), $timestamp - 60 ));
        $res = $q->fetch();
        
        if($res['count'] >= 9){
            //block the player
            $q = $db->prepare("INSERT INTO submission_block VALUES (?,?,?)");
            // @todo make configurable
            $q->execute(array($this->contestant->getId(),$timestamp,$timestamp + 60)); //minimal block time == 60 seconds
            $this->handleError(new Error("flag_error", "You are temporarily blocked from submitting flags due to spam!"));
        }
        else {
            $q = $db->prepare("INSERT INTO submission_block VALUES (?,?,?)");
            $q->execute(array($this->contestant->getId(),$timestamp,0)); 
        }
    }
}

?>
