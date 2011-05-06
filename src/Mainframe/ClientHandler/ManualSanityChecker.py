import socket
import DatabaseHandler
from SanityCheck import checkIP
import P2PSanityCheck

class ManualSanityCheckerService:

    def __init__(self, host, port):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.bind((host,port))
        self.sock.listen(1)
        self.contestants = DatabaseHandler.getClientIPs()
        self.portsToScan = DatabaseHandler.getModulePorts()

    def startServer(self):
        self.running = True
        while self.running:
            conn, addr = self.sock.accept()
            self.handle(conn, addr)
            conn.close()
        self.sock.close()

    def handle(self, conn, addr):
        data = conn.recv(1024)
        if data.startswith("CHECK TYPE "):
            checktype = data.split(' ')[2]
            IP = data.split(' ')[3]
            if checktype == "NORMAL":
                results = checkIP(IP, self.portsToScan)
            elif checktype == "P2P":
                p2p = P2PSanityCheck.PeerToPeerSanityChecker(IP,self.contestants, self.portsToScan)
                p2p.checkIP()
                results = p2p.getResults()
            for result in results:
                    if not result['fine']:
                        DatabaseHandler.addSuspiciousContestant(IP, result['port'])
        elif data == "STOPMANUAL":
            self.running = False       
            

