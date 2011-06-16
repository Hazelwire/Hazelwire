package org.hazelwire.gui;

import java.util.ArrayList;

import org.hazelwire.modules.Flag;

public class Challenge
{

	private int id;
	private ArrayList<Tag> tags;
	private String description;
	private int points;
	private final int defaultPoints;

	public Challenge(int id, String description,
			int points)
	{
		this.id = id;
		this.description = description;
		this.points = points;
		this.defaultPoints = points;
	}

	// For comparisons
	public Challenge(int id)
	{
		this.id = id;
		this.tags = new ArrayList<Tag>();
		this.description = "";
		this.points = 0;
		this.defaultPoints = 0;
	}

	public String getIdString()
	{
		return String.valueOf(id);
	}
	
	public int getId()
	{
		return id;
	}

	public ArrayList<Tag> getTags()
	{
		return tags;
	}

	public String getDescription()
	{
		return description;
	}

	public int getPoints()
	{
		return this.points;
	}

	public int getDefaultPoints()
	{
		return this.defaultPoints;
	}

	public void setPoints(int points)
	{
		this.points = points;
	}
	
	public Flag toFlag()
	{
		Flag tempFlag = new Flag(points);
		tempFlag.setId(id);
		tempFlag.setDescription(description);
		return tempFlag;
	}

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
