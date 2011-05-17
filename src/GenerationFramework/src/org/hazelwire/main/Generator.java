package org.hazelwire.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.hazelwire.modules.ManifestGenerator;
import org.hazelwire.modules.Module;
import org.hazelwire.modules.ModuleSelector;
import org.hazelwire.virtualmachine.InstallScriptGenerator;
import org.hazelwire.virtualmachine.SSHConnection;
import org.hazelwire.virtualmachine.VMHandler;

/**
 * @author Tim Strijdhorst
 * 
 * It is absolutely vital that you initiate this class first because it will setup all the configuration
 *
 */
public class Generator
{
	private static Generator instance;
	private static String INSTALLNAME = "install.sh";
	private static String MANIFESTFILENAME = "manifest.xml"; //you should probably not change this...
	private ModuleSelector moduleSelector;
	private TextInterface tui;
	Configuration config;
	private char fileSeperator;
	
	public static synchronized Generator getInstance()
	{
		if(instance == null)
		{
			instance = new Generator();
			
			try
			{
				instance.getModuleSelector().init();
			}
			catch(Exception e)
			{
				instance.getTui().println("ERROR: Cannot load the configuration, exiting."); //this is pretty ugly... have to find a better solution
				System.exit(0);
			}
		}
		
		return instance;
	}
	
	private Generator()
	{
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
		
		
		moduleSelector = new ModuleSelector();
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
    	if(!vmHandler.checkIfImported())
    	{
    		tui.println("Importing VM");
    		vmHandler.importVM();
    	}
    	
    	tui.println("Adding portforward for ssh: "+config.getSSHHostPort()+" to "+config.getSSHGuestPort()+" in the vm");
    	vmHandler.addForward("ssh", config.getSSHHostPort(), config.getSSHGuestPort(), "TCP"); //add forward so we can reach the VM
    	tui.println("Starting VM");
    	vmHandler.startVM();
    	if(vmHandler.discoverBootedVM(config.getSSHHostPort())) //wait for it to actually be booted so we can use the SSH service
    	{
	    	SSHConnection ssh = new SSHConnection("localhost",config.getSSHHostPort(),config.getSSHUsername(),config.getSSHPassword());
	    	tui.println("Creating directories");
	    	prepareVM(ssh);
	    	tui.println("Uploading modules");
	    	uploadModules(ssh);
	    	tui.println("Generating installation script");
	    	generateInstallScript(config.getOutputDirectory()+INSTALLNAME);
	    	tui.println("Generating manifest");
	    	generateManifest(config.getOutputDirectory()+MANIFESTFILENAME);
	    	tui.println("Uploading installation script");
	    	String externalPath = config.getExternalScriptDirectory()+INSTALLNAME;
	    	ssh.scpUpload(config.getOutputDirectory()+INSTALLNAME, externalPath);
	    	tui.println("Executing installation script");
	    	executeInstallScript(ssh,externalPath);
	    	vmHandler.stopVM();
	    	vmHandler.removeForward("ssh");
	    	vmHandler.exportVM();
	    	
	    	new File("config.getOutputDirectory()+INSTALLNAME").delete(); //delete the installationscript from the output dir
    	}
    	else
    	{
    		throw new Exception("Could not connect to the virtualmachine");
    	}
	}
	
	public void uploadModules(SSHConnection ssh)
	{
		ArrayList<Module> modules = moduleSelector.getMarkedModules();
		Iterator<Module> iterate = modules.iterator();
		
		while(iterate.hasNext())
		{
			Module tempModule = iterate.next();
			
			try
			{
				ssh.scpUpload(tempModule.getFullPath(),config.getExternalModuleDirectory()+tempModule.getFileName());
			}
			catch(Exception e)
			{
				tui.println("ERROR: something went wrong while transferring the modules to the VM");
			}
		}
	}
	
	/**
	 * Makes sure all the directories that are needed are indeed created.
	 * It doesn't matter whether they already exist or not this is just for safety.
	 */
	public void prepareVM(SSHConnection ssh)
	{
		try
		{
			Configuration config = Configuration.getInstance();
			ssh.executeRemoteCommand("mkdir "+config.getExternalModuleDirectory());
			ssh.executeRemoteCommand("mkdir "+config.getExternalScriptDirectory());
		}
		catch(Exception e)
		{
			tui.println("ERROR: something went wrong while preparing the directory structure on the VM. Message: "+e.getMessage());
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
	
	public void generateManifest(String filePath)
	{
		try
		{
			ManifestGenerator.saveManifestToDisk(filePath);
		}
		catch (Exception e)
		{
			tui.println("ERROR: something went wrong while generating the manifest file");
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

	public TextInterface getTui()
	{
		return tui;
	}

	public void setTui(TextInterface tui)
	{
		this.tui = tui;
	}
}
