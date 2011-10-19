package org.hazelwire.xml;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hazelwire.modules.Module;
import org.hazelwire.modules.Option;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class generates the configfiles that will be used by the deployscripts in the virtualmachines.
 * This is where the options come into play since the system itself doesn't use the options except for allowing the user to set them.
 * After they are set and the virtualmachine is generating they are placed on the server in a location where the module deployscript can reach them.
 * After that it's the responsibility of the deployscript to handle the options.
 * @author Tim Strijdhorst
 *
 */
public class ConfigGenerator
{
	public static String saveConfigToDisk(Module module,String filePath) throws IOException, ParserConfigurationException, TransformerException
	{
		Collection<Option> options = module.getOptions();
		Iterator<Option> iterate = options.iterator();
		
		File outputFile = new File(filePath);
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		
		Element rootElement = document.createElement("hazelwire-module-options");
		document.appendChild(rootElement);
		
		Element nameElement = document.createElement("name");
		nameElement.appendChild(document.createTextNode(module.getName()));
		rootElement.appendChild(nameElement);
		
		Element optionsElement;
		if(iterate.hasNext())		
		{
			optionsElement = document.createElement("options");
			
			while(iterate.hasNext())
			{
				Option option = iterate.next();
				
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
			
			rootElement.appendChild(optionsElement);
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
