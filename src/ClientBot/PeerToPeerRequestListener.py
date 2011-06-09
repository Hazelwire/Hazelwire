import socket, time

class PeerToPeerRequestListener:
    
    running = False
    
    def __init__(self, host, port):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
        self.sock.bind((host, port))
        self.sock.listen(1)
        
    def startServer(self):
        self.running = True
        while self.running :
            conn, addr = self.sock.accept()
            self.handle(conn)
    
    def handle(self, conn):
        data = conn.recv(1024).strip()
	print "Got %s"
        lines = data.split('\n')
        if data.startswith("CHECK"):
            ip = data.split(' ')[1]
            portsToScan = []
            for line in lines:
                if line.startswith("PORT"):
                    portsToScan.append(line.split(' ')[1].strip())
            results = self.checkIP(ip, portsToScan)
            msg = ''
            for result in results:
                msg += "RESULT " + result['port'] + " " + str(result['fine']) + '\n'
            conn.send(msg + "ENDRESULTS\n")
        elif data == "LISTENSTOP":
            self.running = False
            conn.close()
            self.sock.close()
            
    def checkIP(self, IP, ports):
        results = []
        for port in ports:
            results.append({'port':port,'fine':True})
            failed = False
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            try:
                sock.connect((IP, int(port)))
            except socket.error as error:
                failed = error.strerror == "Connection refused"
            if failed:
                results[-1]['fine'] = False
            print "Checked %s on %s, fine = %s" % (port, IP, results[-1]['fine'])
        return results
            
if __name__ == "__main__":
    l = PeerToPeerRequestListener('',9998)
    print 'Starting PeerToPeerRequestListener on port 9998'
    l.startServer()
