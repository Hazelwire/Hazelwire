package org.hazelwire.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.hazelwire.main.FileName;
import org.hazelwire.main.Generator;

/**
 * This class represents a module in the system and is 'linked' with the actual module on the filesystem.
 * This is actually one of the core functionalities of the system and it manages everything that has to do with
 * a particular module.
 * 
 * @author Tim Strijdhorst
 */
public class Module
{
	private String name, author, deployPath, relativeDir;
	private HashMap<Integer,Flag> flags; //<id,amountOfPoints>
	private HashMap<Integer,Option> options; //<id,Option>
	private ArrayList<String> tags; 
	private Date date;
	private ModulePackage modulePackage;
	private int id, servicePort;
	private FileName filePath;
	private boolean hidden;
	
	public Module(String name, String filePath)
	{
		this.name = name;
		this.filePath = new FileName(filePath,Generator.getInstance().getFileSeperator(),'.');
		init();
	}
	
	
	public Module()
	{
		init();
	}
	
	public void init()
	{
		this.flags = new HashMap<Integer,Flag>();
		this.options = new HashMap<Integer,Option>();
		this.tags = new ArrayList<String>();
		this.hidden = false;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public ArrayList<String> getTags()
	{
		return this.tags;
	}
	
	public void addTag(String tag)
	{
		this.tags.add(tag);
	}
	
	public void removeTag(String tag)
	{
		this.tags.remove(tag);
	}

	public String getFilePath()
	{
		return filePath.getPath();
	}
	
	public String getFileName()
	{
		return filePath.getFileName()+filePath.getExtensionSeparator()+filePath.getExtension();
	}
	
	public String getFileNameWithoutExtension()
	{
		return filePath.getFileName();
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
	
	public void addFlag(Flag flag)
	{
		flag.setId(this.flags.size());
		this.flags.put(flag.getId(), flag);
	}
	
	public void removeFlag(int id)
	{
		this.flags.remove(id);
	}
	
	public void addOption(Option option)
	{
		option.setId(this.options.size());
		this.options.put(option.getId(),option);
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

	public String getDeployPath()
	{
		return deployPath;
	}

	public void setDeployPath(String deployFileName)
	{
		this.deployPath = deployFileName;
	}
	
	/**
	 * Completely replace the optionlist
	 * @param options
	 */
	public void setOptions(ArrayList<Option> options)
	{
		this.options.clear(); //just to be sure
		
		for(Option option : options)
		{
			this.options.put(option.getId(), option); //Add them with the original IDs
		}
	}
	
	public Option getOption(int id)
	{
		return options.get(id);
	}

	public Collection<Flag> getFlags()
	{
		return flags.values();
	}
	
	public Flag getFlag(int id)
	{
		return flags.get(id);
	}
	
	public void setFlags(ArrayList<Flag> flags)
	{
		this.flags.clear();
		
		for(Flag flag : flags)
		{
			this.flags.put(flag.getId(), flag);
		}
	}

	public Collection<Option> getOptions()
	{
		return options.values();
	}

	public void setHidden(boolean hidden)
	{
		this.hidden = hidden;
	}

	public boolean isHidden()
	{
		return hidden;
	}

	public void setServicePort(int servicePort)
	{
		this.servicePort = servicePort;
	}

	public int getServicePort()
	{
		this.syncServicePort(); //sync first
		
		return servicePort;
	}
	
	public void syncServicePort()
	{
		if(options.containsKey(0))
		{
			Option portOption = options.get(0);
			
			//Check again just to be sure...
			if(portOption.getName().equals("Service port"))
			{
				this.servicePort = Integer.valueOf(portOption.getValue());
			}
		}
	}


	public void setRelativedir(String relativeDir)
	{
		this.relativeDir = relativeDir;
	}


	public String getRelativeDir()
	{
		return relativeDir;
	}
}