/*******************************************************************************
 * Copyright (c) 2011 The Hazelwire Team.
 *     
 * This file is part of Hazelwire.
 * 
 * Hazelwire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Hazelwire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
import java.util.zip.ZipInputStream;

import org.hazelwire.main.Configuration;
import org.hazelwire.main.Generator;
import org.hazelwire.xml.ParserModuleConfig;

/**
 * This class has a series of static functions that allows the system to import modules in different ways.
 * It can both import invidual modules into the systems that are packaged in zip format. It can also scan the
 * module directory for modules and use those in the system.
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
					
					try
					{
						tempModule = (Module) xmlParser.parseDocument();
					}
					catch(Exception e)
					{
						System.err.println("ERROR: Failed to parse one or more module configurations.\n Exception-message: "+e.getMessage());
					}
				}
			}
			
			if(fileName == null) throw new Exception("Cannot find module");
			tempModule.setFilePath(dir.getPath()+fileSeperator+fileName);
			tempModule.setRelativedir(dir.getName());
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
	        ZipInputStream in = null;
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
					in = new ZipInputStream(new BufferedInputStream(zipFile.getInputStream(entry)));
					int count;
					byte data[] = new byte[2048];
					dest = new BufferedOutputStream(new FileOutputStream(Configuration.getInstance().getModulePath()+entry.getName()),2048);
					
					while((count = in.read(data,0,2048)) != -1)
					{
						dest.write(data,0,count);
					}
					dest.flush();
					dest.close();
				}
			}
			in.close();
			tempModule = importModuleFromDirectory(dirPath);
			
		}
		catch(IOException e)
		{
			
		}
		
		return tempModule;
	}
}
