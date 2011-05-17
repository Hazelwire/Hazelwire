package org.hazelwire.modules;

import java.util.Date;
import java.util.HashMap;
import org.hazelwire.main.FileName;
import org.hazelwire.main.Generator;

/**
 * 
 * @author Tim Strijdhorst
 * This class represents a module in the system and is 'linked' with the actual module on the filesystem
 */
public class Module
{
	private String name, author, type, deployFileName;
	private HashMap<Integer,Integer> flags; //<id,amountOfPoints>
	private HashMap<Integer,Option> options; //<id,Option>
	private Date date;
	private ModulePackage modulePackage;
	private int id;
	private FileName filePath;
	
	public Module(String name, String filePath)
	{
		this.name = name;
		this.filePath = new FileName(filePath,Generator.getInstance().getFileSeperator(),'.');
		this.flags = new HashMap<Integer,Integer>();
		this.options = new HashMap<Integer,Option>();
	}
	
	public Module()
	{
		this.flags = new HashMap<Integer,Integer>();
		this.options = new HashMap<Integer,Option>();
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
		return filePath.getPath();
	}
	
	public String getFileName()
	{
		return filePath.getFileName()+filePath.getExtensionSeparator()+filePath.getExtension();
	}
	
	public String getFullPath()
	{
		return filePath.getFullPath();
	}

	public void setFilePath(String filePath)
	{
		this.filePath = new FileName(filePath,Generator.getInstance().getFileSeperator(),'.');
	}
	
	public void setFileName(String fileName)
	{
		if(this.filePath != null) filePath = new FileName(filePath.getPath()+fileName,Generator.getInstance().getFileSeperator(),'.');
	}
	
	public void addFlag(int id, int points)
	{
		this.flags.put(id, points);
	}
	
	public void removeFlag(int id)
	{
		this.flags.remove(id);
	}
	
	public void addOption(int id, Option option)
	{
		this.options.put(id,option);
	}
	
	public void removeOption(int id)
	{
		this.options.remove(id);
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public void setModulePackage(ModulePackage modulePackage)
	{
		this.modulePackage = modulePackage;
	}

	public ModulePackage getModulePackage()
	{
		return modulePackage;
	}
	
	public String toString()
	{
		return "id: "+String.valueOf(id)+" name: "+name+" flags: "+String.valueOf(flags.size())+" options: "+String.valueOf(options.size());
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getDeployFileName()
	{
		return deployFileName;
	}

	public void setDeployFileName(String deployFileName)
	{
		this.deployFileName = deployFileName;
	}
}
