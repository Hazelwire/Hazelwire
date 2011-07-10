package org.hazelwire.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.hazelwire.main.Generator;

/**
 * This class is responsible for showing information regarding the 
 * generation of the Virtual Machine and the XML file associated with
 * it. This information consist of a {@link ProgressBar} and a {@link Label}
 * displaying messages. The information is shown in a modal {@link Dialog}, 
 * because it is undesirable to have the user meddle with the settings, whilst
 * the Virtual Machine is being generated. Thus, this class is a subclass of
 * {@link Dialog}.
 */
public class GenerateDialog extends Dialog {
	private String input;
	private Shell shlGeneratingVm;
	private ProgressBar progressBar;
	private Button controlButton;
	private Label lblProgress;

	/**
	 * Constructs an instance of GenerateDialog.
	 * @param parent parent this {@link Shell} is the {@link Object} the 
	 * GenerateDialog is linked to.
	 */
	public GenerateDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * Constructs an instance of GenerateDialog.
	 * @param parent parent this {@link Shell} is the {@link Object} the 
	 * GenerateDialog is linked to.
	 * @param style the style this GenerateDialog should have
	 */
	public GenerateDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * This method creates a new {@link Shell}, calls the method createContents 
	 * to fill it with useful {@link Widget}s and makes sure it displays until
	 * the user has acknowledged a possible error or until the generation 
	 * process is finished.
	 * @return 
	 */
	public String open() {
		shlGeneratingVm = new Shell(getParent(), getStyle());
		shlGeneratingVm.setMinimumSize(new Point(200, 150));
		shlGeneratingVm.setText("Generating VM");
		createContents(shlGeneratingVm);
		shlGeneratingVm.pack();
		shlGeneratingVm.open();
		Display display = getParent().getDisplay();
		while (!shlGeneratingVm.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return input;
	}

	/**
	 * This method creates and lays out the {@link Widget}s in this ErrorDialog:
	 * a {@link Label} containing the progress or error messages, a 'Cancel' {@link Button} 
	 * for the user to stop the VM generation and continue working and a {@link ProgressBar}
	 * to graphically display the progress that has been made.
	 * @param shell this {@link Shell} will be closed whenever the user presses the
	 * 'Cancel' button.
	 * @requires shell != null
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(1, true));

		Composite composite = new Composite(shlGeneratingVm, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		composite.setLayout(new GridLayout(2, true));

		// Show the message
		lblProgress = new Label(composite, SWT.WRAP);
		lblProgress.setAlignment(SWT.CENTER);
		GridData gd_lblProgress = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd_lblProgress.widthHint = 266;
		lblProgress.setLayoutData(gd_lblProgress);
		lblProgress.setSize(400, 13);
		lblProgress.setText("Progress.... Very very very very very very very very very very very very very very very very very long text");

		progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		controlButton = new Button(composite, SWT.PUSH);
		controlButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		controlButton.setSize(112, 23);
		controlButton.setText("Cancel");
		controlButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = null;
				try
				{
					if(controlButton.getText().equals("Cancel"))
					{
						Generator.getInstance().setKeepGenerating(false);
						controlButton.setText("Wait");
						controlButton.setEnabled(false);
						lblProgress.setText("Cleaning up");
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * This method updates the {@link ProgressBar}. If the progressbar is completely
	 * filled, the 'cancel' button will morph into an 'Ok' button.
	 * @param progress int value that the amount of progress must be set to.
	 * @ requires progress >= 0 && progress <= 100
	 */
	public void updateProgressBar(int progress){
		if(progress >=0 && progress <=100){
			progressBar.setSelection(progress);
		}

		if(progress == 100)
		{
			shlGeneratingVm.dispose();
		}
	}
	
	public void setProgressText(String text)
	{
		this.lblProgress.setText(text);
	}
}