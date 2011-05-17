package org.hazelwire.modules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

import org.hazelwire.main.Generator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ManifestGenerator
{		
	public static String saveManifestToDisk(String filePath) throws IOException, ParserConfigurationException, TransformerException
	{
		Collection<Module> modules = Generator.getInstance().getModuleSelector().getSelectedModules().values();
		Iterator<Module> iterate = modules.iterator();
		
		File outputFile = new File(filePath);
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		
		Element rootElement = document.createElement("MANIFEST");
		document.appendChild(rootElement);
		Element moduleRoot = document.createElement("MODULE");
		
		while(iterate.hasNext())
		{
			Module module = iterate.next();
			Collection<Integer> flags = module.getFlags();
			Iterator<Integer> flagIterator = flags.iterator();
			
			//<name>
			Element em = document.createElement("name");
			em.appendChild(document.createTextNode(module.getName()));
			moduleRoot.appendChild(em);
			
			//<deploypath>
			em = document.createElement("deploypath");
			em.appendChild(document.createTextNode(module.getDeployPath()));
			moduleRoot.appendChild(em);
			
			//<flags>
			em = document.createElement("flags");
			
			while(flagIterator.hasNext())
			{
				Element flagElement = document.createElement("flag");
				Element pointsElement = document.createElement("points");
				pointsElement.appendChild(document.createTextNode(String.valueOf(flagIterator.next())));
				flagElement.appendChild(pointsElement);
				em.appendChild(flagElement);
			}
			moduleRoot.appendChild(em);
		}
		
		rootElement.appendChild(moduleRoot);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    DOMSource source = new DOMSource(document);
	    StreamResult result = new StreamResult(outputFile);
	    transformer.transform(source, result);
	    String results = result.toString();
	        
		return results;
	}
}