package virassan.entities.creatures.enemies;

import java.awt.Rectangle;

public enum EnemyType {

	SOLDIER(3, 0.75f, "Soldier", 32, 32, "/textures/entities/blob_spritesheet_testing.png"),
	CAPTAIN(5, 0.10f, "Captain", 32, 32, "/textures/entities/captain_blob.png"),
	BOSS(20, 0.045f, "Boss", 32, 32, "/textures/entities/boss_blob.png");
	
	private String name;
	private int spawnSpacing;
	private float spawnChance;
	private int width, height;
	private Rectangle bounds;
	private String filepath;
	
	private EnemyType(int spawnSpacing, float spawnChance, String name, int width, int height, String filepath){
		this.spawnSpacing = spawnSpacing;
		this.spawnChance = spawnChance;
		this.filepath = filepath;
		this.name= name;
		this.width = width;
		this.height = height;
		bounds = new Rectangle(0,0, width, height);
	}
	
	public String toString(){
		return name;
	}
	
	public String getName(){
		return name;
	}
	
	public int getSpawnSpacing(){
		return spawnSpacing;
	}
	
	public float getSpawnChance(){
		return spawnChance;
	}
	
	public String getFilepath(){
		return filepath;
	}

	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public Rectangle getBounds(){
		return bounds;
	}
}
