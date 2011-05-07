import unittest, sqlite3, os
from DatabaseHandler import DatabaseHandler

class DatabaseHandlerTestCase(unittest.TestCase):
    
    def setUp(self):
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
CREATE TABLE modules (serviceport NUMERIC, id INTEGER PRIMARY KEY, name TEXT, numFlags NUMERIC, basepath TEXT, deployscript TEXT);
INSERT INTO modules VALUES(31337,1,'test1',5,'/home/exploit1/','deploy/deployFlags');
INSERT INTO modules VALUES(61281,2,'test2',2,'/home/exploit2/','deploy/install.py');
CREATE TABLE teams (id INTEGER PRIMARY KEY, name TEXT, VMip NUMERIC);
INSERT INTO teams VALUES(1,'Henkies','10.0.8.1');
INSERT INTO teams VALUES(2,'Sjakies','10.0.10.1');
INSERT INTO teams VALUES(3,'lokale lutsers','127.0.0.1');
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
        res = [{'name':'test1','numFlags':5,'basepath':'/home/exploit1/','deployscript':'deploy/deployFlags'},{'name':'test2','numFlags':2,'basepath':'/home/exploit2/','deployscript':'deploy/install.py'}]
        self.assertEqual(res, self.db.getModuleInfo())
        
    def test_db_4_addModuleInfo(self):
        #First clear out the info put in the db by setUp()
        db = sqlite3.connect("temp.db")
        c = db.cursor()
        c.execute("DELETE FROM flagpoints;")
        c.execute("DELETE FROM modules;")
        db.commit()
        
        res = [{'name':'test1','numFlags':5,'basepath':'/home/exploit1/','deployscript':'deploy/deployFlags'},{'name':'test2','numFlags':2,'basepath':'/home/exploit2/','deployscript':'deploy/install.py'}]
        modules = [{'deployscript': u'deploy/deployFlags', 'numFlags': 5, 'basepath': u'/home/exploit1/', 'name': u'test1', 'flagpoints': [u'12', u'25', u'10', u'15', u'83'], 'serviceport':31337}, {'deployscript': u'deploy/install.py', 'numFlags': 2, 'basepath': u'/home/exploit2/', 'name': u'test2', 'flagpoints': [u'10', u'32'], 'serviceport':61281}]
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
    