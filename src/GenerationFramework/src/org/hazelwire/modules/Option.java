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

/**
 * This class represents a certain option for a module. This can be anything that the creator would like it to be and the system will not do
 * anything with it except for allowing the user to set the value and then passing it on to the deployscript when it's installed on the virtualmachine.
 * @author Tim Strijdhorst
 *
 */
public class Option
{
	private int id;
	String name, type, value, defaultValue;
	
	public Option(String name, String type, String defaultValue)
	{
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = this.defaultValue;
	}
	
	public Option(String name, String defaultValue)
	{
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = this.defaultValue;
	}
	
	public Option()
	{
		//empty constructor, client has to make sure that all the data has been filled for usage...
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}
}
