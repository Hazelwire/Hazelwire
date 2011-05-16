package org.hazelwire.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 
 * @author Tim Strijdhorst
 *
 */
public class ModuleHandler
{
	public static ArrayList<Module> scanForModules(String filePath) throws Exception
	{
		File directory = new File(filePath); //path should lead to the directory where the modules are stored
		ArrayList<Module> moduleList;
		
		if(directory.isFile())
		{
			throw new Exception("Path is not a directory");
		}
		else
		{
			String[] fileList = directory.list();
			moduleList = new ArrayList<Module>();
			
			for(int i=0;i<fileList.length;i++)
			{
				File packageFile = new File(filePath+fileList[i]);
				
				if(packageFile.isFile()) //There shouldn't be any directories but for now we will keep it easy
				{
					moduleList.add(convertPackageToModule(packageFile));
				}
			}
		}
		
		return moduleList;
	}
	
	/**
	 * @todo implement the rest of the specification, right now it just extracts the name
	 * @param packageFile
	 * @return
	 * @throws Exception 
	 */
	public static Module convertPackageToModule(File packageFile) throws Exception
	{
		ZipFile zipFile = new ZipFile(packageFile);
		String name = "";
		Enumeration e = zipFile.entries();
		Module module = null;
		
		while(e.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry) e.nextElement();
			//There has to be a better way for this... linear search is always retarded
			if(entry.equals("config.xml"))
			{
				ParserModuleConfig xmlParser = new ParserModuleConfig(zipFile.getInputStream(entry));
				module = (Module) xmlParser.parseDocument();
			}
		}
		
		if(name.equals("")) throw new Exception("Incorrect XML file");
		
		return module;
	}
}
