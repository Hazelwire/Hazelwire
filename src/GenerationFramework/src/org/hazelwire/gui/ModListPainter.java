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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;

/**
 * This class is responsible for painting the list of selected {@link Mod}s on 
 * the 'Module Selection' tab. It can also be manipulated by the user, who can 
 * manually (de)select and delete {@link Mod}s from this list. Finally it must
 * also pay attention to {@link Mod} selections that occur in the {@link Tag} 
 * {@link Tree} and {@link Mod} adaptations that occur in the 'Settings' tab. Thus,
 * this class is a subclass of {@link PaintListener}, of {@link MouseListener} and
 * of {@link Observer}.
 */
public class ModListPainter implements PaintListener, MouseListener, Observer
{

	private GUIBuilder gUIBuilder;
	private Canvas canvas;

	/**
	 * Creates an instance of ModListPainter and adds itself as an {@link Observer}
	 * to the {@link ModsBookkeeper} instance. It also saves a link to its parent.
	 * @param parent the {@link GUIBuilder} is necessary to be able to update the 
	 * {@link Combo} when a user selects a {@link Mod}.
	 */
	public ModListPainter(GUIBuilder parent)
	{
		this.gUIBuilder = parent;
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
		int modsSize = (1 + ModsBookkeeper.getInstance().getSelectedMods()
				.size()) * 15;
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
	 * {@link Canvas} is scrollable. Now when the user deletes a {@link Mod}, there will be an
	 * empty line, whilst the {@link Canvas} is still scrollable. This is undesired, so the 
	 * empty line is removed, using this method.
	 * @requires canvas != null
	 */
	public void shrink()
	{
		if (canvas == null)
		{
			return;
		}
		int modsSize = (1 + ModsBookkeeper.getInstance().getSelectedMods()
				.size()) * 15;
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
	 * the names and values of the selected {@link Mod}s, using a highlight for the
	 * single mod that has been selected. Finally, it calls updateSize, to ensure 
	 * a perfect fit without excess empty lines.
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
		ArrayList<Mod> mods = ModsBookkeeper.getInstance().getSelectedMods();

		int i;
		for (i = 15; i <= height; i = i + 15)
		{
			if (i == 15)
			{
				col = display.getSystemColor(SWT.COLOR_BLACK);
			}
			else if ((i / 15) - 1 == selected)
			{
				col = new Color(device, 72, 118, 255);
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
				g.drawText("Points", x + (3 * width / 4) + 15, y + i - 15);
			}
			else if ((i / 15) - 2 < mods.size() && i > 15)
			{
				g.setBackground(col);
				g.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				g
						.drawText(mods.get((i / 15) - 2).getName(), x + 20, y
								+ i - 15);
				g.drawText("" + mods.get((i / 15) - 2).getPoints(), x
						+ (3 * width / 4) + 20, y + i - 15);
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
		g.drawLine(x + (3 * width / 4), y, x + (3 * width / 4), y + height);

		g.drawLine(x + 1, y + 1, x + width - 1, y + 1);
		g.drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
		g.drawLine(x + 1, y + 1, x + 1, y + height - 1);
		g.drawLine(x + width - 1, y + 1, x + width - 1, y + height - 1);
		
		this.updateSize();
	}
	


	@Override
	/**
	 * This method is called whenever the user double clicks on the {@link Canvas}. 
	 * It uses the coordinates of the {@link MouseEvent} to compute which {@link Mod}
	 * the user clicked. This mod is then removed from the list of selected
	 * {@link Mod}s as well as deleted from the {@link Canvas} by calling redraw.
	 * If the user double clicked on the {@link Canvas}, but did not select any
	 * {@link Mod}, nothing happens.
	 */
	public void mouseDoubleClick(MouseEvent m)
	{
		Canvas canvas = ((Canvas) m.getSource());
		int y = m.y;

		int i = (y / 15);
		if (i > 0 && i <= ModsBookkeeper.getInstance().getSelectedMods().size())
		{
			String modName = ModsBookkeeper.getInstance().getSelectedMods()
					.get(i - 1).getName();
			if (ModsBookkeeper.getInstance().getSelected() == i)
			{
				ModsBookkeeper.getInstance().setSelected(-1);
			}
			ModsBookkeeper.getInstance().deselectModule(modName);
			canvas.redraw();
		}
	}


	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	/**
	 * This method is called whenever the user clicks on the {@link Canvas}. It 
	 * uses the {@link MouseEvent}'s coordinates to compute the {@link Mod} on 
	 * which the user clicked. If this {@link Mod} is not the selected {@link Mod},
	 * it will be selected. If it is the selected {@link Mod}, it will be 
	 * deselected. Should the user not select any {@link Mod}, the currently selected
	 * {@link Mod} will be deselected and the {@link Canvas} will be redrawn.
	 */
	public void mouseUp(MouseEvent m)
	{
		Canvas canvas = ((Canvas) m.getSource());
		canvas.setFocus();
		int y = m.y;

		int i = (y / 15);
		if (i <= ModsBookkeeper.getInstance().getSelectedMods().size())
		{
			if (i == ModsBookkeeper.getInstance().getSelected())
			{
				ModsBookkeeper.getInstance().setSelected(-1);
				gUIBuilder.updateCombo(null);
			}
			else if (i == 0)
			{
				ModsBookkeeper.getInstance().setSelected(-1);
				gUIBuilder.updateCombo(null);
			}
			else
			{
				ModsBookkeeper.getInstance().setSelected(i);
				gUIBuilder.updateCombo(ModsBookkeeper.getInstance()
						.getSelectedMod());
			}
		}
		else
		{
			ModsBookkeeper.getInstance().setSelected(-1);
			gUIBuilder.updateCombo(null);
		}
		canvas.redraw();
	}

	@Override
	/**
	 * This method is called by the {@link Observable} {@link ModsBookkeeper}, whenever
	 * the collection of selected {@link Mod} or the selected {@link Mod} itself changes.
	 * It checks whether the cause of the call is a {@link Mod} selection or a {@link Mod}
	 * deletion and calls the updateSize and shrink functions respectively. After that, 
	 * it calls redraw, to show the changes in the GUI.
	 */
	public void update(Observable o, Object arg)
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
}
