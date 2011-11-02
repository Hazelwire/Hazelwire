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
import unittest, sqlite3, os, threading, socket, time
from Mainframe.ClientHandler import SanityCheckService, P2PSanityCheck
from Mainframe.ClientHandler.DatabaseHandler import DatabaseHandler
from ClientBot.PeerToPeerRequestListener import PeerToPeerRequestListener

class SanityCheckTestCase(unittest.TestCase):
    
    def setUp(self):
        try:
            os.remove('temp.db')
        except:
            pass
        self.temp = sqlite3.connect("temp.db")
        self.cursor = self.temp.cursor()
        self.cursor.executescript("""BEGIN TRANSACTION;
CREATE TABLE flagpoints (flag_id NUMERIC, mod_id NUMERIC, points NUMERIC);
CREATE TABLE flags (flag_id NUMERIC, mod_id NUMERIC, team_id NUMERIC, flag TEXT);
CREATE TABLE modules (id INTEGER PRIMARY KEY, name TEXT, numFlags NUMERIC, deployscript TEXT, serviceport INTEGER);
INSERT INTO modules VALUES(1,'test1',5,'deploy/deployFlags', 31337);
INSERT INTO modules VALUES(2,'test2',2,'deploy/install.py', 61281);
CREATE TABLE teams (id INTEGER PRIMARY KEY, name TEXT, VMip NUMERIC);
INSERT INTO teams VALUES(1,'lokale lutsers','127.0.0.1');
CREATE TABLE config (config_name TEXT, type TEXT, value TEXT);
INSERT INTO config VALUES('normal_interval','sanitycheck',3);
INSERT INTO config VALUES('p2p_interval','sanitycheck',10);
CREATE TABLE evil_teams (IP TEXT, port NUMERIC, time NUMERIC, reporterIP NUMERIC, seen NUMERIC);
COMMIT;""")
        self.temp.commit()
        self.cursor.close()
        self.temp.close()
        self.db = DatabaseHandler("temp.db")
        self.sanitychecker = SanityCheckService.SanityChecker('temp.db')
        
    def tearDown(self):
        os.remove("temp.db")
        
    def listenPort(self, port):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
        s.bind(('127.0.0.1',port))
        s.listen(1)
        conn, addr = s.accept()
        data = conn.recv(1024).strip()
        
    def test_sanitycheck_1_NormalCheck_notlistening(self):
        self.sanitychecker.NormalCheck() #this should have added 127.0.0.1 to the database.
        db = sqlite3.connect("temp.db")
        c = db.cursor()
        res = c.execute("SELECT IP, port FROM evil_teams").fetchall()
        expected_res = [('127.0.0.1',31337),('127.0.0.1',61281)]
        self.assertEqual(res, expected_res)
        
    def test_sanitycheck_2_NormalCheck_listening(self):
        #start listening
        t1 = threading.Thread(target=self.listenPort, args=[31337])
        t1.start()
        t2 = threading.Thread(target=self.listenPort, args=[61281])
        t2.start()
        time.sleep(.5)
        self.sanitychecker.NormalCheck()
        db = sqlite3.connect("temp.db")
        c = db.cursor()
        res = c.execute("SELECT IP, port FROM evil_teams").fetchall()
        self.assertEqual(res,[]) #this should be empty as the services are running
        
    def test_sanitycheck_3_P2PCheck_notlistening(self):
        p2plisten = PeerToPeerRequestListener('127.0.0.1',9998)
        tl = threading.Thread(target=p2plisten.startServer)
        tl.start()
        time.sleep(.5)
        self.sanitychecker.contestants.append('127.0.0.1') #hack another localhost in there, otherwise nothing would be scanned.
        self.sanitychecker.P2PCheck()
               
        db = sqlite3.connect("temp.db")
        c = db.cursor()               
        res = c.execute("SELECT IP, port, reporterIP FROM evil_teams").fetchall()
        expected_res = [('127.0.0.1',31337, '127.0.0.1'),('127.0.0.1',61281,'127.0.0.1'), ('127.0.0.1',31337, '127.0.0.1'),('127.0.0.1',61281, '127.0.0.1')]
        self.assertEqual(res, expected_res)
        
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect(('127.0.0.1',9998))
        s.send("LISTENSTOP")
        s.close()
        
    def test_sanitycheck_4_P2PCheck_listening(self):        
        t1 = threading.Thread(target=self.listenPort, args=[31337])
        t1.start()
        t2 = threading.Thread(target=self.listenPort, args=[61281])
        t2.start()
        p2plisten = PeerToPeerRequestListener('127.0.0.1',9998)
        tl = threading.Thread(target=p2plisten.startServer)
        tl.start()
        time.sleep(0.5)
        self.sanitychecker.contestants.append('127.0.0.1') 
        self.sanitychecker.P2PCheck()
               
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect(('127.0.0.1',9998))
        s.send("LISTENSTOP")
        s.close()
        
        db = sqlite3.connect("temp.db")
        c = db.cursor()               
        res = c.execute("SELECT IP, port, reporterIP FROM evil_teams").fetchall()
        self.assertEqual(res, [('127.0.0.1',31337, '127.0.0.1'),('127.0.0.1',61281, '127.0.0.1')])
