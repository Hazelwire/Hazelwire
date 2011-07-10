package org.hazelwire.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.Configuration;
import org.hazelwire.virtualmachine.ProgressInterface;

public class DownloadBarLink implements ProgressInterface
{
	@Override
	public void setProgress(final int progress)
	{
		Display display = GUIBuilder.getInstance().getDisplay();
		
		display.asyncExec(new Runnable()
			{
				public void run()
				{
					ProgressBar pb = GUIBuilder.getInstance().getProgressBar();
					
					if(progress >= 0 && progress <= 100)
					{
						pb.setSelection(progress);
					}
				}
			}
		);
	}
	
	public void setFilePath(final String filePath)
	{
		Display display = GUIBuilder.getInstance().getDisplay();
		
		display.asyncExec(new Runnable()
			{
				public void run()
				{
					Text text_filePath = GUIBuilder.getInstance().getTextFilePath();
					text_filePath.setText(filePath);
				}
			}
		);
		
		try
		{
			Configuration.getInstance().setVMPath(filePath);
			Configuration.getInstance().saveUserProperties();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
