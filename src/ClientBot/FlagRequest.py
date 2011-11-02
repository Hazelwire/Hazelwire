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
This module requests flags from the Mainframe when the game has started.
Then it will call the module's management script that takes care
of the deployment of the flags.
"""


import socket, os, time, signal

moduleFlags = [] #array of dictionaries {'ModuleName': String, 'BasePath': String, 'InstallScript': String, 'flags': []}

def received_signal(signum, stack):
    print "Received SIGUSR1, immediately requesting flags."

def requestFlags(host, port):
    """
    Requests flags from the mainframe.
    Mainframe returns REQFAIL if we already requested flags.
    Information received: module name, management script location, flags
    
    @type host: string
    @param host: Hostname/IP of the mainframe
    @type port: integer
    @param port: port number to connect to
    """

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    while True:
	try:
	    s.connect((host,port))
	    break
	except:
	    print "Mainframe is not yet online, sleeping for 5 minutes"
            time.sleep(300)
    sfile = s.makefile("r")
    s.send("REQFLAGS\n")
    first = sfile.readline().strip()
    if first == "STARTFLAGS": #got ok from mainframe
        awaitingModules = True
        while awaitingModules:
            data = sfile.readline().strip()
            if data.startswith("MODNAME"):
                moduleFlags.append({'name':'', 'flags':[], 'deployscript':''})
                moduleFlags[-1]['deployscript'] = sfile.readline().strip().split(' ')[1]
                moduleFlags[-1]['name'] = data.split(' ')[1].strip()
                awaitingFlags = True
                while awaitingFlags:
                    flag = sfile.readline().strip()
                    if flag == "ENDMODULE":
                        awaitingFlags = False
                        break
                    moduleFlags[-1]['flags'].append(flag.split(' ')[1])
            elif data == "ENDFLAGS":
                awaitingModules = False
        return True
    elif first == "REQFAIL": #we already requested flags. 
        return False
    
    
def deployFlags():
    """Calls the management script for every flag in order to deploy flags."""
    for module in moduleFlags:
        os.system(module['deployscript'] + " deploy " + ' '.join(module['flags'])) #execute deploy script
    

if __name__ == "__main__":
    signal.signal(signal.SIGUSR1, received_signal)
    if requestFlags("10.0.0.1",9999):
        deployFlags()
    else:
	print "We already requested flags."
