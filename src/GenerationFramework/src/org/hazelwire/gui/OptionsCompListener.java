package org.hazelwire.gui;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
 * This class is responsible for detecting adaptations to the
 * {@link OptionsComposite}s on the 'settings' tab. Information 
 * will be stored on loss of focus and the defaults may be reset
 * by a {@link Button} click. Therefore, this class is a subclass
 * of both {@link MouseListener} and {@link FocusListener}.
 */
public class OptionsCompListener implements MouseListener, FocusListener
{

	private OptionsComposite oc;

	/**
	 * Creates an instance of OptionsCompListener
	 * @param oc the {@link OptionsComposite} that may be changed
	 */
	public OptionsCompListener(OptionsComposite oc)
	{
		this.oc = oc;
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{}

	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	/**
	 * This method is called whenever the 'default' {@link Button} on
	 * the {@link OptionsComposite} is clicked. It resets the option's
	 * value to its default value and updates the GUI.
	 */
	public void mouseUp(MouseEvent m)
	{
		if (m.getSource() instanceof Button)
		{
			oc.getText().setText(oc.getOption().getDefaultValue());
			
			oc.getOption().setValue(oc.getOption().getDefaultValue());
			oc.getMod().editOption(oc.getOption());
		}
	}
	
	

	@Override
	public void focusGained(FocusEvent arg0)
	{}

	@Override
	/**
	 * This method is called whenever the user directs his focus away
	 * from the {@link Text} in the {@link OptionsComposite}. It saves
	 * the new value of the option.
	 */
	public void focusLost(FocusEvent f)
	{
		if (f.getSource() instanceof Text)
		{
			oc.getOption().setValue(((Text) f.getSource()).getText());
			oc.getMod().editOption(oc.getOption());
		}
	}
}
