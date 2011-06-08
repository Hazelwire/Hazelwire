import java.util.ArrayList;
import java.util.HashMap;


public class Mod {
	
	private String name;
	private ArrayList<Tag> tags;
	private ArrayList<Challenge> challenges;
	private ArrayList<String> packages;
	private HashMap<String, String> options;
	private HashMap<String, String> defaultOptions;
	
	public Mod(String name){
		this.name = name;
		this.tags = new ArrayList<Tag>();
		this.challenges = new ArrayList<Challenge>();
		this.packages = new ArrayList<String>();
		this.options = new HashMap<String, String>();
		this.defaultOptions = new HashMap<String, String>();
	}
		
	public String getName(){
		return this.name;
	}
		
	public ArrayList<Challenge> getChallenges(){
		return this.challenges;
	}
	
	public void addChallenge(Challenge chal){
		this.challenges.add(chal);
		noteTags();
	}
	
	public void addChallenge(String id, ArrayList<Tag> tags, String descr, int points){
		if(tags != null && descr != null){
			this.challenges.add(new Challenge(id, tags, descr, points));
		}
		noteTags();
	}
	
	public void addPackage(String pack){
		int index = this.packages.indexOf(pack);
		if(index == -1){
			this.packages.add(pack);
		}
	}
	
	public void addOption(String name, String val){
		String s = this.options.get(name);
		String t = this.defaultOptions.get(name);
		if(s==null && t == null){
			this.options.put(name, val);
			this.defaultOptions.put(name, val);
		}
	}
	
	public int getPoints(){
		int result = 0;
		for(Challenge c : challenges){
			result += c.getPoints();
		}
		return result;
	}
		
	public ArrayList<Tag> getTags(){		
		return this.tags;
	}
	
	public Challenge getChallenge(String id){
		Challenge result = null;
		int index = this.challenges.indexOf(new Challenge(id));
		if(index != -1){
			result = challenges.get(index);
		}
		return result;
	}
	
	public HashMap<String, String> getOptions(){
		return this.options;
	}
	
	public String getDefaultOption(String option){
		return this.defaultOptions.get(option);
	}
	
	private void noteTags(){
		for(Challenge c : challenges){
			for(Tag t: c.getTags()){
				if(this.tags.indexOf(t) == -1){
					this.tags.add(t);
				}
			}
		}
	}
	
	public ArrayList<String> getPackages(){
		return this.packages;
	}
	
	public boolean equals(Object o){
		boolean result = false;
		if(o instanceof Mod){
			Mod m = ((Mod)o);
			result = m.getName().equals(this.getName());
		}
		return result;
	}
}
