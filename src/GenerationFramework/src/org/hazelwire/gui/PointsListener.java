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

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * This class is responsible for changing the value of a certain {@link Challenge}. 
 * It is bound to several {@link Button}s to increment or decrement the value of a 
 * {@link Challenge} or to reset its value to the default and it is bound to a {@link Text} 
 * where a user can manually set the amount of points. For this reason, it is a subclass of
 * {@link MouseListener} and {@link KeyListener}. It is also a subclass of 
 * {@link SelectionListener}, because it needs to detect which {@link Challenge} is selected.
 */
public class PointsListener implements MouseListener, KeyListener,
		SelectionListener
{

	private Text points;
	private Combo challenges;
	private GUIBuilder gUIBuilder;

	/**
	 * Constructs an instance of PointsListener with the given arguments.
	 * @param points the {@link Text} containing the current amount of points. This
	 * {@link Text} may also be used to set a new value for the current {@link Challenge}.
	 * @param challenges {@link Combo} containing all {@link Challenge}s associated with
	 * the currently selected {@link Mod}.
	 * @param gUIBuilder the {@link GUIBuilder} is necessary to update the GUI after values
	 * have been changed.
	 */
	public PointsListener(Text points, Combo challenges, GUIBuilder gUIBuilder)
	{
		this.points = points;
		this.challenges = challenges;
		this.gUIBuilder = gUIBuilder;
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{}

	@Override
	public void mouseDown(MouseEvent arg0)
	{}

	@Override
	/**
	 * This method is called when one of the {@link Button}s is clicked. It checks
	 * whether the source is an up or down arrow or the default {@link Button}. If the down
	 * {@link Button} was pressed, the current value of the current {@link Challenge} is 
	 * decremented by one. If the up {@link Button} was pressed, this value is incremented
	 * by one. If it was the default {@link Button}, the value of the current {@link Challenge}
	 * is reset to its default value.
	 * Finally, the GUI is updated to show the new values.
	 */
	public void mouseUp(MouseEvent m)
	{
		Mod selected = ModsBookkeeper.getInstance().getSelectedMod();
		if (m.getSource() instanceof Button)
		{
			if (selected != null)
			{
				Challenge challenge = selected.getChallenge(Integer.valueOf(challenges
						.getText()));
				Button b = (Button) m.getSource();
				if (b.getData().equals("defaultB") && challenge != null)
				{
					points.setText("" + challenge.getDefaultPoints());
					challenge.setPoints(challenge.getDefaultPoints());
				}
				else if (b.getData().equals("upArrow") && challenge != null)
				{
					challenge.setPoints(challenge.getPoints() + 1);
					points.setText(challenge.getPoints() + "");
				}
				else if (b.getData().equals("downArrow") && challenge != null)
				{
					if (challenge.getPoints() > 0)
					{
						challenge.setPoints(challenge.getPoints() - 1);
						points.setText(challenge.getPoints() + "");
					}
				}
				else if (challenge == null)
				{
					points.setText("");
				}
				gUIBuilder.updateModList();
				gUIBuilder.updateScoreLabels();
				gUIBuilder.updateChallengesTree();
			}
		}
	}
	
	@Override
	/**
	 * This method is called whenever the user hits a key. It checks whether the key
	 * hit was a return ('\n') and if this is the case, it tries to convert the contents
	 * of the {@link Text} to an int. If this is successfull, the method saves this int 
	 * value as the new value for the currently selected {@link Challenge} and updates the
	 * GUI. Is this not the case, it sets the text in the {@link Text} to 'NaN' and leaves 
	 * the value as it was.
	 */
	public void keyPressed(KeyEvent k)
	{
		if (k.character == '\r')
		{
			Mod selected = ModsBookkeeper.getInstance().getSelectedMod();
			if (selected != null)
			{
				Challenge challenge = selected.getChallenge(Integer.valueOf(challenges.getText()));
				if (challenge != null)
				{
					try
					{
						int newPoints = Integer.parseInt(points.getText());
						challenge.setPoints(newPoints);
						points.setText(newPoints + "");
						gUIBuilder.updateModList();
						gUIBuilder.updateScoreLabels();
						gUIBuilder.updateChallengesTree();
					}
					catch (NumberFormatException n)
					{
						n.printStackTrace();
						points.setText("NaN");
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0)
	{}

	@Override
	/**
	 * This method is called when the user makes a selection in the {@link Combo}.
	 * It first retrieves the current {@link Mod} and from this {@link Mod} and the
	 * value in the {@link Combo}, it retrieves the current {@link Challenge}. It then sets 
	 * the text in the {@link Text} to be the value of the current {@link Challenge}.
	 * If no {@link Mod} is selected, it sets the text in the {@link Text} to an empty {@link String}.
	 */
	public void widgetSelected(SelectionEvent s)
	{
		Mod m = ModsBookkeeper.getInstance().getSelectedMod();
		if (m != null)
		{
			Challenge c = m.getChallenge(Integer.valueOf(challenges.getText()));
			if (c != null)
			{
				points.setText(c.getPoints() + "");
			}
		}
		else
		{
			points.setText("");
		}

	}
}
