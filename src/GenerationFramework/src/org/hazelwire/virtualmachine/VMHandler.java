package org.hazelwire.virtualmachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class VMHandler
{
	String virtualBoxPath, vmName, vmPath;
	boolean debug;
	
	public VMHandler(String virtualBoxPath, String vmName, String vmPath, boolean debug)
	{
		this.virtualBoxPath = virtualBoxPath;
		this.debug = debug;
		this.vmName = vmName;
		this.vmPath = vmPath;
	}
	
	public void startVM() throws Exception
	{
		if(!this.checkIfImported()) throw new Exception("VM is not imported yet");
		
		try
		{			
			Process process = Runtime.getRuntime().exec(virtualBoxPath+" startvm "+vmName+" --type headless");
			System.out.println(virtualBoxPath+" startvm "+vmName+" --type headless");
			
			if(debug) new VMLogger(process);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void stopVM() throws Exception
	{
		if(!this.checkIfImported()) throw new Exception("VM is not imported yet");
		
		try
		{
			Process process = Runtime.getRuntime().exec(virtualBoxPath+" controlvm "+vmName+" poweroff");
			
			if(debug) new VMLogger(process);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The to be imported vm should be in .ova format
	 */
	public void importVM() throws Exception
	{
		if(this.checkIfImported()) throw new Exception("VM is already imported");
		
		try
		{
			Process process = Runtime.getRuntime().exec(virtualBoxPath+" import "+vmPath);
			process.waitFor(); //Wait untill the process is done with importing, else calling starvm would end horribly
			
			if(debug) new VMLogger(process);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void exportVM() throws Exception
	{
		if(!this.checkIfImported()) throw new Exception("VM is not imported yet");
		
		try
		{
			Process process = Runtime.getRuntime().exec(virtualBoxPath+" export "+vmName+" -o "+vmName+".ova");
			process.waitFor();
			
			if(debug) new VMLogger(process);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * This will forward a certain hostPort in the VM to a certain guestPort in the host-system.
	 * @param protocol
	 * @param hostPort
	 * @param guestPort
	 * @param transProtocol
	 * @throws Exception
	 */
	public void addForward(String protocol, int hostPort, int guestPort, String transProtocol) throws Exception
	{
		String[] commands = 
		{
			virtualBoxPath+" setextradata \""+vmName+"\" \"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/HostPort\""+hostPort,
			virtualBoxPath+" setextradata \""+vmName+"\" \"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/GuestPort\""+guestPort,
			virtualBoxPath+" setextradata \""+vmName+"\" \"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/GProtocol\""+transProtocol
		};
		
		if(!this.checkIfImported()) throw new Exception("VM is not imported yet");
		
		try
		{
			for(int i=0;i<commands.length;i++)
			{
				Process process = Runtime.getRuntime().exec(commands[i]);
				process.waitFor(); //The commands need to be in sequence
				
				if(debug) new VMLogger(process);
			}
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * This will remove a certain portforwarding.
	 * @pre ports are actually forwarded to the vm
	 * @post forward does not exist anymore
	 * @param protocol
	 * @throws Exception
	 */
	public void removeForward(String vmName, String protocol) throws Exception
	{
		String[] commands = 
		{
			virtualBoxPath+" setextradata \""+vmName+"\" \"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/HostPort\"",
			virtualBoxPath+" setextradata \""+vmName+"\" \"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/GuestPort\"",
			virtualBoxPath+" setextradata \""+vmName+"\" \"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/GProtocol\"",
		};
		
		if(!this.checkIfImported()) throw new Exception("VM is not imported yet");
		
		try
		{
			for(int i=0;i<commands.length;i++)
			{
				Process process = Runtime.getRuntime().exec(commands[i]);
				process.waitFor(); //The commands need to be in sequence
				
				if(debug) new VMLogger(process);
			}
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Checks whether a vm is already imported or not
	 * @return
	 */
	public boolean checkIfImported()
	{
		try
		{
			Process process = Runtime.getRuntime().exec(virtualBoxPath+" list vms");
			Pattern pattern = Pattern.compile(".*"+vmName+".*");
			boolean found = false;
			
			String s = null;
			
			BufferedReader procOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			while((s = procOut.readLine()) != null)
			{
				found = pattern.matcher(s).find();
			}
			
			return found;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
