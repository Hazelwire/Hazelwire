import threading
import SocketServer
import DatabaseHandler
import string, random

# Modules are represented by an array of dictionaries with the keys ModuleName and numFlags, BasePath, InstallScript path
modules = []
db = DatabaseHandler.DatabaseHandler()

class ThreadedTCPRequestHandler(SocketServer.StreamRequestHandler):

    def handle(self):
        self.data = self.rfile.readline().strip()
        if self.data == "REQFLAGS":
            # * Check client IP to see if already requested, if yes: return REQFAIL
            # * Generate number of flags that are needed according to the manifest
            # * Send the flags one by one separated by a new module
            print "[FLAGDISTRIB] Got request from " + self.client_address[0]
            if db.checkClientIP(self.client_address[0]):
                #Client already requested flags in the past
                response = "REQFAIL\n"
                self.wfile.write(response)
                return
            else:
                self.wfile.write("STARTFLAGS\n") #initiate flag transmission
                #Client has no flags assigned to him, generate some.
                for module in modules:
                    self.wfile.write("MODNAME " + module['name']+'\n')
                    self.wfile.write("BASEPATH " + module['basepath'] + '\n')
                    self.wfile.write("DEPLOYSCRIPT " + module['deployscript'] + '\n')
                    module['flags'] = generateFlags(module['name'], module['numFlags'], self.client_address[0])
                    for flag in module['flags']:
                        self.wfile.write("FLAG " + flag+'\n')
                    self.wfile.write("ENDMODULE\n")
                self.wfile.write("ENDFLAGS\n")
                print "[FLAGDISTRIB] Sent " + self.client_address[0] + " some flags."
        elif self.data == "REQSHUTDOWN":
            if self.client_address[0] == "127.0.0.1": #can only be sent from localhost
                print "[FLAGDISTRIB] Server shutting down..."
                self.server.shutdown()
            return

class ThreadedTCPServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):
    pass

def setDB(dbname):
    global db
    db = DatabaseHandler.DatabaseHandler(dbname)


def getModules():
    """Check the database for list of modules, how many flags must be created for each module and the paths"""
    global modules
    modules = db.getModuleInfo()


def generateFlags(modulename, number, clientIP):
    """Takes a number of flags that need to be generated for clientIP. Adds them to the database"""
    while True:
        flags = []
        for x in range(number):
            flags.append("FLG")
            for x in range(61):
                flags[-1] += random.choice(string.letters+string.digits) #choose a random letter or digit
        if db.addFlags(modulename, flags, clientIP): break #if succesfully added, we're done.
    return flags

def startServer(host,port):
    server = ThreadedTCPServer((host,port), ThreadedTCPRequestHandler)
    ip, port = server.server_address
    server_thread = threading.Thread(target=server.serve_forever)
    server_thread.start()
    print "\n[FLAGDISTRIB] Server running on %s and port %d" % (host, port)
    
if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    setDB("test.db")
    getModules()
    startServer(HOST, PORT)
    print "Started flag administration service on port " + str(PORT)
