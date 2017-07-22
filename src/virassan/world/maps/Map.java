package virassan.world.maps;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.simple.*;

import virassan.entities.Entity;
import virassan.entities.EntityManager;
import virassan.entities.creatures.Attack;
import virassan.entities.creatures.enemies.EnemySpecies;
import virassan.entities.creatures.enemies.EnemyType;
import virassan.entities.creatures.enemies.Soldier;
import virassan.entities.creatures.npcs.Merchant;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.statics.Statics;
import virassan.gfx.Assets;
import virassan.gfx.ImageLoader;
import virassan.gfx.SpriteSheet;
import virassan.items.Drop;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.main.Vector2F;
import virassan.quests.Quest;


/**
 * Creates a Map Object and loads Map from JSON file. Handles rendering and ticking of EntityManager.
 * @author Virassan
 *
 */
public class Map{
	
	private String filepath;
	private String mapName;
	private Handler handler;
	private EntityManager entityManager;
	
	private int width, height, spawn_x, spawn_y;
	private int[][] worldTiles;
	
	
	/**
	 * Creates the Map
	 * @param handler Pass the game's Handler through
	 * @param filepath Filepath of the map JSON
	 */
	public Map(Handler handler, String filepath) {
		this.filepath = filepath;
		this.handler = handler;
		entityManager = new EntityManager(handler);
		entityManager.setPlayer(handler.getPlayer());
	}
	
	public void render(Graphics g){
		entityManager.render(g);
	}

	public void tick(){
		entityManager.tick();
	}
	
