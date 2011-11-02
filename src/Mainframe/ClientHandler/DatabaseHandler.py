# Copyright (c) 2011 The Hazelwire Team.
#     
# This file is part of Hazelwire.
# 
# Hazelwire is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# Hazelwire is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
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
            c.execute("INSERT INTO flags VALUES (?, (SELECT id FROM modules WHERE name=?), (SELECT id FROM teams WHERE VMip=?), ?);", [flags.index(flag)+1,modulename, IP, flag])
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
            res.append({'id': module[0],'name':module[1], 'numFlags':module[2], 'deployscript':module[3]})
        self.disconnect()
        return res

    def addSuspiciousContestant(self, IP, port, reporterIP, modulename):
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
        c.execute("INSERT INTO evil_teams VALUES(?,?,?,?,1,?);", [IP, port, int(time.time()), reporterIP, modulename])
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
    
    def getModulePortsAndNames(self):
        """Returns a dict of {'moduleName','port'}"""
        res = []
        self.connect()
        c = self.conn.cursor()
        c.execute("SELECT name,serviceport FROM modules;")
        for module in c.fetchall():
            res.append({'name':module[0],'port':module[1]})
        c.close()
        self.disconnect()
        return res
    
    def getClientFlagsByModule(self, clientIP):
        """Returns the flags for a given client"""
        modules = self.getModuleInfo()
        self.connect()
        c = self.conn.cursor()
        for module in modules:
            module['flags'] = []
            c.execute("SELECT flag FROM flags WHERE team_id = (SELECT id FROM teams WHERE VMip=?) AND mod_id=?;", [clientIP, module['id']])
            for flag in c.fetchall():
                module['flags'].append(flag[0])
        return modules
        
