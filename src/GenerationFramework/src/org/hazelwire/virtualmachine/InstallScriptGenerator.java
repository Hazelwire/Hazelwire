package org.hazelwire.virtualmachine;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InstallScriptGenerator
{
	InetAddress localIP;
	
	public InstallScriptGenerator() throws UnknownHostException
	{
		localIP = InetAddress.getLocalHost(); //local ip is used for the callback to see when the installation is done
	}
}
