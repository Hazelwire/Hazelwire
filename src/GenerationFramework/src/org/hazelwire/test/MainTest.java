package org.hazelwire.test;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.hazelwire.main.Configuration;
import org.hazelwire.modules.Module;
import org.hazelwire.virtualmachine.VMHandler;

public class MainTest
{
	public static void main(String[] args)
	{
		VMHandler vmHandler = new VMHandler("/usr/bin/vboxmanage","HazelwireTest","/home/shokora/test/HazelwireTest.ova", true);
		try
		{
			//vmHandler.importVM("/home/shokora/test/HazelwireTest.ova","HazelwireTest");
			vmHandler.startVM();
			vmHandler.stopVM();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
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