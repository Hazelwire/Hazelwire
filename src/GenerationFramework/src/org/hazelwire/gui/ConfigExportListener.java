package org.hazelwire.gui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

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
	public void mouseUp(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

}
