package org.hazelwire.test;

import java.util.ArrayList;

import org.hazelwire.main.Generator;

/** This example demonstrates uploading of a file over SCP to the SSH server. */
public class MainTest {

    public static void main(String[] args) throws Exception
    {
    	Generator generator = Generator.getInstance();
    	generator.getModuleSelector().selectModule(0);
    	generator.generateVM();
    	
    	ArrayList<String> moduleDesc = generator.getAvailableModules();
    	
    	for(int i=0;i<moduleDesc.size();i++)
    	{
    		System.out.println(moduleDesc.get(i));
    	}
    }
}