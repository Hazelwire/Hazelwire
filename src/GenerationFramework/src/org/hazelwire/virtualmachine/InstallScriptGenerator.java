package org.hazelwire.virtualmachine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.hazelwire.main.Configuration;
import org.hazelwire.main.Generator;
import org.hazelwire.modules.Module;

/**
 * This class will generate an install script that is to be executed inside of the virtualmachine. After all the modules are uploaded the execution
 * of the script will result in an automatic installation of all the modules.
 * @author Tim Strijdhorst
 *
 */
public class InstallScriptGenerator
{	
	/**
	 * @throws Exception 
	 */
	public static void saveScriptToDisk(String filePath) throws Exception
	{		
		BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
		out.write(generateInstallScript(true));
		out.flush();
	}
	
	/**
	 * Userleveldebugging means that the actual installation script, module-files and log will not be deleted.
	 * This means that you will have to delete these yourself (or not, but that might give people a LOT of advantage :P).
	 * @param userlevelDebugging
	 * @return
	 * @throws IOException
	 */
	private static String generateInstallScript(boolean userlevelDebugging) throws IOException
	{
		ArrayList<Module> modules = Generator.getInstance().getModuleSelector().getMarkedModules();
		String script = "#!/bin/bash\n";
		script += "sudo apt-get update\n"; //update the repos first or else older vm's might fail because of old packages
		script += "sudo apt-get -y install openvpn\n"; //Download openvpn if it's not already there
		script += "sudo apt-get -y install screen\n"; //TODO i should really read this in from a file -_-
		Iterator<Module> iterate = modules.iterator();
		
		while(iterate.hasNext())
		{
			Module module = iterate.next();
			String externalFilePath = Configuration.getInstance().getExternalModuleDirectory()+module.getFileNameWithoutExtension()+"/"+module.getFileName();
			script += "sudo gdebi -n "+externalFilePath+" > log\n";
		}
		
		if(userlevelDebugging)
		{
			script += "rm -rf "+Configuration.getInstance().getExternalModuleDirectory()+"\n"; //remove all the module files
			script += "rm log\n"; //debug
			script += "rm ./install.sh\n";
		}
		
		return script;
	}
}
