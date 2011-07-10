package org.hazelwire.virtualmachine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.hazelwire.main.Configuration;
import org.hazelwire.main.Generator;

/**
 * This class will download the virtualmachine (and basicly anything else) over HTTP.
 * @author shokora
 *
 */
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
		byte[] buf = new byte[1024];
		URL u = new URL(Configuration.getInstance().getBaseVMMirror());
		URLConnection conn = u.openConnection();
		this.outputFilePath = Configuration.getInstance().getDownloadDirectory()+
		Generator.getInstance().getFileSeperator()+u.getFile();
		File outputFile = new File(this.outputFilePath);
		FileOutputStream fos = new FileOutputStream(outputFile);		
		
		InputStream is = conn.getInputStream();
		
		int totalSize = conn.getContentLength();
		int progress = 0;
		int bytesRead = 0;
		int bytesWritten = 0;
		
		while((bytesRead = is.read(buf)) != -1)
		{
			fos.write(buf,0,bytesRead);
			bytesWritten += bytesRead;
			progress = (int)((float)bytesWritten / totalSize * 100);
			progressInterface.setProgress(progress);
		}
		
		progressInterface.setFilePath(outputFilePath);
	}
}
