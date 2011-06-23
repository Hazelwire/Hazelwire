package org.hazelwire.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;

public class ErrorDialog extends Dialog {

	private String msg = "ErrorMessage goes here baakhbek eau ea kjenkje nejjenf jewnkafeuuejn ekunefjwo t43jkkjef 4u4 jnd ejlknfe n";
	private Shell dialogShell;
	
	public ErrorDialog(Shell parent, String msg) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		// TODO Auto-generated constructor stub
		this.msg = msg;
	}
	
	/**
	   * Opens the dialog and returns the input
	   * 
	   * @return String
	   */
	  public void open() {
	    // Create the dialog window
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
	    // Return the entered value, or null
	  }

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
	  
	  public void setMessage(String msg){
		  this.msg = msg;
	  }
}

