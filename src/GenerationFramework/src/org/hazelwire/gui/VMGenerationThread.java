package org.hazelwire.gui;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.Generator;
import org.hazelwire.modules.Flag;
import org.hazelwire.modules.Module;
import org.hazelwire.modules.ModuleSelector;
import org.hazelwire.modules.Option;

/**
 * @todo find a way to stop the generation process from another thread when pushed cancel
 * @author shokora
 *
 */
public class VMGenerationThread extends Thread
{
	GenerateDialog progressDialog;
	private static VMGenerationThread genThread;
	
	public static synchronized VMGenerationThread getInstance()
	{
		if(!(genThread instanceof VMGenerationThread))
		{
			genThread = new VMGenerationThread();
		}
		
		return genThread;
	}
	
	public VMGenerationThread()
	{
		super("generation thread");
		
	}
	
	public void init(GenerateDialog progressDialog)
	{
		this.progressDialog = progressDialog;
	}
	
	@Override
	public void run()
	{
		try
		{
			synchronizeModules();
			Generator.getInstance().setTui(new GUIOutput(GUIBuilder.getInstance().getDisplay(),GUIBuilder.getInstance().getTextOutput(),this.progressDialog));
			((GUIOutput) Generator.getInstance().getTui()).clear(); //clear the text area before starting the generation process
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
	 */
	public static void synchronizeModules()
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
}