package org.hazelwire.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.hazelwire.modules.Module;
import org.hazelwire.modules.ModuleSelector;
import org.hazelwire.virtualmachine.InstallScriptGenerator;
import org.hazelwire.virtualmachine.SSHConnection;
import org.hazelwire.virtualmachine.VMHandler;

public class Generator
{
	private static Generator instance;
	private static String INSTALLNAME = "install.sh";
	private ModuleSelector moduleSelector;
	private TextInterface tui;
	Configuration config;
	private char fileSeperator;
	
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
	    	
	    	if(config.getOS().equals("windows")) setFileSeperator('\\');
	    	else setFileSeperator('/');
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
	
	/**
	 * @todo it should wait with stopping the vm until the callback is done
	 */
	public void generateVM() throws Exception
	{
    	VMHandler vmHandler = new VMHandler(config.getVirtualBoxPath(), config.getVMName(), config.getVMPath(), true);
    	vmHandler.importVM();
    	vmHandler.addForward("ssh", config.getSSHHostPort(), config.getSSHGuestPort(), "TCP"); //add forward so we can reach the VM
    	vmHandler.startVM();
    	if(vmHandler.discoverBootedVM(config.getSSHHostPort())) //wait for it to actually be booted so we can use the SSH service
    	{
	    	SSHConnection ssh = new SSHConnection("localhost",config.getSSHHostPort(),config.getSSHUsername(),config.getSSHPassword());
	    	uploadModules(ssh);
	    	generateInstallScript(INSTALLNAME);
	    	String externalPath = Configuration.getInstance().getExternalScriptDirectory()+INSTALLNAME;
	    	executeInstallScript(ssh,externalPath);	    	
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
			FileName modulePath = new FileName(iterate.next().getFilePath(),fileSeperator,'.');
			
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
	
	public void uploadInstallScript(SSHConnection ssh, String scriptPathLocal)
	{
		try
		{
			ssh.scpUpload(scriptPathLocal, Configuration.getInstance().getExternalScriptDirectory());
		}
		catch(Exception e)
		{
			tui.println("ERROR: something went wrong while transferring the installscript to the VM");
		}
	}
	
	public void executeInstallScript(SSHConnection ssh, String scriptPathExternal)
	{
		try
		{
			ssh.executeRemoteCommand("chmod u+x "+scriptPathExternal); //make it executable
			ssh.executeRemoteCommand(scriptPathExternal); //run the script
		}
		catch(Exception e)
		{
			tui.println("ERROR: something went wrong while executing the installscript");
		}
	}
	
	public void generateInstallScript(String filePath)
	{
		try
		{
			InstallScriptGenerator.saveScriptToDisk(filePath);
		}
		catch (Exception e)
		{
			tui.println("ERROR: something went wrong while generating the installscript");
		}
	}

	public void setFileSeperator(char fileSeperator)
	{
		this.fileSeperator = fileSeperator;
	}

	public char getFileSeperator()
	{
		return fileSeperator;
	}
}
