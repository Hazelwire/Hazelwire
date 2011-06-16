package org.hazelwire.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.hazelwire.main.Generator;
import org.hazelwire.xml.ImportModuleConfiguration;



public class MainTest {

	public static void main(String[] args) throws Exception
	{
		Generator.getInstance();
		
		Generator.getInstance().getModuleSelector().selectModule(0);
		Generator.getInstance().getModuleSelector().selectModule(1);
		//Generator.getInstance().getModuleSelector().getSelectedModules().get(1).getOption(0).setValue("blaaaaaaaa");
		//new ExportModuleConfiguration().exportModuleConfiguration("/home/shokora/Desktop/kanker.xml");
		File file = new File("/home/shokora/Desktop/kanker.xml");
		new ImportModuleConfiguration(new FileInputStream(file)).parseDocument();
		System.out.println(Generator.getInstance().getModuleSelector().getSelectedModules().get(1).getOption(0).getValue());
	}

}