package org.hazelwire.gui;

import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class TabItemTest extends TabItem implements Drawable
{
	public TabItemTest(TabFolder parent, int style)
	{
		super(parent, style);

	}

	public void internal_dispose_GC(int arg0, GCData arg1)
	{
	}

	@Override
	public int internal_new_GC(GCData arg0)
	{
		return 0;
	}

	public void internal_dispose_GC(long arg0, GCData arg1)
	{
		// TODO Auto-generated method stub

	}
}
