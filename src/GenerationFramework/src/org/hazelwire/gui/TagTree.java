package org.hazelwire.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This class is responsible for displaying all available {@link Mod}s, sorted
 * by {@link Tag}. It can also show a subset of the {@link Mod}s, based on a 
 * search phrase. Finally, the user should be able to select {@link Mod}s by 
 * double clicking on them in the TagTree. TagTree therefore is a subclass of
 * {@link MouseListener}.
 */
public class TagTree implements MouseListener
{
	/**
	 * Static method that takes a {@link Tree} as input and populates it by
	 * retrieving all {@link Mod}s sorted by {@link Tag} from the instance of
	 * {@link ModsBookkeeper}, creating a {@link TreeItem} for each tag and adding
	 * child {@link TreeItem}s for each {@link Mod} with this {@link Tag}.
	 * @param tree the unpopulated {@link Tree} that needs to be populated.
	 * @return the populated {@link Tree}.
	 * @requires tree != null
	 */
	public static Tree populateTree(Tree tree)
	{

		HashMap<Tag, ArrayList<Mod>> modsPerTag = ModsBookkeeper.getInstance()
				.getModsPerTag();
		for (Tag t : modsPerTag.keySet())
		{
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText(t.getName());
			for (Mod m : modsPerTag.get(t))
			{
				TreeItem subItem = new TreeItem(item, SWT.NONE);
				subItem.setText(m.getName());
			}
		}
		return tree;
	}

	/**
	 * This method updates the {@link Tree} according to a search phrase. It retrieves
	 * the list of {@link Mod}s sorted by {@link Tag} from {@link ModsBookkeeper} and populates
	 * the tree much in the same fashion as the function populateTree. However, the method only 
	 * creates a {@link TreeItem} for a {@link Tag} if either the {@link Tag} itself, one of the
	 * {@link Mod}s with that {@link Tag} or a description of one of these {@link Mod}s contains
	 * the search phrase. Moreover, it only adds {@link Mod}s whose name or description contain 
	 * the search phrase.
	 * @param tree the {@link Tree} to be updated.
	 * @param search the {@link String} that serves as a search phrase.
	 */
	public static void updateTagtree(Tree tree, String search)
	{
		tree.removeAll();
		HashMap<Tag, ArrayList<Mod>> modsPerTag = ModsBookkeeper.getInstance()
				.getModsPerTag();
		for (Tag t : modsPerTag.keySet())
		{
			//Tag contains search phrase.
			if (t.getName().toLowerCase().contains(search.toLowerCase()))
			{
				TreeItem item = new TreeItem(tree, SWT.NONE);
				item.setText(t.getName());
				for (Mod m : modsPerTag.get(t))
				{
					TreeItem subItem = new TreeItem(item, SWT.NONE);
					subItem.setText(m.getName());
				}
			}
			else
			{
				// Mod has the search phrase in its title.
				TreeItem item = null;
				for (Mod m : modsPerTag.get(t))
				{
					if (m.getName().toLowerCase()
							.contains(search.toLowerCase()))
					{
						if (item == null)
						{
							item = new TreeItem(tree, SWT.NONE);
							item.setText(t.getName());
						}
						TreeItem subItem = new TreeItem(item, SWT.NONE);
						subItem.setText(m.getName());
						tree.showItem(subItem);
					}
					//Mods description contains the search phrase.
					else{
						TreeItem subItem = null;
						for(Challenge c : m.getChallenges()){
							if(c.getDescription().toLowerCase().contains(search.toLowerCase())){
								if(subItem == null){
									if(item == null){
										item = new TreeItem(tree, SWT.NONE);
										item.setText(t.getName());
									}
									subItem = new TreeItem(item, SWT.NONE);
									subItem.setText(m.getName());
									tree.showItem(subItem);
								}
							}
						}
					}
				}
			}
		}
		tree.redraw();

	}

	@Override
	/**
	 * This method is called when the user double clicks on the {@link Tree}. It
	 * detects whether the user clicked a {@link TreeItem} and if so, it checks
	 * whether the item clicked was a {@link Tag} or a {@link Mod}. In the latter 
	 * case, the method checks whether this {@link Mod} was already in the list of
	 * selected {@link Mod}s. If so, it removes the {@link Mod} from the list. 
	 * Otherwise, it adds the {@link Mod} to the list of selected {@link Mod}s.
	 */
	public void mouseDoubleClick(MouseEvent m)
	{
		if (m.getSource() instanceof Tree)
		{
			Tree t = ((Tree) m.getSource());
			//t.getSelection();
			TreeItem ti = t.getItem(new Point(m.x, m.y));
				if (ti != null && ti.getItemCount() == 0 &&!ModsBookkeeper.getInstance().isSelected(ti.getText()))
				{
					ModsBookkeeper.getInstance().selectModule(ti.getText());
					ModsBookkeeper.getInstance().setSelected(ModsBookkeeper.getInstance().getSelectedMods().size());
				}
				else if(ti != null && ti.getItemCount()== 0)
				{
					ModsBookkeeper.getInstance().setSelected(-1);
					ModsBookkeeper.getInstance().deselectModule(ti.getText());
				}
				else if(ti != null){
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