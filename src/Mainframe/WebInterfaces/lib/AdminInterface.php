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
        if($this->fatal_error){
            $smarty->assignByRef("errors", $this->errors);
            $smarty->display("fatal_error.tpl");
            return;
        }else if(count($this->errors)>0){
            $errors_by_name = array();
            for($i=0;$i<count($this->errors);$i++){
                /* @var $error Error */
                $error = $this->errors[$i];
                $smarty->assign($error->getType(), $error->getMessage());
                $errors_by_name[$error->getType()] = $error;
            }
            $smarty->assign("errors", array_merge($this->errors,$errors_by_name));
        }

        //echo $this->getCurrentState();
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
        if ($this->getCurrentState() == -1)
            $db->exec("INSERT INTO 'state' VALUES (" . $state . ");"); //@TODO change into ?
        else
            $db->exec("UPDATE state SET state = " . $state.";"); //@TODO change into ?
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
                if($_SERVER['REQUEST_METHOD'] == "POST") {
                    /* @var $db PDO */
                    $db = &$this->database;
                    
                    $gamename = $db->quote($_POST['name']);
                    $auto_p2p_interval = intval($_POST['p2p_interval']);
                    $auto_s2p_interval = intval($_POST['s2p_interval']);
                    if($auto_p2p_interval > 0 && $auto_s2p_interval > 0)
                    {
                        $result = $db->exec("INSERT INTO 'config' VALUES (" . $gamename . ",".$auto_p2p_interval.",".$auto_s2p_interval.");"); //TODO change into ?
                        if($result !== false){
                            $this->setState (POSTCONFIG);
                        }
                        else{
                            $this->handleError(new Error("db_error", "An error occured while trying to insert something into the database", true));
                            return;
                        }
                            
                    }
                    else
                    {
                        $this->handleError(new Error("config_input_error", "Please supply valid configuration inputs!", false));
                    }
                }

                break;
            case POSTCONFIG:


                break;
            case PREVPN:


                break;
            case PREGAMESTART:


                break;
            case GAMEINPROGRESS:


                break;
            case POSTGAME:


                break;
            default:
                break;
        }
    }

}

?>
