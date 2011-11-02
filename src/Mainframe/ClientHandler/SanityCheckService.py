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
The main class for the sanity checks.
This module is started when the game is started.
It creates 4 threads:
    - Configuration check (checks the database to see if the intervals have changed)
    - Automatic normal sanity check
    - Automatic peer-to-peer sanity check
    - Manual sanity check request listener
    - Control listener, listens for stop signals and shuts down the service    
"""

import threading, sys, socket, copy, logging
import DatabaseHandler, SanityCheck, P2PSanityCheck, ManualSanityChecker

class RepeatTimer(threading.Thread):
    """
    Helper class that implements repeating timers, because apparently python's default Timer class only executes once.
    """
    def __init__(self, interval, callable, *args, **kwargs):
        threading.Thread.__init__(self)
        self.interval = interval
        self.callable = callable
        self.args = args
        self.kwargs = kwargs
        self.event = threading.Event()
        self.event.set()

    def run(self):
        while self.event.is_set():
            t = threading.Timer(self.interval, self.callable,
                                self.args, self.kwargs)
            t.start()
            t.join()

    def cancel(self):
        self.event.clear()

class SanityChecker:
    
    def __init__(self, db):
        """
        Initialises the service. Creates a L{DatabaseHandler} for the given database file.
        @type db: string
        @param db: path to the SQLite database file.
        """
        self.db = DatabaseHandler.DatabaseHandler(db)
        self.normal_interval, self.p2p_interval = self.db.getIntervals()
        self.modules = self.db.getModulePortsAndNames()
        self.normal_dbWriteLock = threading.Lock()
        logging.basicConfig(format='%(asctime)s %(message)s', datefmt='%d/%m/%Y %I:%M:%S %p', level=logging.INFO, filename='sanitycheck.log')
        
    def controlListener(self):
        """
        The control listener. Listens for the STOPSANITYSERVICE commands, which shuts down the service by
        cancelling the timers and stopping the Manual Sanity Check Request Listener.
        """
        self.running = True
        while self.running:
            conn, addr = self.sock.accept()
            data = conn.recv(1024).strip('\n')
            if data == "STOPSANITYSERVICE":
                try:
                    logging.info("[SANITYSERVICE] Got a stop signal")
                    self.running = False
                    self.autoNormalTimer.cancel()
                    logging.info("[SANITYSERVICE] Cancelled NORMALCHECK timer...")
                    self.autoP2PTimer.cancel()
                    logging.info("[SANITYSERVICE] Cancelled P2PCHECK timer...")
                    self.configTimer.cancel()
                    logging.info("[SANITYSERVICE] Cancelled CONFIGCHECK timer...")
                    self.msc.stopServer()
                    logging.info("[SANITYSERVICE] Stopped ManualSanityCheckRequestListener...")
                except:
                    pass
                conn.close()
        self.sock.close()

    def checkConfig(self):
        """
        The configuration check thread. Checks the database every minute to see if the intervals have changed. Sets the class variables accordingly.
        """
        self.normal_dbWriteLock.acquire()
        new_normal_interval, new_p2p_interval = self.db.getIntervals()
        self.normal_dbWriteLock.release()
        logging.info("[CONFIGCHECK] Got intervals from db " + str(new_normal_interval) + " " + str(new_p2p_interval))
        if new_normal_interval != self.normal_interval:
            logging.info( "[CONFIGCHECK]  Got a new time for normal_interval")
            self.autoNormalTimer.cancel()
            self.normal_interval = new_normal_interval
            self.autoNormalTimer = RepeatTimer(self.normal_interval*60, self.checkIP)
            self.autoNormalTimer.start()
            logging.info("[CONFIGCHECK] Started normal check timer with timeout " + str(self.normal_interval*60))
        if new_p2p_interval != self.p2p_interval:
            logging.info("[CONFIGCHECK] Got a new time for p2p_interval")
            self.autoP2PTimer.cancel()
            self.p2p_interval = new_p2p_interval
            self.autoP2PTimer = RepeatTimer(self.p2p_interval*60, self.P2PCheck)
            self.autoP2PTimer.start()
            logging.info( "[CONFIGCHECK] Started p2p check Timer with timeout " + str(self.p2p_interval*60))

    def NormalCheck(self):
        """
        The automatic normal sanity check.
        Normal sanity checking involves the Mainframe to check every port used by the modules on all the team's VMs.
        When it can't connect to a specific port it adds an entry to the suspicious IP table.
        """
        logging.info("[NORMALCHECK] Running check...")
        self.contestants = self.db.getClientIPs()
        for module in self.modules:
            self.normal_threads = []
            logging.info("[NORMALCHECK] Checking module " + module['name'] + " on port " + str(module['port']))
            for contestant in self.contestants:
                logging.info("[NORMALCHECK] Checking " + contestant)
                self.normal_threads.append(threading.Thread(target=self.NormalCheck_checkIP, args=[contestant,module['port'], module['name']]))
                self.normal_threads[-1].start()
            for thread in self.normal_threads:
                thread.join()
            logging.info("[NORMALCHECK] Finished checking module " + module['name'])
        logging.info("[NORMALCHECK] Finished check")
        
    def NormalCheck_checkIP(self, IP, port, name):
        """
        Helper function to check a port on an IP
        @type IP: string
        @param IP: the IP to check
        @type ports: list
        @param ports: the list of ports to check
        """
        result = SanityCheck.checkIP(IP, port)[0]
        if not result['fine']:
            logging.info("[NORMALCHECK] Adding " + IP + " with port " + str(result['port']))
            self.normal_dbWriteLock.acquire()
            self.db.addSuspiciousContestant(IP, result['port'],'', name)
            self.normal_dbWriteLock.release()

    def P2PCheck(self):
        """
        The automatic Peer-to-Peer sanity check thread.
        Peer-to-peer sanity checking involves the mainframe asking all the VMs to scan each other.
        When a VM finds a suspicious client the IP of the reporting VM will also be added to the database.
        """
        logging.info( "[P2PCHECK] Running check...")
        self.contestants = self.db.getClientIPs()
        for module in self.modules:
            logging.info("[P2PCHECK] Checking module " + module['name'] + " on port " + str(module['port']))
            for contestant in self.contestants:
                temp = copy.copy(self.contestants)
                temp.remove(contestant)
                logging.info("[P2PCHECK] " + contestant + " is being checked by "  + ', '.join(temp))
                p2p = P2PSanityCheck.PeerToPeerSanityChecker(contestant, temp, module['port'])
                p2p.checkIP()
                allresults = p2p.getResults()
                for client in allresults:
                    for result in client['results']:
                        #logging.info("[P2PCHECK] %s reports port %s on %s: fine = %s" % (client['IP'], str(result['port']), contestant, result['fine']))                    
                        if result['fine'] != "True":
                            if str(result['port']) == "": #this means the contestant is not running P2PRequestListener
                                logging.info("[P2PCHECK] Adding "+ client["IP"] + " for not running PeerToPeerRequestListener")
                                self.normal_dbWriteLock.acquire()
                                self.db.addSuspiciousContestant(client["IP"], "","", "PeerToPeerRequestListener")
                                self.normal_dbWriteLock.release()
                            else:
                                logging.info("[P2PCHECK] Adding " + contestant + " with port " + str(result['port']) + "reported by " + client['IP'])
                                self.normal_dbWriteLock.acquire()
                                self.db.addSuspiciousContestant(contestant, result['port'], client['IP'], module['name'])
                                self.normal_dbWriteLock.release()
                logging.info("[P2PCHECK] Finished checking module " + module['name'])
        logging.info("[P2PCHECK] Finished check.")

    def start(self):
        """
        Starts the service by creating the Timers and threads
        """
        self.autoNormalTimer = RepeatTimer(self.normal_interval*60, self.NormalCheck)
        self.autoNormalTimer.start()
        logging.info("[SANITYSERVICE] Started Automatic Sanity Checking timer...")
        self.autoP2PTimer = RepeatTimer(self.p2p_interval*60, self.P2PCheck)
        self.autoP2PTimer.start()
        logging.info("[SANITYSERVICE] Started Automatic P2P Sanity Checking timer...")
        self.configTimer = RepeatTimer(60, self.checkConfig)
        self.configTimer.start()
        logging.info("[SANITYSERVICE] Started config checking timer...")
        self.msc = ManualSanityChecker.ManualSanityCheckerService('127.0.0.1',9997, sys.argv[1])
        self.ManualSanityCheckerThread = threading.Thread(target=self.msc.startServer)
        self.ManualSanityCheckerThread.start()
        logging.info("[SANITYSERVICE] Started Manual Sanity Check Request Service...")
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
        self.sock.bind(('localhost',9996))
        self.sock.listen(1)
        self.control = threading.Thread(target=self.controlListener)
        self.control.start()
        logging.info("[SANITYSERVICE] Started control listener thread...")

if __name__ == "__main__":
    sanityService = SanityChecker(sys.argv[1])
    sanityService.start()
