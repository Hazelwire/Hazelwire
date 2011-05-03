package org.hazelwire.test;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.hazelwire.modules.Module;

public class MainTest
{
	public static void main(String[] args)
	{
		try
		{
			MainTest test = new MainTest();
			File zip = new File("/home/shokora/FirefoxDownload/tjerkovara-weet_ik_veel.zip");
			test.convertPackageToModule(zip);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Module convertPackageToModule(File packageFile) throws ZipException, IOException
	{
		ZipFile zipFile = new ZipFile(packageFile);
		
		Enumeration e = zipFile.entries();
		
		while(e.hasMoreElements())
		{
			System.out.println(e.nextElement());
		}
		
		return null;
	}
}