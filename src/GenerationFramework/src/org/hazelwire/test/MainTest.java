package org.hazelwire.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.hazelwire.main.Configuration;
import org.hazelwire.main.FileName;
import org.hazelwire.main.Generator;
import org.hazelwire.xml.ImportModuleConfiguration;



public class MainTest {

	public static void main(String[] args) throws Exception
	{
		FileName bla = new FileName("output/test.ova",Generator.getInstance().getFileSeperator(),'.');
		bla.setFileName("KANKER");
	}
}