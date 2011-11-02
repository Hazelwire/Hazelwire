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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


/**
 * This class is necessary to update the tree of {@link Challenge}s that
 * is displayed in the GUI. Whenever a {@link Mod} is selected or deselected,
 * its {@link Challenge}s must be added to or removed from the tree. That is 
 * what ChallengesTree is responsible for.
 */
public class ChallengesTree implements MouseListener
{

	/**
	 * A helper method that calls updateTree after retrieving the index of the 
	 * currently selected {@link Mod}, using that index and the {@link Tree}
	 * parameter.
	 * @param tree {@link Tree} that will be updated by the method
	 */
	public static void populateTree(Tree tree)
	{
		updateTree(tree, ModsBookkeeper.getInstance().getSelected());
	}

	/**
	 * Method that updates the given {@link Tree} based on the index of the 
	 * given {@link Mod}. It first removes all previous entries in the
	 * {@link Tree}. Next, it adds the id, number of points and the description
	 * of each {@link Challenge} that is associated with the selected {@link Mod}.
	 * Finally, it calls redraw(), so that all changes will be displayed in the GUI.
	 * @param challenges {@link Tree} that will be updated
	 * @param selected int that represents the index of the selected {@link Mod}.
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
				
				/*
				TreeItem subItem1 = new TreeItem(item, SWT.CHECK);
				subItem1.setText(selectedMod.getTags().toString()); // Apparently
																	// not
																	 NO TAGS FOR YOUUUU
																	 *
																	 */

				TreeItem pointsSub = new TreeItem(item, SWT.NONE);
				pointsSub.setText("Nr. of points: " + c.getPoints());

				/*
				 Challenges don't have descriptions for now
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
				}*/
			}
		}
		challenges.getShell().redraw();
	}

	@Override
	public void mouseDoubleClick(MouseEvent m)
	{
		if(m.getSource() instanceof Tree){
			Tree t = ((Tree) m.getSource());
			TreeItem ti = t.getItem(new Point(m.x, m.y));
			if(ti != null){
				//Expand TreeItems on Linux and Mac OS
				String osName = System.getProperty("os.name");
				//TODO: meer? Also: let someone check this.
				if(osName.toLowerCase().contains("linux") || osName.toLowerCase().contains("mac os")){
					ti.setExpanded(!ti.getExpanded());
				}
			}
		}

	}

	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	public void mouseUp(MouseEvent arg0)
	{}
}
