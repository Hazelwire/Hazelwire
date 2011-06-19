package org.hazelwire.gui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.hazelwire.main.Generator;
import org.hazelwire.modules.ModuleHandler;

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
		{ "*.zip" };
		fd.setFilterExtensions(filterExt);
		/*
		 * String selected is het (absolute) pad naar het geselecteerde bestand.
		 */
		String selected = fd.open();
		if(selected != null)
		{
			/*
			 * 
			try
			{
				Generator.getInstance().getModuleSelector().addModule(ModuleHandler.importModule(selected));
				
				ArrayList<String> availableModules = Generator.getInstance().getAvailableModules();
				
				for(String module : availableModules)
				{
					System.out.println(module);
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}

}
