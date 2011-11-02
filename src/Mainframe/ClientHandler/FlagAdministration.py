# Copyright (c) 2011 The Hazelwire Team.
#     
# This file is part of Hazelwire.
# 
# Hazelwire is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# Hazelwire is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
"""
This module handles the distribution of flags.
When the game is started, this service is started alongside. 
The module information (how many flags) is retrieved from the database first.
The team's VMs connect to this service and request flags for their modules.
When they haven't yet requested any flags, the service generates flags and sends them to the requesting VM.
When a VM tries to request flags again, it will fail.

@var modules: A list of dictionaries which contain the module info. The dictionary contains the following keys: `modulename`, `numFlags`, `deployscript`
@var db: The DatabaseHandler instance.

"""

import threading, string, random, sys, copy
import SocketServer
import DatabaseHandler

modules = []
db = DatabaseHandler.DatabaseHandler()

class ThreadedTCPRequestHandler(SocketServer.StreamRequestHandler):
    """Helper class to handle multiple requests at the same time"""

    def handle(self):
        """
        Handles a request.
            1. Check client IP to see if already requested, if yes: return REQFAIL
            2. Generate number of flags that are needed according to the manifest
            3. Send the flags one by one separated by a new module
        """
        global modules
        modules2 = copy.deepcopy(modules)
        self.data = self.rfile.readline().strip()
        if self.data == "REQFLAGS":
            print "[FLAGDISTRIB] Got request from " + self.client_address[0]
            modules2 = db.getClientFlagsByModule((self.client_address[0]))
            self.wfile.write("STARTFLAGS\n") #initiate flag transmission
            for module in modules2:
                self.wfile.write("MODNAME " + module['name']+'\n')
                self.wfile.write("DEPLOYSCRIPT " + module['deployscript'] + '\n')
                if len(module['flags']) == 0:
                    print "[FLAGDISTRIB] %s has no flags yet, generating some..." % \
                            (self.client_address[0])
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
    """Sets the database module variable to a new DatabaseHandler
    @type dbname: string
    @param dbname: The filename of the SQLite database.
    """
    global db
    db = DatabaseHandler.DatabaseHandler(dbname)


def getModules():
    """
    Get module info from the database.
    """
    global modules
    modules = db.getModuleInfo()


def generateFlags(modulename, number, IP):
    """
    Generates a number of flags for the module with the given modulename and for the given team VM ip and adds them to the database.
    Flags always start with the string FLG and 61 random characters/digits follow that.
    @type modulename: string
    @param modulename: the name of the module
    @type number: integer
    @param number: the number of flags to generate
    @type IP: string
    @param IP: the IP of the team VM the flags will belong to
    """
    while True:
        flags = []
        for x in range(number):
            flags.append("FLG")
            for x in range(61):
                flags[-1] += random.choice(string.letters+string.digits) #choose a random letter or digit
        if db.addFlags(modulename, flags, IP): break #if succesfully added, we're done.
    return flags

def startServer(host,port):
    """
    Starts the FlagAdministration service on the given host and port
    @type host: string
    @param host: the IP to listen on
    @type port: integer
    @param port: the port to listen on
    """
    server = ThreadedTCPServer((host,port), ThreadedTCPRequestHandler)
    ip, port = server.server_address
    server_thread = threading.Thread(target=server.serve_forever)
    server_thread.start()
    print "\n[FLAGDISTRIB] Server running on %s and port %d" % (host, port)
    
if __name__ == "__main__":
    HOST, PORT = "", 9999
    setDB(sys.argv[1])
    getModules()
    startServer(HOST, PORT)
    print "Started flag administration service on port " + str(PORT)
