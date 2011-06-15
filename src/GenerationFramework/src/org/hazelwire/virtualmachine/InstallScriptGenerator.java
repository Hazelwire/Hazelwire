package org.hazelwire.virtualmachine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
		ArrayList<Module> modules = Generator.getInstance().getModuleSelector().getMarkedModules();
		String script = "#!/bin/bash\n";
		Iterator<Module> iterate = modules.iterator();
		
		while(iterate.hasNext())
		{
			Module module = iterate.next();
			String externalFilePath = Configuration.getInstance().getExternalModuleDirectory()+module.getFileNameWithoutExtension()+"/"+module.getFileName();
			script += "sudo gdebi -n "+externalFilePath+" > log\n";
		}
		
		return script;
	}
}
