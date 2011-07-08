package org.hazelwire.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;

/**
 * A class for graphically displaying error messages. It creates pop ups,
 * and thus is a subclass of {@link Dialog}. All ErrorDialogs are modal,
 * which means that there can be no interaction with the system, while an
 * error message is being displayed.
 */
public class ErrorDialog extends Dialog {

	private String msg = "ErrorMessage goes here baakhbek eau ea kjenkje nejjenf jewnkafeuuejn ekunefjwo t43jkkjef 4u4 jnd ejlknfe n";
	private Shell dialogShell;
	
	/**
	 * Constructs an instance of ErrorDialog, a modal {@link Dialog} that
	 * shows the error message.
	 * @param parent this {@link Shell} is the {@link Object} the ErrorDialog
	 * is linked to.
	 * @param msg a {@link String} message describing what went wrong.
	 */
	public ErrorDialog(Shell parent, String msg) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.msg = msg;
	}

	/**
	 * This method creates a new {@link Shell}, calls the method createContents 
	 * to fill it with useful {@link Widget}s and makes sure it displays until
	 * the user has acknowledged the error.
	 */
	public void open() {
		dialogShell = new Shell(getParent(), getStyle());
		dialogShell.setMinimumSize(new Point(200, 150));
		dialogShell.setText("ERROR");
		createContents(dialogShell);
		Display display = getParent().getDisplay();
		while (!dialogShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
  	}

	/**
	 * This method creates and lays out the {@link Widget}s in this ErrorDialog:
	 * a {@link Label} containing the error message and an 'Ok' {@link Button} for
	 * the user to acknowledge the error and continue working.
	 * @param shell this {@link Shell} will be closed whenever the user presses the
	 * 'Ok' button.
	 * @requires shell != null
	 */
	private void createContents(final Shell shell) {
		dialogShell.pack();
	    dialogShell.setLayout(new GridLayout(1, false));
	    
	    Label lblErrormessageGoesHere = new Label(dialogShell, SWT.WRAP | SWT.CENTER);
	    lblErrormessageGoesHere.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
	    lblErrormessageGoesHere.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	    lblErrormessageGoesHere.setText(msg);
	    
	    Button btnOk = new Button(dialogShell, SWT.NONE);
	    GridData gd_btnOk = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
	    gd_btnOk.widthHint = 59;
	    btnOk.setLayoutData(gd_btnOk);
	    btnOk.setText("Ok");
	    btnOk.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
              shell.close();
            }
          });
	    dialogShell.open();
	}
  
	/**
	 * This method sets the message that will be displayed.
	 * @param msg {@link String} error message
	 */
	public void setMessage(String msg){
		this.msg = msg;
	}
}

