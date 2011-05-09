package org.hazelwire.test;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.hazelwire.main.Configuration;
import org.hazelwire.modules.Module;

public class MainTest
{
	public static void main(String[] args)
	{
		Configuration lol;
		try
		{
			lol = new Configuration();
			System.out.println(lol.getUserDir());
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			System.out.println("PRINT LOL BIATCH");
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