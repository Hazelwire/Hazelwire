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

/**
 * This class represents a tag: an indication of what a {@link Mod} or 
 * Challenge entails.
 */
public class Tag
{
	private String tagName;

	/**
	 * Constructs an instance of Tag with the given argument.
	 * @param tagName {@link String} consisting of the Tag's name.
	 */
	public Tag(String tagName)
	{
		this.tagName = tagName;
	}

	/**
	 * @return the {@link String} consisting of the Tag's name.
	 */
	public String getName()
	{
		return tagName;
	}

	/**
	 * Sets the name of the Tag to the given {@link String}.
	 * @param tagName the new Tag name.
	 */
	public void setTagName(String tagName)
	{
		this.tagName = tagName;
	}

	/**
	 * This method overrides the {@link Object}'s toString method, in
	 * order to just return the Tag name, since this is useful in using
	 * it throughout the system.
	 */
	public String toString()
	{
		return tagName;
	}

}
