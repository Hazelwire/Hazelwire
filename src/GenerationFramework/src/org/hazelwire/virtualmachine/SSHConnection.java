package org.hazelwire.virtualmachine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;


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
	
	public void scpUpload(ArrayList<String> files, String targetDir) throws IOException, ClassNotFoundException
	{
		SSHClient ssh = this.getSSHConnection();
        Iterator<String> iterator = files.iterator();
        
        try
        {
            // Present here to demo algorithm renegotiation - could have just put this before connect()
            // Make sure JZlib is in classpath for this to work
            ssh.useCompression();
            
            while(iterator.hasNext())
            {
            	ssh.newSCPFileTransfer().upload(iterator.next(),targetDir);
            }

        }
        finally 
        {
            ssh.disconnect();
        }
	}
	
	public int executeRemoteCommand(String command) throws IOException, ClassNotFoundException
	{
		SSHClient ssh = getSSHConnection();
        
		Session session = ssh.startSession();
        
		try
		{
			Command cmd = session.exec(command);
			return cmd.getExitStatus(); //return exitstatus to see if it was succesful or not
		}
		finally
		{
			session.close();
		}
		
	}
	
	public SSHClient getSSHConnection() throws IOException
	{
		SSHClient ssh = new SSHClient();
        ssh.loadKnownHosts();
        ssh.connect(this.hostname);
        ssh.authPassword(username, password);
        
        return ssh;
	}
}
