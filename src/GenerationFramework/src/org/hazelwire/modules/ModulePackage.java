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

/**
 * This class represents the module package i.e. a set of modules that belong together.
 * This can be used for packaging modules from the same wargame for instance.
 * @author Tim Strijdhorst
 *
 */
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
