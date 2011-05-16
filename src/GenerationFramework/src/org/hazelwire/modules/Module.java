package org.hazelwire.modules;

import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author Tim Strijdhorst
 * This class represents a module in the system and is 'linked' with the actual module on the filesystem
 */
public class Module
{
	private String name, filePath, author, type;
	private HashMap<Integer,Integer> flags; //<id,amountOfPoints>
	private HashMap<Integer,Option> options; //<id,Option>
	private Date date;
	private ModulePackage modulePackage;
	
	public Module(String name, String filePath)
	{
		this.name = name;
		this.filePath = filePath;
		this.flags = new HashMap<Integer,Integer>();
		this.options = new HashMap<Integer,Option>();
	}
	
	public Module()
	{
		//Make it possible to create an empty module and set all the info later
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
}
