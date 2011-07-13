package org.hazelwire.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabItem;
import org.hazelwire.main.Generator;
import org.hazelwire.modules.Module;
import org.hazelwire.modules.ModuleHandler;
import org.hazelwire.modules.ModuleSelector;

/**
 * This class is used to import {@link Mod}s into the system and to display
 * them in the GUI. It is a subclass of {@link MouseListener} and it is linked
 * to the 'import modules' {@link Button}, on the 'Module Selection' {@link TabItem}
 */
public class ImportMouseListener implements MouseListener
{

	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{}

	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	/**
	 * This method is called whenever the user clicks the 'import modules'
	 * {@link Button}. It creates a new {@link FileDialog} in which the user 
	 * can select a .zip module file. When the user presses 'Open', the path
	 * to this module file will be saved and used to import the module into
	 * the system.
	 */
	public void mouseUp(MouseEvent arg0)
	{
		FileDialog fd = new FileDialog(((Button) arg0.getSource()).getShell(),
				SWT.OPEN);
		fd.setText("Import");
		String[] filterExt =
		{ "*.zip" };
		fd.setFilterExtensions(filterExt);

		String selected = fd.open();
		if(selected != null)
		{
			 
			try
			{
				ModuleSelector modselect = Generator.getInstance().getModuleSelector();
				Module testModule = ModuleHandler.importModule(selected);
				modselect.addModule(testModule);
				GUIBridge.synchronizeModulesBackToFront();
				GUIBuilder.getInstance().updateModList();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
