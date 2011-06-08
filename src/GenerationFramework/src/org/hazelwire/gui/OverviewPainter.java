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

public class OverviewPainter implements PaintListener, Observer, MouseListener
{

	private GUIBuilder gUIBuilder;
	private Canvas canvas;
	private boolean sameSize = false;
	private boolean tooLarge = false;
	private ArrayList<Object> printThis = new ArrayList<Object>();

	public OverviewPainter(GUIBuilder parent)
	{
		this.gUIBuilder = parent;
		ModsBookkeeper.getInstance().addObserver(this);
	}

	public void updateSize()
	{
		if (canvas == null)
			return;
		int curSize = canvas.getParent().getSize().y;
		int modsSize = (1 + printThis.size()) * 15;
		if (curSize > modsSize)
		{
			canvas.setBounds(0, 0, canvas.getSize().x, curSize);
			// canvas.setSize(canvas.computeSize(canvas.getSize().x, curSize));
			System.out.println("Stick to current size: " + curSize);
		}
		else
		{
			canvas.setBounds(0, 0, canvas.getSize().x, modsSize);
			// canvas.setSize(canvas.computeSize(canvas.getSize().x, modsSize));
			System.out.println("Increase size to :" + modsSize);
		}
		System.out.println("canvas size: " + canvas.getBounds());
	}

	public void shrink()
	{
		if (canvas == null)
		{
			return;
		}
		int modsSize = (1 + printThis.size()) * 15;
		if (ModsBookkeeper.getInstance().getSelectedMods().size() < 19)
		{
			canvas.setBounds(0, 0, 200, 300);
		}
		else
		{
			canvas.setBounds(0, 0, canvas.getSize().x, modsSize);
		}
		System.out.println("canvas size: " + canvas.getBounds());
	}

	public void fillHeight()
	{
		sameSize = canvas.getBounds().height == canvas.getParent().getBounds().height - 4;
		int modsHeight = (1 + ModsBookkeeper.getInstance().getSelectedMods()
				.size()) * 15;
		if (!sameSize && modsHeight < canvas.getParent().getSize().y)
		{
			canvas.setSize(canvas.getBounds().width, canvas.getParent()
					.getBounds().height - 4);
		}
	}

	@Override
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
				// col = new Color(device, 255, 0, 0);
				col = display.getSystemColor(SWT.COLOR_BLACK);
			}
			else if (i % 2 == 0)
			{
				// col = new Color(device, 133, 133, 133);
				col = display.getSystemColor(SWT.COLOR_DARK_GRAY);
			}
			else
			{
				// col = new Color(device, 255, 255, 255);
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
			col = display.getSystemColor(SWT.COLOR_DARK_GRAY);
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
	}

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

	public void update(Observable o, Object arg)
	{
		String s = (String) arg;
		if (s.equals("SELECTED"))
		{
			// this.fillHeight();
			updatePrintList();
			this.updateSize();
		}
		else if (s.equals("DESELECTED"))
		{
			updatePrintList();
			this.shrink();
		}
		else
		{
			canvas.redraw();
		}

	}

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
		canvas.setFocus();

	}

}
