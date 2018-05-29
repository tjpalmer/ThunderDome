package virassan.entities.creatures.enemies;

import java.awt.Rectangle;

public class EnemyType {
	
	private int spawnSpacing;
	private float spawnChance;
	private int width, height;
	private Rectangle bounds;
	private String filepath;
	private String enemyID;
	
	public EnemyType(String enemyID, int spawnSpacing, float spawnChance, int width, int height, String filepath){
		this.spawnSpacing = spawnSpacing;
		this.spawnChance = spawnChance;
		this.filepath = filepath;
		this.width = width;
		this.height = height;
		bounds = new Rectangle(0,0, width, height);
	}
	
	public String getEnemyID(){
		return enemyID;
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
	
	public String toString(){
		return enemyID;
	}
}
