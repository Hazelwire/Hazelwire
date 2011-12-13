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
 * OpenVPNManager manages the the VPN servers and their keys. It can build keys, start and stop servers etc.
 * 
 * @author sjikke
 */
class OpenVPNManager {

    /**
     * Build the initial keys for the VPN, i.e. the CA keys and the DH keys
     * @global WebInterface $interface The WebInterface to work with (i.e. get data from)
     */
    public static function buildInitKeys() {
        global $interface;
        $config =$interface->getConfig();
        $pwd = getcwd();
        chdir($config['RSA_location']);
        shell_exec(". ./vars && ./clean-all && ./pkitool --initca");
        shell_exec(". ./vars && ./build-dh > /dev/null 2>/dev/null &");
        
        chdir($pwd);
    }

    /**
     * Build VPNserver keys with the given common name.
     * @global WebInterface $interface The WebInterface to work with (i.e. get data from)
     * @param string $cname The common name to create the keys with
     */
    public static function buildServerKeys($cname) {
        global $interface;
        $config =$interface->getConfig();
        
        $pwd = getcwd();
        chdir($config['RSA_location']);
        
        shell_exec(". ./vars && ./pkitool --server server_" . $cname);
        
        chdir($pwd);
    }

    /**
     * Creates VPN client keys with a certain Common Name. Note that the original common name is used for 1 key pair, and
     * the Common Name with '_vm' appended for a second pair.
     * @global WebInterface $interface The WebInterface to work with (i.e. get data from)
     * @param string $cname The Common Name to use for the keys
     */
    public static function buildClientKeys($cname){
        global $interface;
        $config =$interface->getConfig();
        
        $pwd = getcwd();
        chdir($config['RSA_location']);
        
        shell_exec(". ./vars && ./pkitool " . $cname . "_vm");
        shell_exec(". ./vars && ./pkitool " . $cname);
        
        chdir($pwd);
    }

    /**
     * Generates Client Config Files for a team (keys) with the given Common name. Again
     * both 'cn' and 'cn_vm', where cn is the common name, is used. These files are needed for
     * Authenthication with the VPN server
     * @global WebInterface $interface The WebInterface to work with (i.e. get data from)
     * @param string $cname The Common Name for the key for which the CCF should be created
     * @param string $vmip The IP of the Client side
     * @param string $vmip_endpoint The IP of the tunnel endpoint on the server side
     */
    public static function createClientConfigFile($cname, $vmip, $vmip_endpoint){
        global $interface;
        $config =$interface->getConfig();
        deleteAll($config['openvpn_location'] . "ccd/" . $cname);
        
        mkdir($config['openvpn_location'] . "ccd/" . $cname);
        exec(sprintf("echo \"ifconfig-push %s %s\" > %s", $vmip, $vmip_endpoint, $config['openvpn_location'] . "ccd/" . $cname . "/" . $cname . "_vm"));
        exec(sprintf("echo \" \" > %s", $config['openvpn_location'] . "ccd/" . $cname . "/" . $cname));
    }
    
    /** 
     * Tries to start a VPN server from a given contestant
     *
     * @global WebInterface $interface The Interface that handles the errors
     * @param Contestant $contestant The Contestant from whom the VPN must start
     */
    public static function startVPN(&$contestant){
        global $interface; /* @var $interface WebInterface */
        $config = $interface->getConfig();
        $fp = @fsockopen("127.0.0.1", 10000, $errno, $errstr, 5);
        if(!$fp){
            $interface->handleError(new Error("vpn_error", "Error #1: Cannot start openVPN service! (".$errno.")", false));
        }else{
            $config_path = $config['public_site_folder'] . $config['openvpn_location'] . "Team" . $contestant->getId() . ".conf";
            fwrite($fp, "STARTVPN " . $config_path);
            fclose($fp);
            // @todo test if start failed
        }
    }

