<?php
/**
 * Description of OpenVPNManager
 * 
 * @author sjikke
 */
class OpenVPNManager {

    public static function buildInitKeys() {
        global $interface;
        $config =$interface->getConfig();
        $pwd = getcwd();
        chdir($config['RSA_location']);
        shell_exec(". ./vars && ./clean-all && ./pkitool --initca");
        shell_exec(". ./vars && ./build-dh > /dev/null 2>/dev/null &");
        
        chdir($pwd);
    }
    
    public static function buildServerKeys($cname) {
        global $interface;
        $config =$interface->getConfig();
        
        $pwd = getcwd();
        chdir($config['RSA_location']);
        
        shell_exec(". ./vars && ./pkitool --server server_" . $cname);
        
        chdir($pwd);
    }
    
    public static function buildClientKeys($cname){
        global $interface;
        $config =$interface->getConfig();
        
        $pwd = getcwd();
        chdir($config['RSA_location']);
        
        shell_exec(". ./vars && ./pkitool " . $cname . "_vm");
        shell_exec(". ./vars && ./pkitool " . $cname);
        
        chdir($pwd);
    }
    
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
            $config_path = $config['site_folder'] . $config['openvpn_location'] . "Team" . $contestant->getId() . ".conf";
            fwrite($fp, "STARTVPN " . $config_path);
            fclose($fp);
            // @todo test if start failed
        }
    }
    
    public static function stopVPN(&$contestant){
        global $interface; /* @var $interface WebInterface */
        $config =$interface->getConfig();
        $fp = @fsockopen("127.0.0.1", $config['management_port_base'] + $contestant->getId(), $errno, $errstr, 5);
        if(!$fp){
            $interface->handleError(new Error("vpn_error", "Error #2: Cannot stop openVPN service! (".$errno.")", false));
        }else{
            fwrite($fp, "signal SIGTERM\r\n");
            fclose($fp);
        }
    }

    /**
     *
     * @global WebInterface $interface
     * @param Contestant $contestant
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
     * @todo Test this
     * @param Contestant $contestant 
     */
    public static function getVPNStatus(&$contestant){
        global $interface; /* @var $interface WebInterface */
        $config =$interface->getConfig();
        $config_path = $config['site_folder'] . $config['openvpn_location'] . "Team".$contestant->getId() . ".conf";
        $result = exec(sprintf("(ps -e -o pid,args | grep 'openvpn %s') | sed '/ grep /d'", $config_path));
        return ($result != "");
    }

    public static function getBaseVPNStatus(){
        global $interface; /* @var $interface WebInterface */
        $config =$interface->getConfig();
        $config_path = $config['site_folder'] . $config['openvpn_location'] . "basevpn.conf";
        $result = exec(sprintf("(ps -e -o pid,args | grep 'openvpn %s') | sed '/ grep /d'", $config_path));
        return ($result != "");
    }
    
    /** 
     *
     * @global WebInterface $interface
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

    public static function createBaseVPNServer(){
        global $interface;
        $config =$interface->getConfig();

        $pwd = getcwd();
        chdir($config['RSA_location']);

        shell_exec(". ./vars && ./pkitool --server basevpn");

        chdir($pwd);
    }

    public static function startBaseVPN(){
        global $interface; /* @var $interface WebInterface */
        $config = $interface->getConfig();
        $fp = @fsockopen("127.0.0.1", 10000, $errno, $errstr, 5);
        if(!$fp){
            $interface->handleError(new Error("vpn_error", "Error #1: Cannot start openVPN service! (".$errno.")", false));
        }else{
            $config_path = $config['site_folder'] . $config['openvpn_location'] . "basevpn.conf";
            fwrite($fp, "STARTVPN " . $config_path);
            fclose($fp);
            // @todo test if start failed
        }
    }

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
     *
     * @global WebInterface $interface
     * @param Contestant $contestant
     */
    public static function getNumConnForContestant(&$contestant){
        global $interface; /* @var $interface WebInterface */
        $retval = 0;
        $config =$interface->getConfig();
        $fp = @fsockopen("127.0.0.1", $config['management_port_base'] + $contestant->getId(), $errno, $errstr, 5);
        if(!$fp){
            $interface->handleError(new Error("vpn_error", "Error #1337: Cannot fetch number of connections to openVPN service! (".$errno.")", false));
            return false;
        }else{
            fwrite($fp, "status\r\n");
            while(!startsWith(($line = fgets($fp)),"END")){
                if(startsWith($line, "Team".$contestant->getId())){
                    $retval++;
                }
            }
            fwrite($fp, "quit\n");
            fclose($fp);
            return $retval;
        }
    }
    
}

?>
