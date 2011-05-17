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
        
        exec(sprintf("echo \"ifconfig-push %s %s\" > %s", $vmip, $vmip_endpoint, $config['openvpn_location'] . "ccd/" . $cname . "_vm"));
        exec(sprintf("echo \" \" > %s", $config['openvpn_location'] . "ccd/" . $cname));
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
            $config_path = $config['site_folder'] . $config['openvpn_location'] . $contestant->getTeamname() . ".conf";
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
     * @todo Test this
     * @param Contestant $contestant 
     */
    public static function getVPNStatus(&$contestant){
        global $interface; /* @var $interface WebInterface */
        $config =$interface->getConfig();
        $config_path = $config['site_folder'] . $config['openvpn_location'] . $contestant->getTeamname() . ".conf";
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
    
    
    
}

?>
