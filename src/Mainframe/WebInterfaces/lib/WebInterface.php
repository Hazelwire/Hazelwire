<?php

/**
 * WebInterface contains the data and logic which is used for both the ContestantInterface and the AdminInterface.
 * It manages the database, the configs, Smarty, gamestate and BB parsing and is used to handle (i.e. collect) the Error which have occurred.
 *
 * @author Daniel
 */
class WebInterface {
    
    /* @var $this->database PDO */ 
    public      $database = null;
    protected   $db_ready = true;
    protected   $smarty;
    protected   $config;
    public      $gameConfig = null;
    protected   $contestant_list;
    
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


        $this->contestant_list = array();
        
        if($this->db_ready){ 
            $db = &$this->database; /* @var $db PDO */
            $q = $db->query("SELECT id FROM teams"); /* @var $q PDOStatement */
            
            foreach($q as $data){
                $c = Contestant::getById($data['id'], $db);
                if($c !== false)
                    array_push ($this->contestant_list, $c);
            }
            
            $this->gameConfig = new gameConfig($db); /* @var $gameConfig gameConfig */

        }
    }

    /**
     * Unbans all the contestants which bantime is over
     * @return void Nada, nothing ...
     */
    public function unban(){
        $q = $this->database->prepare("SELECT * FROM bans WHERE end_timestamp < ? AND end_timestamp!=-1");
        $q->execute(array(time()));
        foreach($q as $res){
            $c = Contestant::getById($res['team_id'], $this->database);
            exec("mv ".$this->config['site_folder']."lib/admin/openvpn/ccd/_Team".$c->getId(). "_vm ".$this->config['site_folder']."lib/admin/openvpn/ccd/Team".$c->getId()."_vm");

            $smarty = &$this->getSmarty();
            $tpl = $smarty->createTemplate("server.conf"); /* @var $tpl Smarty_Internal_Template */
            $tpl->assign("filename", "server_Team".$c->getId());
            $tpl->assign("path_to_rsa", $this->config['site_folder'] . $this->config['RSA_location']);
            $tpl->assign("path_to_openvpn", $this->config['site_folder'] . $this->config['openvpn_location']);
            $tpl->assign("server_ip_range",  $c->getSubnet());
            $tpl->assign("man_port",$this->config['management_port_base'] + $res['team_id']);
            $tpl->assign("port",$this->config['base_port'] + $res['team_id']);
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
            
            if(OpenVPNManager::getVPNStatus($c)){
                OpenVPNManager::diconnectVPN($c);
                OpenVPNManager::stopVPN($c);
                sleep(5);
            }
            OpenVPNManager::startVPN($c);

            $q = $this->database->prepare("DELETE FROM bans WHERE team_id = ?");
            $q->execute(array(intval($res['team_id'])));
        }
    }

    /**
     * Tries to connect to the database and returns if this was successful or not
     * @return boolean true if it succeeded, false otherwise
     */
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
    public function getCurrentState(){
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

    /**
     * Gets the config array for this WebInterface
     * @return mixed Returns the config arrayfor this Webinterface
     */
    public function getConfig(){
        return $this->config;
    }

    /**
     * Parses the BB code in the given text
     * @param string $text The text to parse the BB codes from
     * @return string The input text with the BB codes parsed
     */
    public static function parseBB($text){
        $bbcode = new StringParser_BBCode ();
        $bbcode->addFilter (STRINGPARSER_FILTER_PRE, 'convertlinebreaks');

        $bbcode->addParser (array ('block', 'inline', 'link', 'listitem'), 'htmlspecialchars');
        $bbcode->addParser (array ('block', 'inline', 'link', 'listitem'), 'nl2br');
        $bbcode->addParser (array ('block', 'inline', 'listitem'), 'autoParseLinks');
        $bbcode->addParser ('list', 'bbcode_stripcontents');

        $bbcode->addCode ('b', 'simple_replace', null, array ('start_tag' => '<b>', 'end_tag' => '</b>'),
                          'inline', array ('listitem', 'block', 'inline', 'link'), array ());
        $bbcode->addCode ('i', 'simple_replace', null, array ('start_tag' => '<i>', 'end_tag' => '</i>'),
                          'inline', array ('listitem', 'block', 'inline', 'link'), array ());
        $bbcode->addCode ('url', 'usecontent?', 'do_bbcode_url', array ('usecontent_param' => 'default'),
                          'link', array ('listitem', 'block', 'inline'), array ());
        $bbcode->addCode ('color', 'callback_replace', 'do_bbcode_color', array (),
                          'inline', array ('listitem', 'block', 'inline','link'), array ());
        $bbcode->addCode ('size', 'callback_replace', 'do_bbcode_size', array (),
                          'inline', array ('listitem', 'block', 'inline','link'), array ());
        $bbcode->addCode ('img', 'usecontent', 'do_bbcode_img', array (),
                          'image', array ('listitem', 'block', 'inline', 'link'), array ());
        $bbcode->addCode ('list', 'simple_replace', null, array ('start_tag' => '<ul>', 'end_tag' => '</ul>'),
                          'list', array ('block', 'listitem'), array ());
        $bbcode->addCode ('*', 'simple_replace', null, array ('start_tag' => '<li>', 'end_tag' => '</li>'),
                          'listitem', array ('list'), array ());
        $bbcode->setCodeFlag ('*', 'closetag', BBCODE_CLOSETAG_OPTIONAL);
        $bbcode->setCodeFlag ('*', 'paragraphs', true);
        $bbcode->setCodeFlag ('list', 'paragraph_type', BBCODE_PARAGRAPH_BLOCK_ELEMENT);
        $bbcode->setCodeFlag ('list', 'opentag.before.newline', BBCODE_NEWLINE_DROP);
        $bbcode->setCodeFlag ('list', 'closetag.before.newline', BBCODE_NEWLINE_DROP);
        //$bbcode->setRootParagraphHandling (true);

        return $bbcode->parse ($text);
    }
    
}

?>
