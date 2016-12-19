package virassan.items;

public enum ItemType {

	ARMOR("Armor"),
	WEAPON("Weapon"),
	FOOD("Food"),
	MISC("Misc"),
	JUNK("Junk");
	
	private String name;
	
	private ItemType(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
}
