package org.hazelwire.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.hazelwire.modules.Module;

/**
 * This class is necessary to update the tree of {@link Challenge}s that
 * is displayed in the GUI. Whenever a {@link Module} is selected or deselected,
 * its {@link Challenge}s must be added to or removed from the tree. That is 
 * what ChallengesTree is responsible for.
 */
public class ChallengesTree implements MouseListener
{

	/**
	 * A helper method that calls updateTree after retrieving the index of the 
	 * currently selected {@link Module}, using that index and the {@link Tree}
	 * parameter.
	 * @param tree {@link Tree} that will be updated by the method
	 */
	public static void populateTree(Tree tree)
	{
		updateTree(tree, ModsBookkeeper.getInstance().getSelected());
	}

	/**
	 * Method that updates the given {@link Tree} based on the index of the 
	 * given {@link Module}. It first removes all previous entries in the
	 * {@link Tree}. Next, it adds the id, number of points and the description
	 * of each {@link Challenge} that is associated with the selected {@link Module}.
	 * Finally, it calls redraw(), so that all changes will be displayed in the GUI.
	 * @param challenges {@link Tree} that will be updated
	 * @param selected int that represents the index of the selected {@link Module}.
	 * @requires challenges != null
	 * @requires selected == -1 || (selected >= 1 && selected =< 
	 * ModsBookkeeper.getInstance().getSelectedModules().size())
	 */
	public static void updateTree(Tree challenges, int selected)
	{
		ChallengesTree ct = new ChallengesTree();
		challenges.addMouseListener(ct);
		Display display = challenges.getDisplay();
		challenges.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		challenges.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		challenges.removeAll();
		if (selected != -1)
		{
			Mod selectedMod = ModsBookkeeper.getInstance().getSelectedMods()
					.get(selected - 1);
			for (Challenge c : selectedMod.getChallenges())
			{
				TreeItem item = new TreeItem(challenges, SWT.NONE);
				item.setText("Challenge " + c.getIdString());
				TreeItem subItem1 = new TreeItem(item, SWT.CHECK);
				subItem1.setText(selectedMod.getTags().toString()); // Apparently
																	// not

				TreeItem pointsSub = new TreeItem(item, SWT.NONE);
				pointsSub.setText("Nr. of points: " + c.getPoints());

				String d = c.getDescription();
				int width = challenges.getBounds().width;
				int length = width / 6;
				String[] split = d.split(" ");

				for (int k = 0; k < split.length; k++)
				{
					String zin = "";
					int j;
					for (j = k; j < split.length
							&& (zin + split[j]).length() <= length; j++)
					{
						zin += " " + split[j];
					}
					TreeItem subItem2 = new TreeItem(item, SWT.WRAP);
					subItem2.setText(zin);
					k = j - 1;
				}
			}
		}
		challenges.getShell().redraw();
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{
		// FIXME: uitvouwen in linux?

	}

	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	public void mouseUp(MouseEvent arg0)
	{}
}