    /**
     * Starts the VPN server for the given contestant
     * @global WebInterface $interface The WebInterface to work with (i.e. get data from)
     * @param Contestant $contestant The contestant whose VPN needs starting
     */
    public static function stopVPN(&$contestant){
        global $interface; /* @var $interface WebInterface */
        $config =$interface->getConfig();
        $fp = @fsockopen("127.0.0.1", $config['management_port_base'] + $contestant->getId(), $errno, $errstr, 2);
        if(!$fp){
            $interface->handleError(new Error("vpn_error", "Error #2: Cannot stop openVPN service! (".$errno.")", false));
        }else{
            fwrite($fp, "signal SIGTERM\r\n");
            fclose($fp);
        }
    }

    /**
     * Disconnects the clients from the VPN server for the given Contestant
     * @global WebInterface $interface The WebInterface to work with (i.e. get data from)
     * @param Contestant $contestant The Contestant which needs the clients disconnected
     */
    public static function diconnectVPN(&$contestant){
        global $interface; /* @var $interface WebInterface */
        $config = $interface->getConfig();
        $fp = @fsockopen("127.0.0.1", $config['management_port_base'] + $contestant->getId(), $errno, $errstr, 5);
        if(!$fp){
            $interface->handleError(new Error("vpn_error", "Error #9: Cannot d/c client! (".$errno.")", false));
        }else{
            fwrite($fp, "kill Team".$contestant->getId()."\r\n");
            fwrite($fp, "kill Team".$contestant->getId()."_vm\r\n");
            fclose($fp);
        }
    }
    
   /**
    * Gets the VPN server status for the given Contestant.
    * <br> It should be noted that this only checks if the process is running, nothing more, nothing less.
    * @global WebInterface $interface The WebInterface to work with (i.e. get data from)
    * @param <type> $contestant The contestant to check the VPN server status for
    * @return boolean true if the server is running, false otherwise
    */
    public static function getVPNStatus(&$contestant){
        global $interface; /* @var $interface WebInterface */
        $config =$interface->getConfig();
        $config_path = $config['public_site_folder'] . $config['openvpn_location'] . "Team".$contestant->getId() . ".conf";
        $result = exec(sprintf("(ps -e -o pid,args | grep 'openvpn %s') | sed '/ grep /d'", $config_path));
        return ($result != "");
    }

    /**
     * Gets the status of the base VPN server
     * <br> It should be noted that this only checks if the process is running, nothing more, nothing less.
     * @global WebInterface $interface The WebInterface to work with (i.e. get data from)
     * @return boolean true if the base VPN server is running, false otherwise
     */
    public static function getBaseVPNStatus(){
        global $interface; /* @var $interface WebInterface */
        $config =$interface->getConfig();
        $config_path = $config['public_site_folder'] . $config['openvpn_location'] . "basevpn.conf";
        $result = exec(sprintf("(ps -e -o pid,args | grep 'openvpn %s') | sed '/ grep /d'", $config_path));
        return ($result != "");
    }
    
    /** 
     * Starts or stops kernel routing on the machine, usgin OpenVPNService.py
     * @global WebInterface $interface The WebInterface to use for errorhandling and such
     * @param boolean $value true if kernel routing should be enabled, false otherwise.
     */
    public static function setKernelRouting($value){
        global $interface; /* @var $interface WebInterface */
        $fp = @fsockopen("127.0.0.1", 10000, $errno, $errstr, 5);
        if(!$fp){
                $interface->handleError(new Error("start_game_error", "Error #1: Cannot start kernel routing! (".$errno.")", false));
        }else{
            if($value){
                fwrite($fp, "STARTKROUTING");
            }else{
                fwrite($fp, "STOPKROUTING");
            }
            fclose($fp);
        }
        
    }

    /**
     * Creates the files necessary for the base VPN server <br.
     * This server is used for makeing it easier for the VPN clients to connect to the mainframe.
     * Especially the ClientBot needs this in order to request flags. Not using this causes everyone to need a different IP address
     * for the Mainframe, while it is now reachable through 10.0.0.1
     * @global WebInterface $interface The WebInterface to use for errorhandling and such
     */
    public static function createBaseVPNServer(){
        global $interface;
        $config =$interface->getConfig();

        $pwd = getcwd();
        chdir($config['RSA_location']);

        shell_exec(". ./vars && ./pkitool --server basevpn");

        chdir($pwd);
    }

