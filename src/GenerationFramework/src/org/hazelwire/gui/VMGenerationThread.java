package org.hazelwire.gui;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.Generator;
import org.hazelwire.modules.ModuleSelector;
import org.hazelwire.modules.Option;

public class VMGenerationThread extends Thread
{
	Display display;
	Text output;
	
	public VMGenerationThread()
	{
		super("generation thread");
	}
	
	@Override
	public void run()
	{
		try
		{
			synchronizeModules();
			Generator.getInstance().setTui(new GUITextOutput(GUIBuilder.getInstance().getDisplay(),GUIBuilder.getInstance().getTextOutput()));
			Generator.getInstance().generateVM();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Synchronizes the selection of modules in the frontend and the backend + their options.
	 * This will only be done at the very end because there is no reason to keep track of them twice (this can only lead to additional errors).
	 */
	public static void synchronizeModules()
	{
		ModuleSelector modSelect = Generator.getInstance().getModuleSelector();
		ModsBookkeeper modBook = ModsBookkeeper.getInstance();
		
		for(Mod mod : modBook.getSelectedMods())
		{
			ArrayList<Option> options = new ArrayList<Option>(mod.getOptions().values());
			modSelect.getAvailableModules().get(mod.getId()).setOptions(options); //replace all the options with their user specified values
			modSelect.selectModule(mod.getId()); //id corresponds to the backend id
		}
	}
}
