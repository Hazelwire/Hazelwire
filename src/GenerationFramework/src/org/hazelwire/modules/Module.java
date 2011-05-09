package org.hazelwire.modules;

/**
 * 
 * @author Tim Strijdhorst
 * This class represents a module in the system and is 'linked' with the actual module on the filesystem
 */
public class Module
{
	private String name, filePath;
	
	public Module(String name, String filePath)
	{
		this.name = name;
		this.filePath = filePath;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
}
