package org.hazelwire.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.InterfaceOutput;

public class GUIOutput implements InterfaceOutput
{
	Text output;
	Display display;
	GenerateDialog progressDialog;
	
	public GUIOutput(Display display, Text output, GenerateDialog progressDialog)
	{
		this.output = output;
		this.display = display;
		this.progressDialog = progressDialog;
	}
	
	@Override
	public void print(final String message)
	{
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				output.append(message);
			}
		});
	}

	@Override
	public void println(final String message)
	{
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				output.append(message+"\n");
				progressDialog.setText(message);
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
				progressDialog.setText("");
			}
		});
	}
	
	public void setProgress(final int progress)
	{
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				progressDialog.updateProgressBar(progress);
			}
		});
	}
}
