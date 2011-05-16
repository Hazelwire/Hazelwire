package org.hazelwire.modules;

import java.util.ArrayList;

public class ModulePackage
{
	String name;
	ArrayList<Module> packageList;
	
	public ModulePackage(String name)
	{
		packageList = new ArrayList<Module>();
		this.name = name;
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
}
