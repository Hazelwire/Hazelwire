import sqlite3
""" Handler for communication with the Database."""
class DatabaseHandler:

    def __init__(self,db="test.db"):
        self.db = db

    def connect(self):
        self.conn = sqlite3.connect(self.db)

    def disconnect(self):
        self.conn.commit()
        self.conn.close()

    def checkClientIP(self,clientIP):
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT * FROM flags WHERE team_id = (SELECT id FROM teams WHERE VMip=?);", [clientIP])
        res = c.fetchall()
        c.close()
        self.disconnect()
        return len(res) != 0

    def addFlags(self, modulename, flags, clientIP): 
        self.connect()
        c = self.conn.cursor()
        for flag in flags: # check if a flag already is in the database 
            c.execute("SELECT * FROM flags WHERE flag=?", [flag])
            if len(c.fetchall()) != 0:
                return False
        for flag in flags: #actually add the flags
            c.execute("INSERT INTO flags VALUES (?, (SELECT id FROM modules WHERE name=?), (SELECT id FROM teams WHERE VMip=?), ?);", [flags.index(flag)+1,modulename, clientIP, flag]) #TODO: add some actual SQL here.
        c.close()
        self.disconnect()
        return True

    def addModuleInfo(self,modules):
        self.connect()
        c = self.conn.cursor()
        for module in modules:
            c.execute("INSERT INTO modules VALUES (?,?, ?, ?, ?, ?);",[module['serviceport'],None, module['name'], module['numFlags'], module['basepath'], module['deployscript']])
        c.close()
        self.disconnect()
        self.connect()
        c = self.conn.cursor()
        for module in modules:
            for flag in module['flagpoints']:
                c.execute("INSERT INTO flagpoints VALUES (?, ?, ?);", [module['flagpoints'].index(flag)+1, modules.index(module)+1, flag])
        c.close()
        self.disconnect()

    def getModuleInfo(self):
        res = []
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT * FROM modules;")
        for module in c.fetchall():
            res.append({'name':module[2], 'numFlags':module[3], 'basepath':module[4], 'deployscript':module[5]})
        self.disconnect()
        return res

    def addSuspiciousContestant(self, IP, port):
        #TODO: needs to be implemented, the table structure needs to be defined
        pass

    def getIntervals(self):
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT normal_interval, p2p_interval FROM config;")
        res = c.fetchall()
        c.close()
        self.disconnect()
        return res[0], res[1]


    def getClientIPs(self):
        res = []
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT VMip FROM teams;")
        for team in c.fetchall():
            res.append(team[0])
        c.close()
        self.disconnect()
        return res

    def getModulePorts(self):
        res = []
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT serviceport FROM modules;")
        for port in c.fetchall():
            res.append(port[0])
        c.close()
        self.disconnect()
        return res