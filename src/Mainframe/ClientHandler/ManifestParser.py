import xml.sax, sys
import DatabaseHandler

modules = []
correctXML = True

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
        global correctXML
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
            modules[-1]['serviceport'] += ch



def parseManifest(manifest, db):
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
        return True
    return False

if __name__ == "__main__":
    manifest = sys.argv[1]
    db = sys.argv[2]
    print parseManifest(manifest,db)
