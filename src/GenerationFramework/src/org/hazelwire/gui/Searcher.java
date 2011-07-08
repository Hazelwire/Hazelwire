package org.hazelwire.gui;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

/**
 * This class controls the search {@link Text} on the 'Module Selection'
 * tab. It is responsible for extracting the search phrase the user wants
 * to use; for updating the {@link Tree} and for setting the {@link String} 
 * 'search...' in the {@link Text} when it does not have focus. Therefore, it
 * is a subclass of both {@link KeyListener} and {@link FocusListener}.
 */
public class Searcher implements KeyListener, FocusListener
{

	private GUIBuilder gUIBuilder;

	/**
	 * Constructs an instance of Searcher with the specified argument.
	 * @param gUIBuilder the GUIBuilder is necessary to call the methods
	 * that update and redraw the tree given a certain search phrase.
	 */
	public Searcher(GUIBuilder gUIBuilder)
	{
		this.gUIBuilder = gUIBuilder;
	}

	@Override
	/**
	 * This method is called whenever the user hits a key. It checks what
	 * key he hit and if it is a return ('\n'), the method will extract the
	 * contents of the {@link Text}. It then calls the updateTagTree method
	 * in {@link GUIBuilder}.
	 */
	public void keyPressed(KeyEvent k)
	{
		char c = k.character;
		if (c == '\r')
		{
			Text text = ((Text) k.getSource());
			String s = text.getText();
			gUIBuilder.updateTagtree(s);
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{}
	
	@Override
	/**
	 * This method is called whenever the user focuses on the {@link Text}. It
	 * sets the text to the empty {@link String}.
	 */
	public void focusGained(FocusEvent f) {
		if(f.getSource() instanceof Text){
			Text t = (Text)f.getSource();
			if(t.getText().equals("search...")){
				t.setText("");
			}
		}
		
	}

	/**
	 * This method is called whenever the user removes their focus from the 
	 * {@link Text}. It sets the text to "search..."
	 */
	public void focusLost(FocusEvent f) {
		if(f.getSource() instanceof Text){
			Text t = (Text)f.getSource();
			if(t.getText().equals("")){
				t.setText("search...");
			}
		}
		
	}
}
