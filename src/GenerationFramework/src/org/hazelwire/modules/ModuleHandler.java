package org.hazelwire.modules;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.hazelwire.main.Generator;
import org.hazelwire.xml.ParserModuleConfig;

/**
 * 
 * @author Tim Strijdhorst
 *
 */
public class ModuleHandler
{
	/**
	 * The modules should be in the module directory in different directories for every module
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Module> scanForModules(String filePath) throws Exception
	{
		File directory = new File(filePath); //path should lead to the directory where the modules are stored
		ArrayList<Module> moduleList;
		char fileSeperator = Generator.getInstance().getFileSeperator();
		
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
				File subdir = new File(filePath+fileSeperator+fileList[i]);
				
				if(subdir.isDirectory()) //There shouldn't be any files but for now we will keep it easy
				{
					String[] fileList2 = subdir.list();
					String fileName = null;
					Module module = null;
					
					for(int a=0;a<fileList2.length;a++)
					{
						if(fileList2[a].endsWith(".deb"))
						{
							fileName = fileList2[a];
						}
						else if(fileList2[a].equals("config.xml"))
						{
							FileInputStream configFile = new FileInputStream(new File(subdir.getPath()+fileSeperator+fileList2[a]));							
							ParserModuleConfig xmlParser = new ParserModuleConfig(configFile);
							module = (Module) xmlParser.parseDocument();
						}
					}
					
					if(fileName == null) throw new Exception("Cannot find module");
					else
					{
						module.setFilePath(subdir.getPath()+fileSeperator+fileName);
						moduleList.add(module);
					}
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
	public static Module importModule(File packageFile) throws Exception
	{
		Module module = null;
		try
		{
			ZipFile zipFile = new ZipFile(packageFile);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			BufferedOutputStream dest = null;
	        BufferedInputStream in = null;
			
			while(e.hasMoreElements())
			{
				ZipEntry entry = (ZipEntry) e.nextElement();
				if(entry.equals("config.xml"))
				{
					ParserModuleConfig xmlParser = new ParserModuleConfig(zipFile.getInputStream(entry));
					module = (Module) xmlParser.parseDocument();
				}
				else
				{
					//extract the rest of the files to the harddisk (which should actually just be another .deb)
					in = new BufferedInputStream(zipFile.getInputStream(entry));
					int count;
					byte data[] = new byte[2048];
					dest = new BufferedOutputStream(new FileOutputStream(entry.getName()),2048);
					
					while((count = in.read(data,0,2048)) != -1)
					{
						dest.write(data);
					}
					dest.flush();
					dest.close();
					in.close();
				}
			}
		}
		catch(IOException e)
		{
			
		}
		
		return module;
	}
}
