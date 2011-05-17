package org.hazelwire.test;

import org.hazelwire.virtualmachine.SSHConnection;

/** This example demonstrates uploading of a file over SCP to the SSH server. */
public class MainTest {

    public static void main(String[] args) throws Exception
    {
    	SSHConnection ssh = new SSHConnection("localhost",22,"test","test");
    	ssh.executeRemoteCommand("ls");
    }
}