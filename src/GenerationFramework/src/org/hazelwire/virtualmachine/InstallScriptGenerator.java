package org.hazelwire.virtualmachine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;

import org.hazelwire.main.Configuration;
import org.hazelwire.main.Generator;
import org.hazelwire.modules.Module;

public class InstallScriptGenerator
{	
	/**
	 * @throws Exception 
	 */
	public static void saveScriptToDisk(String filePath) throws Exception
	{		
		BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
		out.write(generateInstallScript());
		out.flush();
	}
	
	private static String generateInstallScript() throws IOException
	{
		Collection<Module> modules = Generator.getInstance().getModuleSelector().getSelectedModules().values();
		String script = "#!/bin/sh\r\n";
		Iterator<Module> iterate = modules.iterator();
		
		while(iterate.hasNext())
		{
			Module module = iterate.next();
			String externalFilePath = Configuration.getInstance().getExternalModuleDirectory()+module.getFileName();
			script += "gdebi -n "+externalFilePath+"\r\n";
		}
		
		script += "nc "+InetAddress.getLocalHost()+" "+Configuration.getInstance().getCallbackPort()+"\r\n";
		
		return script;
	}
}
