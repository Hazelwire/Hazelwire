package org.hazelwire.modules;

public class Flag
{
	private int id, points;
	private String description;
	
	public Flag(int points)
	{
		this.points = points;
		this.description = "TESTDATA";
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getPoints()
	{
		return points;
	}

	public void setPoints(int points)
	{
		this.points = points;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
