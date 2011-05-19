import socket,os

moduleFlags = [] #array of dictionaries {'ModuleName': String, 'BasePath': String, 'InstallScript': String, 'flags': []}

def requestFlags(host, port):
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
    for module in moduleFlags:
        os.system(module['deployscript']) #execute deploy script
        
    

if __name__ == "__main__":
    requestFlags('localhost',9999)
    for module in moduleFlags:
        print "Module: " + module['name']
        print "\tDeployscript: " + module['deployscript']
        print '\tFlags:'
        for flag in module['flags']:
            print '\t\tFlag: ' + flag
        print
        
