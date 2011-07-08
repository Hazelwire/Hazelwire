package org.hazelwire.gui;

import java.io.IOException;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.hazelwire.virtualmachine.VMDownloader;

/*
 * Nou hier kun je iets met de download knop (Om de VM te downloaden).
 */
public class DownLoadListener implements MouseListener
{

	private GUIBuilder gUIBuilder;

	public DownLoadListener(GUIBuilder gUIBuilder)
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
		VMDownloader vmDownloader = new VMDownloader(new DownloadBarLink());
		
		try
		{
			vmDownloader.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
