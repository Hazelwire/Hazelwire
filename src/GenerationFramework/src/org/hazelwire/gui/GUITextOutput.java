package org.hazelwire.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.TextInterface;

public class GUITextOutput implements TextInterface
{
	Text output;
	Display display;
	
	public GUITextOutput(Display display, Text output)
	{
		this.output = output;
		this.display = display;
	}
	
	@Override
	public void print(String message)
	{
		final String printMessage = message;
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				output.append(printMessage);
			}
		});
	}

	@Override
	public void println(String message)
	{
		final String printMessage = message;
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				output.append(printMessage+"\n");
			}
		});
	}
	
	public void clear()
	{
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				output.setText("");
			}
		});
	}
}
