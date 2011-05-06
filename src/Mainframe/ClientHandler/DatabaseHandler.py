import sqlite3
""" Handler for communication with the Database."""

db = "test.db"

def connect():
    global conn
    conn = sqlite3.connect(db)
    return conn.cursor()

def disconnect():
    global conn
    conn.commit()
    conn.close()

def checkClientIP(clientIP):
    c = connect()
    c.execute("SELECT * FROM flags WHERE team_id = (SELECT id FROM teams WHERE VMip=?);", [clientIP])
    res = c.fetchall()
    disconnect()
    return len(res) != 0

def addFlags(modulename, flags, clientIP): 
    c = connect()
    for flag in flags: # check if a flag already is in the database 
        c.execute("SELECT * FROM flags WHERE flag=?", [flag])
        if len(c.fetchall()) != 0:
            return False
    for flag in flags: #actually add the flags
        c.execute("INSERT INTO flags VALUES (?, (SELECT id FROM modules WHERE name=?), (SELECT id FROM teams WHERE VMip=?), ?);", [flags.index(flag)+1,modulename, clientIP, flag]) #TODO: add some actual SQL here.
    c.close()
    disconnect()
    return True

def addModuleInfo(modules):
    c = connect()
    for module in modules:
        c.execute("INSERT INTO modules VALUES (?, ?, ?, ?, ?, ?);",[module['serviceport'], None, module['name'], module['numFlags'], module['basepath'], module['deployscript']])
    c.close()
    disconnect()
    c = connect()
    for module in modules:
        for flag in module['flagpoints']:
            c.execute("INSERT INTO flagpoints VALUES (?, ?, ?);", [module['flagpoints'].index(flag)+1, modules.index(module)+1, flag])
    c.close()
    disconnect()
    
def getModuleInfo():
    res = []
    c = connect()
    c.execute("SELECT * FROM modules;")
    for module in c.fetchall():
        res.append({'name':module[1], 'numFlags':module[2], 'basepath':module[3], 'deployscript':module[4]})
    c.close()
    disconnect()
    return res

def getClientIPs():
    res = []
    c = connect()
    c.execute("SELECT VMip FROM teams;")
    for team in c.fetchall():
        res.append(team[0])
    c.close()
    disconnect()
    return res

def getModulePorts():
    res = []
    c = connect()
    c.execute("SELECT serviceport FROM modules;")
    for port in c.fetchall():
        res.append(port[0])
    c.close()
    disconnect()
    return res

def addSuspiciousContestant(IP, port):
    #TODO: needs to be implemented, the table structure needs to be defined
    pass

def getIntervals():
    c = connect()
    c.execute("SELECT normal_interval, p2p_interval FROM config;")
    res = c.fetchall()
    return res[0], res[1]

if __name__ == "__main__":
    assert checkClientIP('10.0.8.1') == True
    assert checkClientIP('10.0.10.1') == False
    assert addFlags("pwnjebox", [{'flag':'FLGZ0sRrUGvTLLaHqv8dUwy3HPgDo8y5ZCweGvCt9VyuXCus4UmUhzqeB9FFr6c7', 'points':41}], '10.0.8.1') == False
    print addFlags("pwnjebox", [{'flag':'FLGZ0sRrUGvTLLaHqv8dUwy3HLoLDERPZCweGvCt9VyuXCus4UmUhzqeB9FFr6c7', 'points':83}], '10.0.8.1')
    print getModuleInfo()
