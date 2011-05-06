import xml.sax
import DatabaseHandler

modules = []
correctXML = True

class ManifestHandler(xml.sax.ContentHandler):

    isNameElement, isBasePathElement, isDeployScriptElement, isServicePortElement, startFlagsection = False,False,False,False,False
    def startElement(self, name, attrs):
        if name == "MODULE":
            modules.append({'name':'','numFlags':0,'basepath':'','deployscript':'', 'flagpoints':[], 'serviceport':''}) 
        elif name == "name":
            self.isNameElement = True
        elif name == "basepath":
            self.isBasePathElement = True
        elif name == "deployscript":
            self.isDeployScriptElement = True
        elif name == "flags":
            self.startFlagSection = True
        elif name == "serviceport":
            self.isServicePortElement = True
        elif name == "flag" and self.startFlagSection:
            modules[-1]['flagpoints'].append(attrs.items()[0][1])

    def endElement(self, name):
        global correctXML
        if name == "name":
            self.isNameElement = False
        elif name == "basepath":
            self.isBasePathElement = False
        elif name == "deployscript":
            self.isDeployScriptElement = False
        elif name == "flags":
            self.startFlagSection = False
        elif name == "serviceport":
            isServicePortElement = False
        elif name == "MODULE":
            modules[-1]['numFlags'] = len(modules[-1]['flagpoints'])
            if modules[-1]['numFlags'] == 0 or modules[-1]['name'] == '' or modules[-1]['basepath'] == '' or modules[-1]['deployscript'] == '':
                #incorrect XML
                correctXML = False

    def characters (self, ch):
        if self.isNameElement:
            modules[-1]['name'] += ch
        elif self.isBasePathElement:
            modules[-1]['basepath'] += ch
        elif self.isDeployScriptElement:
            modules[-1]['deployscript'] += ch
        elif self.isServicePortElement:
            modules[-1]['serviceport'] += ch


def parseManifest(manifest):
    parser = xml.sax.make_parser()
    parser.setContentHandler(ManifestHandler())
    parser.parse(open(manifest,"r"))
    if correctXML:
        DatabaseHandler.addModuleInfo(modules)
        return True
    return False

if __name__ == "__main__":
    print parseManifest("../example_MANIFEST.xml")