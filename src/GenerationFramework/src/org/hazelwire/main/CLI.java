package org.hazelwire.main;

import java.util.ArrayList;
import java.util.Scanner;

public class CLI implements TextInterface
{	
	boolean readyToStop;
	
	public static void main(String[] args)
	{
		new CLI().run();
	}
	
	@Override
	public void print(String message)
	{
		System.out.print(message);
	}

	@Override
	public void println(String message)
	{
		System.out.println(message);
	}
	
	public void run()
	{
		Scanner scanner = new Scanner(System.in);
		Generator generator = Generator.getInstance();
		
		while(!readyToStop)
		{			
			print("Commando: ");
			
			if(scanner.hasNextLine())
			{
				handleCommand(scanner.nextLine());
			}
		}
	}
	
	public void handleCommand(String commandLine)
	{
	}
}
