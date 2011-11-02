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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.hazelwire.xml.ExportModuleConfiguration;

/**
 * This class is responsible for converting the current 
 * generation settings to an XML file. It is a subclass of
 * {@link MouseListener} and it is bound to the 'export to
 * XML' button.
 */
public class ConfigExportListener implements MouseListener
{	
	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{}

	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	/**
	 * When the 'export to XML' button is clicked, this method is called.
	 * It creates a new {@link FileDialog}, in which the user can find the
	 * location to save the XML file to and enter its name, followed by the
	 * .xml extension. If the user completes these steps, the method will
	 * export the current configuration to an XML file.
	 */
	public void mouseUp(MouseEvent m)
	{
		FileDialog fd = new FileDialog(((Button)m.getSource()).getShell(), SWT.SAVE);
        fd.setText("Export configuration");
        String[] ext = new String[1];
        ext[0] = "*.xml";
        fd.setFilterExtensions(ext);

        String selected = fd.open();
        if(selected!=null){
        	try
			{
        		GUIBridge.synchronizeModulesFrontToBack();
				new ExportModuleConfiguration().exportModuleConfiguration(selected);
			}
			catch (Exception e)
			{
				// TODO proper visual errors
				e.printStackTrace();
			}
        }
	}

}
