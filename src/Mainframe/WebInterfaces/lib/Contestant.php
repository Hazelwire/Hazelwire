<?php
/*******************************************************************************
 * Copyright (c) 2011 The Hazelwire Team.
 *     
 * This file is part of Hazelwire.
 * 
 * Hazelwire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Hazelwire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
 
/**
 * Contestant models a contesant in the wargame. It contains all the information
 * the system needs about the contestant. A Contestant initially does not have an
 * ID, but gets this once saved for the first time.
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
    private $bantime_full = 0;
    private $sane = true;

    /**
     * Creates a new Contestant using the given information.
     * 
     * @param string $name The name of the contestant
     * @param string $subnet The subnet of the contestant in CIDR notation (e.g. 10.1.1.0/24) which the players can use.
     * @param string $vm_ip The IP of the Virtual Machine of the Contestant in decimal dotted notation.
     * @param int $id The id of the Contestant. This is only set when it is already stored in the database.
     */
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
     * Tries to get a Contestant from the database with the given ID.
     * 
     * @param int $id The ID of the Contestant to fetch
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
                $result->sane = false;

            $q = $db->prepare("SELECT * FROM bans WHERE team_id = ? AND (end_timestamp > ? OR end_timestamp=-1) ORDER BY end_timestamp DESC");
            $q->execute(array($result->getId(),time()));
            $res = $q->fetch();
            if($res !== false){
                if($res['end_timestamp'] == -1){
                    $result->banned = true;
                    $result->bantime= "-1";
                    $result->bantime_full = -1;
                }else{
                    $result->banned = true;
                    $result->bantime_full = (intval($res['end_timestamp']) - time());
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

    public function getBantime_full() {
        return $this->bantime_full;
    }

    /** 
     * Wrapper for OpenVPNManager's getVPNStatus(...)
     * 
     * @return boolean true if VPN is running for this contestant, otherwise false.
     */
    public function getVPNStatus(){
        return OpenVPNManager::getVPNStatus($this);
    }

    /**
     * Fetches the last n sanity check results for the current Contestant
     * @param integer $n The number of results to fetch
     */
    public function getSanityResults($n){
        return AdminInterface::getSanityResultsForContestant($n,$this);
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

    /**
     * Returns the time this Contestant is still blocked from submitting a flag, due to spamming behaviour
     * @return integer The time this Contestant is still blocked from flag submissions
     */
    public function getBlockTime(){
        return $this->block_time;
    }

    public function getBanned() {
        return $this->banned;
    }
    
    public function getBantime() {
        return $this->bantime;
    }

    /**
     * Returns whether this Contestant is offline or not.
     * @return boolean True if this Contestants's VM is offline, false otherwise.
     */
    public function getOffline() {
        return !ping($this->getVm_ip());
    }

    /**
     * Returns whether this Contestant is sane or not
     * @return boolean true if sane, otherwise false
     */
    public function getSane() {
        return $this->sane;
    }

    /**
     * Returns the number of connections to the VPN server for this Contestant
     * @return integer The number of connections to the VPN server
     */
    public function getNumVPNConn(){
        return OpenVPNManager::getNumConnForContestant($this);
    }

}

?>
