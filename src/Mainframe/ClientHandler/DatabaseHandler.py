import sqlite3, time
""" Handler for communication with the SQLiteDatabase."""

class DatabaseHandler:
        
    def __init__(self,db="test.db"):
        self.db = db

    def connect(self):
        """Creates a connection to the database."""
        self.conn = sqlite3.connect(self.db)

    def disconnect(self):
        """Safely disconnect from the database, commiting changes first"""
        self.conn.commit()
        self.conn.close()

    def checkClientIP(self,IP):
        """
        Checks if a IP already requested and received flag
        @type IP: string
        @param IP: the IP to check
        """
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT * FROM flags WHERE team_id = (SELECT id FROM teams WHERE VMip=?);", [IP])
        res = c.fetchall()
        c.close()
        self.disconnect()
        return len(res) != 0

    def addFlags(self, modulename, flags, IP): 
        """
        Adds a flag to the database.
        First checks if any of the given `flags` are already present, if so return False.
        
        @type modulename: string
        @param modulename: The name of the module the flags are for
        @type flags: list
        @param flags: a list of the flags for the module
        @type IP: string
        @param IP: the IP of the team's VM
        """
        self.connect()
        c = self.conn.cursor()
        for flag in flags: # check if a flag already is in the database 
            c.execute("SELECT * FROM flags WHERE flag=?", [flag])
            if len(c.fetchall()) != 0:
                return False
        for flag in flags: #actually add the flags
            c.execute("INSERT INTO flags VALUES (?, (SELECT id FROM modules WHERE name=?), (SELECT id FROM teams WHERE VMip=?), ?);", [flags.index(flag)+1,modulename, clientIP, flag])
        c.close()
        self.disconnect()
        return True

    def addModuleInfo(self,modules):
        """
        Adds the module info to the database. See the L{ManifestParser} documentation on the format of the L{modules} dictionary.
        
        @type modules: list
        @param modules: the module info of all modules
        """
        self.connect()
        c = self.conn.cursor()
        for module in modules:
            c.execute("INSERT INTO modules VALUES (?, ?, ?, ?, ?);",[None, module['name'], module['numFlags'], module['deployscript'], module['serviceport']])
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
        """Returns info for all modules in the format {'name':"",'numFlags':"",'deployscript':""}"""
        res = []
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT * FROM modules;")
        for module in c.fetchall():
            res.append({'name':module[1], 'numFlags':module[2], 'deployscript':module[3]})
        self.disconnect()
        return res

    def addSuspiciousContestant(self, IP, port, reporterIP):
        """
        Adds an IP to the suspicious contestant table.
        
        @type IP: string
        @param IP: the IP of the offending team VM.
        @type port: integer
        @param port: the port that is not open
        @type reporterIP: string
        @param reporterIP: the IP of the reporting VM, if P2P sanity checking was used. Otherwise blank.
        """
        self.connect()
        c = self.conn.cursor()
        c.execute("INSERT INTO evil_teams VALUES(?,?,?,?,1);", [IP, port, int(time.time()), reporterIP])
        c.close()
        self.disconnect()

    def getIntervals(self):
        """
        Returns the intervals for the automatic sanity checking
        @rtype: tuple
        @return: (normal_interval, p2p_interval)
        """
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT value FROM config WHERE config_name = 'normal_interval';")
        normal_interval = c.fetchall()[0][0]
        c.execute("SELECT value FROM config WHERE config_name = 'p2p_interval';")
        p2p_interval = c.fetchall()[0][0]
        self.disconnect()
        return (int(normal_interval), int(p2p_interval))

    def getClientIPs(self):
        """Returns a list of all the VM IPs of the teams"""
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
        """Returns a list of all the used ports by the modules"""
        res = []
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT serviceport FROM modules;")
        for port in c.fetchall():
            res.append(port[0])
        c.close()
        self.disconnect()
        return res
