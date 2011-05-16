package org.hazelwire.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.hazelwire.modules.Module;
import org.hazelwire.modules.ModuleSelector;
import org.hazelwire.virtualmachine.SSHConnection;
import org.hazelwire.virtualmachine.VMHandler;

public class Generator
{
	private static Generator instance;
	private ModuleSelector moduleSelector;
	private TextInterface tui;
	Configuration config;
	
	public static synchronized Generator getInstance()
	{
		if(instance == null)
		{
			instance = new Generator();
		}
		
		return instance;
	}
	
	private Generator()
	{
		moduleSelector = new ModuleSelector();
		tui = new CLI();
		
		try
		{
			//Initialize configuration
	    	config = Configuration.getInstance();
	    	config.loadDefaultProperties("config/defaultProperties");
	    	config.loadUserProperties();
		}
		catch(IOException e)
		{
			tui.println("ERROR: Cannot load the configuration, exiting.");
			System.exit(0);
		}
	}

	public ModuleSelector getModuleSelector()
	{
		return moduleSelector;
	}
	
	public ArrayList<String> getAvailableModules()
	{
		HashMap<Integer,Module> availableModules = moduleSelector.getAvailableModules();
		Iterator<Integer> iterator = availableModules.keySet().iterator();
		ArrayList<String> descriptions = new ArrayList<String>();
		
		while(iterator.hasNext())
		{
			descriptions.add(availableModules.get(iterator.next()).toString());
		}
		
		return descriptions;
	}
	
	public ArrayList<String> getSelectedModules()
	{
		HashMap<Integer,Module> enabledModules = moduleSelector.getSelectedModules();
		Iterator<Integer> iterator = enabledModules.keySet().iterator();
		ArrayList<String> descriptions = new ArrayList<String>();
		
		while(iterator.hasNext())
		{
			descriptions.add(enabledModules.get(iterator.next()).toString());
		}
		
		return descriptions;
	}
	
	public void generateVM() throws Exception
	{
    	VMHandler vmHandler = new VMHandler(config.getVirtualBoxPath(), config.getVMName(), config.getVMPath(), true);
    	vmHandler.importVM();
    	vmHandler.addForward("ssh", config.getSSHHostPort(), config.getSSHGuestPort(), "TCP");
    	vmHandler.startVM();
    	if(vmHandler.discoverBootedVM(config.getSSHHostPort()))
    	{
	    	SSHConnection ssh = new SSHConnection("localhost",config.getSSHHostPort(),config.getSSHUsername(),config.getSSHPassword());
	    	uploadModules(ssh);
	    	generateInstallScript();
	    	executeInstallScript();
	    	vmHandler.stopVM();
	    	vmHandler.removeForward("ssh");
	    	vmHandler.exportVM(vmHandler.getVmName(),config.getVMExportPath());
    	}
    	else
    	{
    		throw new Exception("Could not connect to the virtualmachine");
    	}
	}
	
	public void uploadModules(SSHConnection ssh)
	{
		Collection<Module> modules = moduleSelector.getSelectedModules().values();
		Iterator<Module> iterate = modules.iterator();
		
		while(iterate.hasNext())
		{
			FileName modulePath = new FileName(iterate.next().getFilePath(),'/','.');
			
			try
			{
				ssh.scpUpload(modulePath.getPath(),config.getExternalModuleDirectory()+modulePath.getFilename());
			}
			catch(Exception e)
			{
				tui.println("ERROR: something went wrong while transferring the modules to the VM");
			}
		}
	}
	
	public void executeInstallScript()
	{
		/**
		 * @todo execute installscript
		 */
	}
	
	public void generateInstallScript()
	{
		/**
		 * @todo generate installscript
		 */
	}
}
