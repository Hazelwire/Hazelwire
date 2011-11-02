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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.InterfaceOutput;

/**
 * 
 * @author Joost
 *
 */
public class GUIOutput implements InterfaceOutput
{
	Text output;
	Display display;
	GenerateDialog progressDialog;
	
	public GUIOutput(Display display, Text output, GenerateDialog progressDialog)
	{
		this.output = output;
		this.display = display;
		this.progressDialog = progressDialog;
	}
	
	@Override
	public void print(final String message)
	{
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				output.append(message);
				progressDialog.setProgressText(message);
			}
		});
	}

	@Override
	public void println(final String message)
	{
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				output.append(message+"\n");
				progressDialog.setProgressText(message);
			}
		});
	}
	
	public void clear()
	{
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				output.setText("");
				progressDialog.setProgressText("");
			}
		});
	}
	
	public void setProgress(final int progress)
	{
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				progressDialog.updateProgressBar(progress);
			}
		});
	}
}
