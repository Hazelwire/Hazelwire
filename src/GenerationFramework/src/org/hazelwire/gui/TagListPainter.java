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

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

/**
 * This class is responsible for painting the list of {@link Tag}s on 
 * the 'VM Generation' tab. It must also pay attention to which {@link Mod} is
 * currently selected. Thus, this class is a subclass of {@link PaintListener}
 * and of {@link Observer}. For scrolling purposes it also implements 
 * {@link MouseListener}.
 */
public class TagListPainter implements PaintListener, Observer, MouseListener
{

	private Canvas canvas;
	
	/**
	 * Creates an instance of TagListPainter and adds itself as an {@link Observer}
	 * to the instance of {@link ModsBookkeeper}.
	 */
	public TagListPainter()
	{
		ModsBookkeeper.getInstance().addObserver(this);
	}
	
	/**
	 * Updates the size of the {@link Canvas} that is being painted on, so that the 
	 * {@link Canvas} grows bigger when the {@link Tag}s do not fit on it.
	 * @requires canvas != null
	 */
	public void updateSize()
	{
		if (canvas == null)
			return;
		int curSize = canvas.getParent().getSize().y;
		
		Mod selected = ModsBookkeeper.getInstance().getSelectedMod();
		int modsSize;
		if(selected != null){
			modsSize = (selected.getTags().size()) * 15;
		}
		else{
			modsSize = curSize;
		}
		
		if (curSize > modsSize)
		{
			canvas.setSize(canvas.getSize().x, curSize);
		}
		else
		{
			canvas.setSize(canvas.getSize().x, modsSize);
		}
	}
	
	/**
	 * This method updates the size of the {@link Canvas} that is being painted on, so
	 * that the {@link Canvas} shrinks whenever it takes up excess space when a {@link Mod}
	 * is deselected or a {@link Mod} with less {@link Tag}s is selected. This is the case 
	 * when the list of {@link Mod}s is too long and the {@link Canvas} is scrollable. Now 
	 * when the user deselects a {@link Mod}, there will be empty lines, whilst the {@link Canvas}
	 * is still scrollable. This is undesired, so the excess empty lines are removed, using this method.
	 * @requires canvas != null
	 */
	public void shrink()
	{
		if (canvas == null)
		{
			return;
		}
		Mod selected = ModsBookkeeper.getInstance().getSelectedMod();
		int modsSize;
		if(selected != null){
			modsSize = (selected.getTags().size()) * 15;
		}
		else{
			modsSize = canvas.getParent().getSize().y;
		}
		
		if (modsSize < canvas.getParent().getSize().y)
		{
			canvas.setBounds(0, 0, canvas.getSize().x, canvas.getParent().getSize().y - 4);
		}
		else
		{
			canvas.setBounds(0, 0, canvas.getSize().x, modsSize);
		}
	}

	@Override
	/**
	 * This method paints the {@link Canvas}. It paints the background and prints 
	 * all the {@link Tag}s associated with the currently selected {@link Mod}. Finally,
	 * it calls updateSize, to ensure a perfect fit without excess empty lines.
	 */
	public void paintControl(PaintEvent p)
	{
		canvas = ((Canvas) p.getSource());
		Display display = canvas.getDisplay();
		Device device = display.getCurrent();
		GC g = p.gc;
		Rectangle clientArea = canvas.getClientArea();
		int height = clientArea.height;
		int width = clientArea.width;
		int x = 0;
		int y = 0;
		Color col;
		int selected = ModsBookkeeper.getInstance().getSelected();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		if (selected != -1)
		{
			Mod mod = ModsBookkeeper.getInstance().getSelectedMods().get(
					selected - 1);
			tags = mod.getTags();
		}
		
		int i;
		for (i = 15; i <= height; i = i + 15)
		{
			if (i % 2 == 0)
			{
				col = display.getSystemColor(SWT.COLOR_WHITE);
			}
			else
			{
				col = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
			}
			g.setBackground(col);

			g.drawRectangle(x, y + i - 15, x + width, y + i);
			g.fillRectangle(x, y + i - 15, x + width, y + i);
			g.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
			
			if ((i / 15) - 1 < tags.size() && i >= 15)
			{
				g.setBackground(col);
				g.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				g.drawText(tags.get((i / 15) - 1).getName(), x + 20, y + i
						- 15);
			}
		}
		if (i % 2 == 0)
		{
			col = display.getSystemColor(SWT.COLOR_WHITE);
		}
		else
		{
			col = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
		g.setBackground(col);
		g.drawRectangle(x, y + i - 15, x + width, height);
		g.fillRectangle(x, y + i - 15, x + width, height);

		g.setForeground(device.getSystemColor(SWT.COLOR_BLACK));
		g.setLineWidth(2);

		g.drawLine(x + 1, y + 1, x + width - 1, y + 1);
		g.drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
		g.drawLine(x + 1, y + 1, x + 1, y + height - 1);
		g.drawLine(x + width - 1, y + 1, x + width - 1, y + height - 1);
		
		this.updateSize();
	}
	
	//@Override
	/**
	 * This method is called by the {@link Observable} {@link ModsBookkeeper}, whenever
	 * the selected {@link Mod} changes. It checks whether the cause of 
	 * the call is a {@link Mod} selection or a {@link Mod} deselection and calls the 
	 * updateSize and shrink functions respectively. After that, if the canvas has already
	 * been instantiated, it calls redraw, to show the changes in the GUI.
	 */
	public void update(Observable arg0, Object arg)
	{
		String s = (String) arg;
		if (s.equals("SELECTED"))
		{
			if (canvas != null)
			{
				this.updateSize();
				canvas.redraw();
			}
		}
		else if (s.equals("DESELECTED"))
		{
			if(canvas != null){
				this.shrink();
				canvas.redraw();
			}
		}

	}
	
	@Override
	public void mouseDoubleClick(MouseEvent arg0) {}

	@Override
	public void mouseDown(MouseEvent arg0) {}

	@Override
	/**
	 * This method makes sure the focus is on the {@link Canvas}. This is
	 * necessary because it allows the user to scroll using the scroll wheel.
	 */
	public void mouseUp(MouseEvent arg0) {
		canvas.setFocus();
	}

}
