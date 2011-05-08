<?php
// IP range from which admin(s) will connect. 127.0.0.1 will always be considered admin.
$config['admin_ip_range'] = '192.168.0.0/16';

// name of the database file (perhaps location?)
$config['database_file_name'] = 'Hazelwire.sqlite';

// The path to the root folder of the webinterfaces
$config['site_folder'] = '/var/www/WebInterfaces/';

$config['RSA_location'] = 'lib/admin/rsa/';
$config['openvpn_location'] = 'lib/admin/openvpn/';

$config['base_port'] = 40000;

$config['management_port_base'] = 45000;
?>
