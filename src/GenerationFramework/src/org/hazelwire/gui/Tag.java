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
