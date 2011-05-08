<?php
/**
 * Description of Contestant
 *
 * @author sjikke
 */
class Contestant {
    private $teamname;
    private $id;
    private $subnet;
    private $vm_ip;
    
    function __construct($name, $subnet, $vm_ip, $id = -1) {
        $this->teamname = $name;
        $this->subnet = $subnet;
        $this->vm_ip = $vm_ip;
        $this->id = $id;
    }
    
    /**
     * Saves the data of the current Contestant to the database. This
     * Can either be an insert, or an update. 
     * 
     * @param PDO $db The database the data should be saved in.
     */
    public function save(&$db){
        if($this->id == -1){
            $insert_q = $db->prepare("INSERT INTO teams(name,VMip,subnet) VALUES (?,?,?);");
            $insert_q->execute(array($this->teamname,$this->vm_ip,$this->subnet));
            $this->id = intval($db->lastInsertId());
        }else{
            $update_q = $db->prepare("UPDATE teams SET name = ?, VMip = ?, subnet = ? WHERE id = ?;");
            $update_q->execute(array($this->teamname,$this->vm_ip,$this->subnet,$this->id));
        }
    }
    
    /** 
     *
     * @param integer $id The ID of the Contestant to fetch
     * @param PDO $db The database in which the Contestant resides.
     * 
     * @return Contestant|bool Returns the Contestant with given ID. On failure returns false;
     */
    public static function getById($id, &$db){
        $q = $db->prepare("SELECT * FROM teams WHERE id = ?");
        $q->execute(array($id));
        $res = $q->fetchAll();
        if($res !== false && count($res) > 0){
            return new Contestant($res[0]['name'],$res[0]['VMip'],$res[0]['subnet'],$res[0]['id']);
        }
        return false;
    }

    /*
     * GETTERS AND SETTERS
     */

    public function getTeamname() {
        return $this->teamname;
    }

    public function getId() {
        return $this->id;
    }

    /**
     * Returns the CIDR subnet the current Contestant is in.
     * 
     * @return string The CIDR notation of the subnet this Contestant is using
     */
    public function getSubnet() {
        return $this->subnet;
    }

    public function getVm_ip() {
        return $this->vm_ip;
    }

    public function setTeamname($teamname) {
        $this->teamname = $teamname;
    }

    /**
     * Sets the CIDR (e.g. 10.0.0.0/24) notated subnet for the Contestant. <br>
     * Please note that this only saves it into the database, and is used for the webpages.
     * The openVPN is not updated.
     * 
     * @param string $subnet The CIDR notation of the subnet to be allocated to the Contestant
     */
    public function setSubnet($subnet) {
        $this->subnet = $subnet;
    }

    public function setVm_ip($vm_ip) {
        $this->vm_ip = $vm_ip;
    }
    
}

?>
