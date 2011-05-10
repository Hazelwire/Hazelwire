import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(("127.0.0.1",10000))
s.listen(1)
running = True

while running:
    conn, addr = socket.accept()
    data = conn.recv(1024)
    if data.startswith("STARTVPN "):
        conffile = data.split(' ')[1]
        os.system('/usr/sbin/openvpn ' + conffile)
        conn.close()
    elif data == "STARTKROUTING":
        os.system('sysctl net.ipv4.ip_forward=1')
    elif data == "STOPKROUTING":
        os.system('sysctl net.ipv4.ip_forward=0')
    elif data == "STOPSERVICE":
        conn.close()
        s.close()
        running = False

