package org.hazelwire.gui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Display;

/*
 * Spreekt redelijk voor zich: wanneer een Administrator op
 * deze knop klikt, moet de VM gegenereerd worden.
 */
public class GenerateListener implements MouseListener
{

	private GUIBuilder gUIBuilder;

	public GenerateListener(GUIBuilder gUIBuilder)
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
	}

	@Override
	public void mouseUp(MouseEvent arg0)
	{
		try
		{			
			//new VMGenerationThread().start();
			/*
			 * Je moet dus ergens de dialog.setInput(<String>)gebruiken om input op iets anders dan null te zetten
			 * Daarnaast kun je dialog.updateProgressBar(<int>) gebruiken om de progressbar te updaten. 
			 */
			GenerateDialog dialog = new GenerateDialog(gUIBuilder.getDisplay().getActiveShell());
			String s = dialog.open();
			if(s != null){
				gUIBuilder.getDisplay().getActiveShell().dispose();
			}
		}
		catch (Exception e)
		{
			// TODO add visual error handling
			e.printStackTrace();
		}
	}

}
