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
"""
This module listens for requests for Peer-to-peer sanity checks from the Mainframe.
It receives an IP and a set of ports to check, and returns the result to the Mainframe.
"""

import socket 

class PeerToPeerRequestListener:

    running = False

    def __init__(self, host, port):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
        self.sock.bind((host, port))
        self.sock.listen(1)

    def startServer(self):
        self.running = True
        while self.running :
            conn, addr = self.sock.accept()
            self.handle(conn, addr)

    def handle(self, conn, addr):
        if not addr[0].endswith("0.1") or addr[0] == "127.0.0.1":
            conn.close() #This is not the Mainframe so not processing further.
            return
        data = conn.recv(1024).strip()
        if data == '':
            data = conn.recv(1024).strip()
        lines = data.replace('\\n', '\n').split('\n')
        if data.startswith("CHECK"):
            ip = lines[0].split(' ')[1]
            portsToScan = []
            print lines
            for line in lines:
                if line.startswith("PORT"):
                    portsToScan.append(line.split(' ')[1].strip())
            results = self.checkIP(ip, portsToScan)
            msg = ''
            for result in results:
                msg += "RESULT " + result['port'] + " " + str(result['fine']) + '\n'
            conn.send(msg + "ENDRESULTS\n")
        elif data == "LISTENSTOP":
            self.running = False
            conn.close()
            self.sock.close()
        else:
            conn.close()

    def checkIP(self, IP, ports):
        results = []
        for port in ports:
            results.append({'port':port,'fine':True})
            failed = False
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.settimeout(2)
            try:
                sock.connect((IP, int(port)))
            except socket.timeout:
                failed = True
            except socket.error as error:
                if error.strerror == "Connection refused" or error.strerror == "Connection timed out":
                    failed = True
            if failed:
                results[-1]['fine'] = False
        return results

if __name__ == "__main__":
    l = PeerToPeerRequestListener('',9998)
    l.startServer()
