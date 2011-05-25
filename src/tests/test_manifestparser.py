import unittest, sqlite3, os

from Mainframe.ClientHandler import ManifestParser

class ManifestParserTestCase(unittest.TestCase):

       def setUp(self):
              try:
                     os.remove("temp.db") #removing just to be sure
              except:
                     pass
              self.db = sqlite3.connect("temp.db")
              self.cursor = self.db.cursor()
              self.cursor.executescript("""BEGIN TRANSACTION;
      CREATE TABLE flagpoints (flag_id NUMERIC, mod_id NUMERIC, points NUMERIC);
      CREATE TABLE flags (flag_id NUMERIC, mod_id NUMERIC, team_id NUMERIC, flag TEXT);
      CREATE TABLE modules (serviceport NUMERIC, id INTEGER PRIMARY KEY, name TEXT, numFlags NUMERIC, deployscript TEXT);
      CREATE TABLE teams (id INTEGER PRIMARY KEY, name TEXT, VMip NUMERIC);
      INSERT INTO teams VALUES(1,'Henkies','10.0.8.1');
      INSERT INTO teams VALUES(2,'Sjakies','10.0.10.1');
      INSERT INTO teams VALUES(3,'lokale lutsers','127.0.0.1');
      COMMIT;""")
              self.db.commit()
              self.cursor.close()
          
       def tearDown(self):
              self.db.close()
              os.remove("temp.db")
              
       def test_manifestparser_1_good_xml(self):
              self.assertTrue(ManifestParser.parseManifest("tests/good_MANIFEST.xml", "temp.db"))
              #get results from database
              c = self.db.cursor()
              c.execute("SELECT * FROM modules;")
              modules = c.fetchall()
              c.execute("SELECT * FROM flagpoints;")
              flagpoints = c.fetchall()
              real_flagpoints = [(1,1,12),(2,1,25),(3,1,10),(4,1,15),(5,1,83),(1,2,10),(2,2,32)]
              real_modules = [{'name':'test1','numFlags':5,'deployscript':'deploy/deployFlags'},{'name':'test2','numFlags':2,'deployscript':'deploy/install.py'}]
              self.assertEqual(real_flagpoints, flagpoints)
              self.assertEqual(real_flagpoints, flagpoints)
              
       def test_manifestparser_2_bad_xml(self):
              self.assertFalse(ManifestParser.parseManifest("tests/bad_MANIFEST.xml","temp.db"))
      
       def test_manifestparser_3_empty_xml(self):
              self.assertFalse(ManifestParser.parseManifest("tests/empty_MANIFEST.xml","temp.db"))
