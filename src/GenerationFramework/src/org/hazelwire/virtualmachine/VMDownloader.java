package org.hazelwire.virtualmachine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.hazelwire.main.Configuration;
import org.hazelwire.main.Generator;

public class VMDownloader extends Thread
{
	private ProgressInterface progressInterface;
	private String outputFilePath;

	public VMDownloader(ProgressInterface progressInterface)
	{
		super("vm-downloader");
		this.progressInterface = progressInterface;
	}
	
	public void run()
	{
		try
		{
			downloadVM();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	public void downloadVM() throws IOException
	{
		BufferedReader in;
		BufferedWriter out;
		String s;
		URL u = new URL(Configuration.getInstance().getBaseVMMirror());
		URLConnection conn = u.openConnection();
		this.outputFilePath = Configuration.getInstance().getDownloadDirectory()+
		Generator.getInstance().getFileSeperator()+u.getFile();
		File outputFile = new File(this.outputFilePath);
		FileOutputStream fos = new FileOutputStream(outputFile);
	    
		in = new BufferedReader( new InputStreamReader(conn.getInputStream()));	
		out = new BufferedWriter(new OutputStreamWriter(fos));
		
		int totalSize = conn.getContentLength();
		int progress = 0;
		int bytesRead = 0;
		int i=0;
		
		
		//actually download the file
		while ((s = in.readLine()) != null)
		{
			i++;
			if(i == 100000)
			{
				System.out.println("bla");
			}
			
            out.write(s);
            bytesRead += s.getBytes().length;
            progress = (int)((float)bytesRead / totalSize * 100);
            progressInterface.setProgress(progress);
        }
		
		progressInterface.setFilePath(outputFilePath);
	}
}
