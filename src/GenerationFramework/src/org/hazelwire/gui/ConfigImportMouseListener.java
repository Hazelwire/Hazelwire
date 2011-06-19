package org.hazelwire.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.hazelwire.xml.ExportModuleConfiguration;
import org.hazelwire.xml.ImportModuleConfiguration;

/*
 * Deze klasse is voor het afhandelen van het klikken op de import knop in
 * het tweede tabblad. Die moet er voor zorgen dat je een configuratie kunt 
 * importen vanuit XML of iets dergelijks.
 */
public class ConfigImportMouseListener implements MouseListener
{

	private GUIBuilder gUIBuilder;

	public ConfigImportMouseListener(GUIBuilder gUIBuilder)
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
				new ImportModuleConfiguration(selected);
			}
			catch (Exception e)
			{
				// TODO proper visual errors
				e.printStackTrace();
			}
        }
	}

}
