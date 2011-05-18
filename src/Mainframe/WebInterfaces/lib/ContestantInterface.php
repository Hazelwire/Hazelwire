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
        foreach($this->contestant_list as $c){ /* @var $c Contestant */
            if(ip_in_range($_SERVER['REMOTE_ADDR'], $c->getSubnet())){
                $this->contestant = &$c;
            }
        }
    }
    
    
    public function show(){
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
            $smarty->assign("errors", array_merge($this->errors, $errors_by_name));
        }
        
        if ($this->getCurrentState() != GAMEINPROGRESS) {
            return $smarty->fetch("game_not_started.tpl");
        } else {
            return $smarty->fetch("game_contestant_view.tpl");
        }
    }
    
    public function doWork(){
        // @TODO check for db_ready.
        if($this->getCurrentState() == GAMEINPROGRESS){
            if (strtolower($_SERVER['REQUEST_METHOD']) == "post") {
                if(isset($_POST['sub_flag'])){
                    if(isset($_POST['flag'])){
                        $flag = $_POST['flag'];
                        if((startsWith($flag, "FLG") && strlen($flag) && ctype_alnum($flag))){
                            
                            $db = &$this->database; /* @var $db PDO */
                            $q = $db->prepare("SELECT * FROM scores WHERE flag = ? AND team_id = ?"); /* @var $q PDOStatement */
                            $q->execute(array($flag,$this->contestant->getId()));
                            if($q->fetch()){
                                $this->handleError(new Error("flag_error", "You already submitted that flag!"));
                                return;
                            }
                            // flags (flag_id INTEGER, mod_id INTEGER, team_id INTEGER, flag TEXT);
                            // flagpoints (flag_id INTEGER, mod_id INTEGER, points INTEGER);
                            // teams (id INTEGER PRIMARY KEY, name TEXT, VMip TEXT, subnet TEXT);
                            $q = $db->prepare("SELECT flags.team_id as team_id, flags.flag as flag, flagpoints.points as points FROM flags INNER JOIN flagpoints ON flagpoints.flag_id = flags.flag_id WHERE flag = ?");
                            $q->execute(array($flag,$this->contestant->getId()));
                            $result = $q->fetch();
                            if($result === false){
                                $this->handleError(new Error("flag_error", "Unknown flag!"));
                            }  elseif ($result['team_id'] == $this->contestant->getId()) {
                                $this->handleError(new Error("flag_error", "Rooting your own box is not a cool story bro!"));
                            } else {
                                $timestamp = time();
                                $q = $db->prepare("INSERT INTO scores VALUES (?, ? ,? ,?)team_id , flag , timestamp , points ");
                                $q->execute(array($this->contestant->getId(), $flag, $timestamp, $result['points']));
                                $this->flag_success = true;
                            }

                        }else{
                            $this->handleError(new Error("flag_error", "The flag you submitted is incorrect!"));
                        }
                    }
                }
            }
        }
    }
}

?>