    /**
     * Starts the base VPN server
     * @global WebInterface $interface The WebInterface to use for errorhandling and such
     */
    public static function startBaseVPN(){
        global $interface; /* @var $interface WebInterface */
        $config = $interface->getConfig();
        $fp = @fsockopen("127.0.0.1", 10000, $errno, $errstr, 5);
        if(!$fp){
            $interface->handleError(new Error("vpn_error", "Error #1: Cannot start openVPN service! (".$errno.")", false));
        }else{
            $config_path = $config['public_site_folder'] . $config['openvpn_location'] . "basevpn.conf";
            fwrite($fp, "STARTVPN " . $config_path);
            fclose($fp);
            // @todo test if start failed
        }
    }

    /**
     * Stops the base VPN server
     * @global WebInterface $interface The WebInterface to use for errorhandling and such
     */
    public static function stopBaseVPN(){
        global $interface; /* @var $interface WebInterface */
        $config =$interface->getConfig();
        $fp = @fsockopen("127.0.0.1", $config['management_port_base'], $errno, $errstr, 5);
        if(!$fp){
            $interface->handleError(new Error("vpn_error", "Error #2: Cannot stop openVPN service! (".$errno.")", false));
        }else{
            fwrite($fp, "signal SIGTERM\r\n");
            fclose($fp);
        }
    }

    /**
     * Looks up the number of connections to the VPN server through the VPN management interface
     * @global WebInterface $interface The WebInterface to use for errorhandling and such
     * @param Contestant $contestant The Contestants whose number of connections to find
     * @return int The number of connections the VPN server for the given Contestant lists
     */
    public static function getNumConnForContestant(&$contestant){
        global $interface; /* @var $interface WebInterface */

        $q = $interface->database->prepare("SELECT * FROM cache WHERE cache_id = ?");
        $q->execute(array('vpn_conn_data'));
        if(($res = $q->fetch())!= FALSE){
            if($res['lifetime_end'] > time()){
                $cache_data = json_decode($res['data']);
                $arr_data = (array)$cache_data;
                return array_key_exists("Team".$contestant->getId(), $arr_data)?$arr_data["Team".$contestant->getId()]:0;
            }
        }

        $conn_data = array();
        $config =$interface->getConfig();
        $lines = array();
        $fp = @fsockopen("127.0.0.1", $config['management_port_base'] + $contestant->getId(), $errno, $errstr, 5);
        if(!$fp){
            $interface->handleError(new Error("vpn_error", "Error #1337: Cannot fetch number of connections to openVPN service! (".$errno.")", false));
            return false;
        }else{
            fwrite($fp, "status\r\n");
            while(!startsWith(($line = fgets($fp)),"END")){
                $lines[] = $line;
            }
            fwrite($fp, "quit\n");
            fclose($fp);
        }

        $contestants = $interface->get_contestant_list();

        foreach($contestants as $c){
            $conn_data["Team".$c->getId()] = 0;
            foreach ($lines as $line) {
                if(startsWith($line, "Team".$c->getId().",") || startsWith($line, "Team".$c->getId()."_vm,")){
                        $conn_data["Team".$c->getId()]++;
                    }

            }
        }
        
        if($res == FALSE){
            $q = $interface->database->prepare( "INSERT INTO cache VALUES (?,?,?)");
            $q->execute(array("vpn_conn_data",time()+2*60, json_encode((object) $conn_data)));
        }
        else {
            $q = $interface->database->prepare( "UPDATE cache SET lifetime_end = ?, data = ? WHERE cache_id = ?");
            $q->execute(array(time()+2*60, json_encode((object) $conn_data),"vpn_conn_data"));
        }

        return $conn_data["Team".$contestant->getId()];

    }
    
}

?>
