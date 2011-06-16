package org.hazelwire.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;

/*
 * Deze klasse is voor het importeren van de modules. Nu doet hij nog niks, behalve
 * een FileDialog laten zien en daar een pad uit krijgen.
 */
public class ImportMouseListener implements MouseListener
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
		FileDialog fd = new FileDialog(((Button) arg0.getSource()).getShell(),
				SWT.OPEN);
		fd.setText("Import");
		/*
		 * Nu zijn alle extensies toegestaan. Maar je kunt ook een subset
		 * aangeven. Als je meer dan een extensie wil toestaan, kun je die
		 * gewoon met , "*.<extensie>" toevoegen.
		 */
		String[] filterExt =
		{ "*.*" };
		fd.setFilterExtensions(filterExt);
		/*
		 * String selected is het (absolute) pad naar het geselecteerde bestand.
		 */
		String selected = fd.open();

	}

}
