package org.hazelwire.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.hazelwire.main.Configuration;
import org.hazelwire.virtualmachine.SSHConnection;
import org.hazelwire.virtualmachine.VMHandler;

/** This example demonstrates uploading of a file over SCP to the SSH server. */
public class MainTest {

    public static void main(String[] args) throws Exception
    {
    	/*
    	VMHandler vmHandler = new VMHandler("/usr/bin/vboxmanage", "HazelwireTest", "/home/shokora/test/HazelwireTest.ova", true);
    	//vmHandler.importVM();
    	vmHandler.addForward("ssh", 2222, 22, "TCP");
    	vmHandler.startVM();
    	if(vmHandler.discoverBootedVM(2222))
    	{
	    	SSHConnection ssh = new SSHConnection("localhost",2222,"hazelwire","hazelwire");
	    	ssh.scpUpload("/home/shokora/test.txt","/home/hazelwire/test.txt");
	    	ssh.executeRemoteCommand("cp test.txt test2.txt");
	    	vmHandler.stopVM();
	    	vmHandler.removeForward("ssh");
	    	vmHandler.exportVM(vmHandler.getVmName(),"/home/shokora/test.ova");
    	}
    	else
    	{
    		throw new Exception("Could not connect to the virtualmachine");
    	}*/
    	
    	String curDir = System.getProperty("user.dir");
    	System.out.println(curDir);
    }
}