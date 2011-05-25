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
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((clientIP, 9998))
        msg = ''
        msg += 'CHECK ' + str(clientIP) + '\n'
        for port in self.ports:
            msg += "PORT " + str(port) + '\n'
        self.sock.send(msg + "ENDPORTS\n")
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