package org.hazelwire.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author Tim Strijdhorst
 *
 */
public class ModuleSelector
{
	private HashMap<Integer,Module> selectedModules, availableModules;
	private int idCounter;
	private String filePath; //This will come out of some external configuration later on
	private HashMap<String,ModulePackage> modulePackageList; //<Name,Package>
	
	public ModuleSelector()
	{
		this.selectedModules = new HashMap<Integer,Module>();
		this.availableModules = new HashMap<Integer,Module>();
		this.idCounter = 0; //Will not be updated when an item is removed
	}
	
	public void init() throws Exception
	{
		ArrayList<Module> moduleList = ModuleHandler.scanForModules(this.filePath);
		
		Iterator<Module> iterator = moduleList.iterator();
		
		while(iterator.hasNext())
		{
			this.availableModules.put(this.idCounter++, iterator.next());
		}
	}
	
	/**
	 * @pre the value with the key 'id' exists
	 * @param id
	 */
	public void selectModule(int id)
	{
		this.selectedModules.put(id, this.availableModules.get(id));
	}
	
	public void deselectModule(int id)
	{
		this.selectedModules.remove(id);
	}
	
	public void addModule(Module module) throws Exception
	{		
		if(module.getModulePackage() != null)
		{
			if(!modulePackageList.containsKey(module.getModulePackage().getName()))
			{
				addModulePackage(module.getModulePackage());
			}
		}
		
		this.availableModules.put(this.idCounter++,module);
	}
	
	public void addModulePackage(ModulePackage modulePackage)
	{
		this.modulePackageList.put(modulePackage.getName(),modulePackage);
	}
	
	public ModulePackage getModulePackage(String name)
	{
		return this.modulePackageList.get(name);
	}
	
	/**
	 * In this version it will not yet be removed from the filesystem, maybe this is needed?
	 * @param id
	 */
	public void removeModule(int id)
	{
		this.availableModules.remove(id);
	}
}
