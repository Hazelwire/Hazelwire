# Copyright (c) 2011 The Hazelwire Team.
#     
# This file is part of Hazelwire.
# 
# Hazelwire is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# Hazelwire is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.

import socket, os, subprocess

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
s.bind(("127.0.0.1",10000))
s.listen(1)
running = True

while running:
    conn, addr = s.accept()
    data = conn.recv(1024).strip()
    print "Got %s from %s" % (data, addr[0])
    if data.startswith("STARTVPN "):
        conffile = data.split(' ')[1]
        print 'Calling /usr/sbin/openvpn ' + conffile
        subprocess.Popen('/usr/sbin/openvpn ' + conffile, shell=True)
        conn.close()
    elif data == "STARTKROUTING":
        os.system('sysctl net.ipv4.ip_forward=1')
    elif data == "STOPKROUTING":
        os.system('sysctl net.ipv4.ip_forward=0')
    elif data == "STOPSERVICE":
        conn.close()
        s.close()
        running = False
