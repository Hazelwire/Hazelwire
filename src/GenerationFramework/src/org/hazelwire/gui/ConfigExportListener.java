package org.hazelwire.gui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.Configuration;
import org.hazelwire.xml.ExportModuleConfiguration;

/*
 * In deze klasse moet je het tegenovergestelde doen als
 * bij ConfigImportMouseListener: de huidige configuratie 
 * exporteren als XML of iets dergelijks.
 */
public class ConfigExportListener implements MouseListener
{

	private GUIBuilder gUIBuilder;

	public ConfigExportListener(GUIBuilder gUIBuilder)
	{
		this.gUIBuilder = gUIBuilder;
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDown(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseUp(MouseEvent m)
	{
		FileDialog fd = new FileDialog(((Button)m.getSource()).getShell(), SWT.OPEN);
        fd.setText("Export configuration");

        /*
         * Dit (String selected) is het (absolute) pad naar het geselecteerde bestand.
         */
        String selected = fd.open();
        if(selected!=null){
        	try
			{
        		VMGenerationThread.synchronizeModules();
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
