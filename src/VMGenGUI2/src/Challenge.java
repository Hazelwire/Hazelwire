import java.util.ArrayList;


public class Challenge {
	
	private String id = "";
	private ArrayList<Tag> tags;
	private String description;
	private int points;
	private final int defaultPoints;
	
	
	public Challenge(String id, ArrayList<Tag> tags, String description, int points){
		this.id += id;
		this.tags = tags;
		this.description = description;
		this.points = points;
		this.defaultPoints = points;
	}
	
	//For comparisons
	public Challenge(String id){
		this.id = id;
		this.tags = new ArrayList<Tag>();
		this.description ="";
		this.points = 0;
		this.defaultPoints=0;
	}


	public String getId() {
		return id;
	}


	public ArrayList<Tag> getTags() {
		return tags;
	}


	public String getDescription() {
		return description;
	}
	
	public int getPoints(){
		return this.points;
	}
	
	public int getDefaultPoints(){
		return this.defaultPoints;
	}
	
	public void setPoints(int points){
		this.points = points;
	} 
	
	public boolean equals(Object o){
		boolean result = false;
		if(o instanceof Challenge){
			result = this.id.equals(((Challenge)o).getId());
		}
		return result;
	}
	
	
}
