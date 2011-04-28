import sqlite3
""" Handler for communication with the Database."""

def connect():
    global conn
    conn = sqlite3.connect("derp")
    return conn.cursor()

def disconnect():
    global conn
    conn.close()

def checkClientIP(clientIP):
    return False #just for debugging
    c = connect()
    c.execute("SELECT * FROM flags WHERE clientip=?", clientIP)
    c.close()
    disconnect()
    return len(c) == 0

def addFlags(flags, clientIP): 
    return True #just for debugging
    c = connect()
    for flag in flags: # check if a flag already is in the database 
        c.execute("SELECT * FROM flags WHERE flag=?", flag)
        if len(c) != 0:
            return False
    for flag in flags: #actually add the flags
        c.execute("INSERT INTO flags ") #TODO: add some actual SQL here.
    c.commit()
    c.close()
    disconnect()
    
   