package org.hazelwire.gui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.Configuration;


/*
 * Deze klasse zorgt ervoor dat er een VM geselecteerd wordt (vanaf de harde schijf). Nu zet
 * hij alleen nog maar het pad in het textveld.
 */
public class BrowseMouseListener implements MouseListener {
	
	private Text text;
	
	public BrowseMouseListener(Text text){
		this.text = text;
	}

	@Override
	public void mouseUp(MouseEvent m) {
		FileDialog fd = null;
		if(m.getSource() instanceof Button ){
			fd = new FileDialog(((Button)m.getSource()).getShell(), SWT.OPEN);
		}
		else if(m.getSource() instanceof Text){
			fd = new FileDialog(((Text)m.getSource()).getShell(), SWT.OPEN);
		}
        fd.setText("Browse");
        /*
         * De extensie van het bestand die is toegestaan.
         */
        String[] filterExt = { "*.ova", "*.ovf" };
        fd.setFilterExtensions(filterExt);
        /*
         * Dit (String selected) is het (absolute) pad naar het geselecteerde bestand.
         */
        String selected = fd.open();
        if(selected!=null){
        	text.setText(selected);
        	try
			{
				Configuration.getInstance().setVMPath(selected);
				Configuration.getInstance().saveUserProperties();
			}
			catch (IOException e)
			{
				// TODO proper visual errors
				e.printStackTrace();
			}
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

}
