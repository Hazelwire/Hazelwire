package org.hazelwire.modules;

import java.io.InputStream;
import org.w3c.dom.*;

public class ParserModuleConfig extends XMLParser
{
	String name;
	
	public ParserModuleConfig(InputStream in)
	{
		super(in);
		name = "";
	}

	@Override
	public void parseDocument() throws Exception
	{
        //get the root element
        Element docElement = dom.getDocumentElement();

        //Get a nodelist of names (should be just one...)
        NodeList nl = docElement.getElementsByTagName("name");

        if(nl != null && nl.getLength() > 0)
        {
        	//For now just get the name
        	Element el = (Element)nl.item(0);
        	this.name = extractName(el);
        }
	}
	
	public String extractName(Element el)
	{
		return getTextValue(el,"name");
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
