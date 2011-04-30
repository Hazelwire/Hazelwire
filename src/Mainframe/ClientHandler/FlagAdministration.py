import threading
import SocketServer
import DatabaseHandler
import string, random

# Modules are represented by an array of dictionaries with the keys ModuleName and numFlags, BasePath, InstallScript path
modules = []

class ThreadedTCPRequestHandler(SocketServer.StreamRequestHandler):

    def handle(self):
        self.data = self.rfile.readline().strip()
        print "Got " + self.data + " from " + self.client_address[0]
        if self.data == "REQFLAGS":
            # * Check client IP to see if already requested, if yes: return REQFAIL
            # * Generate number of flags that are needed according to the manifest
            # * Send the flags one by one separated by a new module
            print "Got request from " + self.client_address[0]
            if DatabaseHandler.checkClientIP(self.client_address[0]):
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
                print "Sent " + self.client_address[0] + " some flags."
        elif self.data == "REQSHUTDOWN":
            if self.client_address[0] == "127.0.0.1": #can only be sent from localhost
                self.server.shutdown()
            return

class ThreadedTCPServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):
    pass

def getModules():
    """Check the database for list of modules, how many flags must be created for each module and the paths"""
    global modules
    modules = DatabaseHandler.getModuleInfo()


def generateFlags(modulename, number, clientIP):
    """Takes a number of flags that need to be generated for clientIP. Adds them to the database"""
    while True:
        flags = []
        for x in range(number):
            flags.append("FLG")
            for x in range(61):
                flags[-1] += random.choice(string.letters+string.digits) #choose a random letter or digit
        if DatabaseHandler.addFlags(modulename, flags, clientIP): break #if succesfully added, we're done.
    return flags

def startServer(host,port):
    server = ThreadedTCPServer((HOST, PORT), ThreadedTCPRequestHandler)
    ip, port = server.server_address
    server_thread = threading.Thread(target=server.serve_forever)
    server_thread.start()

        
    
if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    #modules = [{'name':'test1', 'flags':[], 'numFlags':5, 'points':[13,2,1,3,4],'basepath':'/exploit1/', 'deployscript':'deploy/script.py'},{'name':'test2', 'flags':[], 'numFlags':2, 'points':[42,23,12,41,1], 'basepath':'/exploit2/', 'deployscript':'deploy/script.py'},{'name':'test3', 'flags':[], 'numFlags':4, 'points':[13,2,1,3,4], 'basepath':'/exploit3/', 'deployscript':'deploy/script.py'}]
    getModules()
    startServer(HOST, PORT)
    print "Started flag administration service on port " + str(PORT)
