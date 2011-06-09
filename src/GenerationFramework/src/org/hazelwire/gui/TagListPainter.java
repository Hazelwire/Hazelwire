package org.hazelwire.gui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class TagListPainter implements PaintListener, Observer
{

	private Canvas canvas;
	private GUIBuilder gUIBuilder;
	private boolean sameSize = false;

	public TagListPainter(GUIBuilder gUIBuilder)
	{
		this.gUIBuilder = gUIBuilder;
		ModsBookkeeper.getInstance().addObserver(this);
	}

	public void updateSize()
	{
		if (canvas == null)
			return;
		int curSize = canvas.getParent().getSize().y;
		int modsSize = (1 + ModsBookkeeper.getInstance().getSelectedMods()
				.size()) * 15;
		if (curSize > modsSize)
		{
			canvas.setBounds(0, 0, canvas.getSize().x, curSize);
		}
		else
		{
			canvas.setBounds(0, 0, canvas.getSize().x, modsSize);
		}
	}

	public void shrink()
	{
		if (canvas == null)
		{
			return;
		}
		int modsSize = (1 + ModsBookkeeper.getInstance().getSelectedMods()
				.size()) * 15;
		if (ModsBookkeeper.getInstance().getSelectedMods().size() < 19)
		{
			canvas.setBounds(0, 0, 125, 150);
		}
		else
		{
			canvas.setBounds(0, 0, canvas.getSize().x, modsSize);
		}
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
		// fillHeight();
		// System.out.println(canvas.getBounds());
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
		// ArrayList<Mod> mods = ModsBookkeeper.getInstance().getSelectedMods();

		int i;
		for (i = 15; i <= height; i = i + 15)
		{
			if (i % 2 == 0)
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

			// if(i == 15){
			// Font oldFont = g.getFont();
			// Font boldFont = new Font(oldFont.getName(), Font.BOLD,
			// oldFont.getSize());
			// g.setFont(boldFont);
			// g.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
			// g.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
			// g.drawText("Tags", x + 20, y+i-15);
			// g.drawText("Points", x + (3*width/4)+15, y+i-15);
			// g.setFont(oldFont);
			// }
			if ((i / 15) - 1 < tags.size() && i >= 15)
			{
				g.setBackground(col);
				g.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				g.drawText(tags.get((i / 15) - 1).getName(), x + 20, y + i
						- 15);
				// g.drawText(""+mods.get((i/15)-2).getPoints(), x +
				// (3*width/4)+20, y+i-15);
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
		// g.drawLine(x+ (3*width/4), y, x+ (3*width/4), y+height);

		g.drawLine(x + 1, y + 1, x + width - 1, y + 1);
		g.drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
		g.drawLine(x + 1, y + 1, x + 1, y + height - 1);
		g.drawLine(x + width - 1, y + 1, x + width - 1, y + height - 1);
	}

	@Override
	public void update(Observable arg0, Object arg)
	{
		String s = (String) arg;
		if (s.equals("SELECTED"))
		{
			if (canvas != null)
			{
				// canvas.redraw();
				this.updateSize();
			}
		}
		else if (s.equals("DESELECTED"))
		{
			this.shrink();
		}

	}

}
