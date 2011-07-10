package org.hazelwire.gui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.hazelwire.virtualmachine.VMDownloader;

/**
 * Class DownLoadListener is responsible for downloading the Hazelwire
 * base Virtual Machine. A download is started by clicking the 'download'
 * button, so this class is a subclass of {@link MouseListener}.
 */
public class DownLoadListener implements MouseListener
{
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
