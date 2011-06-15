package org.hazelwire.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.hazelwire.main.Generator;
import org.hazelwire.modules.Flag;
import org.hazelwire.modules.Module;
import org.hazelwire.modules.ModuleSelector;
import org.hazelwire.modules.Option;


/**
 * This class is the bridge between the backend and the GUI. This is done so the backend doesn't use any code
 * from the GUI so anyone can build an alternative interface without having to deal with backend code.
 * @author shokora
 *
 */
public class GUIBridge
{
	public static ArrayList<Mod> getModulesForGUI()
	{
		/*
		 * Lijst met Mods (Module klasse) die wordt teruggegeven naar de
		 * singleton klasse ModsBookkeeper. Dat is waar jou data ook heen
		 * moeten: de ArrayList mods bevat alle modules in het systeem (Zie ook
		 * klasse ModsBookkeeper).
		 */
		ArrayList<Mod> tempMods = new ArrayList<Mod>();
		Collection<Module> tempModules = Generator.getInstance().getModuleSelector().getAvailableModules().values();
		
		for(Module tempModule : tempModules)
		{			
			Mod m = new Mod(tempModule.getName());
			m.setId(tempModule.getId()); //The id should correspond with the backend id so we can easily select them in the backend
			
			for(String tag : tempModule.getTags())
			{
				m.addTag(tag);
			}
			
			/**
			 * @todo this works totally different than intended, have to fix that
			 */
			if(tempModule.getModulePackage() != null)
			{
				m.addPackage(tempModule.getModulePackage().getName());
			}
			
			for(Flag flag : tempModule.getFlags())
			{
				m.addChallenge(flag.getId(), flag.getDescription(), flag.getPoints());
			}
			
			for(Option option : tempModule.getOptions())
			{
				m.addOption(option);
			}
			
			tempMods.add(m);
		}
		
		return tempMods;
	}
}
