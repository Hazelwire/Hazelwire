"""
The main class for the Peer-to-Peer sanity check.
Works by sending every VM a request to scan a given IP address on the given ports.
When all threads are finished return the list of results.
"""

import socket, threading, logging

class PeerToPeerSanityChecker:
    
    def __init__(self, targetIP, clients, ports):
        """
        Initialises the PeerToPeerSanityChecker.
        @type targetIP: string
        @param targetIP: the IP of the target VM.
        @type clients: list
        @param clients: the list of VMs to send a Peer-to-Peer sanity check request to.
        @type ports: list
        @param ports: the list of ports to scan.
        """
        self.threads = []
        self.allresults = []
        self.VMs = clients
        self.ports = ports
        self.targetIP = targetIP
        self.writeLock = threading.Lock()

    def sendRequest(self, IP):
        """
        Send a Peer-to-Peer sanity check request to the given IP. Receive the results and append them to the list of all results.
        @type IP: string
        @param IP: the IP to send the request to.
        """
        logging.info("[P2PCHECK] Asking " + IP + " to do a P2PCheck on " + self.targetIP)
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(5)
        try:
            sock.connect((IP, 9998))
        except:
            logging.info("[P2PCHECK] Client " + IP + " is not running P2PRequestListener!")
            self.writeResults([{'port':'','fine':"False"}], IP)
            return
        msg = 'CHECK ' + str(self.targetIP) + '\n'
        for port in self.ports:
            msg += "PORT " + str(port) + '\n'
        msg += "ENDPORTS\n"
        sock.settimeout(None)
        sock.send(msg)
        results = []
        data = sock.recv(1024).strip()
        lines = data.split('\n')
        for line in lines:                
            if line.startswith("RESULT"):
                results.append({'port': line.split(' ')[1], 'fine':line.split(' ')[2]})
        self.writeResults(results, IP)
        sock.close()

    def writeResults(self, results, IP):
        """
        Helper function to safely append the given results reported by the given IP to the list of all results.
        @type results: list
        @param results: the list of results, a result consists of a port number and a boolean indicating if the check was succesful.
        @type IP: string
        @param IP: the IP of the VM that did the check.
        """
        self.writeLock.acquire()
        self.allresults.append({'results':results, 'IP':IP})
        self.writeLock.release()

    def getResults(self):
        """
        Waits for all the checks to finish and returns the list of all the results
        @rtype: list
        @return: the list of all results
        """
        for thread in self.threads:
            thread.join()
        return self.allresults

    def checkIP(self):
        """
        Check the IP given at initialization.
        Creates a thread for every VM to speed up the check.
        """
        for VM in self.VMs:
            self.threads.append(threading.Thread(target=self.sendRequest, args=[VM]))
            self.threads[-1].start()
