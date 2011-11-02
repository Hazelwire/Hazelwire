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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.hazelwire.main.Configuration;


/**
 * Crappy logger class will be deprecated very soon.
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
		//this.configuration = new Configuration(); //This might not be the best way to do this?
		this.procOut 	= new BufferedReader(new InputStreamReader(process.getInputStream()));
		this.procError 	= new BufferedReader(new InputStreamReader(process.getErrorStream()));
		//FileWriter fstream = new FileWriter(configuration.getVMLogPath());
		this.out = new BufferedWriter(new OutputStreamWriter(System.out));
		this.logToFile(); //start logging
	}
	
	public void logToFile() throws IOException
	{		
		this.out.write("START LOG\r\nFROM STDOUT:\r\n");
		String s = null;
		
		while((s = this.procOut.readLine()) != null)
		{
			this.out.write(s+"\r\n");
		}
		
		this.out.write("FROM STDERR:\r\n");
		
		while((s = this.procError.readLine()) != null)
		{
			this.out.write(s+"\r\n");
		}
		
		this.out.write("\r\nEND LOG"+"\r\n");
		this.out.flush(); //if for some reason it didn't send it yet, flush it
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