	/**
	 * Loads the Map information from a JSON file
	 * @param filepath
	 */
	public void loadMap(String filepath){
		if(filepath.equals(this.filepath)){
			Random gen = new Random(37274);
			JSONObject jsonObject;
			String name, npc_json;
			long width, height, spawn_x, spawn_y, base_tile;
			try{
				jsonObject = (JSONObject)JSONValue.parse(new FileReader(filepath));
				System.out.println(filepath);
				name = (String)jsonObject.get("name");
				npc_json = (String)jsonObject.get("npc_json");
				base_tile = (Long)jsonObject.get("base_tile");
				width = (Long)jsonObject.get("width");
				height = (Long)jsonObject.get("height");
				spawn_x = (Long)jsonObject.get("spawn_x");
				spawn_y = (Long)jsonObject.get("spawn_y");
				handler.getPlayer().setX(spawn_x);
				handler.getPlayer().setY(spawn_y);
				this.height = (int)height;
				this.width = (int)width;
				JSONArray arrays = (JSONArray)jsonObject.get("arrays");
				worldTiles = new int[(int)height][(int)width];
				for(int i = 0; i < arrays.size(); i++){
					switch(i){
					case 0: // warp portals
						JSONObject warpObj = (JSONObject)arrays.get(i);
						JSONArray warpArray = (JSONArray)warpObj.get("warp_portal");
						for(Object o : warpArray){
							long x, y, tempX, tempY;
							String mapfile = (String)((JSONObject)o).get("map_json");
							x = (Long)((JSONObject)o).get("x");
							y = (Long)((JSONObject)o).get("y");
							tempX = (Long)((JSONObject)o).get("spawn_x");
							tempY = (Long)((JSONObject)o).get("spawn_y");
							try{
								entityManager.addStatic(new Statics(handler, (int)x, (int)y, 64, 64, "warp_portal", mapfile, (int)tempX, (int)tempY, Assets.warp_portal_images, false));
							}catch(NullPointerException e){
								e.printStackTrace();
								System.out.println("Something is null! - mapfile = " + mapfile + ", x= " + x + ", y= " + y + ", tempX= " + tempX + ", tempY= " + tempY);
							}
						}
						break;
					case 1: // statics
						JSONObject staticObj = (JSONObject)arrays.get(i);
						JSONArray staticArray = (JSONArray)staticObj.get("statics");
						for(Object o : staticArray){
							String type = (String)((JSONObject)o).get("type");
							switch(type){
							case "tile":
								long idnum = (Long)((JSONObject)o).get("IDnum");
								JSONArray cords = (JSONArray)((JSONObject)o).get("cords");
								for(Object num : cords){
									JSONObject cordObj = (JSONObject)num;
									long x = (Long)cordObj.get("x");
									long y = (Long)cordObj.get("y");
									try{
										worldTiles[(int)x][(int)y] = (int)idnum;
									}catch(NullPointerException e){
										e.printStackTrace();
										System.out.println("One of these static json objects are null");
									}
								}
								break;
							case "static":
								String imagefile = (String)((JSONObject)o).get("spritesheet");
								int image_x = (int)((long)(Long)((JSONObject)o).get("x"));
								int image_y = (int)((long)(Long)((JSONObject)o).get("y"));
								int image_width = (int)((long)(Long)((JSONObject)o).get("width"));
								int image_height = (int)((long)(Long)((JSONObject)o).get("height"));
								JSONArray static_cords = (JSONArray)((JSONObject)o).get("cords");
								for(Object num : static_cords){
									JSONObject cordObj = (JSONObject)num;
									long x = (Long)cordObj.get("x");
									long y = (Long)cordObj.get("y");
									try{
										SpriteSheet temp = new SpriteSheet(ImageLoader.loadImage(imagefile));
										BufferedImage image = temp.sprite(image_x, image_y, image_width, image_height);
										entityManager.addEntity(new Statics(handler, (float)x, (float)y, image));
									}catch(NullPointerException e){
										e.printStackTrace();
										System.out.println("One of the static type json objects is null");
									}
								}
								break;
							}
						}
						break;
					case 2: // monsters
						//TODO: Later add Skills and Such
						JSONObject monsterObj = (JSONObject)arrays.get(i);
						JSONArray monsterArray = (JSONArray)monsterObj.get("monsters");
						for(Object o : monsterArray){
							String mon_name = (String)((JSONObject)o).get("name");
							int mon_width = (int)(long)((Long)((JSONObject)o).get("width"));
							int mon_height = (int)(long)((Long)((JSONObject)o).get("height"));
							String enemy_type = (String)((JSONObject)o).get("enemy_type");
							String enemy_species = (String)((JSONObject)o).get("enemy_species");
							int level = (int)(long)((Long)((JSONObject)o).get("level"));
							level = gen.nextInt(4) + (level - 1);
							int min = (int)(long)((Long)((JSONObject)o).get("min"));
							int max = (int)(long)((Long)((JSONObject)o).get("max"));
							int amount = 0;
							if(max != min){
								amount = gen.nextInt(max - min) + min;
							}else{
								amount = max;
							}
							ArrayList<Drop> dropList = new ArrayList<Drop>();
							JSONArray drops = (JSONArray)((JSONObject)o).get("drops");
							for(Object d : drops){
								String item_name = (String)((JSONObject)d).get("item");
								float rate = (Long)((JSONObject)d).get("rate");
								try{
									float perc = rate/100;
									dropList.add(new Drop(Item.valueOf(item_name), perc));
								}catch(Exception e){
									e.printStackTrace();
									System.out.println("in monster drops, some json object is null or couldn't find the item");
								}
							}
							Attack attack = new Attack(0, 0, null, 0, 0, null);
							JSONArray attacks = (JSONArray)((JSONObject)o).get("attack");
							for(Object a : attacks){
								int speed = (int)(long)((Long)((JSONObject)a).get("speed"));
								int dmgMod = (int)(long)((Long)((JSONObject)a).get("dmg_mod"));
								int attack_width = (int)(long)((Long)((JSONObject)a).get("width"));
								String attack_name = (String)((JSONObject)a).get("name");
								attack = new Attack(speed, dmgMod*level + level*((dmgMod*2)/100), null, attack_width, attack_width, attack_name);
							}
							Rectangle rect = new Rectangle(0, 0, 0, 0);
							JSONArray region = (JSONArray)((JSONObject)o).get("region");
							for(Object r : region){
								int x = (int)(long)((Long)((JSONObject)r).get("x"));
								int y = (int)(long)((Long)((JSONObject)r).get("y"));
								int rect_height = (int)(long)((Long)((JSONObject)r).get("height"));
								int rect_width = (int)(long)((Long)((JSONObject)r).get("width"));
								rect = new Rectangle(x, y, rect_width, rect_height);
							}
							try{
								for(int k = 0; k < amount; k++){
									int tempX = 0;
									int tempY = 0;
									boolean isEmpty = true;
									do{
										tempX = gen.nextInt(rect.width) + rect.x;
										tempY = gen.nextInt(rect.height) + rect.y;
										for(Entity e : entityManager.getEntities()){
											if((new Rectangle(tempX, tempY, mon_width, mon_height)).intersects(new Rectangle((int)e.getX(), (int)e.getY(), e.getWidth(), e.getHeight()))){
												isEmpty = false;
											}
										}
									}while(!isEmpty);
									Soldier entity = new Soldier(handler, mon_name, tempX, tempY, mon_height, mon_width, ID.Enemy, EnemyType.valueOf(enemy_type), EnemySpecies.valueOf(enemy_species), level, dropList);
									entity.getStats().setMaxHealth(entity.getStats().getMaxHealth() + 2 * level);
									if(entity.getEnemyType() == EnemyType.BOSS){
										entity.setDefaultAttack(attack);
									}
									entity.getStats().setArmorRating(2*level);
									entityManager.addEntity(entity);
									System.out.println("Monster spawn at: " + entity.getX() + ", " + entity.getY());
								}
							}catch(NullPointerException e){
								e.printStackTrace();
								System.out.println("Something went wrong with the spawning.");
							}
						}
						break;
					}
				}
				if(base_tile != 0){
					for(int c = 0; c < worldTiles.length; c++){
						for(int r = 0; r < worldTiles[c].length; r++){
							if(worldTiles[c][r] == 0){
								worldTiles[c][r] = (int)base_tile;
							}
						}
					}
				}
				loadNPCs(npc_json);
				mapName = name;
			}catch(IOException e){
				e.printStackTrace();
				System.out.println("Cannot find Map file: " + filepath);
			}
		}
	}
	
