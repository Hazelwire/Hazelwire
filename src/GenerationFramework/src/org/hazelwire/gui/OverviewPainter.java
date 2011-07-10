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
 * This class is responsible for painting the list of selected {@link Mod}s on 
 * the 'VM Generation' tab. It must pay attention to {@link Mod} selections 
 * that occur in the {@link Tag} {@link Tree} and {@link Mod} adaptations that 
 * occur in the 'Settings' tab. Thus, this class is a subclass of {@link PaintListener}
 * and of {@link Observer}. For scrolling purposes it also implements {@link MouseListener}.
 */
public class OverviewPainter implements PaintListener, Observer, MouseListener
{

	private Canvas canvas;
	private ArrayList<Object> printThis = new ArrayList<Object>();
	
	/**
	 * Creates an instance of OverviewPainter and adds itself as an {@link Observer}
	 * to the instance of {@link ModsBookkeeper}.
	 */
	public OverviewPainter()
	{
		ModsBookkeeper.getInstance().addObserver(this);
	}

	/**
	 * Updates the size of the {@link Canvas} that is being painted on, so that the 
	 * {@link Canvas} grows bigger when the selected {@link Mod}s no longer fit on it.
	 * @requires canvas != null
	 */
	public void updateSize()
	{
		if (canvas == null)
			return;
		int curSize = canvas.getParent().getSize().y;
		int modsSize = (1 + printThis.size()) * 15;
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
	 * is deleted. This is the case when the list of {@link Mod}s is too long and the 
	 * {@link Canvas} is scrollable. Now when the user deletes a {@link Mod}, there will be
	 * empty lines, whilst the {@link Canvas} is still scrollable. This is undesired, so the 
	 * empty lines are removed, using this method.
	 * @requires canvas != null
	 */
	public void shrink()
	{
		if (canvas == null)
		{
			return;
		}
		int modsSize = (1 + printThis.size()) * 15;
		if (modsSize < canvas.getParent().getSize().y)
		{
			canvas.setBounds(0, 0, canvas.getSize().x, canvas.getParent().getSize().y -4);
		}
		else
		{
			canvas.setBounds(0, 0, canvas.getSize().x, modsSize);
		}
	}

	@Override
	/**
	 * This method paints the {@link Canvas}. It paints the background and prints 
	 * the names and values of the selected {@link Mod}s. Moreover, it prints the
	 * names and values of each of the {@link Challenge}s associated with each 
	 * selected {@link Mod}. Finally, it calls updateSize, to ensure a perfect fit 
	 * without excess empty lines.
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
		ArrayList<Mod> mods = ModsBookkeeper.getInstance().getSelectedMods();

		printThis = new ArrayList<Object>();
		for (Mod m : mods)
		{
			printThis.add(m);
			for (Challenge c : m.getChallenges())
			{
				printThis.add(c);
			}
		}

		int i;
		for (i = 15; i <= height; i = i + 15)
		{
			if (i == 15)
			{
				col = display.getSystemColor(SWT.COLOR_BLACK);
			}
			else if (i % 2 == 0)
			{
				col = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
			}
			else
			{
				col = display.getSystemColor(SWT.COLOR_WHITE);
			}
			g.setBackground(col);

			g.drawRectangle(x, y + i - 15, x + width, y + i);
			g.fillRectangle(x, y + i - 15, x + width, y + i);
			g.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

			if (i == 15)
			{
				g.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
				g.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				g.drawText("Module", x + 20, y + i - 15);
				g.drawText("Challenge", x + (7 * width / 15) + 15 - 3, y + i
						- 15);
				g
						.drawText("Points", x + (10 * width / 15) + 15 - 3, y
								+ i - 15);
				g.drawText("Subtotal", x + (12 * width / 15) + 15 - 3, y + i
						- 15);
			}
			else if ((i / 15) - 2 < printThis.size() && i > 15)
			{
				g.setBackground(col);
				g.setForeground(display.getSystemColor(SWT.COLOR_BLACK));

				if (printThis.get((i / 15) - 2) instanceof Mod)
				{
					Mod mod = (Mod) (printThis.get((i / 15) - 2));
					g.drawText(mod.getName(), x + 20, y + i - 15);
					g.drawText(mod.getPoints() + "",
							x + (12 * width / 15) + 15, y + i - 15);
				}
				else if (printThis.get((i / 15) - 2) instanceof Challenge)
				{
					Challenge c = (Challenge) printThis.get((i / 15) - 2);
					g
							.drawText(c.getIdString(), x + (7 * width / 15) + 15, y
									+ i - 15);
					g.drawText(c.getPoints() + "", x + (10 * width / 15) + 15,
							y + i - 15);
				}
			}
		}
		if (i % 2 == 0)
		{
			col = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
		else
		{
			col = display.getSystemColor(SWT.COLOR_WHITE);
		}
		g.setBackground(col);
		g.drawRectangle(x, y + i - 15, x + width, height);
		g.fillRectangle(x, y + i - 15, x + width, height);

		g.setForeground(device.getSystemColor(SWT.COLOR_BLACK));
		g.setLineWidth(2);
		g.drawLine(x + (7 * width / 15 + 3), y, x + (7 * width / 15 + 3), y
				+ height + 3);
		g.drawLine(x + (10 * width / 15 + 3), y, x + (10 * width / 15 + 3), y
				+ height);
		g.drawLine(x + (12 * width / 15 + 5), y, x + (12 * width / 15 + 5), y
				+ height);

		g.drawLine(x + 1, y + 1, x + width - 1, y + 1);
		g.drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
		g.drawLine(x + 1, y + 1, x + 1, y + height - 1);
		g.drawLine(x + width - 1, y + 1, x + width - 1, y + height - 1);
		
		
		this.updateSize();
	}
	
	/**
	 * This method is a helper method for paintControl. It creates a new {@link ArrayList}
	 * of {@link Object}s and stores it as a global variable. Next, it fills this list with
	 * each {@link Mod} on the list of selected {@link Mod}s, followed by all {@link Challenge}s
	 * associated with that {@link Mod}.
	 */
	private void updatePrintList()
	{
		printThis = new ArrayList<Object>();
		ArrayList<Mod> mods = ModsBookkeeper.getInstance().getSelectedMods();
		for (Mod m : mods)
		{
			printThis.add(m);
			for (Challenge c : m.getChallenges())
			{
				printThis.add(c);
			}
		}
	}

	@Override
	/**
	 * This method is called by the {@link Observable} {@link ModsBookkeeper}, whenever
	 * the collection of selected {@link Mod}s changes. It checks whether the cause of 
	 * the call is a {@link Mod} selection or a {@link Mod} deletion and calls the 
	 * updateSize and shrink functions respectively. After that, if the canvas has already
	 * been instantiated it calls redraw, to show the changes in the GUI.
	 */
	public void update(Observable o, Object arg)
	{
		String s = (String) arg;
		if (s.equals("SELECTED"))
		{
			updatePrintList();
			this.updateSize();
		}
		else if (s.equals("DESELECTED"))
		{
			updatePrintList();
			this.shrink();
		}
		
		if(canvas != null){
			canvas.redraw();
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{}

	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	/**
	 * This method makes sure the focus is on the {@link Canvas}. This is
	 * necessary because it allows the user to scroll using the scroll wheel.
	 */
	public void mouseUp(MouseEvent arg0)
	{
		canvas.setFocus();
	}
}
