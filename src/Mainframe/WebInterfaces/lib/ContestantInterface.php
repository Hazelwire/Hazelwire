<?php

/**
 * Description of ContestantInterface
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
    
    
    public function show(){
        if($this->contestant === false)
            die("Not allowed to access this webpage. Shoo!");
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
        
        if ($this->getCurrentState() != GAMEINPROGRESS) {
            return $smarty->fetch("game_not_started.tpl");
        } else {
            if(!isset($_POST['ajax'])){
//            if($this->flag_success)
//                $smarty->assign("flag_success", 1);
                $db = &$this->database;
                $q = $db->query("SELECT teams.id as team_id, ifnull(sum(scores.points),0) as points FROM " . /* @var $q PDOStatement */
                             "teams LEFT OUTER JOIN scores ON teams.id = scores.team_id ".
                             "GROUP BY teams.id ORDER BY sum(scores.points) DESC;");

                $contestants = array();
                while (($res = $q->fetch()) !== false){
                    $c = Contestant::getById($res['team_id'], $db);
                    array_push($contestants, $c);
                }
                $smarty->assign("contestants",$contestants);
                return $smarty->fetch("contestant.tpl");
            }else if(strcmp($_POST['ajax'],"flagsub")==0){
                if($this->flag_success){
                    $smarty->assign("flag_success", 1);
                }
                return $smarty->fetch("contestant_ajax_flagsub.tpl");
            }
        }
    }
    
    public function doWork(){
        if($this->contestant === false || !$this->db_ready)
                return;
        // @TODO check for db_ready.
        if($this->getCurrentState() == GAMEINPROGRESS){
            if (strtolower($_SERVER['REQUEST_METHOD']) == "post") {
                if(isset($_POST['sub_flag'])){
                    $now = time(); // take a timestamp.
                    $db = &$this->database; /* @var $db PDO */
                    
                    if(isset($_POST['flag'])){
                        if(!$this->contestant->isFlagSubmissionBlocked()){
                            $flag = $_POST['flag'];
                            if((startsWith($flag, "FLG") && strlen($flag) && ctype_alnum($flag))){

                                // Check for duplicate flag
                                $q = $db->prepare("SELECT * FROM scores WHERE flag = ? AND team_id = ?"); /* @var $q PDOStatement */
                                $q->execute(array($flag,$this->contestant->getId()));
                                if($q->fetch()){
                                    $this->handleError(new Error("flag_error", "You already submitted that flag!"));
                                    $this->addFlagFailure($now);
                                    return;
                                }
                                // flags (flag_id INTEGER, mod_id INTEGER, team_id INTEGER, flag TEXT);
                                // flagpoints (flag_id INTEGER, mod_id INTEGER, points INTEGER);
                                // teams (id INTEGER PRIMARY KEY, name TEXT, VMip TEXT, subnet TEXT);
                                // scores (team_id INTEGER, flag TEXT, timestamp INTEGER, points INTEGER);
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
                                    
                                    //Calculate points
                                    $q = $db->prepare("SELECT f.flag_id FROM scores s INNER JOIN flags f ON s.flag = f.flag WHERE s.team_id = ? AND " . 
                                                        "f.flag_id = (SELECT flag_id FROM flags WHERE flag = ?) AND f.mod_id = (SELECT mod_id FROM flags WHERE flag = ?)");
                                    $q->execute(array($this->contestant->getId(), $flag, $flag));
                                    $exp = count($q->fetchAll(PDO::FETCH_COLUMN, 0));
                                    $gc = &$this->gameConfig; /* @var $gc GameConfig */
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
                            $q = $db->prepare("SELECT * FROM submission_block WHERE team_id = ? AND block_timestamp >= ? ORDER BY try_timestamp DESC LIMIT 0,1");
                            $q->execute(array($this->contestant->getId(),$now));
                            $res = $q->fetch();
                            if($res != false){
                                $time_blocked = $res['block_timestamp'] - $res['try_timestamp'];

                                $time_left = $res['block_timestamp'] - $now;

                                // double block time
                                $new_block_time = ($time_blocked * 2) + $now; // @todo make configurable
                                $q = $db->prepare("INSERT INTO submission_block VALUES (?,?,?)");
                                $q->execute(array($this->contestant->getId(), $now, $new_block_time));

                                $this->handleError(new Error("flag_error", "You are temporarily blocked from submitting flags due to spam! (" . $time_left . "s left)"));
                            }
                        }
                    }
                    
                     //clear block info that is too long ago now (90 seconds). 
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
