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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.sftp.SFTPClient;

import org.hazelwire.main.Configuration;

/**
 * This class manages all the SSH communication with the virtualmachine in the generation process. It is implemented using the SSHJ library.
 * @author Tim Strijdhorst
 *
 */
public class SSHConnection
{
	String hostname, username, password;
	int port;
	
	public SSHConnection(String hostname, int port, String username, String password)
	{
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public void sftpUpload(ArrayList<String> files, String targetDir) throws IOException, ClassNotFoundException
	{
		SSHClient ssh = this.getSSHConnection();
        Iterator<String> iterator = files.iterator();
        
        // Present here to demo algorithm renegotiation - could have just put this before connect()
        // Make sure JZlib is in classpath for this to work
        //ssh.useCompression();
        final SFTPClient sftp = ssh.newSFTPClient();
        
        while(iterator.hasNext())
        {
        	try
        	{
        		sftp.put(iterator.next(),targetDir);
        	}
        	finally
        	{
        		sftp.close();
        	}
        }
	}
	
	public void sftpUpload(String filePath, String targetDir) throws IOException, ClassNotFoundException
	{
		ArrayList<String> files = new ArrayList<String>();
		files.add(filePath);
		this.sftpUpload(files, targetDir);
	}
	
	public void executeRemoteCommand(String command) throws IOException, ClassNotFoundException
	{
		SSHClient ssh = getSSHConnection();
        int exitStatus; //for some odd reason it doesn't always return an exit status, 
		Session session = ssh.startSession();
		
		try
		{
			Command cmd = session.exec(command);
			cmd.join();
		}
		finally
		{
			session.close();
		}
	}
	
	public SSHClient getSSHConnection() throws IOException
	{
		SSHClient ssh = new SSHClient();
		
        ssh.loadKnownHosts(new File(Configuration.getInstance().getKnownHostsPath()));
        ssh.connect(this.hostname,this.port);
        ssh.authPassword(username, password);
        
        return ssh;
	}
}
