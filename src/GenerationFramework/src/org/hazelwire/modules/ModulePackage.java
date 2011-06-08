package org.hazelwire.modules;

import java.util.ArrayList;

public class ModulePackage
{
	private String name;
	private int id;
	private ArrayList<Module> packageList;
	
	public ModulePackage(int id, String name)
	{
		packageList = new ArrayList<Module>();
		this.name = name;
		this.id = id;
	}
	
	public void addModule(Module module)
	{
		packageList.add(module);
	}
	
	public void removeModule(Module module)
	{
		packageList.remove(module);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
}
