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
    private $points;
    private $is_flag_blocked;
    private $block_time;
    private $banned;
    private $bantime;
    private $sane = true;
    
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
            $q=$db->query("select * from team_id");
            $res = $q->fetch();
            $id = $res[0];
            $q=$db->prepare("update team_id set id=?");
            $q->execute(array($id+1));
            $insert_q = $db->prepare("INSERT INTO teams(id,name,VMip,subnet) VALUES (?,?,?,?);");
            $insert_q->execute(array($id,$this->teamname,$this->vm_ip,$this->subnet));
            $this->id = $id;
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
            $result =  new Contestant($res[0]['name'],$res[0]['subnet'],$res[0]['VMip'],$res[0]['id']);
            
            //collect flag submission block information.
            $q = $db->prepare("SELECT * FROM submission_block WHERE team_id = ? ORDER BY block_timestamp DESC LIMIT 0,1");
            $q->execute(array($id));
            $res = $q->fetch();
            if($res == false || time() > $res['block_timestamp']){
                $result->is_flag_blocked = false;
                $result->is_flag_blocked = 0;
            } else {
                $result->is_flag_blocked = true;
                $result->block_time = $res['block_timestamp'];
            }
            
            //collect ban information
            $q = $db->prepare("SELECT m.name, e.timestamp, e.reporter FROM evil_teams e INNER JOIN modules m ON m.serviceport = e.port INNER JOIN teams t ON t.VMip = e.ip WHERE e.ip = ? AND e.timestamp > ? ORDER BY e.timestamp DESC");
            // Only select the notification which are 'recent'
            $q->execute(array($result->getVm_ip(), time() -10*60 )); // @TODO make configurable or make seen boolean

            if($q->fetch() !== false)
                $this->sane = false;

            $q = $db->prepare("SELECT * FROM bans WHERE team_id = ? AND (end_timestamp > ? OR end_timestamp=-1) ORDER BY end_timestamp DESC");
            $q->execute(array($result->getId(),time()));
            $res = $q->fetch();
            if($res !== false){
                if($res['end_timestamp'] == -1){
                    $result->banned = true;
                    $result->bantime= "-1";
                }else{
                    $result->banned = true;
                    $result->bantime = round((intval($res['end_timestamp']) - time())/60);
                    if($result->bantime <0){
                        $result->banned = false;
                        $result->bantime= "-";
                    }
                }
            }else{
                $result->banned = false;
                $result->bantime= "-";
            }
            
            $q = $db->prepare("SELECT ifnull(sum(points),0) as sum FROM scores WHERE team_id = ?");
            $q->execute(array($id));
            $res = $q->fetch();
            $result->setPoints($res['sum']);


            
            return $result;
        }
        return false;
    }
    
    /** 
     * Wrappert voor OpenVPNManager's getVPNStatus(...)
     * 
     * @return boolean true als VPN draait voor deze contestant, anders false.
     */
    public function getVPNStatus(){
        return OpenVPNManager::getVPNStatus($this);
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

    public function getPoints() {
        return $this->points;
    }

    public function setPoints($points) {
        $this->points = $points;
    }
    
    public function isFlagSubmissionBlocked(){
        return $this->is_flag_blocked;
    }
    
    public function getBlockTime(){
        return $this->block_time;
    }

    public function getBanned() {
        return $this->banned;
    }
    
    public function getBantime() {
        return $this->bantime;
    }

    public function getOffline() {
        return !ping($this->getVm_ip());
    }

    public function getSane() {
        return $this->sane;
    }


}

?>
