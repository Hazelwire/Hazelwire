import threading
import DatabaseHandler, SanityCheck, P2PSanityCheck, ManualSanityChecker
#3 threads: Automatic Sanity checking, Listening for manual sanity check requests, checking for config changes

class SanityChecker:
    
    def __init__(self, normal, p2p, db):
        self.db = DatabaseHandler.DatabaseHandler(db)
        self.normal_interval = normal
        self.p2p_interval = p2p
        self.contestants = self.db.getClientIPs()
        self.ports = self.db.getModulePorts()
       
    def checkConfig(self):
        new_normal_interval, new_p2p_interval = self.db.getIntervals()
        if new_normal_interval != self.normal_interval:
            self.autoNormalTimer.cancel()
            self.normal_interval = new_normal_interval
            self.autoNormalTimer = threading.Timer(self.normal_interval*60, self.checkIP)
            self.autoNormalTimer.start()
        if new_p2p_interval != self.p2p_interval:
            self.autoP2PTimer.cancel()
            self.p2p_interval = new_p2p_interval
            self.autoP2PTimer = threading.Timer(self.p2p_interval*60, self.P2PCheck)
            self.autoP2PTimer.start()
            
    def NormalCheck(self):
        for contestant in self.contestants:
            results = SanityCheck.checkIP(contestant, self.ports)
            for result in results:
                if not result['fine']:
                    self.db.addSuspiciousContestant(contestant, result['port'],'')
                    
    def P2PCheck(self):
        for contestant in self.contestants:
            temp = self.contestants
            temp.remove(contestant)
            p2p = P2PSanityCheck.PeerToPeerSanityChecker(contestant, temp, self.ports)
            p2p.checkIP()
            allresults = p2p.getResults()
            for client in allresults:
                for result in client['results']:
                    print "%s reports %s, fine = %s" % (client['IP'], result['port'], result['fine'])
                    if result['fine'] != "True":
                        self.db.addSuspiciousContestant(contestant, result['port'], client['IP'])

    def start(self):
        self.autoNormalTimer = threading.Timer(self.normal_interval*60, self.NormalCheck)
        self.autoNormalTimer.start()
        print "Started Automatic Sanity Checking timer..."
        self.autoP2PTimer = threading.Timer(self.p2p_interval*60, self.P2PCheck)
        self.autoP2PTimer.start()
        print "Started Automatic P2P Sanity Checking timer..."
        self.configTimer = threading.Timer(60, self.checkConfig)
        self.configTimer.start()
        print "Started config checking timer..."
        self.msc = ManualSanityChecker.ManualSanityCheckerService('127.0.0.1',9997, self.db)
        self.ManualSanityCheckerThread = threading.Thread(target=self.msc.startServer)
        self.ManualSanityCheckerThread.start()
        print "Started Manual Sanity Check Request Service..."
        
if __name__ == "__main__":
    sanityService = SanityChecker(3,10,'test.db')
    sanityService.start()