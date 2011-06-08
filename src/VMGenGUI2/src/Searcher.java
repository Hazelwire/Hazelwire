import java.util.ArrayList;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;


public class Searcher implements KeyListener {
	
	
	private ArrayList<Mod> mods;
	private GUIBuilder gUIBuilder;
	
	public Searcher(GUIBuilder gUIBuilder){
		this.gUIBuilder = gUIBuilder;
	}
	
	@Override
	public void keyPressed(KeyEvent k) {
		char c = k.character;
		if(c == '\r'){
			Text text =((Text)k.getSource());
			String s = text.getText();
			gUIBuilder.updateTagtree(s);
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
