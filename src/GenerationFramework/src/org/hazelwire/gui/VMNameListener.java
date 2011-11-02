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

import java.io.IOException;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Text;

/**
 * This class is responsible for taking the user input with regard
 * to the VM name and storing them in the system. In order to fulfill 
 * this task, it implements {@link FocusListener}.
 */
public class VMNameListener implements FocusListener {

	
	@Override
	public void focusGained(FocusEvent arg0) {}

	@Override
	/**
	 * This method is called whenever the user stops editing (focussing on)
	 * a certain {@link Widget}, in our case a {@link Text}. When it is called,
	 * the {@link String} inside the {@link Text} will be saved to the correct
	 * configuration option in the {@link ConfigFile}.
	 */
	public void focusLost(FocusEvent f) {
		if(f.getSource() instanceof Text){
			Text t = (Text)f.getSource();
			try
			{
				ModsBookkeeper.getInstance().getConfigFile().setActual("vmName", t.getText());
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}
	}
}
