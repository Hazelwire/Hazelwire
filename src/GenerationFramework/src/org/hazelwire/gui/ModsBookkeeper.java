package org.hazelwire.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * This class keeps track of all the {@link Mod}s. It knows what
 * {@link Mod}s have been imported into the system, which {@link Mod}s
 * were selected by the user, which single {@link Mod} is currently
 * selected and it knows all the configuration settings. Because this
 * class is the central class for Module Selection, it has been made
 * into an {@link Observable}.
 */
public class ModsBookkeeper extends Observable
{

	private ArrayList<Mod> selectedMods;
	private int selected;
	private ArrayList<Mod> mods;
	private HashMap<Tag, ArrayList<Mod>> modsPerTag;
	private static ModsBookkeeper instance;
	private ConfigFile configFile;

	/**
	 * This method checks whether an instance of ModsBookkeeper exists. Be that the 
	 * case, it returns this instance. Otherwise, it calls the private constructor 
	 * method, saves the newly created ModsBookkeeper and returns it.
	 * This method is synchronized in order to make sure that all parts of the
	 * system deal with an up to date ModsBookkeeper and that no information gets
	 * lost.
	 * @return the instance of ModsBookkeeper.
	 */
	public synchronized static ModsBookkeeper getInstance()
	{
		if (instance == null)
		{
			instance = new ModsBookkeeper();
		}
		return instance;
	}

	/**
	 * Private GUIBuilder constructor that instantiates ModsBookkeeper. The constructor is
	 * private because the Singleton pattern is being used so as to make sure that only 
	 * one instance of the bookkeeper is created and used.
	 */
	private ModsBookkeeper()
	{}
	
	/**
	 * This method initializes the ModsBookkeeper. It creates the lists needed to keep track
	 * of the selected {@link Mod}s, sorts the {@link Mod}s per {@link Tag} and creates a 
	 * new {@link ConfigFile}.
	 * @param mods list of {@link Mod}s that are already imported into the system.
	 */
	public void initMods(ArrayList<Mod> mods)
	{
		//@todo TODO Properly reimplement this with a TreeMap instead of a HashMap due to the tags being objects.
		//Either that or also keeping track of all the tag objects so the same objects can be used.
		
		this.selected = -1;
		this.selectedMods = new ArrayList<Mod>();
		this.mods = mods;
		this.configFile = new ConfigFile(ConfigFile.dummyDefaults());
		HashMap<String,Tag> tagList = new HashMap<String,Tag>(); //this is so ugly i want to cry
																 //want a tissue?
		this.modsPerTag = new HashMap<Tag, ArrayList<Mod>>();
		for (Mod m : mods)
		{
			for (Tag t : m.getTags())
			{
				if(tagList.containsKey(t.getName()))
				{
					t = tagList.get(t.getName());
					modsPerTag.get(t).add(m);
				}
				else
				{
					tagList.put(t.getName(),t);
					ArrayList<Mod> modList = new ArrayList<Mod>();
					modList.add(m);
					modsPerTag.put(t, modList);
				}
			}
		}
	}

	/**
	 * @return the list containing all {@link Mod}s the user has selected.
	 */
	public ArrayList<Mod> getSelectedMods()
	{
		return this.selectedMods;
	}

	/**
	 * return the list containing all {@link Mod}s that are in the system,
	 * sorted by {@link Tag}.
	 * @return
	 */
	public HashMap<Tag, ArrayList<Mod>> getModsPerTag()
	{
		return this.modsPerTag;
	}

	/**
	 * Checks whether the {@link Mod} with the given modName is the currently
	 * selected {@link Mod}.
	 * @param modName a {@link String} consisting of the name of the Module that
	 * is checked.
	 * @return true if the {@link Mod} with name modName is the selected 
	 * {@link Mod}, false otherwise.
	 * @requires selectedMods != null
	 */
	public boolean isSelected(String modName)
	{
		int index = selectedMods.indexOf(new Mod(modName));
		return index != -1;
	}
	
	/**
	 * This method selects the {@link Mod} with name modName and places it
	 * in the list of selected {@link Mod}s. Afterwards, it notifies all
	 * {@link Observer}s.
	 * @param modName the {@link String} consisting of the name of the {@link Mod}
	 * that is to be selected.
	 * @requires mods != null
	 * @requires selectedMods != null
	 * @requires mods.indexOf(new Mod(modName)) != -1
	 */
	public void selectModule(String modName)
	{
		int index = mods.indexOf(new Mod(modName));
		if (index != -1)
		{
			if (selectedMods.indexOf(new Mod(modName)) == -1)
			{
				Mod mod = mods.get(index);
				selectedMods.add(mod);
				setChanged();
				notifyObservers("SELECTED");
			}
		}
	}
	
	/**
	 * This method deselects the {@link Mod} with name modName and removes
	 * it from the list of selected {@link Mod}s. Afterwards, it notifies all
	 * {@link Observer}s.
	 * @param modName the {@link String} consisting of the name of the {@link Mod}
	 * that is to be deselected.
	 * @requires selectedMods != null
	 * @requires selectedMods.indexOf(new Mod(modName)) != -1
	 */
	public void deselectModule(String modName)
	{
		int index = selectedMods.indexOf(new Mod(modName));
		if (index != -1)
		{
			Mod mod = selectedMods.get(index);
			selectedMods.remove(mod);
			setChanged();
			notifyObservers("DESELECTED");
		}
	}
	
	/**
	 * This method computes the total amount of points the current
	 * selection of {@link Mod}s is worth by summing the value of
	 * each mod.
	 * @return the total amount of points the current selection of
	 * {@link Mod}s is worth.
	 */
	public int getTotalPoints()
	{
		int total = 0;
		for (Mod m : selectedMods)
		{
			total += m.getPoints();
		}
		return total;
	}

	/**
	 * @return the int representation of the selected {@link Mod} or -1, if
	 * no {@link Mod} is currently selected.
	 * @ensures getSelected() == -1 ||
	 * 			getSelected() == selectedMods.indexOf(getSelectedMod()) + 1
	 */
	public int getSelected()
	{
		return this.selected;
	}

	/**
	 * @return the {@link Mod} that is currently selected.
	 * @requires selectedMods != null
	 */
	public Mod getSelectedMod()
	{
		Mod result = null;
		if (selected != -1)
		{
			result = this.selectedMods.get(selected - 1);
		}
		return result;
	}

	/**
	 * This method sets the selected {@link Mod} to the given int value and
	 * notifies all {@link Observer}s.
	 * @param selected int value representing the selected {@link Mod}. -1 
	 * means that no {@link Mod} is currently selected. x means that the
	 * {@link Mod} with index x-1 in selectedMods is selected.
	 */
	public void setSelected(int selected)
	{
		this.selected = selected;
		setChanged();
		notifyObservers("SELECTED");
	}
	
	/**
	 * @return the list of all {@link Mod}s that are imported into the system.
	 */
	public ArrayList<Mod> getMods()
	{
		return this.mods;
	}
	
	/**
	 * @return the {@link ConfigFile}.
	 */
	public ConfigFile getConfigFile(){
		return this.configFile;
	}
}
