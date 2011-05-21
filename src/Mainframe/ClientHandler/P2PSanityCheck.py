import socket, threading

class PeerToPeerSanityChecker:
    
    def __init__(self, targetIP, clients, ports):
        self.threads = []
        self.allresults = []
        self.clients = clients
        self.ports = ports
        self.targetIP = targetIP
        self.writeLock = threading.Lock()
            
    def sendRequest(clientIP):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((clientIP, 9998))
        self.sock.send('CHECK ' + str(clientIP) + '\n')
        for port in self.ports:
            self.sock.send("PORT " + str(port) + '\n')
            self.sock.send("ENDPORTS\n")
        results = []
        getResults = True
        while getResults:
            data = self.sock.recv(1024).strip()
            if data.startswith("RESULT"):
                results.append({'port': data.split(' ')[1], 'fine':data.split(' ')[2]})
            elif data == "ENDRESULTS":
                getResults = False
        self.writeResults(results)
        sock.close()
        
    def writeResults(self, results):
        self.writeLock.acquire()
        self.allresults.append(results)
        self.writeLock.release()
        
    def getResults(self):
        for thread in self.threads:
            thread.join()
        return results
        
    def checkIP(self):
        for client in self.clients:
            self.threads.append(threading.Thread(target=self.sendRequest(client)))
            self.threads[-1].start()