package org.hazelwire.gui;

import org.hazelwire.main.Generator;

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
			GUIBridge.synchronizeModulesFrontToBack();
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
}