
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
 
// IP ranges from which admin(s) will connect, separated with comma's (no spaces!)
//$config['admin_ip_range'] = '192.168.0.0/16';
$config['admin_ip_range'] = '127.0.0.01/32,130.89.224.172/0';

// name of the database file (perhaps location?)
$config['database_file_name'] = '../Hazelwire.sqlite';

// The path to the root folder of the webinterfaces
//$config['site_folder'] = '/var/www/WebInterfaces/';
$config['site_folder'] = dirname(__DIR__)."/";

$config['public_site_folder'] = __DIR__."/";



$config['RSA_location'] = '../lib/admin/rsa/';
$config['openvpn_location'] = '../lib/admin/openvpn/';

// ClientHandler location
$config['ch_location'] = $config['public_site_folder']."../../ClientHandler/";

$config['base_port'] = 40000;

$config['management_port_base'] = 45000;
?>
