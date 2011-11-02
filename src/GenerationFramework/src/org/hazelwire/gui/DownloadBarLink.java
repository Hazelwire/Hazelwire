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
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.Configuration;
import org.hazelwire.virtualmachine.ProgressInterface;

public class DownloadBarLink implements ProgressInterface
{
	@Override
	public void setProgress(final int progress)
	{
		Display display = GUIBuilder.getInstance().getDisplay();
		
		display.asyncExec(new Runnable()
			{
				public void run()
				{
					ProgressBar pb = GUIBuilder.getInstance().getProgressBar();
					
					if(progress >= 0 && progress <= 100)
					{
						pb.setSelection(progress);
					}
				}
			}
		);
	}
	
	public void setFilePath(final String filePath)
	{
		Display display = GUIBuilder.getInstance().getDisplay();
		
		display.asyncExec(new Runnable()
			{
				public void run()
				{
					Text text_filePath = GUIBuilder.getInstance().getTextFilePath();
					text_filePath.setText(filePath);
				}
			}
		);
		
		try
		{
			Configuration.getInstance().setVMPath(filePath);
			Configuration.getInstance().saveUserProperties();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
