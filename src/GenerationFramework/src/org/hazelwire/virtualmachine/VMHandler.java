package org.hazelwire.virtualmachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class VMHandler
{
	String virtualBoxPath;
	boolean debug;
	
	public VMHandler(String virtualBoxPath, boolean debug)
	{
		this.virtualBoxPath = virtualBoxPath;
		this.debug = debug;
	}
	
	/**
	 * @param vmName
	 * @param debug
	 */
	public void startVM(String vmName) throws Exception
	{		
		try
		{
			if(this.checkIfVMExists(vmName)) throw new Exception("VM is already imported");
			
			Process process = Runtime.getRuntime().exec(virtualBoxPath+" "+vmName+" --type headless");
			
			if(debug) new VMLogger(process);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The to be imported vm should be in .ova format
	 * @param vmPath
	 */
	public void importVM(String vmPath)
	{
		try
		{
			Process process = Runtime.getRuntime().exec(virtualBoxPath+" import "+vmPath);
			
			if(debug) new VMLogger(process);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Checks whether a vm is already imported or not
	 * @param vmName
	 * @return
	 */
	public boolean checkIfVMExists(String vmName)
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
