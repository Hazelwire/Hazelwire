package org.hazelwire.xml;

import java.io.File;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hazelwire.main.Generator;
import org.hazelwire.modules.Flag;
import org.hazelwire.modules.Module;
import org.hazelwire.modules.Option;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class allows the exportation of the current module set configuration. This configuration includes the currently selected modules and it's settings
 * like flag and option information.
 * @author Tim Strijdhorst
 *
 */
public class ExportModuleConfiguration
{
	public String exportModuleConfiguration(String filePath) throws ParserConfigurationException, TransformerException
	{
		Collection<Module> modules = Generator.getInstance().getModuleSelector().getSelectedModules().values();
		
		File outputFile = new File(filePath);
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		
		Element rootElement = document.createElement("hazelwire-module-configuration");
		document.appendChild(rootElement);
		
		for(Module module : modules)
		{
			Collection<Option> options = module.getOptions();
			Collection<Flag> flags = module.getFlags();
			
			Element moduleElement = document.createElement("module");
			
			Element nameElement = document.createElement("name");
			nameElement.appendChild(document.createTextNode(module.getName()));
			moduleElement.appendChild(nameElement);
			
			if(module.getModulePackage() != null)
			{
				Element packageElement = document.createElement("package");
				Element packageNameElement = document.createElement("name");
				packageNameElement.appendChild(document.createTextNode(module.getModulePackage().getName()));
				packageElement.appendChild(packageNameElement);
				moduleElement.appendChild(packageElement);
			}
			
			Element optionsElement = document.createElement("options");		
			
			for(Option option : options)
			{				
				Element optionElement = document.createElement("option");
				
				//<name>
				Element em = document.createElement("name");
				em.appendChild(document.createTextNode(option.getName()));
				optionElement.appendChild(em);
				
				//value
				em = document.createElement("value");
				em.appendChild(document.createTextNode(option.getValue()));
				em.setAttribute("type", option.getType());
				optionElement.appendChild(em);
				
				optionsElement.appendChild(optionElement);
			}
			
			moduleElement.appendChild(optionsElement);
			
			//<flags>
			
			Element flagsElement = document.createElement("flags");
			for(Flag flag : flags)
			{
				Element flagElement = document.createElement("flag");
				Element pointsElement = document.createElement("points");
				pointsElement.appendChild(document.createTextNode(String.valueOf(flag.getPoints())));
				flagElement.appendChild(pointsElement);
				flagsElement.appendChild(flagElement);
			}
			
			moduleElement.appendChild(flagsElement);
			rootElement.appendChild(moduleElement);
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    DOMSource source = new DOMSource(document);
	    StreamResult result = new StreamResult(outputFile);
	    transformer.transform(source, result);
	    String results = result.toString();
	        
		return results;
	}
}
