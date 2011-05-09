package org.hazelwire.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration
{
	Properties defaultProps, applicationProps;
	
	public Configuration() throws IOException
	{
		//Load the default properties
		FileInputStream in = new FileInputStream("defaultProperties");
		defaultProps = new Properties();
		defaultProps.load(in);
		in.close();
		
		this.loadUserProperties();
	}
	
	public void loadUserProperties(String propertyPath) throws IOException
	{
		applicationProps = new Properties(defaultProps);
		FileInputStream in = new FileInputStream(propertyPath);
		applicationProps.load(in);
		in.close();
	}
	
	public void loadUserProperties() throws IOException
	{
		this.loadUserProperties(defaultProps.getProperty("propertyPath"));
	}
	
	public String getModulePath()
	{
		return applicationProps.getProperty("modulePath");
	}
	
	public void setModulePath(String modulePath)
	{
		applicationProps.setProperty("modulePath", modulePath);
	}
	
	/**
	 * This is the path to the VBoxManage binary
	 * @return
	 */
	public String getVirtualBoxPath()
	{
		return applicationProps.getProperty("virtualBoxPath");
	}
	
	public void setVirtualBoxPath(String vmPath)
	{
		applicationProps.setProperty("virtualBoxPath", vmPath);
	}
	
	public String getVMName()
	{
		return applicationProps.getProperty("vmName");
	}
	
	public void setVMName(String vmName)
	{
		applicationProps.setProperty("vmName", vmName);
	}
	
	public String getVMLogPath()
	{
		return applicationProps.getProperty("vmLogPath");
	}
	
	public void setVMLogPath(String vmLogPath)
	{
		applicationProps.setProperty("vmLogPath", vmLogPath);
	}
	
	public String getOS()
	{
		return System.getProperty("os.name");
	}
	
	public String getUserDir()
	{
		return System.getProperty("user.dir");
	}
}
