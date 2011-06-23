<?php
include_once 'config.php';
include_once 'lib/etc_funcs.php';

if(strcmp($_SERVER['REMOTE_ADDR'], '127.0.0.1') === 0 || ip_in_range($_SERVER['REMOTE_ADDR'], $config['admin_ip_range'])){
    if(isset($_GET['team']) && !ctype_alnum($_GET['team']) || !isset($_GET['team']))
            exit;

    $tmpname = tempnam("tmp", "tmpzip");
    $zip = new ZipArchive();
    $zip->open($tmpname, ZipArchive::OVERWRITE);
    $zip->addFile($config['RSA_location'] . "keys/Team" . $_GET['team'] . ".key","Team". $_GET['team'] . ".key");
    $zip->addFile($config['RSA_location'] . "keys/Team" . $_GET['team'] . ".crt","Team" . $_GET['team'] . ".crt");
    $zip->addFile($config['RSA_location'] . "keys/Team" . $_GET['team'] . "_vm.key","Team". $_GET['team'] . "_vm.key");
    $zip->addFile($config['RSA_location'] . "keys/Team" . $_GET['team'] . "_vm.crt","Team" . $_GET['team'] . "_vm.crt");
    $zip->addFile($config['openvpn_location'] ."Team". $_GET['team'] . "_client.conf","Team". $_GET['team'] . "_client.conf");
    $zip->addFile($config['openvpn_location'] ."Team". $_GET['team'] . "_client.conf","Team". $_GET['team'] . "_vm_client.conf");
    $zip->addFile($config['RSA_location'] . "keys/ca.crt","ca.crt");
    $zip->close();

    loadFile($tmpname, "KeysForTeam".$_GET['team']);
    @unlink($tmpname);
    exit;
}

function loadFile($file,$name){
    header('Content-Description: File Transfer');
    header('Content-Type: application/zip');
    header('Content-Disposition: attachment; filename="'.$name.'.zip"');
    header('Expires: 0');
    header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
    header('Pragma: public');
    header('Content-Length: ' . filesize($file));
    ob_clean();
    flush();
    readfile($file);
}

?>