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
r"""
This service listens for manual sanity check requests from the Admin interface.
It supports both the normal and peer-to-peer sanity checks.
Send C{CHECK TYPE} <type> <IP> to port 9997 on C{localhost} to request a sanity check.
"""
import socket, copy, logging
from DatabaseHandler import DatabaseHandler
from SanityCheck import checkIP
import P2PSanityCheck

class ManualSanityCheckerService:
    
    def __init__(self, host, port, db):
        """
        Initialises the service.
        @type host: string
        @param host: the IP to listen on.
        @type port: integer
        @param port: the port to listen on.
        @type db: string
        @param db: path to the SQLite database file.
        """
        self.db = DatabaseHandler(db)
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
        self.sock.bind((host,port))
        self.sock.listen(1)
        self.modules = self.db.getModulePortsAndNames()

    def startServer(self):
        self.running = True
        while self.running:
            conn, addr = self.sock.accept()
            self.handle(conn, addr)
            conn.close()
        self.sock.close()

    def handle(self, conn, addr):
        """Handles a request"""
        data = conn.recv(1024).strip('\n')
        if data.startswith("CHECK TYPE "):
            self.contestants = self.db.getClientIPs()
            checktype = data.split(' ')[2]
            IP = data.split(' ')[3]
            if checktype == "NORMAL":
                logging.info("[MANUALNORMAL] Starting check")
                for module in self.modules:
                    logging.info("Checking module " + module['name'] + "on port " + str(module['port']))
                    result = checkIP(IP, module['port'])[0]
                    if not result['fine']:
                        logging.info("[MANUALNORMAL] Adding " + IP + " with port " + str(result['port']))
                        self.db.addSuspiciousContestant(IP, result['port'],'',module['name'])
                logging.info("[MANUALNORMAL] Finished check")

            elif checktype == "P2P":
                logging.info("[MANUALP2P] Starting check")
                for module in self.modules:
                    logging.info("[MANUALP2P] Checking module " + module['name'] + " on port " + str(module['port']))
                    self.targets = copy.copy(self.contestants)
                    self.targets.remove(IP)
                    p2p = P2PSanityCheck.PeerToPeerSanityChecker(IP,self.targets, module['port'])
                    p2p.checkIP()
                    results = p2p.getResults()
                    for client in results:
                        for result in client['results']:
                            #logging.info("%s reports port %s on %s: fine = %s" % (client['IP'], result['port'], IP, result['fine']))
                            if result['fine'] == 'False':
                                if  result['port'] == "":
                                    logging.info("[MANUALP2P] Adding " + client['IP'] + " for not running P2PRequestListener")
                                    self.db.addSuspiciousContestant(client["IP"], "","", "PeerToPeerRequestListener")
                                else:
                                    logging.info("[MANUALP2P] Adding " + IP + " with port " + str(result['port']) + "reported by " + client['IP'])
                                    self.db.addSuspiciousContestant(IP, result['port'], client['IP'], module['name'])
                logging.info("[MANUALP2P] Finished check.")

        elif data == "STOPMANUAL":
            logging.info("[MANUALSERVICE] Stopping Manual Sanity Check Service...")
            self.running = False
        
    def stopServer(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((self.host, self.port))
        sock.send('STOPMANUAL')
        sock.close()
            

