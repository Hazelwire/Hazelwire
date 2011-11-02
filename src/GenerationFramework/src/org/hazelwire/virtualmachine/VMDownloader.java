/*******************************************************************************
 * Copyright (c) 2011 The Hazelwire Team.
 *     
 * This file is part of Hazelwire.
 * 
 * Hazelwire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Hazelwire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.hazelwire.virtualmachine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.hazelwire.main.Configuration;
import org.hazelwire.main.Generator;

/**
 * This class will download the virtualmachine (and basicly anything else) over HTTP.
 * @author Tim Strijdhorst
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
