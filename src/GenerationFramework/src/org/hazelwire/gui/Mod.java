package org.hazelwire.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.hazelwire.modules.Option;

public class Mod
{

	private String name;
	private ArrayList<Tag> tags;
	private ArrayList<Challenge> challenges;
	private ArrayList<String> packages;
	private HashMap<String, Option> options;
	private int id;

	public Mod(String name)
	{
		this.name = name;
		this.tags = new ArrayList<Tag>();
		this.challenges = new ArrayList<Challenge>();
		this.packages = new ArrayList<String>();
		this.options = new HashMap<String, Option>();
	}

	public String getName()
	{
		return this.name;
	}

	public ArrayList<Challenge> getChallenges()
	{
		return this.challenges;
	}

	public void addChallenge(Challenge chal)
	{
		this.challenges.add(chal);
	}

	public void addChallenge(int id, String descr, int points)
	{
		if (tags != null && descr != null)
		{
			this.challenges.add(new Challenge(id, descr, points));
		}
	}

	public void addPackage(String pack)
	{
		int index = this.packages.indexOf(pack);
		if (index == -1)
		{
			this.packages.add(pack);
		}
	}

	public void addOption(Option option)
	{
		if(this.options.get(option.getName()) == null)
		{
			this.options.put(option.getName(), option);
		}
	}

	public int getPoints()
	{
		int result = 0;
		for (Challenge c : challenges)
		{
			result += c.getPoints();
		}
		return result;
	}

	public ArrayList<Tag> getTags()
	{
		return this.tags;
	}

	public Challenge getChallenge(int id)
	{
		Challenge result = null;
		int index = this.challenges.indexOf(new Challenge(id));
		if (index != -1)
		{
			result = challenges.get(index);
		}
		return result;
	}

	public HashMap<String, Option> getOptions()
	{
		return this.options;
	}
	
	public void editOption(Option option)
	{
		this.options.put(option.getName(), option);
	}
	
	public Option getOption(String optionName)
	{
		return this.options.get(optionName);
	}

	public ArrayList<String> getPackages()
	{
		return this.packages;
	}

	public boolean equals(Object o)
	{
		boolean result = false;
		if (o instanceof Mod)
		{
			Mod m = ((Mod) o);
			result = m.getName().equals(this.getName());
		}
		return result;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public void addTag(String tag)
	{
		this.tags.add(new Tag(tag));
	}
	
	public void addTag(Tag tag)
	{
		this.tags.add(tag);
	}
	
	public void removeTag(String tag)
	{
		this.tags.remove(new Tag(tag));
	}
}
