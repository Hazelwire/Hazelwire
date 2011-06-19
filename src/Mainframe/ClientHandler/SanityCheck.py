import socket

def checkIP(IP, ports):
    results = []
    for port in ports:
        results.append({'port':port,'fine':True})
        failed = False
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(2)
        print "[NORMALCHECK] Checking port " + str(port) + " on IP " + IP
        try:
            sock.connect((IP, int(port)))
        except socket.timeout:
	    print "[NORMALCHECK] Got connection timeout"
            failed = True
            continue
        except socket.error as error:
            print "[NORMALCHECK] Got error " + error.strerror
            if error.strerror == "Connection refused" or error.strerror == "Connection timed out":
                failed = True
        if failed:
            results[-1]['fine'] = False
    return results





