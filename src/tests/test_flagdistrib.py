import unittest, sqlite3, socket, os

from ClientBot import FlagRequest
from Mainframe.ClientHandler import FlagAdministration, DatabaseHandler

class FlagDistributionTestCase(unittest.TestCase):
    
    def setUp(self):
        try:
            os.remove("temp.db") #removing just to be sure
        except:
            pass
        self.temp = sqlite3.connect("temp.db")
        self.cursor = self.temp.cursor()
        self.cursor.executescript("""BEGIN TRANSACTION;
CREATE TABLE flagpoints (flag_id NUMERIC, mod_id NUMERIC, points NUMERIC);
INSERT INTO flagpoints VALUES(1,1,12);
INSERT INTO flagpoints VALUES(2,1,25);
INSERT INTO flagpoints VALUES(3,1,10);
INSERT INTO flagpoints VALUES(4,1,15);
INSERT INTO flagpoints VALUES(5,1,83);
INSERT INTO flagpoints VALUES(1,2,10);
INSERT INTO flagpoints VALUES(2,2,32);
CREATE TABLE flags (flag_id NUMERIC, mod_id NUMERIC, team_id NUMERIC, flag TEXT);
CREATE TABLE modules (id INTEGER PRIMARY KEY, name TEXT, numFlags NUMERIC, basepath TEXT, deployscript TEXT);
INSERT INTO modules VALUES(1,'test1',5,'/home/exploit1/','deploy/deployFlags');
INSERT INTO modules VALUES(2,'test2',2,'/home/exploit2/','deploy/install.py');
CREATE TABLE teams (id INTEGER PRIMARY KEY, name TEXT, VMip NUMERIC);
INSERT INTO teams VALUES(1,'Henkies','10.0.8.1');
INSERT INTO teams VALUES(2,'Sjakies','10.0.10.1');
INSERT INTO teams VALUES(3,'lokale lutsers','127.0.0.1');
COMMIT;""")
        self.temp.commit()
        self.cursor.close()
        self.temp.close()
        self.db = DatabaseHandler.DatabaseHandler("temp.db")
        FlagAdministration.setDB("temp.db")
        FlagAdministration.getModules()
        FlagAdministration.startServer('127.0.0.1',9999)
        
    def tearDown(self):
        os.remove("temp.db")
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect(('127.0.0.1',9999))
        sock.send("REQSHUTDOWN\n")
        sock.close()
        
    def test_flagdistrib_1_requestFlags(self):
        self.assertTrue(FlagRequest.requestFlags('127.0.0.1',9999))
        res = [{'name':'test1','basepath':'/home/exploit1/','deployscript':'deploy/deployFlags'},{'name':'test2','basepath':'/home/exploit2/','deployscript':'deploy/install.py'}]
        res2 = FlagRequest.moduleFlags
        #get the flag information from the database
        db = sqlite3.connect("temp.db")
        c = db.cursor()
        flags1 = c.execute("SELECT flag FROM flags WHERE mod_id = 1;").fetchall()
        flags2 = c.execute("SELECT flag FROM flags WHERE mod_id = 2;").fetchall()
        c.close()
        db.close()
        res[0]['flags'] = []
        res[1]['flags'] = []
        for flag in flags1:
            res[0]['flags'].append(flag[0])
        for flag in flags2:
            res[1]['flags'].append(flag[0])
        self.assertEqual(res,res2)
        
        #requesting again, should return a failure
        self.assertFalse(FlagRequest.requestFlags('127.0.0.1',9999))