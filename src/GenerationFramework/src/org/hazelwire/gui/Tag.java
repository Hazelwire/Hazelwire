package org.hazelwire.gui;

import java.util.ArrayList;

public class Tag
{

	private String tagName;

	public Tag(String tagName)
	{
		this.tagName = tagName;
	}

	public String getName()
	{
		return tagName;
	}

	public void setTagName(String tagName)
	{
		this.tagName = tagName;
	}

	public static ArrayList<Tag> dummyData()
	{
		ArrayList<Tag> result = new ArrayList<Tag>();
		for (int i = 0; i < 6; i++)
		{
			result.add(new Tag("Tag " + (i + 1)));
		}
		return result;
	}

	public String toString()
	{
		return tagName;
	}

}
