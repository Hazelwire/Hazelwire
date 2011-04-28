import socket

moduleFlags = [] #array of dictionaries {'name': String, 'flags': []}

def requestFlags(host, port):
    global moduleFlags
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host,port))
    sfile = s.makefile("rb")
    s.send("REQFLAGS\n")
    print "sent request"
    first = sfile.readline().strip()
    print first
    if first == "STARTFLAGS": #got ok from mainframe
        print "got ok from mainframe"
        recv = True
        while recv:
            data = sfile.readline().strip()
            if not data == '': print data
            if data.startswith("NEWMODULE"):
                moduleFlags.append({'name':'', 'flags':[]})
                moduleFlags[-1]['name'] = sfile.readline().split(' ')[1].strip()
                print "got new module with name " + moduleFlags[-1]['name']
                flag = sfile.readline().strip()
                while flag.startswith("FLAG"):
                    moduleFlags[-1]['flags'].append(flag.split(' ')[1])
                    print "got new flag " + moduleFlags[-1]['flags'][-1]
                    flag = sfile.readline().strip()
            elif data == "ENDFLAGS":
                recv = False
        return True
    elif first == "REQFAIL": #we already requested flags. 
        return False
    
    
def deployFlags():
    #I have no clue how to deploy dem flags.
    pass
    

if __name__ == "__main__":
    requestFlags('localhost',9999)
    print moduleFlags