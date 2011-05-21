import socket

class PeerToPeerRequestListener:
    
    running = False
    
    def __init__(self, host, port):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.bind((host, port))
        self.sock.listen(1)
        
    def startServer(self):
        self.running = True
        while self.running :
            conn, addr = self.sock.accept()
            self.handle(conn)
    
    def handle(self, conn):
        data = conn.recv(1024).strip()
        if data.startswith("CHECK"):
            ip = data.strip(' ')[1]
            getPorts = True
            portsToScan = []
            while getPorts:
                data = conn.recv(1024).strip()
                if data.startswith("PORT"):
                    portsToScan.append({'port': data.split(" ")[1], 'result':''})
                elif data == "ENDPORTS":
                    getPorts = False
            results = self.checkIP(ip, portsToScan)
            for result in results:
                conn.send("RESULT " + result['port'] + " " + result['fine'] + '\n')
            conn.send("ENDRESULTS")
            conn.close()
        elif data == "LISTENSTOP":
            self.running = False
            conn.close()
            self.sock.close()
            
    def checkIP(IP, ports):
        results = []
        for port in ports:
            results.append({'port':port,'fine':True})
            failed = False
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            try:
                sock.connect(IP, port)
            except socket.error as error:
                failed = error == "[Errno 111] Connection refused"
            if failed:
                results[-1]['fine'] = False
        return results
            
if __name__ == "__main__":
    l = PeerToPeerRequestListener('',9998)
    print 'Starting PeerToPeerRequestListener on port 9998'
    l.startServer()