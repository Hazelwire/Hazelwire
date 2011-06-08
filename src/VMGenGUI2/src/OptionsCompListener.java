import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;


public class OptionsCompListener implements MouseListener, FocusListener {
	
	private OptionsComposite oc;
	private GUIBuilder gUIBuilder;
	
	public OptionsCompListener(OptionsComposite oc, GUIBuilder gUIBuilder){
		this.oc = oc;
		this.gUIBuilder = gUIBuilder;
	}
	
	@Override
	public void mouseDoubleClick(MouseEvent arg0) {}

	@Override
	public void mouseDown(MouseEvent arg0) {}

	@Override
	public void mouseUp(MouseEvent m) {
		if(m.getSource() instanceof Button){
			oc.getText().setText(oc.getMod().getDefaultOption(oc.getOption()));
			oc.getMod().getOptions().put(oc.getOption(), oc.getMod().getDefaultOption(oc.getOption()));
		}
	}

	@Override
	public void focusGained(FocusEvent arg0) {}

	@Override
	public void focusLost(FocusEvent f) {
		if(f.getSource() instanceof Text){
			oc.getMod().getOptions().put(oc.getOption(), ((Text)f.getSource()).getText());
			System.out.println(""+oc.getMod().getOptions().toString());
		}
	}

}
