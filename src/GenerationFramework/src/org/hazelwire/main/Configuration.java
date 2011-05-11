package org.hazelwire.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * 
 * @author Tim Strijdhorst
 * This class manages all the configuration, first a default configuration is loaded then the values are overwritten with user-set values if set.
 * In order to ensure synchronisity of configuration-values among all classes it is implemented with the singleton pattern.
 * It is synchronized to ensure it's also thread-safe
 *
 */
public class Configuration
{
	Properties defaultProps, applicationProps;
	
	private static Configuration configuration; //singleton
	
	public static synchronized Configuration getConfiguration() throws IOException
	{
		if(configuration == null)
		{
			configuration = new Configuration();
		}
		
		return configuration;
	}
	
	/**
	 * Singleton so don't try to clone it
	 */
	public Object clone() throws CloneNotSupportedException 
	{
		throw new CloneNotSupportedException();
	}
	
	private Configuration() throws IOException
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
	
	public String getSSHUsername()
	{
		return applicationProps.getProperty("sshUsername");
	}
	
	public void setSSHUsername(String username)
	{
		applicationProps.setProperty("sshUsername", username);
	}
	
	public String getSSHPassword()
	{
		return applicationProps.getProperty("sshPassword");
	}
	
	/**
	 * This is just for connecting to the virtualmachine for uploading and installing the modules.
	 * This means it is not in any way secret.
	 * @param password
	 */
	public void setSSHPassword(String password)
	{
		applicationProps.setProperty("sshPassword", password);
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
