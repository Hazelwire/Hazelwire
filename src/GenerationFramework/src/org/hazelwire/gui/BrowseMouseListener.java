package org.hazelwire.gui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.main.Configuration;

/**
 * This class, BrowseMouseListener is a subclass of {@link MouseListener}. It is used to allow
 * a user to select a Virtual Machine from the hard disk. An instance of BrowseMouseListener is 
 * attached to the 'Browse' button and the Text field in front of it.
 */
public class BrowseMouseListener implements MouseListener {
	
	private Text text;
	
	/**
	 * Constructor method that constructs an instance of this class
	 * @param text {@link Text} that is saved for a reason unknown to me
	 */
	public BrowseMouseListener(Text text){
		this.text = text;
	}

	@Override
	/**
	 * Overrides the mouseUp method in {@link MouseListener}. It opens a 
	 * new {@link FileDialog} which can be used to select a Virtual 
	 * Machine from the user's hard disk.
	 * If the user selects a VM, its path name will both be displayed in the 
	 * {@link Text} and it will be saved to the {@link Configuration}.
	 */
	public void mouseUp(MouseEvent m) {
		FileDialog fd = null;
		if(m.getSource() instanceof Button ){
			fd = new FileDialog(((Button)m.getSource()).getShell(), SWT.OPEN);
		}
		else if(m.getSource() instanceof Text){
			fd = new FileDialog(((Text)m.getSource()).getShell(), SWT.OPEN);
		}
        fd.setText("Browse");
        String[] filterExt = { "*.ova", "*.ovf" };
        fd.setFilterExtensions(filterExt);
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
	public void mouseDoubleClick(MouseEvent arg0) {}

	@Override
	public void mouseDown(MouseEvent arg0) {}

}
