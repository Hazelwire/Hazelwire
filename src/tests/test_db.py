import unittest, sqlite3, os
from Mainframe.ClientHandler.DatabaseHandler import DatabaseHandler

class DatabaseHandlerTestCase(unittest.TestCase):
    
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
CREATE TABLE modules (serviceport NUMERIC, id INTEGER PRIMARY KEY, name TEXT, numFlags NUMERIC, deployscript TEXT);
INSERT INTO modules VALUES(31337, 1,'test1',5,'deploy/deployFlags');
INSERT INTO modules VALUES(61281, 2,'test2',2,'deploy/install.py');
CREATE TABLE teams (id INTEGER PRIMARY KEY, name TEXT, VMip NUMERIC);
INSERT INTO teams VALUES(1,'Henkies','10.0.8.1');
INSERT INTO teams VALUES(2,'Sjakies','10.0.10.1');
INSERT INTO teams VALUES(3,'lokale lutsers','127.0.0.1');
CREATE TABLE config (config_name TEXT, type TEXT, value TEXT);
INSERT INTO config VALUES('normal_interval','sanitycheck',3);
INSERT INTO config VALUES('p2p_interval','sanitycheck',10);
CREATE TABLE evil_teams (IP TEXT, port NUMERIC, time NUMERIC, reporterIP NUMERIC,  seen NUMERIC);
COMMIT;""")
        self.temp.commit()
        self.cursor.close()
        self.temp.close()
        self.db = DatabaseHandler("temp.db")
        
    def tearDown(self):
        os.remove("temp.db")
        
    def test_db_1_addFlag(self):
        #Add a new flag
        self.assertTrue(self.db.addFlags('test1', ['FLGHF4yZkrXBtPuIVLbNULcaJ9zDYwAAEcZ48cATSniwwnMTprrIf2OJPssWiizw'], '127.0.0.1'))
        #try to add the same flag to a different team
        self.assertFalse(self.db.addFlags('test2,',['FLGHF4yZkrXBtPuIVLbNULcaJ9zDYwAAEcZ48cATSniwwnMTprrIf2OJPssWiizw'], '10.0.8.1'))
    
    def test_db_2_checkClientIP(self):
        #Client with no flags = False
        self.assertFalse(self.db.checkClientIP('127.0.0.1'))
        #Add a flag to a team, checkClientIP should return false
        self.db.addFlags('test1', ['FLGHF4yZkrXBtPuIVLbNULcaJ9zDYwAAEcZ48cATSniwwnMTprrIf2OJPssWiizw'], '127.0.0.1')
        self.assertTrue(self.db.checkClientIP('127.0.0.1'))
        
    def test_db_3_getModuleInfo(self):
        res = [{'name':'test1','numFlags':5,'deployscript':'deploy/deployFlags'},{'name':'test2','numFlags':2,'deployscript':'deploy/install.py'}]
        self.assertEqual(res, self.db.getModuleInfo())
        
    def test_db_4_addModuleInfo(self):
        #First clear out the info put in the db by setUp()
        db = sqlite3.connect("temp.db")
        c = db.cursor()
        c.execute("DELETE FROM flagpoints;")
        c.execute("DELETE FROM modules;")
        db.commit()
        

        res = [{'name':'test1','numFlags':5,'deployscript':'deploy/deployFlags'},{'name':'test2','numFlags':2,'deployscript':'deploy/install.py'}]
        modules = [{'deployscript': u'deploy/deployFlags', 'numFlags': 5,'name': u'test1', 'flagpoints': [u'12', u'25', u'10', u'15', u'83'], 'serviceport':31337}, {'deployscript': u'deploy/install.py', 'numFlags': 2, 'name': u'test2', 'flagpoints': [u'10', u'32'], 'serviceport':61281}]
        self.db.addModuleInfo(modules)
        self.assertEquals(res, self.db.getModuleInfo()) #check first part
        res2 = c.execute("SELECT * FROM flagpoints;").fetchall()
        flagpoints = [(1,1,12),(2,1,25),(3,1,10),(4,1,15),(5,1,83),(1,2,10),(2,2,32)] #got that from the example manifest
        self.assertEqual(res2,flagpoints)

    def test_db_5_getClientIPs(self):
        res = [('10.0.8.1'),('10.0.10.1'),('127.0.0.1')]
        self.assertEqual(res,self.db.getClientIPs())
            
    def test_db_6_getModulePorts(self):
        res = [(31337),(61281)]
        self.assertEqual(res, self.db.getModulePorts())   
    
    def test_db_7_getIntervals(self):
        res = (3,10)
        self.assertEqual(res, self.db.getIntervals())
    
    def test_db_8_addSuspiciousContestant(self):
        self.db.addSuspiciousContestant('127.0.0.1',31337,'127.0.0.1')
        db = sqlite3.connect("temp.db")
        c = db.cursor()
        c.execute("SELECT * FROM evil_teams;")
        res = c.fetchall()
        self.assertTrue(len(res)==1)
        self.assertTrue(res[0][0] == '127.0.0.1')
        self.assertTrue(res[0][1] == 31337)
        self.assertTrue(res[0][3] == '127.0.0.1')
        self.assertTrue(res[0][4] == 1)
