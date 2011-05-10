package org.hazelwire.test;

import java.io.File;

import net.schmizz.sshj.SSHClient;

/** This example demonstrates uploading of a file over SCP to the SSH server. */
public class MainTest {

    public static void main(String[] args) throws Exception
    {
    		System.out.println("ZAAD");
		   SSHClient ssh = new SSHClient();
	       ssh.loadKnownHosts();
	       ssh.connect("localhost");
	       try {
	           ssh.authPublickey(System.getProperty("user.name"));
	           ssh.authPassword("test", "test");
	
	           // Present here to demo algorithm renegotiation - could have just put this before connect()
	           // Make sure JZlib is in classpath for this to work
	           //ssh.useCompression();
	
	           final String src = System.getProperty("user.home") + File.separator + "test.txt";
	           final String target = "/home/test/test.txt";
	           ssh.newSCPFileTransfer().upload(src, target);
	       } finally {
	           ssh.disconnect();
	       }
    }
}