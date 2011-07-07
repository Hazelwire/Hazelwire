package org.hazelwire.virtualmachine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.hazelwire.main.Configuration;
import org.hazelwire.main.Generator;

public class VMDownloader
{
	public static String downloadVM() throws IOException
	{		
		URL u = new URL(Configuration.getInstance().getBaseVMMirror());
		BufferedReader in;
		BufferedWriter out;
		String s;
		File outputFile = new File(Configuration.getInstance().getDownloadDirectory()+
									Generator.getInstance().getFileSeperator()+u.getFile());
		FileOutputStream fos = new FileOutputStream(outputFile);
	    
		in = new BufferedReader( new InputStreamReader(u.openStream()));	
		out = new BufferedWriter(new OutputStreamWriter(fos));
		
		//actually download the file
		while ((s = in.readLine()) != null)
		{
            out.write(s);
        }
		
		return outputFile.getAbsolutePath();
	}
}
