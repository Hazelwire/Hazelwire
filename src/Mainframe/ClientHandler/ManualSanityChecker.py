import socket, copy
from DatabaseHandler import DatabaseHandler
from SanityCheck import checkIP
import P2PSanityCheck

class ManualSanityCheckerService:

    def __init__(self, host, port, db):
        self.db = db
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
        self.sock.bind((host,port))
        self.sock.listen(1)
        self.contestants = self.db.getClientIPs()
        self.portsToScan = self.db.getModulePorts()

    def startServer(self):
        self.running = True
        while self.running:
            conn, addr = self.sock.accept()
            self.handle(conn, addr)
            conn.close()
        self.sock.close()

    def handle(self, conn, addr):
        data = conn.recv(1024).strip('\n')
        if data.startswith("CHECK TYPE "):
            checktype = data.split(' ')[2]
            IP = data.split(' ')[3]
            if checktype == "NORMAL":
                results = checkIP(IP, self.portsToScan)
                for result in results:
                    if not result['fine']:
                        print "Adding " + IP + " with port " + str(result['port'])
                        self.db.addSuspiciousContestant(IP, result['port'],'')

            elif checktype == "P2P":
                print "Contestants: " + str(self.contestants)
                self.targets = copy.copy(self.contestants)
                self.targets.remove(IP)
                print "Targets: " + str(self.targets)
                p2p = P2PSanityCheck.PeerToPeerSanityChecker(IP,self.targets, self.portsToScan)
                p2p.checkIP()
                results = p2p.getResults()
                for client in results:
                    for result in client['results']:
                        print "%s reports port %s on %s: fine = %s" % (client['IP'], str(result['port']), IP, result['fine'])
                        if result['fine'] == 'False':
                            self.db.addSuspiciousContestant(IP, result['port'], client['IP'])
                print "Finished P2PCheck."

        elif data == "STOPMANUAL":
            self.running = False
        
    def stopServer(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((self.host, self.port))
        sock.send('STOPMANUAL')
        sock.close()
            

