<?php
/**
 * This class holds the config wich was entered in the Config phase of the wargame
 *
 * @author Daniel
 */
class GameConfig {
    private $_db;
    
    public $gamename;
    public $server_ip;
    
    public $p2p_interval;
    public $normal_interval;
    
    public $points_decay_mod;
    public $points_min;
    public $points_penalty_mod;
    
    public $penalty_offline;
    
    /**
     *
     * @param PDO $db The database this GameConfig should fetch it's info from
     */
    function __construct(& $db) {
        $this->_db = $db;
        
        $q = $db->prepare("SELECT value FROM config WHERE config_name = ?");
        try {
            $q->execute(array('gamename'));
            $res = $q->fetch();
            $this->gamename = $res[0];
            $q->execute(array('server_ip'));
            $res = $q->fetch();
            $this->server_ip = $res[0];

            $q->execute(array('p2p_interval'));
            $res = $q->fetch();
            $this->p2p_interval = intval($res[0]);
            $q->execute(array('normal_interval'));
            $res = $q->fetch();
            $this->normal_interval = intval($res[0]);

            $q->execute(array('points_decay_mod'));
            $res = $q->fetch();
            $this->points_decay_mod = doubleval($res[0]);
            $q->execute(array('points_min'));
            $res = $q->fetch();
            $this->points_min = intval($res[0]);
            $q->execute(array('point_penalty_mod'));
            $res = $q->fetch();
            $this->points_penalty_mod = doubleval($res[0]);

            $q->execute(array('penalty_offline'));
            $res = $q->fetch();
            $this->penalty_offline = intval($res[0]);
        } catch (OutOfBoundsException $e){
            global $interface; /* @var $interface WebInterface */
            if($interface->getCurrentState() != preConfig){
                $interface->handleError(new Error("config_input_error", "Error: not all the required settings are in place, while you already passed the config state!<br />\nStarting over (i.e. deleting the DB file) _might_ work.", true));
            }
        }
    }

    /**
     * Saves the config data into the database
     */
    public function save(){
        $db = &$this->db; /* @var $db PDO */
        
        $update = $db->prepare("UPDATE config SET value = ? WHERE config_name = ?"); /* @var $update PDOStatement */

        $update->execute(array($this->gamename, 'gamename'));
        $update->execute(array($this->server_ip, 'server_ip'));


        $update->execute(array($this->p2p_interval, 'p2p_interval'));
        $update->execute(array($this->normal_interval, 'normal_interval'));
        $update->execute(array($this->points_decay_mod , 'points_decay_mod'));
        $update->execute(array($this->points_min , 'points_min'));
        $update->execute(array($this->points_penalty_mod , 'point_penalty_mod'));
        $update->execute(array($this->penalty_offline , 'penalty_offline'));
        
    }
}

?>
