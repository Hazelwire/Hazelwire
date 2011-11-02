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
package org.hazelwire.gui;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import org.hazelwire.main.Configuration;

/**
 * This class keeps track of both the configurations the user specifies
 * and the default configurations that Hazelwire provides. 
 */
public class ConfigFile {
	
	private HashMap<String, String> defaults;
	
	public ConfigFile() throws IOException
	{
		this.defaults = defaultsFromBackend();
	}
	
	public HashMap<String,String> defaultsFromBackend() throws IOException
	{
		HashMap<String, String> result = new HashMap<String, String>();
		
		Enumeration<?> config = Configuration.getInstance().getRawDefaultProperties().propertyNames();
		
		while(config.hasMoreElements())
		{
			String key = (String) config.nextElement();
			result.put(key, Configuration.getInstance().getMagic(key));
		}
		
		return result;
	}
	
	/**
	 * @return {@link HashMap<{@link String}, {@link String}>} containing
	 * all configurable options and their default values. 
	 */
	public HashMap<String, String> getDefaults(){
		return this.defaults;
	}
	
	/**
	 * This method finds the default value for a specific option.
	 * @param key {@link String} consisting of the name of the configurable
	 * option.
	 * @return the default value associated with this particular option, or
	 * null, if no such option exists.
	 * @requires this.defaults != null
	 */
	public String getDefault(String key){
		return defaults.get(key);
	}
	
	/**
	 * This method finds the current (possibly user adapted) value
	 * for a specific option.
	 * @param key {@link String} consisting of the name of the configurable
	 * option.
	 * @return the current value this option has, or null, if no such
	 * option exists.
	 * @requires this.actuals != null
	 */
	public String getActual(String key) throws IOException
	{
		return Configuration.getInstance().getMagic(key);
	}
	
	/**
	 * This method changes the current value of a particular configurable
	 * option.
	 * @param key {@link String} consisting of the name of the configurable
	 * option whose value will be changed.
	 * @param value {@link String} consisting of the new value for option key.
	 * @throws IOException 
	 * @requires this.actuals != null
	 * @requires this.actuals.get(key) != null
	 */
	public void setActual(String key, String value) throws IOException{
		if(getActual(key)!=null){
			this.saveToBackend(key, value);
		}
	}
	
	/**
	 * This method resets the current value of a particular configurable
	 * option to its default value.
	 * @param key {@link String} consisting of the name of the option that
	 * is to be reset to its default value.
	 * @throws IOException 
	 */
	public void resetActual(String key) throws IOException
	{
		if(getActual(key)!= null){
			this.saveToBackend(key, getDefault(key));
		}
	}
	
	/**
	 * This method resets the current values of every configurable option
	 * in the ConfigFile to their default values.
	 * @throws IOException 
	 */
	public void resetActuals() throws IOException
	{
		Enumeration<?> propertyEnum = Configuration.getInstance().getRawProperties().propertyNames();
		
		while(propertyEnum.hasMoreElements())
		{
			resetActual((String) propertyEnum.nextElement());
		}
	}
	
	public void saveToBackend(String key, String value) throws IOException
	{
		Configuration.getInstance().setMagic(key, value);
		Configuration.getInstance().saveUserProperties();
	}
}
