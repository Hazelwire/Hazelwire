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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;

/**
 * This class is responsible for starting the VM generation process. It
 * is linked to the 'generate' {@link Button}, and thus it is a 
 * subclass of {@link MouseListener}.
 */
public class GenerateListener implements MouseListener
{

	private GUIBuilder gUIBuilder;
	
	/**
	 * Creates an instance of GenerateListener.
	 * @param gUIBuilder the {@link GUIBuilder} parameter is necessary in order
	 * to obtain the program's shell which first needs to serve as a parent for the
	 * {@link GenerateDialog} and later, when the generation process is completed,
	 * needs to be disposed.
	 */
	public GenerateListener(GUIBuilder gUIBuilder)
	{
		this.gUIBuilder = gUIBuilder;
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{}

	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	/**
	 * This method is called when the 'generate' {@link Button} is
	 * clicked. It creates a new {@link GenerateDialog} and initiates the 
	 * VM generation process. If the process is completed, this method disposes
	 * the GUI shell.
	 */
	public void mouseUp(MouseEvent m)
	{
		try
		{
			GenerateDialog dialog = new GenerateDialog(gUIBuilder.getDisplay().getActiveShell());
			VMGenerationThread.getInstance().init(dialog);
			VMGenerationThread.getInstance().start();
			
			String s = dialog.open();
			if(s != null){
				gUIBuilder.getDisplay().getActiveShell().dispose();
			}
		}
		catch (Exception e)
		{
			// TODO add visual error handling
			e.printStackTrace();
		}
	}

}
