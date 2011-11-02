/*******************************************************************************
 * Copyright (c) 2011 The Hazelwire Team.
 *     
 * This file is part of Hazelwire.
 * 
 * Hazelwire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Hazelwire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
