package virassan.entities.creatures.enemies;

public enum EnemySpecies {
	
	SLIME("Slime", "/textures/entities/blob_spritesheet"),
	CUCU("Cucu", "/textures/entities/cucu_spritesheet");
	
	
	private String name;
	private String filepath;
	
	
	EnemySpecies(String name, String filepath){
		this.name = name;
		this.filepath = filepath;
	}
	
	public String toString(){
		return name;
	}
	
	public String getFilePath(){
		return filepath;
	}
	
	public String getName(){
		return name;
	}
}
