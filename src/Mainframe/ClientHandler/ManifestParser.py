import xml.sax, sys
import DatabaseHandler

modules = []
correctXML = True

class ManifestHandler(xml.sax.ContentHandler):

    isNameElement, isDeployScriptElement, startFlagsection, inFlagDefinition, startPointElement = False,False,False, False, False

    def startElement(self, name, attrs):
        if name == "MODULE":
            modules.append({'name':'','numFlags':0,'deployscript':'', 'flagpoints':[]}) 
        elif name == "name":
            self.isNameElement = True
        elif name == "deployscript":
            self.isDeployScriptElement = True
        elif name == "flags":
            self.startFlagSection = True
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
        elif name == "MODULE":
            modules[-1]['numFlags'] = len(modules[-1]['flagpoints'])
            if modules[-1]['numFlags'] == 0 or modules[-1]['name'] == '' or modules[-1]['deployscript'] == '':
                #incorrect XML
                correctXML = False

    def characters (self, ch):
        if self.isNameElement:
            modules[-1]['name'] += ch
        elif self.isDeployScriptElement:
            modules[-1]['deployscript'] += ch
        elif self.startPointElement:
            modules[-1]['flagpoints'][-1] += ch


def parseManifest(manifest, db):
    parser = xml.sax.make_parser()
    parser.setContentHandler(ManifestHandler())
    parser.parse(open(manifest,"r"))
    if correctXML:
        db = DatabaseHandler.DatabaseHandler(db)
        db.addModuleInfo(modules)
        return True
    return False

if __name__ == "__main__":
    manifest = sys.argv[1]
    db = sys.argv[2]
    print parseManifest(manifest,db)
    
