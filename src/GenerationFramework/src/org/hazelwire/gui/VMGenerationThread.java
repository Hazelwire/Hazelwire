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
		progressDialog.setVMGenerationThread(this);
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
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
