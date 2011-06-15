package org.hazelwire.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TagTree implements MouseListener, Observer
{

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

	public static void updateTagtree(Tree tree, String search)
	{
		tree.removeAll();
		HashMap<Tag, ArrayList<Mod>> modsPerTag = ModsBookkeeper.getInstance()
				.getModsPerTag();
		for (Tag t : modsPerTag.keySet())
		{
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
				// Als een module de gezochte text in zijn titel heeft...
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
				}
			}
		}
		tree.redraw();

	}

	public TagTree()
	{
		ModsBookkeeper.getInstance().addObserver(this);
	}

	@Override
	public void mouseDoubleClick(MouseEvent m)
	{
		if (m.getSource() instanceof Tree)
		{
			Tree t = ((Tree) m.getSource());
			t.getSelection();
			TreeItem ti = t.getItem(new Point(m.x, m.y));
			//for (TreeItem ti : t.getSelection())
			//{
				if (ti != null && ti.getItemCount() == 0 &&!ModsBookkeeper.getInstance().isSelected(ti.getText()))
				{
					ModsBookkeeper.getInstance().selectModule(ti.getText());
					ModsBookkeeper.getInstance().setSelected(ModsBookkeeper.getInstance().getSelectedMods().size());
				}
				else if(ti != null)
				{
					ModsBookkeeper.getInstance().setSelected(-1);
					ModsBookkeeper.getInstance().deselectModule(ti.getText());
				}
			//}
			System.out.println("ti: "+ti);
		}

	}

	@Override
	public void mouseDown(MouseEvent arg0)
	{
	}

	@Override
	public void mouseUp(MouseEvent arg0)
	{
	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		// TODO Auto-generated method stub

	}

}