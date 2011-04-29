<?php

/**
 * Description of WebInterface
 *
 * @author Daniel
 */
class WebInterface {
    
    /* @var $this->database PDO */
    public $database = null;
    protected $db_ready = true;
    protected $smarty;
    protected $config;
    
    protected $fatal_error = false;
    protected $errors = null;
    
    function WebInterface(){
        global $config;
        $this->config = $config;
        
        if(file_exists($this->config['database_file_name']))
            $this->connectDB();
        else
            $this->db_ready = false;
        
        $this->fatal_error = false;
        $this->errors = array();

        $this->smarty = new Smarty();
        $this->smarty->template_dir = $this->config['site_folder']. 'templates/';
        $this->smarty->compile_dir = $this->config['site_folder'].  'templates_c/';
        $this->smarty->config_dir = $this->config['site_folder'].   'configs/';
        $this->smarty->cache_dir = $this->config['site_folder'].    'cache/';
        $this->smarty->addPluginsDir("lib/smartyPlugins");
    }
    public function connectDB() {
        if($this->database == null){
            return $this->database = new PDO ("sqlite:" . $this->config['database_file_name']);
        }
        else
            return false;
    }
    
    /**
     * Return a reference to the Smarty object the Webinterface works with
     * @return Smarty The Smarty object the Webinterface is working with 
     */
    public function &getSmarty(){
        return $this->smarty;
    }
    
    
    /**
     * Returns the current state of the wargame
     * @return integer Returns -1 on no state found, otherwise returns state as defined in index.php
     */
    protected function getCurrentState(){
        $db =& $this->database;
        /* @var $db PDO */
        /* @var $resobj PDOStatement */
        $resobj = $db->query("SELECT * FROM 'state';");
        if(($res = $resobj->fetch()) != FALSE)
            return $res['state'];
        else 
            return -1;
    }
    
    /**
     * Adds the Error to the list with errors IFF this error is the first fatal OR there are no fatal errors
     * and the given error is not fatal.
     * This list of errors is used to give error descriptions to Smarty for displaying them to the user.
     * The error's type will be used for naming the Smarty variable, which holds the error's description.
     * @param Error $error The error that needs handling
     */
    public function handleError($error){
        if($error->isFatal() && !$this->fatal_error){
            $this->errors = array();
            array_push($this->errors, $error);
            $this->fatal_error = true;
        } else if( !$error->isFatal() && !$this->fatal_error){
            array_push($this->errors,$error);
        }
    }
    
    public function getConfig(){
        return $this->config;
    }
}

?>
