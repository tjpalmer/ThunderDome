package virassan.entities.creatures.enemies;

public enum EnemySpecies {
	
	SLIME("Slime");
	
	
	private String name;
	
	
	EnemySpecies(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
	
	public String getName(){
		return name;
	}
}
