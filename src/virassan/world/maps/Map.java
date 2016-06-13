package virassan.world.maps;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import virassan.entities.Entity;
import virassan.entities.EntityManager;
import virassan.entities.creatures.enemies.EnemyType;
import virassan.entities.creatures.enemies.Soldier;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.utils.Utils;


/**
 * Loads a Map and Fills it with shit
 * @author Virassan
 *
 */
public class Map{
	
	private String filepath;
	private String mapName;
	private Handler handler;
	private EntityManager entityManager;
	private ArrayList<EnemyType> enemyList;
	
	private int width, height, spawnX, spawnY;
	private int[][] worldTiles;
	
	
	/**
	 * Creates the Map
	 * @param handler
	 * @param filepath
	 * @param mapName
	 * @param enemyList
	 */
	public Map(Handler handler, String filepath, String mapName) {
		// TODO Auto-generated constructor stub
		this.filepath = filepath;
		this.handler = handler;
		this.mapName = mapName;
		entityManager = new EntityManager(handler);
		entityManager.setPlayer(handler.getPlayer());
		enemyList = new ArrayList<EnemyType>();
		addEnemy(EnemyType.SOLDIER);
	}
	
	public void render(Graphics g){
		entityManager.render(g);
	}

	public void tick(){
		entityManager.tick();
	}
	
	public void loadMap(){
		String file = Utils.loadFileAsString(filepath);
		String[] tokens = file.split("\\s+");
		width = Utils.parseInt(tokens[0]);
		height = Utils.parseInt(tokens[1]);
		spawnX = Utils.parseInt(tokens[2]);
		spawnY = Utils.parseInt(tokens[3]);
		
		handler.getPlayer().setX(spawnX);
		handler.getPlayer().setY(spawnY);
		
		//Takes the string numbers in the txt and puts them into a 2D array
		worldTiles = new int[width][height];
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				worldTiles[x][y] = Utils.parseInt(tokens[(x + y * width + 4)]);
			}
		}
		
		System.out.println(worldTiles.length + " " + worldTiles[0].length);
		
		/*
		//Shit for the Static Entities
		int count = height * width + 4;
		int secondCount = height * width + 5;
		for(int i = 1; i <= Utils.parseInt(tokens[count]); i++){
			Statics copy = Statics.statics[Utils.parseInt(tokens[secondCount])];
			int amount = Utils.parseInt(tokens[secondCount + 1]);
			for(int s = 1; s <= amount*2; s += 2){
				int idNum = 300;
				for(int num = 0; num < Statics.statics.length; num++){if(Statics.statics[num] == null){idNum = num;}}
				Statics staticEntity = new Statics(handler, idNum, copy.getBounds().x, copy.getBounds().y, copy.getBounds().width, copy.getBounds().height, copy.getImage(), copy.getName());
				staticEntity.setX(Utils.parseInt(tokens[secondCount + 1 + s]));
				staticEntity.setY(Utils.parseInt(tokens[secondCount + 1 + s + 1]));
				entityManager.addEntity(staticEntity);
			}
			secondCount += amount*2 + 2;
		}
		*/
		spawnEnemies();
	}
	
	
	/**
	 * Spawns Enemies according to this shit method I've created roflcopter
	 */
	private void spawnEnemies(){
		if(enemyList == null){
			return;
		}
		// i is row, k is column
		Random gen = new Random();
		for(int i = 1; i < height; i += gen.nextInt(2)+1){
			int numOfColumns = gen.nextInt(width - 2  - (width/3)) + (width/3);
			for(int k = numOfColumns + 1; k > 0; k--){
				for(EnemyType enemy : enemyList){
					if(enemy.getSpawnChance() < gen.nextFloat()){
						int chosenSpace = k - gen.nextInt(enemy.getSpawnSpacing());
						chosenSpace = Utils.clamp(chosenSpace, 0, k+enemy.getSpawnSpacing());
						if(!getTile(chosenSpace, i).isSolid()){
							int ranx = gen.nextInt(Tile.TILE_WIDTH);
							int rany = gen.nextInt(Tile.TILE_HEIGHT);
							int x = chosenSpace * Tile.TILE_WIDTH + ranx;
							int y = i * Tile.TILE_HEIGHT + rany;
							boolean b = true;
							boolean isFilled = false;
							int count = 0;
							while(b){
								for(Entity entity : entityManager.getEntities()){
									if(count <= 10){
										if(entity.getBounds().contains(new Rectangle(x, y, enemy.getWidth(), enemy.getHeight())) || getTile(x, y).isSolid()){
											count++;
											isFilled = true;
											int tempX = x + entity.getWidth();
											x = Utils.clamp(tempX, chosenSpace * Tile.TILE_WIDTH, chosenSpace * Tile.TILE_WIDTH + (Tile.TILE_WIDTH - 1));
											if(tempX == x){
												int tempY = y + entity.getHeight();
												y = Utils.clamp(tempY, i * Tile.TILE_HEIGHT, i * Tile.TILE_HEIGHT + (Tile.TILE_HEIGHT - 1));
											}
										}else{
											isFilled = false;
										}
									}else{
										break;
									}
								}
								b = false;
							}
							if(!isFilled && !getTile(x/64, y/64).isSolid()){
								k -= chosenSpace;
								Soldier entity = new Soldier(handler, x, y, ID.Enemy, EnemyType.SOLDIER, 3);
								entityManager.addEntity(entity);
								// System.out.println("Spawned Enemy at: " + x + ", " + y);
							}else{
								break;
							}
						}else{
							continue;
						}
					}
				}
			}
		}
		System.out.println(entityManager.getEntities().size() - 1);
	}
	
	
	/**
	 * Get's the tile at the column and row given. If passing x-coord and y-coord, divide by the Tile's width and height, respectively, first.
	 * @param x
	 * @param y
	 * @return
	 */
	public Tile getTile(int x, int y){
		if(x < 0 || y <0 || x >= width || y >= height){
			return Tile.tiles[0];
		}
		Tile t = Tile.tiles[worldTiles[x][y]];
		if(t == null){
			return Tile.tiles[1];
		}
		return t;
	}
	
	public void addEnemy(EnemyType e){
		enemyList.add(e);
	}
	
	public String getMapName(){
		return mapName;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public EntityManager getEntityManager(){
		return entityManager;
	}
}
