import socket

def checkIP(IP, ports):
    results = []
    for port in ports:
        results.append({'port':port,'fine':True})
        failed = False
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            sock.connect((IP, port))
        except socket.error as error:
            failed = error.strerror == "Connection refused"
        if failed:
            results[-1]['fine'] = False
    return results

    
    


            