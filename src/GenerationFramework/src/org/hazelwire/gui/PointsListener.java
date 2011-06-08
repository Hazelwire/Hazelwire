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

public class PointsListener implements MouseListener, KeyListener,
		SelectionListener
{

	private Text points;
	private Combo challenges;
	private GUIBuilder gUIBuilder;

	public PointsListener(Text points, Combo challenges, GUIBuilder gUIBuilder)
	{
		this.points = points;
		this.challenges = challenges;
		this.gUIBuilder = gUIBuilder;
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDown(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
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
						points.setText(newPoints + ""); // Yes, I know dat is
														// niet nodig
						gUIBuilder.updateModList(); // duh Sukkel
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
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
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
