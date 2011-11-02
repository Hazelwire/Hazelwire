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
