package org.hazelwire.modules;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.hazelwire.main.Configuration;
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
				moduleList.add(importModuleFromDirectory(Configuration.getInstance().getModulePath()+fileList[i]));
			}
		}
		
		return moduleList;
	}
	
	public static Module importModuleFromDirectory(String path) throws Exception
	{
		File dir = new File(path);
		char fileSeperator = Generator.getInstance().getFileSeperator();
		Module tempModule = null;
		
		if(dir.isDirectory()) //There shouldn't be any files but for now we will keep it easy
		{
			String[] fileList = dir.list();
			String fileName = null;
			
			for(int a=0;a<fileList.length;a++)
			{
				if(fileList[a].endsWith(".deb"))
				{
					fileName = fileList[a];
				}
				else if(fileList[a].equals("config.xml"))
				{						
					ParserModuleConfig xmlParser = new ParserModuleConfig(dir.getPath()+fileSeperator+fileList[a]);
					tempModule = (Module) xmlParser.parseDocument();
				}
			}
			
			if(fileName == null) throw new Exception("Cannot find module");
			tempModule.setFilePath(dir.getPath()+fileSeperator+fileName);
		}
		
		return tempModule;
	}
	
	/**
	 * @todo make sure it imports teh config.xml first
	 * @param packageFile
	 * @return
	 * @throws Exception 
	 */
	public static Module importModule(String packageFilePath) throws Exception
	{
		Module tempModule = null;
		
		try
		{
			File packageFile = new File(packageFilePath);
			ZipFile zipFile = new ZipFile(packageFile);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			BufferedOutputStream dest = null;
	        BufferedInputStream in = null;
			boolean first = true;
	        String dirPath = "";
	        
			while(e.hasMoreElements())
			{
				ZipEntry entry = (ZipEntry) e.nextElement();
				/*
				if(entry.equals("config.xml"))
				{
					ParserModuleConfig xmlParser = new ParserModuleConfig(zipFile.getInputStream(entry));
					module = (Module) xmlParser.parseDocument();
				}*/
				
				if(entry.isDirectory())
				{
					dirPath = Configuration.getInstance().getModulePath()+entry.getName();
					new File(dirPath).mkdir();
				}
				else
				{
					in = new BufferedInputStream(zipFile.getInputStream(entry));
					int count;
					byte data[] = new byte[2048];
					dest = new BufferedOutputStream(new FileOutputStream(Configuration.getInstance().getModulePath()+entry.getName()));
					
					while((count = in.read(data,0,2048)) != -1)
					{
						dest.write(data);
					}
					dest.flush();
					dest.close();
					in.close();
				}
			}
			
			tempModule = importModuleFromDirectory(dirPath);
			
		}
		catch(IOException e)
		{
			
		}
		
		return tempModule;
	}
}
