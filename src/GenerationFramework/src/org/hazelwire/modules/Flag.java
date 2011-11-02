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
package org.hazelwire.modules;

/**
 * This represents a flag in the vulnerability. A flag is triggered by some action that will lead the contestant to gaining points.
 * @author Tim Strijdhorst
 *
 */
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
