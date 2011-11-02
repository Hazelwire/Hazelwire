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
		
		int i = 0;
		while(iterate.hasNext())
		{
			Module module = iterate.next();
			String externalFilePath = Configuration.getInstance().getExternalModuleDirectory()+module.getRelativeDir()+"/"+module.getFileName();
			script += "sudo gdebi -n "+externalFilePath+" > log"+(i++)+"\n";
		}
		
		if(!userlevelDebugging)
		{
			script += "rm -rf "+Configuration.getInstance().getExternalModuleDirectory()+"\n"; //remove all the module files
			script += "rm log\n"; //debug
			script += "rm ./install.sh\n";
		}
		
		return script;
	}
}
