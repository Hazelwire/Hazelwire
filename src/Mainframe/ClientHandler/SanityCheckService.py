import threading
import DatabaseHandler, SanityCheck, P2PSanityCheck, ManualSanityChecker
#3 threads: Automatic Sanity checking, Listening for manual sanity check requests, checking for config changes

class SanityChecker:
    
    def __init__(self, normal, p2p):
        self.normal_interval = normal
        self.p2p_interval = p2p
        self.contestants = DatabaseHandler.getClientIPs()
        self.ports = DatabaseHandler.getModulePorts()

    def checkConfig(self):
        new_normal_interval, new_p2p_interval = DatabaseHandler.getIntervals()
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
                    DatabaseHandler.addSuspiciousContestant(contestant, result['port'])
                    
    def P2PCheck(self):
        for contestant in self.contestants:
            temp = self.contestants
            p2p = P2PSanityCheck.PeerToPeerSanityChecker(contestant, temp.remove(contestant), self.ports)
            p2p.checkIP()
            results = p2p.getResults()
            for result in results:
                if not result['fine']:
                    DatabaseHandler.addSuspiciousContestant(contestant, result['port'])
                
        
    def start(self):
        self.autoNormalTimer = threading.Timer(self.normal_interval*60, self.NormalCheck)
        self.autoP2PTimer = threading.Timer(self.p2p_interval*60, self.P2PCheck)
        self.configTimer = threading.Timer(60, self.checkConfig)
        self.msc = ManualSanityChecker.ManualSanityCheckerService('127.0.0.1',9997)
        self.ManualSanityCheckerThread = threading.Thread(target=self.msc.startServer)