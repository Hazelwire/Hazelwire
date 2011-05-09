package org.hazelwire.virtualmachine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hazelwire.main.Configuration;


/**
 * @author Tim Strijdhorst
 * @todo maybe this needs to be implemented as a Thread but that's always a pain in the ass with synchronizing the locks for writing.
 * So lets just see if this effects the performance all too much (which it probably doesn't).
 *
 */
public class VMLogger
{
	BufferedReader procOut, procError;
	BufferedWriter out;
	Configuration configuration;
	
	public VMLogger(Process process) throws IOException
	{
		this.configuration = new Configuration(); //This might not be the best way to do this?
		this.procOut 	= new BufferedReader(new InputStreamReader(process.getInputStream()));
		this.procError 	= new BufferedReader(new InputStreamReader(process.getErrorStream()));
		FileWriter fstream = new FileWriter(configuration.getVMLogPath());
		this.out = new BufferedWriter(fstream);
		this.logToFile(); //start logging
	}
	
	public void logToFile() throws IOException
	{
		String newline = System.getProperty("line.seperator");
		
		this.out.write("START LOG"+newline+"FROM STDOUT:");
		String s = null;
		
		while((s = this.procOut.readLine()) != null)
		{
			this.out.write(s+newline);
		}
		
		this.out.write("FROM STDERR:");
		
		while((s = this.procError.readLine()) != null)
		{
			this.out.write(s+newline);
		}
		
		this.out.write("END LOG"+newline);
		this.out.flush(); //if for some odd reason it didn't flush already force it

	}
	
	public void cleanUp()
	{
		try
		{
			this.out.close();
			this.procError.close();
			this.procOut.close();
		}
		catch (IOException e)
		{
			//If we can't cleanup something is seriously fucked
			throw new RuntimeException(e);
		}
	}
}
