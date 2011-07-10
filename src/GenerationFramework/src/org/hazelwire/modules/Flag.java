package org.hazelwire.modules;

public class Flag
{
	private int id, points, defaultPoints;
	private String description;
	
	public Flag(int points)
	{
		this.points = points;
		this.description = "TESTDATA";
		this.defaultPoints = points;
	}
	
	public Flag(int points, int defaultPoints)
	{
		this.points = points;
		this.description = "TESTDATA";
		this.defaultPoints = defaultPoints;
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

	public int getDefaultPoints() {
		return defaultPoints;
	}

	public void setDefaultPoints(int defaultPoints) {
		this.defaultPoints = defaultPoints;
	}
}
