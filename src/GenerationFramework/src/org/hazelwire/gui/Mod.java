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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.hazelwire.modules.Option;

/**
 * This class is the GUI's representation for a module. A module consists 
 * of a name, and int id, a list of {@link Tag}s, a list of {@link Challenge}s, 
 * a list of packages and a list of {@link Option}s.
 */
public class Mod
{

	private String name;
	private ArrayList<Tag> tags;
	private ArrayList<Challenge> challenges;
	private ArrayList<String> packages;
	private HashMap<String, Option> options;
	private int id;

	/**
	 * Constructs an instance of Mod by instantiating all lists and saving the
	 * given name. Names must be unique, since Mod comparison is carried out based
	 * on name.
	 * @param name {@link String} name. 
	 */
	public Mod(String name)
	{
		this.name = name;
		this.tags = new ArrayList<Tag>();
		this.challenges = new ArrayList<Challenge>();
		this.packages = new ArrayList<String>();
		this.options = new HashMap<String, Option>();
	}

	/**
	 * @return {@link String} name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return {@link ArrayList}<{@link Challenge}> challenges: the list
	 * containing all {@link Challenge}s associated with this Mod.
	 */
	public ArrayList<Challenge> getChallenges()
	{
		return this.challenges;
	}

	/**
	 * This method adds the given {@link Challenge} to the list of 
	 * {@link Challenge}s.
	 * @param chal {@link Challenge} that is to be added to the list
	 */
	public void addChallenge(Challenge chal)
	{
		this.challenges.add(chal);
	}
	
	/**
	 * This method creates a new {@link Challenge} based on the given
	 * information and adds it to the list of {@link Challenge}s.
	 * @param id unique int id
	 * @param descr {@link String} describing the {@link Challenge}
	 * @param points an int consisting of the amount of points that
	 * the {@link Challenge} should be worth.
	 * @requires tags != null
	 * @requires descr != null
	 */
	public void addChallenge(int id, String descr, int points)
	{
		if (tags != null && descr != null)
		{
			this.challenges.add(new Challenge(id, descr, points));
		}
	}

	/**
	 * This method adds a package to the list of packages.
	 * @param pack {@link String} name of the package to be added.
	 * @requires packages != null
	 */
	public void addPackage(String pack)
	{
		int index = this.packages.indexOf(pack);
		if (index == -1)
		{
			this.packages.add(pack);
		}
	}

	/**
	 * This method adds the given {@link Option} to the list of
	 * {@link Option}s.
	 * @param option the {@link Option} to be added to the list.
	 */
	public void addOption(Option option)
	{
		if(this.options.get(option.getName()) == null)
		{
			this.options.put(option.getName(), option);
		}
	}

	/**
	 * This method calculates the points by summing the amount of points
	 * each {@link Challenge} in this Mod is worth, and returns this value.
	 * @return the amount of points this Mod is worth
	 */
	public int getPoints()
	{
		int result = 0;
		for (Challenge c : challenges)
		{
			result += c.getPoints();
		}
		return result;
	}

	/**
	 * @return the list of {@link Tag}s associated with this Mod.
	 */
	public ArrayList<Tag> getTags()
	{
		return this.tags;
	}

	/**
	 * Returns the {@link Challenge} associated with this Mod that has
	 * the given id.
	 * @param id The int id that the {@link Challenge} should have.
	 * @return the {@link Challenge} with id id, or null, if no such 
	 * {@link Challenge} exists.
	 * @requires challenges != null
	 */
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
	
	public void setChallenges(Collection<Challenge> challenges)
	{
		this.challenges = new ArrayList<Challenge>(challenges);
	}
	
	public void setOptions(Collection<Option> options)
	{
		HashMap<String, Option> tempOptions = new HashMap<String, Option>();
		
		for(Option option : options)
		{
			tempOptions.put(option.getName(), option);
		}
	}

	/**
	 * @return the list of {@link Option}s associated with this Mod.
	 */
	public HashMap<String, Option> getOptions()
	{
		return this.options;
	}
	
	/**
	 * This method changes the value associated with a certain {@link Option}.
	 * @param option the {@link Option} that is changed.
	 * @requires option != null
	 */
	public void editOption(Option option)
	{
		this.options.put(option.getName(), option);
	}
	
	/**
	 * Returns the {@link Option} with name optionName.
	 * @param optionName {@link String} representation of the name the {@link Option}
	 * should have.
	 * @return the {@link Option} with name optionName, or null, if no such
	 * {@link Option} exists.
	 */
	public Option getOption(String optionName)
	{
		return this.options.get(optionName);
	}

	/**
	 * @return the list of packages associated with this Mod.
	 */
	public ArrayList<String> getPackages()
	{
		return this.packages;
	}

	/**
	 * This method overrides the standard equals method, in order to be able
	 * to search a {@link Collection} of Mods. It checks whether both
	 * {@link Object}s are instances of Mod and if that is the case, it
	 * compares their names.
	 * @param o Object that is compared to this Mod.
	 */
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

	/**
	 * @return the int id of this Mod.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Sets this Mod's id to the given int.
	 * @param id the new id.
	 */
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 * This method creates a new {@link Tag} from the given {@link String} and adds 
	 * it to the list of {@link Tag}s associated with this Mod.
	 * @param tag a {@link String} consisting of the name of the new tag.
	 * @requires tags != null
	 */
	public void addTag(String tag)
	{
		this.tags.add(new Tag(tag));
	}
	
	/**
	 * This method adds the given {@link Tag} to the list of {@link Tag}s associated
	 * with this Mod.
	 * @param tag the {@link Tag} that will be added to the list.
	 * @requires tags != null
	 */
	public void addTag(Tag tag)
	{
		this.tags.add(tag);
	}
	
	/**
	 * This method removes the {@link Tag} with the name tag from the list of {@link Tag}s
	 * associated with this Mod.
	 * @param tag a {@link String} consisting of the name of the {@link Tag} that is to be
	 * removed from the list.
	 * @requires tags != null
	 * @requires tags.get(new Tag(tag)) != null
	 */
	public void removeTag(String tag)
	{
		this.tags.remove(new Tag(tag));
	}
}
