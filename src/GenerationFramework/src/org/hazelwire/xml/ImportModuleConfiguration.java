package org.hazelwire.xml;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.hazelwire.main.Generator;
import org.hazelwire.modules.Module;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class allows the system to synchronize the configuration of the selected module set with a previously exported one by reading
 * the exported XML file and changing the system likewise.
 * @author Tim Strijdhorst
 *
 */
public class ImportModuleConfiguration extends XMLParser
{

	public ImportModuleConfiguration(String filePath) throws FileNotFoundException
	{
		super(filePath);
	}
	
	public ImportModuleConfiguration(InputStream in)
	{
		super(in);
	}

	@Override
	public Object parseDocument() throws Exception
	{
		Element docElement = dom.getDocumentElement();
		NodeList moduleNl = docElement.getElementsByTagName("module");
		
		if(moduleNl != null && moduleNl.getLength() >0)
		{
			for(int a=0;a<moduleNl.getLength();a++)
			{
				Element moduleElement = (Element)moduleNl.item(a);
				String name = "";
				String packageName = "";
				
				name = this.getTextValue(moduleElement, "name");			
			
			
				NodeList packageNl = moduleElement.getElementsByTagName("package");
				
				if(packageNl != null && packageNl.getLength() >0)
				{
					Element el = (Element)packageNl.item(0);
					
					packageName = this.getTextValue(el, "name");			
				}
				
				Module tempModule =	Generator.getInstance().getModuleSelector().getModuleByNameAndPackage(name, packageName); //this is a reference
				
				if(tempModule == null) throw new Exception("Malformed configuration file");
				
		        NodeList flagsNl = moduleElement.getElementsByTagName("flag");
		        
		        if(flagsNl != null)
		        {
		        	for(int i=0;i<flagsNl.getLength();i++)
		        	{
		        		Element el = (Element)flagsNl.item(i);        		
		        		tempModule.getFlag(i).setPoints(this.getIntValue(el, "points")); //import the flag configuration
		        	}        	
		        }
		        
		        NodeList optionNl = moduleElement.getElementsByTagName("option");
		        
		        if(optionNl != null)
		        {
		        	for(int i=0;i<optionNl.getLength();i++)
		        	{
		        		Element el = (Element)optionNl.item(i);
		        		
		        		tempModule.getOption(i).setValue(getTextValue(el, "value")); //import the option configuration
		        	}
		        }
		        
		        Generator.getInstance().getModuleSelector().selectModule(tempModule.getId());
			}
		}
		
		/**
		 * This is very ugly but we need to do this at the moment because of the parent class we need... have to fix that.
		 * @todo fix the return statements
		 */
		return null;
	}
}
