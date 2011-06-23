"""
Parses the MANIFEST.xml file created by the GenerationFramework.
Available tags are: MANIFEST, module, name, deployscript, serviceport, flags, flag, points.
The module information is stored in a list of dictionaries. The dictionaries have the following keys:
    - name: The name of the module.
    - numFlags: the number of flags for the module.
    - deployScript: the path to the manage script (deploy flags and setting other options if applicable)
    - flagpoints: a list of points indicating how much points the flags are worth.
    - serviceport: the port the module uses.
"""

import xml.sax, sys
import DatabaseHandler


modules = [] #: the module information.
correctXML = True #: boolean to keep track if the XML file is well-formed.

class ManifestHandler(xml.sax.ContentHandler):
   
    isNameElement, isDeployScriptElement, startFlagsection, inFlagDefinition, startPointElement, isServicePortElement = False,False,False, False, False, False

    def startElement(self, name, attrs):
        if name == "MODULE":
            modules.append({'name':'','numFlags':0,'deployscript':'', 'flagpoints':[], 'serviceport':''}) 
        elif name == "name":
            self.isNameElement = True
        elif name == "deployscript":
            self.isDeployScriptElement = True
        elif name == "flags":
            self.startFlagSection = True
        elif name == "serviceport":
            self.isServicePortElement = True
        elif name == "flag" and self.startFlagSection:
            self.inFlagDefinition = True
        elif name == "points" and self.startFlagSection and  self.inFlagDefinition:
            modules[-1]['flagpoints'].append('')
            self.startPointElement = True

    def endElement(self, name):
        if name == "name":
            self.isNameElement = False
        elif name == "deployscript":
            self.isDeployScriptElement = False
        elif name == "flags":
            self.startFlagSection = False
        elif name == "flag":
            self.inFlagDefinition = False
        elif name == "points":
            self.startPointElement = False
        elif name == "serviceport":
            isServicePortElement = False

    def characters (self, ch):
        if self.isNameElement:
            modules[-1]['name'] += ch
        elif self.isDeployScriptElement:
            modules[-1]['deployscript'] += ch
        elif self.startPointElement:
            modules[-1]['flagpoints'][-1] += ch
        elif self.isServicePortElement:
            modules[-1]['serviceport'] += ch.strip('\r\t\n ')

def parseManifest(manifest, db):
    """
    Parse a given MANIFEST.xml file and store the parsed module information in the database, if the XML is well-formed.
    @type manifest: string
    @param manifest: path to the MANIFEST.xml file
    @type db: string
    @param db: path to the SQLite database
    @rtype: boolean
    @return: True if the XML was well-formed, False if not.
    """
    correctXML = True
    parser = xml.sax.make_parser()
    parser.setContentHandler(ManifestHandler())
    try:
        parser.parse(open(manifest,"r"))
    except:
        correctXML = False
    for module in modules:
        module['numFlags'] = len(module['flagpoints'])
        if module['numFlags'] == 0 or module['name'] == '' or module['deployscript'] == '' or module['serviceport'] == '':
            #incorrect XML
            correctXML = False
    if correctXML:
        db = DatabaseHandler.DatabaseHandler(db)
        db.addModuleInfo(modules)
        print modules
        return True
    return False

if __name__ == "__main__":
    manifest = sys.argv[1]
    db = sys.argv[2]
    print parseManifest(manifest,db)