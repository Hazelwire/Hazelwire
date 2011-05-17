<?php
include_once 'config.php';
include_once 'lib/etc_funcs.php';

if(strcmp($_SERVER['REMOTE_ADDR'], '127.0.0.1') === 0 || ip_in_range($_SERVER['REMOTE_ADDR'], $config['admin_ip_range'])){
    if(isset($_GET['team']) && !ctype_alnum($_GET['team']))
            exit;
    if(isset($_GET['team']) && isset($_GET['type']) && $_GET['type'] == "tkey"){
        
        $file = $config['RSA_location'] . "keys/" . $_GET['team'] . ".key";
        
        if(!file_exists($file))
            exit;
        
        loadFile($file);
        
    }elseif(isset($_GET['team']) && isset($_GET['type']) && $_GET['type'] == 'tcert'){
        
        $file = $config['RSA_location'] . "keys/" . $_GET['team'] . ".crt";
        
        if(!file_exists($file))
            exit;
        
        loadFile($file);    
        
    }elseif(isset($_GET['team']) && isset($_GET['type']) && $_GET['type'] == 'vkey'){
        $file = $config['RSA_location'] . "keys/" . $_GET['team'] . "_vm.key";
        
        if(!file_exists($file))
            exit;
        
        loadFile($file);
        
    }elseif(isset($_GET['team']) && isset($_GET['type']) && $_GET['type'] == 'vcert'){
        
        $file = $config['RSA_location'] . "keys/" . $_GET['team'] . "_vm.crt";
        
        if(!file_exists($file))
            exit;
        
        loadFile($file);
    }elseif(isset($_GET['type']) && $_GET['type'] == 'ca'){
        
        $file = $config['RSA_location'] . "keys/ca.crt";
        
        if(!file_exists($file))
            exit;
        
        loadFile($file);
    }elseif(isset($_GET['team']) && isset($_GET['type']) && $_GET['type'] == 'vpnconf'){

        $file = $config['openvpn_location'] . $_GET['team'] . "_client.conf";

        if(!file_exists($file))
            exit;

        loadFile($file);
    }
    
}

function loadFile($file){
    header('Content-Description: File Transfer');
    header('Content-Type: application/octet-stream');
    header('Content-Disposition: attachment; filename='.basename($file));
    header('Content-Transfer-Encoding: binary');
    header('Expires: 0');
    header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
    header('Pragma: public');
    header('Content-Length: ' . filesize($file));
    ob_clean();
    flush();
    readfile($file);
    exit;
}

?>