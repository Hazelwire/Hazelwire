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
            $smarty->assign("errors", array_merge($this->errors, $errors_by_name));
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
            return $smarty->fetch("add_contestants.tpl");
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
                    $manifest = $_FILES['manifest'];
                    $temp = explode(".", $manifest['name']);
                    $ext = $temp[count($temp) - 1];

                    if (strcmp($ext, "xml") != 0) {
                        $this->handleError(new Error("config_input_error", "You can only upload XML files!", false));
                        return;
                    } elseif ($auto_p2p_interval > 0 && $auto_s2p_interval > 0 && $manifest['error'] === 0) {

                        //@TODO, use maartens script to parse XML
                        $q = $db->prepare("INSERT INTO 'config' VALUES (?,?,?);");
                        /* @var $q PDOStatement */
                        $result = $q->execute(array($gamename, $auto_p2p_interval, $auto_s2p_interval));

                        if ($result !== false) {
                            $this->setState(POSTCONFIG);
                        } else {
                            $this->handleError(new Error("db_error", "An error occured while trying to insert something into the database", true));
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
                    if (isset($_POST['button']) && strtolower($_POST['button']) == 'next') {

                        /* @var $result PDOStatement */
                        $result = $db->query("SELECT COUNT(*) FROM teams");

                        if ($result->fetchColumn() > 0) {
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
                        $this->handleError(new Error("fatal_error", "Failure FTW", true));
                    }
                }
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
