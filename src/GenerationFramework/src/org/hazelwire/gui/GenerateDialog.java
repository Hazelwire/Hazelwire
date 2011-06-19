package org.hazelwire.gui;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.Point;
import org.hazelwire.main.Generator;

/**
 * This class demonstrates how to create your own dialog classes. It allows users
 * to input a String
 */
public class GenerateDialog extends Dialog {
  private String message;
  private String input;
  private Shell shlGeneratingVm;
  private ProgressBar progressBar;
  private Button controlButton;
  private Label lblProgress;

  /**
   * InputDialog constructor
   * 
   * @param parent the parent
   */
  public GenerateDialog(Shell parent) {
    // Pass the default styles here
    this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
  }

  /**
   * InputDialog constructor
   * 
   * @param parent the parent
   * @param style the style
   */
  public GenerateDialog(Shell parent, int style) {
    // Let users override the default styles
    super(parent, style);
  }

  /**
   * Gets the message
   * 
   * @return String
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message
   * 
   * @param message the new message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the input
   * 
   * @return String
   */
  public String getInput() {
    return input;
  }

  /**
   * Sets the input
   * 
   * @param input the new input
   */
  public void setInput(String input) {
    this.input = input;
  }
  
  public void setText(String text)
  {
	  lblProgress.setText(text);
  }

  /**
   * Opens the dialog and returns the input
   * 
   * @return String
   */
  public String open() {
    // Create the dialog window
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
    // Return the entered value, or null
    return input;
  }

  /**
   * Creates the dialog's contents
   * 
   * @param shell the dialog window
   */
  private void createContents(final Shell shell) {
    shell.setLayout(new GridLayout(1, true));
    
    Composite composite = new Composite(shlGeneratingVm, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
    composite.setLayout(new GridLayout(2, true));
    
        // Show the message
        lblProgress = new Label(composite, SWT.NONE);
        lblProgress.setAlignment(SWT.CENTER);
        lblProgress.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
        lblProgress.setSize(400, 13);
        lblProgress.setText("Progress....");
                
                progressBar = new ProgressBar(composite, SWT.NONE);
                progressBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
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
										if(controlButton.getText().equals("Cancel")) Generator.getInstance().shutDown(true,false);
									}
									catch (Exception e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
                                    shell.close();
                                  }
                                });

  }
  
  public void updateProgressBar(int progress){
	 if(progress >=0 && progress <=100){
		 progressBar.setSelection(progress);
	 }
	 
	 if(progress == 100)
	 {
		 controlButton.setText("Ok");
	 }
  }
}