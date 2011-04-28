import threading
import SocketServer
import DatabaseHandler
import string, random

# Modules are represented by an array of dictionaries with the keys ModuleName and NumberOfFlags.
modules = []

class ThreadedTCPRequestHandler(SocketServer.StreamRequestHandler):

    def handle(self):
        global modules
        self.data = self.rfile.readline().strip()
        print "Got " + self.data + " from " + self.client_address[0]
        if self.data == "REQFLAGS":
            # * Check client IP to see if already requested, if yes: return REQFAIL
            # * Generate number of flags that are needed according to the manifest
            # * Send the flags one by one separated by a new module
            print "Got request from " + self.client_address[0]
            if DatabaseHandler.checkClientIP(self.client_address):
                #Client already requested flags in the past
                response = "REQFAIL\n"
                self.wfile.write(response)
                return
            else:
                self.wfile.write("STARTFLAGS\n") #initiate flag transmission
                #Client has no flags assigned to him, generate some.
                for module in modules:
                    self.wfile.write("MODNAME " + module['name']+'\n')
                    #module['flags']= generateFlags(module['numberOfFlags'], clientIP)
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

def readManifest(manifest):
    """Check the manifest for list of modules and how many flags must be created"""
    pass


def generateFlags(number, clientIP):
    """Takes a number of flags that need to be generated for clientIP. Adds them to the database"""
    while True:
        flags = []
        for x in range(number):
            flags.append("FLG")
            for x in range(61):
                flags[-1] += [random.choice(string.letters+string.digits)] #choose a random letter or digit
        if DatabaseHandler.addFlags(flags, clientIP): break #if succesfully added, we're done.
    return flags

def startServer(host,port):
    server = ThreadedTCPServer((HOST, PORT), ThreadedTCPRequestHandler)
    ip, port = server.server_address
    server_thread = threading.Thread(target=server.serve_forever)
    server_thread.start()

        
    
if __name__ == "__main__":
    global modules
    HOST, PORT = "localhost", 9999
    modules = [{'name':'test1', 'flags':['derp1','derp2','derp3']},{'name':'test2', 'flags':['derp4','derp5','derp6']},{'name':'test3', 'flags':['derp7','derp8','derp9']},{'name':'test4','flags':['derp10','derp11','derp12']},{'name':'test5','flags':['derp13','derp14','derp15']}]
    startServer(HOST, PORT)
    print "Started flag administration service on port " + str(PORT)

