import threading, sys, socket, copy, logging
import DatabaseHandler, SanityCheck, P2PSanityCheck, ManualSanityChecker
#3 threads: Automatic Sanity checking, Listening for manual sanity check requests, checking for config changes

class RepeatTimer(threading.Thread):
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
        self.db = DatabaseHandler.DatabaseHandler(db)
        self.normal_interval, self.p2p_interval = self.db.getIntervals()
        self.contestants = self.db.getClientIPs()
        self.ports = self.db.getModulePorts()
        logging.basicConfig(format='%(asctime)s %(message)s', datefmt='%d/%m/%Y %I:%M:%S %p', level=logging.INFO, filename='sanitycheck.log')
        
    def controlListener(self):
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
        new_normal_interval, new_p2p_interval = self.db.getIntervals()
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
        self.normal_dbWriteLock = threading.Lock()
        self.normal_threads = []
        logging.info("[NORMALCHECK] Running check...")
        for contestant in self.contestants:
            logging.info("[NORMALCHECK] Checking " + contestant + " on ports " + str(self.ports))
            self.normal_threads.append(threading.Thread(target=self.NormalCheck_checkIP, args=[contestant, self.ports]))
            self.normal_threads[-1].start()
        for thread in self.normal_threads:
            thread.join()
        logging.info("[NORMALCHECK] Finished check")
        
    def NormalCheck_checkIP(self, IP, ports):
        results = SanityCheck.checkIP(IP, self.ports)
        for result in results:
            if not result['fine']:
                logging.info("[NORMALCHECK] Adding " + IP + " with port " + str(result['port']))
                self.normal_dbWriteLock.acquire()
                self.db.addSuspiciousContestant(IP, result['port'],'')
                self.normal_dbWriteLock.release()

    def P2PCheck(self):
        logging.info( "[P2PCHECK] Running check...")
        for contestant in self.contestants:
            temp = copy.copy(self.contestants)
            temp.remove(contestant)
            logging.info("[P2PCHECK] " + contestant + " is being checked by "  + ', '.join(temp))
            p2p = P2PSanityCheck.PeerToPeerSanityChecker(contestant, temp, self.ports)
            p2p.checkIP()
            allresults = p2p.getResults()
            for client in allresults:
                for result in client['results']:
                    #logging.info("[P2PCHECK] %s reports port %s on %s: fine = %s" % (client['IP'], str(result['port']), contestant, result['fine']))
                    if str(result['port']) == "" and client['IP'] == "": #this means the contestant is not running P2PRequestListener
                        logging.info("[P2PCHECK] Adding "+ contestant + " for not running PeerToPeerRequestListener")
                    else:
                        logging.info("[P2PCHECK] Adding " + contestant + " with port " + str(result['port']) + "reported by " + client['IP'])
                    if result['fine'] != "True":
                        self.db.addSuspiciousContestant(contestant, result['port'], client['IP'])
        logging.info("[P2PCHECK] Finished check.")

    def start(self):
        self.autoNormalTimer = RepeatTimer(self.normal_interval*60, self.NormalCheck)
        self.autoNormalTimer.start()
        logging.info("[SANITYSERVICE] Started Automatic Sanity Checking timer...")
        self.autoP2PTimer = RepeatTimer(self.p2p_interval*60, self.P2PCheck)
        self.autoP2PTimer.start()
        logging.info("[SANITYSERVICE] Started Automatic P2P Sanity Checking timer...")
        self.configTimer = RepeatTimer(60, self.checkConfig)
        self.configTimer.start()
        logging.info("[SANITYSERVICE] Started config checking timer...")
        self.msc = ManualSanityChecker.ManualSanityCheckerService('127.0.0.1',9997, self.db)
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
