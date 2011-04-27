import threading
import SocketServer
import DatabaseHandler

# Modules are represented by an array of dictionaries with the keys ModuleName and NumberOfFlags.
modules = []

class ThreadedTCPRequestHandler(SocketServer.StreamRequestHandler):

    def handle(self):
        self.data = self.rfile.readline().strip()
        if self.data == "REQFLAGS":
            # * Check client IP to see if already requested, if yes: return REQFAIL
            # * Generate number of flags that are needed according to the manifest
            # * Send the flags one by one separated by a new module
            if DatabaseHandler.checkClientIP(self.client_address):
                #Client already requested flags in the past
                response = "REQFAIL"
                self.request.send(response)
                return
            else:
                #Client has no flags assigned to him, generate some.
                for module in modules:
                    self.request.send("NEWMODULE")
                    self.request.send("MODNAME " + module['name'])
                    flags = generateFlags(module['numberOfFlags'], clientIP)
                    for flag in flags:
                        self.request.send("FLAG " + flag)
                self.request.send("ENDFLAGS")
        elif self.data == "REQSHUTDOWN":
            if self.client_address == "127.0.0.1": #can only be sent from localhost
                self.server.shutdown()
            return

class ThreadedTCPServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):
    pass

def readManifest(manifest):
    """Check the manifest for list of modules and how many flags must be created"""
    pass


def generateFlags(number, clientIP):
    """Takes a number of flags that need to be generated for clientIP. Adds them to the database"""
    flags = []
    for x in range(number):
        flag.append("some generated flag")#generate flag
    while not DatabaseHandler.addFlags(flags, clientIP): #keep on generating flags when some flag already existed
        for x in range(number):
            flag.append("some generated flag")#generate flag
    
def startServer(host,port):
    server = ThreadedTCPServer((HOST, PORT), ThreadedTCPRequestHandler)
    ip, port = server.server_address
    server_thread = threading.Thread(target=server.serve_forever)
    server_thread.start()
    
if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    startServer(HOST, PORT)
    print "Started flag administration service on port " + str(PORT)

