package virassan.entities.creatures.utils;

public enum SkillType {

	BASIC("Basic"),
	SPECIAL("Special"),
	RESTORE("Restoritive"),
	MAGIC("Magic");
	
	String name;
	
	SkillType(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return name;
	}
}
