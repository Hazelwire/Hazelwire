<?php

/**
 * Description of ContestantInterface
 *
 * @author Daniel
 */
class ContestantInterface extends WebInterface{
    
    private $contestant;
    
    function __construct() {
        parent::WebInterface();
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
        if($this->getCurrentState() == GAMEINPROGRESS){
            if (strtolower($_SERVER['REQUEST_METHOD']) == "post") {
                if(isset($_POST['sub_flag'])){
                    if(isset($_POST['flag'])){
                        $flag = $_POST['flag'];
                        if((startsWith($flag, "FLG") && strlen($flag) && ctype_alnum($flag))){
                            
                            $db = $this->database; /* @var $db PDO */
                            $q = $db->prepare("SELECT * FROM scores WHERE flag = ? AND team_id = ?");
                            
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
