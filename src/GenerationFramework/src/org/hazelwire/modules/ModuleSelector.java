/*******************************************************************************
 * Copyright (c) 2011 The Hazelwire Team.
 *     
 * This file is part of Hazelwire.
 * 
 * Hazelwire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Hazelwire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.hazelwire.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.hazelwire.main.Configuration;

/**
 * This class is responsible for managing everything that has to do with the set of modules in the backend. It will import, select, deselect both
 * normal and hidden (system) modules.
 * @author Tim Strijdhorst
 *
 */
public class ModuleSelector
{
	private HashMap<Integer,Module> selectedModules, availableModules;
	private ArrayList<Module> hiddenModules;
	private int idCounter;
	private HashMap<String,ModulePackage> modulePackageList; //<Name,Package>
	
	public ModuleSelector()
	{
		this.selectedModules = new HashMap<Integer,Module>();
		this.availableModules = new HashMap<Integer,Module>();
		this.hiddenModules = new ArrayList<Module>();
		this.idCounter = 0; //Will not be updated when an item is removed
	}
	
	public void init() throws Exception
	{
		ArrayList<Module> moduleList = ModuleHandler.scanForModules(Configuration.getInstance().getModulePath());
		
		Iterator<Module> iterator = moduleList.iterator();
		
		while(iterator.hasNext())
		{
			Module tempModule = iterator.next();
			
			if(tempModule.isHidden())
			{
				this.hiddenModules.add(tempModule);
			}
			else
			{
				tempModule.setId(idCounter++);
				this.availableModules.put(tempModule.getId(),tempModule);
			}
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
	
	/**
	 * Linear search is gay, but this is just for a while to make it work.
	 * @todo redo the search
	 * @param moduleName
	 */
	public Module getModuleByNameAndPackage(String moduleName, String packageName)
	{
		Module tempModule = null;
		for(Module module : availableModules.values())
		{
			if(module.getName().equals(moduleName))
			{
				if(packageName != null && module.getModulePackage() != null 
						&& module.getModulePackage().getName().equals(packageName) || module.getModulePackage() == null)
				{
					tempModule = module;
				}
			}
		}
		
		return tempModule;
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
		
		module.setId(idCounter++);
		
		this.availableModules.put(this.idCounter,module);
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
	
	public void addHiddenModule(Module module)
	{
		this.hiddenModules.add(module);
	}
	
	public void removeHiddenModule(Module module)
	{
		this.hiddenModules.remove(module);
	}
	
	/**
	 * Get all modules marked for installation (selected + hidden)
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Module> getMarkedModules()
	{
		ArrayList<Module> tempArr = (ArrayList<Module>) hiddenModules.clone();
		tempArr.addAll(this.getSelectedModules().values());
		return tempArr;
	}
	
	public ArrayList<Module> getHiddenModules()
	{
		return this.hiddenModules;
	}

	public HashMap<Integer, Module> getSelectedModules()
	{
		return selectedModules;
	}

	public HashMap<Integer, Module> getAvailableModules()
	{
		return availableModules;
	}

	public HashMap<String, ModulePackage> getModulePackageList()
	{
		return modulePackageList;
	}
}
