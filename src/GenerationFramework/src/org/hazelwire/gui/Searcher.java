/*******************************************************************************
 * Copyright (c) 2011 The Hazelwire Team.
 *     
 * This file is part of Hazelwire.
 * 
 * Hazelwire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Hazelwire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
