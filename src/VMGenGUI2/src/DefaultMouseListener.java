import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class DefaultMouseListener implements MouseListener {
	
	private GUIBuilder gUIBuilder;
	private Composite allOptions;

	public DefaultMouseListener(GUIBuilder gUIBuilder, Composite allOptions){
		this.gUIBuilder = gUIBuilder;
		this.allOptions = allOptions;
	}
	
	@Override
	public void mouseDoubleClick(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDown(MouseEvent m) {
		Control[] kids = allOptions.getChildren();
		for(Control c : kids){
			if(c instanceof OptionsComposite){
				OptionsComposite oc = (OptionsComposite)c;
				oc.getMod().getOptions().put(oc.getOption(), oc.getMod().getDefaultOption(oc.getOption()));
				oc.getText().setText(oc.getMod().getOptions().get(oc.getOption()));
			}
		}

	}

	@Override
	public void mouseUp(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
