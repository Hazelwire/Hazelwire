package org.hazelwire.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MainTest
{
	public static void main(String args[])
	{
		try
		{
			String path = "/home/shokora/";
			
			File fileTest = new File(path);
			
			if(fileTest.isFile())
			{
				FileInputStream test = new FileInputStream(path);
				InputStreamReader testReader = new InputStreamReader(test,"UTF-8");
				Scanner bla = new Scanner(test);
				
				while(bla.hasNext())
				{
					System.out.print(bla.next());
				}
			}
			else
			{
				String[] fileList = fileTest.list();
				
				for(int i=0;i<fileList.length;i++)
				{
					System.out.println(fileList[i]);
				}
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			
		}
		
		
	}
}
