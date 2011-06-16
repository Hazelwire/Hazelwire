package org.hazelwire.gui;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class ConfigListener implements MouseListener, FocusListener {
	
	private GUIBuilder gUIBuilder;

	public ConfigListener(GUIBuilder gUIBuilder){
		this.gUIBuilder = gUIBuilder;
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent f) {
		if(f.getSource() instanceof Text){
			Text t = (Text)f.getSource();
			ModsBookkeeper.getInstance().getConfigFile().setActual((String)t.getData(), t.getText());
		}

	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDown(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseUp(MouseEvent m) {
		if(m.getSource() instanceof Button){
			Button b = (Button)m.getSource();
			if(b.getData().equals("Defaults")){
				ModsBookkeeper.getInstance().getConfigFile().resetActuals();
				for(String key : ModsBookkeeper.getInstance().getConfigFile().getDefaults().keySet()){
					gUIBuilder.updateConfig(key);
				}
			}
			else{
				ModsBookkeeper.getInstance().getConfigFile().resetActual((String)b.getData());
				//FIXME: how to update text thing?
				gUIBuilder.updateConfig((String)b.getData());
			}
			
		}
	}

}
