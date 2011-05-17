package org.hazelwire.virtualmachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import javax.management.RuntimeErrorException;

/**
 * @author Tim Strijdhorst
 * @todo write proper error handling
 *
 */
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
		else
		{
			try
			{			
				String[] arguments = {virtualBoxPath,"startvm",vmName,"--type","headless"};
				Process process = Runtime.getRuntime().exec(arguments);
				
				System.out.println(virtualBoxPath+" startvm "+vmName+" --type headless");
				
				if(debug) new VMLogger(process);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	public void stopVM() throws Exception
	{
		if(!this.checkIfImported()) throw new Exception("VM is not imported yet");
		else
		{
			try
			{
				String[] arguments = {virtualBoxPath,"controlvm",vmName,"poweroff"};
				Process process = Runtime.getRuntime().exec(arguments);
				
				if(debug) new VMLogger(process);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Keep trying to make a connection to a certain port on the host system that is routed to the VM by a NAT forward.
	 * Assuming that the port is really forwarded and the service is start on bootup it will fail to connect untill the VM has fully booted.
	 * @param hostPort
	 */
	public boolean discoverBootedVM(int hostPort)
	{
		boolean up = false;
		int errorCounter = 0;
		
		while(!up && errorCounter < 50)
		{
			try
			{
				Socket socket = new Socket("localhost",hostPort);
				up = true;
			}
			catch (IOException e)
			{
				//don't do anything just keep running the loop
				try
				{
					Thread.sleep(1000); //sleep for one second, no use checking every nanosecond whether it's up yet...
				}
				catch(Exception ex)
				{
					throw new RuntimeException(ex);
				}
			}
		}
		
		return up;		
	}
	
	/**
	 * The to be imported vm should be in .ova format
	 * 
	 */
	public void importVM() throws Exception
	{
		if(this.checkIfImported()) System.out.println("VM is already imported"); 
		else
		{
			try
			{			
				String[] arguments = {virtualBoxPath,"import",vmPath};
				Process process = Runtime.getRuntime().exec(arguments);
				process.waitFor(); //Wait untill the process is done with importing, else calling starvm would end horribly
				
				if(debug) new VMLogger(process);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Export to the current working dir with filename *vmName*.ova
	 * @throws Exception
	 */
	public void exportVM() throws Exception
	{
		this.exportVM(vmName,vmName+".ova");
	}
	
	public void exportVM(String vmName, String exportPath) throws Exception
	{
		if(!this.checkIfImported()) throw new Exception("VM is not imported yet");
		else
		{
			try
			{
				String[] arguments = {virtualBoxPath,"export",vmName,"-o",exportPath};
				Process process = Runtime.getRuntime().exec(arguments);
				process.waitFor();
				
				if(debug) new VMLogger(process);
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
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
		String[][] commands = 
		{
			{virtualBoxPath,"setextradata",vmName,"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/HostPort",String.valueOf(hostPort)},
			{virtualBoxPath,"setextradata",vmName,"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/GuestPort",String.valueOf(guestPort)},
			{virtualBoxPath,"setextradata",vmName,"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/Protocol",transProtocol}
		};
		
		if(!this.checkIfImported()) throw new Exception("VM is not imported yet");
		else
		{
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
	}
	
	/**
	 * This will remove a certain portforwarding.
	 * @pre ports are actually forwarded to the vm
	 * @post forward does not exist anymore
	 * @param protocol
	 * @throws Exception
	 */
	public void removeForward(String protocol) throws Exception
	{
		String[][] commands = 
		{
			{virtualBoxPath,"setextradata",vmName,"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/HostPort"},
			{virtualBoxPath,"setextradata",vmName,"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/GuestPort"},
			{virtualBoxPath,"setextradata",vmName,"VBoxInternal/Devices/pcnet/0/LUN#0/Config/"+protocol+"/Protocol"}
		};
		
		if(!this.checkIfImported()) throw new Exception("VM is not imported yet");
		else
		{
			try
			{
				for(int i=0;i<commands.length;i++)
				{
					Process process = Runtime.getRuntime().exec(commands[i]);
					process.waitFor(); //The commands need to be in sequence
					
					if(debug) new VMLogger(process);
				}
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
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
			String[] arguments = {virtualBoxPath,"list","vms"};
			Process process = Runtime.getRuntime().exec(arguments);
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

	public String getVirtualBoxPath()
	{
		return virtualBoxPath;
	}

	public void setVirtualBoxPath(String virtualBoxPath)
	{
		this.virtualBoxPath = virtualBoxPath;
	}

	public String getVmName()
	{
		return vmName;
	}

	public void setVmName(String vmName)
	{
		this.vmName = vmName;
	}

	public String getVmPath()
	{
		return vmPath;
	}

	public void setVmPath(String vmPath)
	{
		this.vmPath = vmPath;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}
}
