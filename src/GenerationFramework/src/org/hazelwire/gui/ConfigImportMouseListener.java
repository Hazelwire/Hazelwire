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
import org.hazelwire.xml.ImportModuleConfiguration;

/**
 * This class is used for reading an XML file containing
 * Virtual Machine configuration information (as generated in
 * {@link ConfigExportListener}) and converting it so that
 * the GUI displays and stores it correctly. It is a subclass of
 * {@link MouseListener} and it is bound to the 'import from XML'
 * button.
 */
public class ConfigImportMouseListener implements MouseListener
{
	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{}

	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	/**
	 * When the 'import from XML' button is clicked, this method
	 * is called. It creates a new {@link FileDialog}, in which the
	 * user can select the XML file to be used. If this is completed
	 * correctly, the method will parse the XML and set all configurations
	 * specified in it in the system. These settings will also be
	 * displayed in the GUI.
	 */
	public void mouseUp(MouseEvent m)
	{
		FileDialog fd = new FileDialog(((Button)m.getSource()).getShell(), SWT.OPEN);
        //Hier stond eerst export. Nu niet meer.
		fd.setText("Import configuration");
		String[] ext = new String[1];
		ext[0] = "*.xml";
		fd.setFilterExtensions(ext);
		
        String selected = fd.open();
        if(selected!=null){
        	try
			{
				new ImportModuleConfiguration(selected).parseDocument();
				GUIBridge.synchronizeModulesBackToFront();
			}
			catch (Exception e)
			{
				// TODO proper visual errors
				e.printStackTrace();
			}
        }
	}

}
