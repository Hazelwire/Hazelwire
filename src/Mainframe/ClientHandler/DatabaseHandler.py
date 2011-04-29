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
        c.execute("SELECT * FROM flags WHERE flag=?", [flag['flag']])
        if len(c.fetchall()) != 0:
            return False
    for flag in flags: #actually add the flags
        c.execute("INSERT INTO flags VALUES (?, (SELECT id FROM modules WHERE name=?), (SELECT id FROM teams WHERE VMip=?), ?, ?);", [flags.index(flag)+1,modulename, clientIP, flags[flags.index(flag)]['points'], flag['flag']]) #TODO: add some actual SQL here.
    c.close()
    disconnect()
    return True

def getModuleInfo(): #TODO: get the points info as well, not yet in the database: need to figure out how to store.
    res = []
    c = connect()
    c.execute("SELECT * FROM modules;")
    for module in c.fetchall():
        res.append({'name':module[1], 'numFlags':module[2], 'basepath':module[3], 'deployscript':module[4], 'points':[4,12,3,12,23,41]}) #points are bogus
    return res

if __name__ == "__main__":
    assert checkClientIP('10.0.8.1') == True
    assert checkClientIP('10.0.10.1') == False
    assert addFlags("pwnjebox", [{'flag':'FLGZ0sRrUGvTLLaHqv8dUwy3HPgDo8y5ZCweGvCt9VyuXCus4UmUhzqeB9FFr6c7', 'points':41}], '10.0.8.1') == False
    print addFlags("pwnjebox", [{'flag':'FLGZ0sRrUGvTLLaHqv8dUwy3HLoLDERPZCweGvCt9VyuXCus4UmUhzqeB9FFr6c7', 'points':83}], '10.0.8.1')
    print getModuleInfo()