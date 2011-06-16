package org.hazelwire.gui;

import java.util.HashMap;

public class ConfigFile {
	
	private HashMap<String, String> defaults;
	private HashMap<String, String> actuals;
	
	public ConfigFile(HashMap<String, String> defaults){
		this.defaults = defaults;
		actuals = new HashMap<String, String>(defaults);
	}
	
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
	
	public HashMap<String, String> getDefaults(){
		return this.defaults;
	}
	
	public String getDefault(String key){
		return defaults.get(key);
	}
	
	public String getActual(String key){
		return actuals.get(key);
	}
	
	public void setActual(String key, String value){
		if(actuals.get(key)!=null){
			actuals.put(key, value);
		}
	}
	
	public void resetActual(String key){
		if(actuals.get(key)!= null){
			actuals.put(key, getDefault(key));
		}
	}
	
	public void resetActuals(){
		for(String key : actuals.keySet()){
			resetActual(key);
		}
	}
	
}
