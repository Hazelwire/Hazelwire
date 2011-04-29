import socket

moduleFlags = [] #array of dictionaries {'name': String, 'flags': []}

def requestFlags(host, port):
    global moduleFlags
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host,port))
    sfile = s.makefile("r")
    s.send("REQFLAGS\n")
    first = sfile.readline().strip()
    if first == "STARTFLAGS": #got ok from mainframe
        awaitingModules = True
        while awaitingModules:
            data = sfile.readline().strip()
            if data.startswith("MODNAME"):
                moduleFlags.append({'name':'', 'flags':[]})
                moduleFlags[-1]['name'] = data.split(' ')[1].strip()
		awaitingFlags = True
                while awaitingFlags:
                    flag = sfile.readline().strip()
		    if flag == "ENDMODULE":
		        awaitingFlags = False
			break
                    moduleFlags[-1]['flags'].append(flag.split(' ')[1])
            elif data == "ENDFLAGS":
                awaitingFlags = False
        return True
    elif first == "REQFAIL": #we already requested flags. 
        return False
    
    
def deployFlags():
    #I have no clue how to deploy dem flags.
    pass
    

if __name__ == "__main__":
    requestFlags('localhost',9999)
    print moduleFlags
