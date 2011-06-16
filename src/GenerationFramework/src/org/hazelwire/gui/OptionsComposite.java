package org.hazelwire.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.hazelwire.modules.Option;

public class OptionsComposite extends Composite
{

	// public static void main(String[] args){
	// Display display = new Display();
	// Shell shell = new Shell(display/*, SWT.NO_TRIM | SWT.ON_TOP*/);
	// new OptionsComposite(shell, SWT.NONE, "Testoptie",
	// ModsBookkeeper.getInstance().getMods().get(0) new);
	// shell.pack();
	// shell.open ();
	// while (!shell.isDisposed()) {
	// if (!display.readAndDispatch())
	// display.sleep();
	// }
	// display.dispose();
	// }

	private Composite parent;
	private Option option;
	private Text txtWaardeRightNow;
	private OptionsCompListener ocl;
	private Mod mod;

	public OptionsComposite(Composite parent, int style, Option option,
			Mod mod, GUIBuilder gUIBuilder)
	{
		super(parent, SWT.BORDER);
		this.parent = parent;
		this.option = option;
		this.mod = mod;
		this.ocl = new OptionsCompListener(this, gUIBuilder);

		setLayout(new GridLayout(3, true));
		// this.setSize(new Point(332, 31));
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		this.setLayoutData(gd);

		Text lblOption = new Text(this, SWT.WRAP);
		//lblOption.setAlignment(SWT.CENTER);
		GridData gd_lblOption = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_lblOption.widthHint = 75;
		lblOption.setLayoutData(gd_lblOption);
		lblOption.setSize(75, SWT.DEFAULT);
		lblOption.setText(option.getName());
		lblOption.setEditable(false);

		txtWaardeRightNow = new Text(this, SWT.BORDER);
		txtWaardeRightNow.setText(option.getValue()); // motte wel de
																	// waarde
																	// uit de
																	// Module
																	// trekken?
																	// True
																	// Better?
																	// Test it;)
																	// poep
																	// :(Wacht
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

	public Mod getMod()
	{
		return this.mod;
	}

	public Option getOption()
	{
		return this.option;
	}

	public Text getText()
	{
		return this.txtWaardeRightNow;
	}
}
