package org.hazelwire.test;

import org.hazelwire.main.Configuration;
import org.hazelwire.virtualmachine.SSHConnection;
import org.hazelwire.virtualmachine.VMHandler;

/** This example demonstrates uploading of a file over SCP to the SSH server. */
public class MainTest {

    public static void main(String[] args) throws Exception
    {
    	Configuration config = Configuration.getInstance();
    	config.loadDefaultProperties("config/defaultProperties");
    	config.loadUserProperties();
    	
    	VMHandler vmHandler = new VMHandler(config.getVirtualBoxPath(), config.getVMName(), config.getVMPath(), true);
    	vmHandler.importVM();
    	vmHandler.addForward("ssh", config.getSSHHostPort(), config.getSSHGuestPort(), "TCP");
    	vmHandler.startVM();
    	if(vmHandler.discoverBootedVM(config.getSSHHostPort()))
    	{
	    	SSHConnection ssh = new SSHConnection("localhost",config.getSSHHostPort(),config.getSSHUsername(),config.getSSHPassword());
	    	ssh.scpUpload("/home/shokora/test.txt","/home/hazelwire/test.txt");
	    	ssh.executeRemoteCommand("cp test.txt test2.txt");
	    	vmHandler.stopVM();
	    	vmHandler.removeForward("ssh");
	    	vmHandler.exportVM(vmHandler.getVmName(),config.getVMExportPath());
    	}
    	else
    	{
    		throw new Exception("Could not connect to the virtualmachine");
    	}
    }
}