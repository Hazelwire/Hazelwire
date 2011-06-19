import socket, threading

class PeerToPeerSanityChecker:

    def __init__(self, targetIP, clients, ports):
        self.threads = []
        self.allresults = []
        self.clients = clients
        self.ports = ports
        self.targetIP = targetIP
        self.writeLock = threading.Lock()

    def sendRequest(self, clientIP):
        print "[P2PCHECK] Asking " + clientIP + " to do a P2PCheck on " + self.targetIP
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            self.sock.connect((clientIP, 9998))
        except:
            print "Client " + clientIP + " is not running P2PRequestListener!"
            return
        msg = 'CHECK ' + str(self.targetIP) + '\n'
        for port in self.ports:
            msg += "PORT " + str(port) + '\n'
        msg += "ENDPORTS\n"
        print repr(msg)
        self.sock.send(msg)
        results = []
        data = self.sock.recv(1024).strip()
        lines = data.split('\n')
        for line in lines:                
            if line.startswith("RESULT"):
                results.append({'port': line.split(' ')[1], 'fine':line.split(' ')[2]})
        self.writeResults(results, clientIP)
        self.sock.close()

    def writeResults(self, results, IP):
        self.writeLock.acquire()
        self.allresults.append({'results':results, 'IP':IP})
        self.writeLock.release()

    def getResults(self):
        for thread in self.threads:
            thread.join()
        return self.allresults

    def checkIP(self):
        for client in self.clients:
            self.threads.append(threading.Thread(target=self.sendRequest, args=[client]))
            self.threads[-1].start()