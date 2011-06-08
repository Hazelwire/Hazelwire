import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


public class ModsBookkeeper extends Observable {

	private ArrayList<Mod> selectedMods;
	private int selected;
	private ArrayList<Mod> mods;
	private HashMap<Tag, ArrayList<Mod>> modsPerTag;
	private static ModsBookkeeper instance;

	public static ModsBookkeeper getInstance(){
		if(instance == null){
			instance = new ModsBookkeeper();
		}
		return instance;
	}
	
	
	private ModsBookkeeper(){
		/*
		 * Integer die bijhoudt welke module geselecteerd is. -1 betekent dat er niks 
		 * geselecteerd is. De indexes worden verder als wiskundige indexes behandeld
		 * (omdat dat gemakkelijker was bij het tekenen van de composites). 0 is dus niks,
		 * maar vanaf 1 duidt het op een geselecteerde module.
		 */
		this.selected = -1;
		/*
		 * Lijst die bijhoudt welke modules door de Administrator zijn geselecteerd.
		 */
		this.selectedMods = new ArrayList<Mod>();
		/*
		 * Dit is dus de lijst die je moet vullen met alle beschikbare modules. Die worden
		 * dan weergegeven in de tree waaruit ze geselecteerd kunnen worden.
		 */
		this.mods = DummyData.dummydata();	
		/*
		 * Voor het gemak, worden hier de modules per Tag gesorteerd. Daar hoef jij verder niks
		 * mee te doen.
		 */
		this.modsPerTag = new HashMap<Tag, ArrayList<Mod>>();
		for(Mod m : mods){
			for(Tag t: m.getTags()){
				if(modsPerTag.get(t) != null){
					modsPerTag.get(t).add(m);
				}
				else{
					ArrayList<Mod> modList = new ArrayList<Mod>();
					modList.add(m);
					modsPerTag.put(t, modList);
				}
			}
		}
	}
	
	
	public ArrayList<Mod> getSelectedMods(){
		return this.selectedMods;
	}
	
	public HashMap<Tag, ArrayList<Mod>> getModsPerTag(){
		return this.modsPerTag;
	}
	
	public boolean isSelected(String modName){
		int index = selectedMods.indexOf(new Mod(modName));
		return index != -1;
	}
	
	public void selectModule(String modName){
		int index = mods.indexOf(new Mod(modName));
		if(index != -1){
			if(selectedMods.indexOf(new Mod(modName))==-1){
				Mod mod = mods.get(index);
				selectedMods.add(mod);
				setChanged();
				notifyObservers("SELECTED");
			}
		}
	}
	
	public void deselectModule(String modName){
		int index = selectedMods.indexOf(new Mod(modName));
		if(index != -1){
			Mod mod = selectedMods.get(index);
			selectedMods.remove(mod);
			setChanged();
			notifyObservers("DESELECTED");
		}
	}
	
	public int getTotalPoints(){
		int total = 0;
		for(Mod m : selectedMods){
			total += m.getPoints();
		}
		return total;
	}
	
	public int getSelected(){
		return this.selected;
	}
	
	public Mod getSelectedMod(){
		Mod result = null;
		if(selected != -1){
			result = this.selectedMods.get(selected-1);
		}
		return result;
	}

	public void setSelected(int selected){
		this.selected = selected;
		setChanged();
		notifyObservers("SELECTED");
	}

	public ArrayList<Mod> getMods(){
		return this.mods;
	}
}