	/**
	 * Creates NPC objects from the Map's NPC JSON file.
	 * @param filepath Filepath of the NPC JSON file.
	 */
	public void loadNPCs(String filepath){
		try{
			JSONObject jObj = (JSONObject)JSONValue.parse(new FileReader(filepath));
			JSONArray arrays = (JSONArray)jObj.get("NPCs");
			for(int i = 0; i < arrays.size(); i++){
				JSONObject npcObj = (JSONObject)arrays.get(i);
				String name = (String)npcObj.get("name");
				String type = (String)npcObj.get("type");
				long x = (Long)npcObj.get("x");
				long y = (Long)npcObj.get("y");
				long width = (Long)npcObj.get("width");
				long height = (Long)npcObj.get("height");
				String image = (String)npcObj.get("image");
				switch(type){
				case "Merchant":
					JSONArray items = (JSONArray)npcObj.get("items");
					HashMap<Item, Integer> buyList = new HashMap<>();
					for(Object item : items){
						String i_name = (String)((JSONObject)item).get("item");
						int stack = (int)(long)(Long)((JSONObject)item).get("stack");
						buyList.put(Item.valueOf(i_name), stack);
					}
					entityManager.addEntity(new Merchant(handler, name, (float)x, (float)y, (int)width, (int)height, image, buyList));
					break;
				case "NPC":
					JSONArray quests = (JSONArray)npcObj.get("quests");
					ArrayList<Quest> questList = new ArrayList<>();
					for(Object q : quests){
						String q_name = (String)((JSONObject)q).get("ID");
						questList.add(Quest.valueOf(q_name));
					}
					entityManager.addEntity(new NPC(handler, (float)x, (float)y, (int)width, (int)height, name, image, questList));
					break;
				}
			}
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Cannot find NPC json file for this map at path: " + filepath);
		}
	}
	
	/**
	 * Returns the tile at the column and row given.
	 * @param x Column index number of Tile - ie the Array's first dimension. If passing the pixel coordinate, first divide by the Tile width.
	 * @param y Row index number of Tile - ie the Array's second dimension. If passing the pixel coordinate, first divide by the Tile height.
	 * @return the Tile object at that location.
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
	
	/**
	 * Returns the filepath of the Map - mostly for saving and loading saves
	 * @return
	 */
	public String getFilepath(){
		return filepath;
	}
	
	public Vector2F getSpawn(){
		return new Vector2F(spawn_x, spawn_y);
	}
}
