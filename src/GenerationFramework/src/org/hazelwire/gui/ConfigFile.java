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
	private HashMap<String, String> actuals;
	
	/**
	 * Creates an instance of Configfile with the specified set of
	 * defaults. It saves the set of defaults and leaves a copy 
	 * available for changes.
	 * @param defaults {@link HashMap<{@link String}, {@link String}>}
	 * containing all configurable settings and the default value
	 * for eacht of those.
	 */
	public ConfigFile(HashMap<String, String> defaults){
		this.defaults = defaults;
		actuals = new HashMap<String, String>(defaults);
	}
	
	public ConfigFile() throws IOException
	{
		this.defaults = defaultsFromBackend();
		this.actuals = new HashMap<String,String>(this.defaults);
	}
	
	/**
	 * This method returns the set containing all configurable options 
	 * and their default values for the Linux version of Hazelwire.
	 * @return {@link HashMap<{@link String}, {@link String}>} containing
	 * options and defaults.
	 */
	public static HashMap<String, String> dummyDefaults(){
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("SSH host port", "2222");
		result.put("SSH guest port", "22");
		result.put("VM log path", "/dev/null");
		result.put("SSH password", "hazelwire");
		result.put("SSH username", "hazelwire");
		result.put("VM path", "dist/HazelwireTest.ova");
		result.put("VM export path", "output/test.ova");
		result.put("Module path", "modules/");
		result.put("Virtualbox path", "/usr/bin/vboxmanage");
		result.put("Property path", "config/userProperties");
		result.put("External module directory", "/home/hazelwire/modules/");
		result.put("External script directory", "/home/hazelwire/");
		result.put("Callback port", "31337");
		result.put("External deploy directory", "/home/hazelwire/deploy/");
		result.put("Output directory", "output/");
		result.put("Temp directory", "tmp/");
		result.put("Known hosts file", "config/known_hosts");
		return result;
	}
	
	public HashMap<String,String> defaultsFromBackend() throws IOException
	{
		HashMap<String, String> result = new HashMap<String, String>();
		
		Enumeration<?> config = Configuration.getInstance().getRawProperties().propertyNames();
		
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
	public String getActual(String key){
		return actuals.get(key);
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
		if(actuals.get(key)!=null){
			actuals.put(key, value);
			this.saveToBackend(key, value);
		}
	}
	
	/**
	 * This method resets the current value of a particular configurable
	 * option to its default value.
	 * @param key {@link String} consisting of the name of the option that
	 * is to be reset to its default value.
	 * @requires this.actuals != null
	 * @requires this.defaults != null
	 * @requires this.actuals.get(key) != null
	 * @requires this.defaults.get(key)!= null
	 * @ensures this.actuals.get(key).equals(this.defaults.get(key))
	 */
	public void resetActual(String key){
		if(actuals.get(key)!= null){
			actuals.put(key, getDefault(key));
		}
	}
	
	/**
	 * This method resets the current values of every configurable option
	 * in the ConfigFile to their default values.
	 * @requires this.actuals != null
	 * @requires this.defaults != null
	 * @requires 	for({@link String} key : actuals.keySet()){
	 * 					this.actuals.get(key) != null
	 * 				}
	 * @requires 	for ({@link String} key : defaults.keySet()){
	 * 					(this.defaults.get(key)!= null
	 * 				}
	 * @ensures	for({@link String} key : actuals.keySet()){
	 * 				this.actuals.get(key).equals(this.defaults.get(key))
	 * 			}
	 */
	public void resetActuals(){
		for(String key : actuals.keySet()){
			resetActual(key);
		}
	}
	
	public void saveToBackend(String key, String value) throws IOException
	{
		Configuration.getInstance().setMagic(key, value);
		Configuration.getInstance().saveUserProperties();
	}
	
	public void synchronizeWithBackend() throws IOException
	{
		Configuration config = Configuration.getInstance();
		
		for(String key : actuals.keySet())
		{
			config.setMagic(key, actuals.get(key));
		}
	}
}
