package org.hazelwire.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.hazelwire.modules.Flag;

/**
 * Instances of this class represent a challenge, which is a part
 * of a {@link Mod} that represents a {@link Flag} and the values 
 * associated with it.
 *
 */
public class Challenge
{

	private int id;
	private ArrayList<Tag> tags;
	private String description;
	private int points;
	private final int defaultPoints;

	/**
	 * Creates an instance of Challenge by specifying its id, its
	 * description and its value.
	 * @param id unique integer that is used to tell different challenges
	 * apart.
	 * @param description String that describes the Challenge
	 * @param points integer number that indicates the value of the {@link Flag}
	 * associated with this challenge. The default amount of points for this 
	 * Challenge is set to be the value of points.
	 */
	public Challenge(int id, String description,
			int points)
	{
		this.id = id;
		this.description = description;
		this.points = points;
		this.defaultPoints = points;
	}

	/**
	 * Creates an 'empty' Challenge. This method is only used for 
	 * comparisons, since two challenges are equal whenever their 
	 * id is equal. This method therefore sets only the id and assigns
	 * standard values to the other variables.
	 * @param id unique integer that is used to tell different challenges
	 * apart.
	 */
	public Challenge(int id)
	{
		this.id = id;
		this.tags = new ArrayList<Tag>();
		this.description = "";
		this.points = 0;
		this.defaultPoints = 0;
	}
	
	public Challenge(Flag flag)
	{
		this.id = flag.getId();
		this.tags = new ArrayList<Tag>();
		this.description = flag.getDescription();
		this.points = flag.getPoints();
		this.defaultPoints = flag.getDefaultPoints();
	}

	/**
	 * Returns the Challenges id, as a String value
	 * @return the String value of id
	 */
	public String getIdString()
	{
		return String.valueOf(id);
	}
	
	/**
	 * Returns the Challenges id, as an int value
	 * @return id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Returns the list of {@link Tag}s associated with this
	 * particular Challenge.
	 * @return an {@link ArrayList} of {@link Tag}s
	 * IMPORTANT: is never called!
	 */
	public ArrayList<Tag> getTags()
	{
		return tags;
	}

	/**
	 * @return the String description of this Challenge
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * @return an integer amount of points that indicate the value
	 * of this Challenge's {@link Flag}
	 */
	public int getPoints()
	{
		return this.points;
	}
	
	/**
	 * @return an integer returning the default amount of points this
	 * Challenge is worth.
	 */
	public int getDefaultPoints()
	{
		return this.defaultPoints;
	}

	/**
	 * Using this method, the value of the {@link Flag} may be increased
	 * or decreased. Using this method does not change the default amount
	 * of points this Challenge is worth.
	 * @param points the integer amount of points that should be the new
	 * value of the Challenge.
	 */
	public void setPoints(int points)
	{
		this.points = points;
	}
	
	/**
	 * Creates a new {@link Flag} with the data that is stored in this
	 * instance of Challenge: points, id and description.
	 * @return The new {@link Flag}.
	 */
	public Flag toFlag()
	{
		Flag tempFlag = new Flag(points);
		tempFlag.setId(id);
		tempFlag.setDescription(description);
		return tempFlag;
	}
	
	/**
	 * This method overrides the standard equals method, in order to be able
	 * to search a {@link Collection} of Challenges. It checks whether both
	 * {@link Object}s are instances of Challenge and if that is the case, it
	 * compares their ids.
	 * @param o Object that is compared to this Challenge.
	 */
	public boolean equals(Object o)
	{
		boolean result = false;
		if (o instanceof Challenge)
		{
			result = String.valueOf(this.id).equals(((Challenge) o).getIdString());
		}
		return result;
	}
}