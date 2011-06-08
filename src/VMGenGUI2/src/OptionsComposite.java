import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Point;


public class OptionsComposite extends Composite{
	
//	public static void main(String[] args){
//		Display display = new Display();
//		Shell shell = new Shell(display/*, SWT.NO_TRIM | SWT.ON_TOP*/);
//		new OptionsComposite(shell, SWT.NONE, "Testoptie", ModsBookkeeper.getInstance().getMods().get(0) new);
//		shell.pack();
//		shell.open ();
//		while (!shell.isDisposed()) {
//		 if (!display.readAndDispatch()) 
//		  display.sleep();
//		}
//		display.dispose();
//	}
	
	private Composite parent;
	private String option;
	private Text txtWaardeRightNow;
	private OptionsCompListener ocl;
	private Mod mod;
	
	public OptionsComposite(Composite parent, int style, String option, Mod mod, GUIBuilder gUIBuilder) {
		super(parent, SWT.BORDER);
		this.parent = parent;
		this.option = option;
		this.mod = mod;
		this.ocl = new OptionsCompListener(this, gUIBuilder);
		
		setLayout(new GridLayout(3, true));
		//this.setSize(new Point(332, 31));
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		this.setLayoutData(gd);
		
		Label lblOption = new Label(this, SWT.NONE);
		lblOption.setAlignment(SWT.CENTER);
		lblOption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblOption.setText(option);
		
		txtWaardeRightNow = new Text(this, SWT.BORDER);
		txtWaardeRightNow.setText(mod.getOptions().get(option)); // motte wel de waarde uit de Module trekken? True Better? Test it;) poep :(Wacht
		txtWaardeRightNow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtWaardeRightNow.addFocusListener(ocl);
		
		Button btnDefault = new Button(this, SWT.NONE);
		btnDefault.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnDefault.setText("Default");
		btnDefault.addMouseListener(ocl);
		super.setLayout(new GridLayout(3,true));
	}	
	
	public Mod getMod(){
		return this.mod;
	}
	
	public String getOption(){
		return this.option;
	}
	
	public Text getText(){
		return this.txtWaardeRightNow;
	}
}
