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

include_once 'config.php';
include_once '../lib/etc_funcs.php';

if(ip_in_range($_SERVER['REMOTE_ADDR'], explode(',',$config['admin_ip_range']))){
    if(isset($_GET['team']) && !ctype_alnum($_GET['team']) || !isset($_GET['team']))
            exit;

    $tmpname = tempnam("tmp", "tmpzip");
    $zip = new ZipArchive();
    $zip->open($tmpname, ZipArchive::OVERWRITE);
    $zip->addFile($config['RSA_location'] . "keys/Team" . $_GET['team'] . ".key","Team". $_GET['team'] . "_team.key");
    $zip->addFile($config['RSA_location'] . "keys/Team" . $_GET['team'] . ".crt","Team" . $_GET['team'] . "_team.crt");
    $zip->addFile($config['RSA_location'] . "keys/Team" . $_GET['team'] . "_vm.key","Team". $_GET['team'] . "_vm.key");
    $zip->addFile($config['RSA_location'] . "keys/Team" . $_GET['team'] . "_vm.crt","Team" . $_GET['team'] . "_vm.crt");
    $zip->addFile($config['openvpn_location'] ."Team". $_GET['team'] . "_client.conf","Team". $_GET['team'] . "_team.conf");
    $zip->addFile($config['openvpn_location'] ."Team". $_GET['team'] . "_vm_client.conf","Team". $_GET['team'] . "_vm.conf");
    $zip->addFile($config['RSA_location'] . "keys/ca.crt","ca.crt");
    $zip->close();

    loadFile($tmpname, "KeysForTeam".$_GET['team']);
    @unlink($tmpname);
    exit;
}

/**
 * Outputs a file
 * @param string $file The file to output, including the path to it.
 * @param string $name The name of the file, i.e. the basename exluding the extension
 */
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