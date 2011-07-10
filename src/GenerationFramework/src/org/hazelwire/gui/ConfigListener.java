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
public class ConfigListener implements MouseListener, FocusListener {
	
	private GUIBuilder gUIBuilder;

	/**
	 * Creates an instance of ConfigListener.
	 * @param gUIBuilder the {@link GUIBuilder} is necessary for obtaining
	 * the data and updating the GUI to show the current values of 
	 * all configuration options.
	 */
	public ConfigListener(GUIBuilder gUIBuilder){
		this.gUIBuilder = gUIBuilder;
	}
	
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
				ModsBookkeeper.getInstance().getConfigFile().setActual((String)t.getData(), t.getText());
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
	/**
	 * This method is called whenever the user clicks one of the 'default' {@link Button}s 
	 * or clicks the large 'defaults' {@link Button}. When it is called, the default value
	 * of the associated configuration option(s) will be restored. The GUI will be updated
	 * accordingly.
	 */
	public void mouseUp(MouseEvent m) {
		if(m.getSource() instanceof Button){
			Button b = (Button)m.getSource();
			if(b.getData().equals("Defaults")){
				try
				{
					ModsBookkeeper.getInstance().getConfigFile().resetActuals();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				for(String key : ModsBookkeeper.getInstance().getConfigFile().getDefaults().keySet()){
					gUIBuilder.updateConfig(key);
				}
			}
			else{
				try
				{
					ModsBookkeeper.getInstance().getConfigFile().resetActual((String)b.getData());
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				gUIBuilder.updateConfig((String)b.getData());
			}
			
		}
	}

}
