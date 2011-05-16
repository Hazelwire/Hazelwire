package org.hazelwire.main;

import org.hazelwire.modules.ModuleSelector;

public class Generator
{
	private static Generator instance;
	private ModuleSelector moduleSelector;
	
	public static synchronized Generator getInstance()
	{
		if(instance == null)
		{
			instance = new Generator();
		}
		
		return instance;
	}
	
	private Generator()
	{
		moduleSelector = new ModuleSelector();
	}

	public ModuleSelector getModuleSelector()
	{
		return moduleSelector;
	}
}
