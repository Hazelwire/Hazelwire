<?php
/**
 * Description of OpenVPNManager
 *
 * @author sjikke
 */
class OpenVPNManager {
    
    private $path_to_rsa;
    private $path_to_openvpn;
    private $basepath;


    function __construct($rsa_path, $openvpn_path, $basepath) {
        $this->path_to_rsa = $rsa_path;
        $this->path_to_openvpn = $openvpn_path;
        $this->basepath = $basepath;
    }
    
    public function buildInitKeys() {
        $pwd = getcwd();
        chdir($this->path_to_rsa);
        shell_exec(". ./vars && ./clean-all && ./pkitool --initca");
        shell_exec(". ./vars && ./build-dh > /dev/null 2>/dev/null &");
        
        chdir($pwd);
    }
    
    public function buildServerKeys($cname) {
        $pwd = getcwd();
        chdir($this->path_to_rsa);
        
        shell_exec(". ./vars && ./pkitool --server server_" . $cname);
        
        chdir($pwd);
    }
    
    public function buildClientKeys($cname){
        $pwd = getcwd();
        chdir($this->path_to_rsa);
        
        shell_exec(". ./vars && ./pkitool " . $cname . "_vm");
        shell_exec(". ./vars && ./pkitool " . $cname);
        
        chdir($pwd);
    }
    
    public function createClientConfigFile($cname, $vmip, $vmip_endpoint){
        exec(sprintf("echo \"ifconfig-push %s %s\" > %s", $vmip, $vmip_endpoint, $this->path_to_openvpn . "ccd/" . $cname . "_vm"));
        exec(sprintf("echo \" \" > %s", $this->path_to_openvpn . "ccd/" . $cname));
    }
    
}

?>
