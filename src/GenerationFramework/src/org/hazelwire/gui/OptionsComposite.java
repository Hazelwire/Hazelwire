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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.hazelwire.modules.Option;

/**
 * This class draws the three important {@link Widget}s for an 
 * option and keeps them together and accessible. The three GUI
 * components of an OptionsComposite are a {@link Label} showing 
 * what option this {@link Composite} represents; a {@link Text}
 * showing the current value of this option and a {@link Button}
 * for resetting the option to its default. Finally, there is a
 * link to the {@link Option}: the back end representation of an
 * option. 
 * This class is a subclass of {@link Composite}
 */
public class OptionsComposite extends Composite
{
	private Option option;
	private Text txtWaardeRightNow;
	private OptionsCompListener ocl;
	private Mod mod;

	/**
	 * Constructs an instance of OptionsComposite with the given parameters, drawing
	 * the three elements of an OptionsComposite: {@link Label}, {@link Text} and
	 * {@link Button}.
	 * @param parent the parent {@link Composite} is necessary for the call to the 
	 * constructor of superclass {@link Composite}.
	 * @param style the style ints are necessary for the call to the constructor
	 * of the superclass {@link Composite}.
	 * @param option the {@link Option} this OptionsComposite is representing.
	 * @param mod the {@link Mod} that the option is associated with.
	 */
	public OptionsComposite(Composite parent, int style, Option option,
			Mod mod)
	{
		super(parent, SWT.BORDER);
		this.option = option;
		this.mod = mod;
		this.ocl = new OptionsCompListener(this);

		setLayout(new GridLayout(3, true));
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		this.setLayoutData(gd);

		Text lblOption = new Text(this, SWT.WRAP);
		GridData gd_lblOption = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_lblOption.widthHint = 75;
		lblOption.setLayoutData(gd_lblOption);
		lblOption.setSize(75, SWT.DEFAULT);
		lblOption.setText(option.getName());
		lblOption.setEditable(false);

		txtWaardeRightNow = new Text(this, SWT.BORDER);
		txtWaardeRightNow.setText(option.getValue()); 
		txtWaardeRightNow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		txtWaardeRightNow.addFocusListener(ocl);

		Button btnDefault = new Button(this, SWT.NONE);
		btnDefault.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		btnDefault.setText("Default");
		btnDefault.addMouseListener(ocl);
		super.setLayout(new GridLayout(3, true));
	}

	/**
	 * @return the {@link Mod} this OptionsComposite is associated with.
	 */
	public Mod getMod()
	{
		return this.mod;
	}
	
	/**
	 * @return the {@link Option} this OptionsComposite is associated with.
	 */
	public Option getOption()
	{
		return this.option;
	}

	/**
	 * @return the {@link Text} element in this OptionsComposite.
	 */
	public Text getText()
	{
		return this.txtWaardeRightNow;
	}
}
