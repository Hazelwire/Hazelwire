package org.hazelwire.test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.hazelwire.main.Generator;
import org.hazelwire.xml.ExportModuleConfiguration;



public class MainTest {

	public static void main(String[] args) throws ParserConfigurationException, TransformerException
	{
		Generator.getInstance();
		
		Generator.getInstance().getModuleSelector().selectModule(0);
		Generator.getInstance().getModuleSelector().selectModule(1);
		new ExportModuleConfiguration().exportModuleConfiguration("/home/shokora/Desktop/kanker.xml");
	}

}