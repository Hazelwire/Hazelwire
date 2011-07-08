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
	
	/**
	 * Synchronizes the selection of modules in the frontend and the backend + their options.
	 */
	public static void synchronizeModulesFrontToBack()
	{
		ModuleSelector modSelect = Generator.getInstance().getModuleSelector();
		ModsBookkeeper modBook = ModsBookkeeper.getInstance();
		
		for(Mod mod : modBook.getSelectedMods())
		{
			ArrayList<Challenge> challenges = mod.getChallenges();
			ArrayList<Flag> flags = new ArrayList<Flag>();
			
			for(Challenge challenge : challenges)
			{
				flags.add(challenge.toFlag());
			}
			
			ArrayList<Option> options = new ArrayList<Option>(mod.getOptions().values());
			
			Module tempModule = modSelect.getAvailableModules().get(mod.getId());
			tempModule.setFlags(flags);
			tempModule.setOptions(options); //replace all the options with their user specified values
			modSelect.selectModule(mod.getId()); //id corresponds to the backend id
		}
	}
	
	public static void synchronizeModulesBackToFront()
	{
		ModuleSelector modSelect = Generator.getInstance().getModuleSelector();
		ModsBookkeeper modBook = ModsBookkeeper.getInstance();
		
		for(Module module : modSelect.getSelectedModules().values())
		{
			Collection<Flag> flags = module.getFlags();
			ArrayList<Challenge> challenges = new ArrayList<Challenge>();
			
			for(Flag flag : flags)
			{
				challenges.add(new Challenge(flag));
			}
			
			Mod tempMod = modBook.getModuleByName(module.getName());
			tempMod.setChallenges(challenges);
			tempMod.setOptions(module.getOptions());
			modBook.selectModule(module.getName());	
		}
		GUIBuilder.getInstance().updateModList();
	}
}
