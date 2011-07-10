package org.hazelwire.gui;

import java.io.IOException;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
 * This class is responsible for taking the user input with regard
 * to the configuration settings and storing them in the system. In 
 * order to fulfill this task, it implements {@link MouseListener}
 * and {@link FocusListener}.
 */
public class VMNameListener implements MouseListener, FocusListener {

	
	@Override
	public void focusGained(FocusEvent arg0) {}

	@Override
	/**
	 * This method is called whenever the user stops editing (focussing on)
	 * a certain {@link Widget}, in our case a {@link Text}. When it is called,
	 * the {@link String} inside the {@link Text} will be saved to the correct
	 * configuration option in the {@link ConfigFile}.
	 */
	public void focusLost(FocusEvent f) {
		if(f.getSource() instanceof Text){
			Text t = (Text)f.getSource();
			try
			{
				ModsBookkeeper.getInstance().getConfigFile().setActual("vmName", t.getText());
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {}

	@Override
	public void mouseDown(MouseEvent arg0) {}

	@Override
	public void mouseUp(MouseEvent m) {}

}
