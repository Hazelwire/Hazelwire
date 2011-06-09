package org.hazelwire.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;



public class MainTest {

	public static void main(String[] args) {

	    final Display display = new Display();

	    Shell shell = new Shell(display);

	    final ProgressBar bar =new ProgressBar(shell,SWT.NONE);

	    bar.setSize(200, 32);

	    shell.pack();

	    shell.open();

	    final int maximum = bar.getMaximum();

	    new Thread() {

	        public void run() {

	            for (int i = 0; i <= maximum; i++) {

	                try {

	                    Thread.sleep(100);

	                } catch (Throwable th) {

	                }

	                final int index = i;

	                display.asyncExec(new Runnable() {

	                    public void run() {

	                        bar.setSelection(index);
	                        System.out.println(Thread.currentThread().getName());
	                    }

	                });

	            }

	        }

	    }.start();

	    while (!shell.isDisposed()) {

	        if (!display.readAndDispatch()) display.sleep();

	    }

	    display.dispose();

	}

}