package org.hazelwire.gui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This class is responsible for restoring the default values
 * for all the options of a {@link Mod}. Because restoring 
 * the values is done by clicking a {@link Button}, this class
 * is a subclass of {@link MouseListener}.
 */
public class DefaultMouseListener implements MouseListener
{
	private Composite allOptions;

	/**
	 * Creates an instance of DefaultMouseListener.
	 * @param allOptions this {@link Composite} is necessary in order
	 * to update the GUI to show the newly reset values and in order
	 * to find all the values that need to be reset in the first place.
	 */
	public DefaultMouseListener(Composite allOptions)
	{
		this.allOptions = allOptions;
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{}

	@Override
	/**
	 * When the 'defaults' {@link Button} is clicked and this method
	 * is called, it finds all options that the {@link Mod} has and
	 * resets their values to their default values, after which it 
	 * updates the GUI.
	 */
	public void mouseDown(MouseEvent m)
	{
		Control[] kids = allOptions.getChildren();
		for (Control c : kids)
		{
			if (c instanceof OptionsComposite)
			{
				OptionsComposite oc = (OptionsComposite) c;
				
				//reset the value to the default value
				oc.getOption().setValue(oc.getOption().getDefaultValue());
				oc.getMod().editOption(oc.getOption());
				
				//set the value again 
				oc.getText().setText(oc.getOption().getValue());
			}
		}

	}

	@Override
	public void mouseUp(MouseEvent arg0)
	{}

}
