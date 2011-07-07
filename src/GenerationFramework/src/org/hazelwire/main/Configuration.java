package org.hazelwire.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	
	private static Configuration instance; //singleton
	
	public static synchronized Configuration getInstance() throws IOException
	{
		if(instance == null)
		{
			instance = new Configuration();
		}
		
		return instance;
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
		//this might be made dynamic
		
	}
	
	public void loadDefaultProperties(String defaultPropertyPath) throws IOException
	{
		//Load the default properties
		FileInputStream in = new FileInputStream(defaultPropertyPath);
		defaultProps = new Properties();
		defaultProps.load(in);
		in.close();
	}
	
	public void loadUserProperties(String propertyPath) throws IOException
	{
		if(defaultProps != null)
		{
			applicationProps = new Properties(defaultProps);		
		}
		else
		{
			applicationProps = new Properties();
		}
		
		FileInputStream in = new FileInputStream(propertyPath);
		applicationProps.load(in);
		in.close();
	}
	
	public void loadUserProperties() throws IOException
	{
		this.loadUserProperties(defaultProps.getProperty("propertyPath"));
	}
	
	public void saveUserProperties(String propertyPath) throws IOException
	{
		FileOutputStream out = new FileOutputStream(propertyPath);
		try
		{
			applicationProps.store(out, "");
		}
		finally
		{
			out.close();
		}
	}
	
	public void saveUserProperties() throws IOException
	{
		saveUserProperties(defaultProps.getProperty("propertyPath"));
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
	
	public String getVMPath()
	{
		return applicationProps.getProperty("vmPath");
	}
	
	public void setVMPath(String path)
	{
		applicationProps.setProperty("vmPath", path);
	}
	
	public String getVMLogPath()
	{
		return applicationProps.getProperty("vmLogPath");
	}
	
	public void setVMLogPath(String vmLogPath)
	{
		applicationProps.setProperty("vmLogPath", vmLogPath);
	}
	
	public String getVMExportPath()
	{
		return applicationProps.getProperty("vmExportPath");
	}
	
	public void setVMExportPath(String path)
	{
		applicationProps.setProperty("vmExportPath", path);
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
	
	public int getSSHHostPort()
	{
		return Integer.valueOf(applicationProps.getProperty("sshHostPort"));
	}
	
	public void setSSHHostPort(int port)
	{
		applicationProps.setProperty("sshHostPort", String.valueOf(port));
	}
	
	public int getSSHGuestPort()
	{
		return Integer.valueOf(applicationProps.getProperty("sshGuestPort"));
	}
	
	public void setSSHGuestPort(int port)
	{
		applicationProps.setProperty("sshGuestPort", String.valueOf(port));
	}
	
	public String getExternalModuleDirectory()
	{
		return applicationProps.getProperty("externalModuleDirectory");
	}
	
	public void setExternalModuleDirectory(String dir)
	{
		applicationProps.setProperty("externalModuleDirectory", dir);
	}
	
	public String getExternalScriptDirectory()
	{
		return applicationProps.getProperty("externalScriptDirectory");
	}
	
	public void setExternalScriptDirectory(String dir)
	{
		applicationProps.setProperty("externalScriptDirectory", dir);
	}
	
	public String getOutputDirectory()
	{
		return applicationProps.getProperty("outputDirectory");
	}
	
	public void setOutputDirectory(String dir)
	{
		applicationProps.setProperty("outputDirectory", dir);
	}
	
	public String getTempDirectory()
	{
		return applicationProps.getProperty("tempDirectory");
	}
	
	public void setTempDirectory(String dir)
	{
		applicationProps.setProperty("tempDirectory", dir);
	}
	
	public String getKnownHostsPath()
	{
		return applicationProps.getProperty("knownHostsFile");
	}
	
	public void setKnownHostsPath(String filePath)
	{
		applicationProps.setProperty("knownHostsFile", filePath);
	}
	
	public void setBaseVMMirror(String mirror)
	{
		applicationProps.setProperty("baseVMMirror", mirror);
	}
	
	public String getBaseVMMirror()
	{
		return applicationProps.getProperty("baseVMMirror");
	}
	
	public void setDownloadDirectory(String downloadDir)
	{
		applicationProps.setProperty("downloadDirectory", downloadDir);
	}
	
	public String getDownloadDirectory()
	{
		return applicationProps.getProperty("downloadDirectory");
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
