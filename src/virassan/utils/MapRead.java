package virassan.utils;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import virassan.entities.Entity;
import virassan.entities.creatures.Attack;
import virassan.entities.creatures.enemies.EnemySpecies;
import virassan.entities.creatures.enemies.Soldier;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.statics.Statics;
import virassan.gfx.Assets;
import virassan.gfx.ImageLoader;
import virassan.gfx.SpriteSheet;
import virassan.items.Drop;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.world.maps.Map;
import virassan.world.maps.Tile;

/**
 * MapRead reads the map file and loads the data into the game
 * @author Virassan
 *
 */
public class MapRead {
	
	public static JSONObject loadFile(String filepath){
		JSONObject jsonObject = null;
		try{
			jsonObject = (JSONObject)JSONValue.parse(new FileReader(filepath));
		
		}catch(FileNotFoundException f){
			System.out.println("Error Message: MapRead_loadFile FILE NOT FOUND " + filepath);
			f.printStackTrace();
		}catch(IOException e){
			System.out.println("Error Message: MapRead_loadFile IO EXCEPTION " + filepath);
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static void loadBasic(Handler handler, JSONObject jsonObject, String filepath){
		String name, map_id;
		long width, height, spawn_x, spawn_y, base_tile;
		try{
			map_id = (String)jsonObject.get("map_id");
			name = (String)jsonObject.get("name");
			base_tile = (Long)jsonObject.get("base_tile");
			width = (Long)jsonObject.get("width");
			height = (Long)jsonObject.get("height");
			spawn_x = (Long)jsonObject.get("spawn_x");
			spawn_y = (Long)jsonObject.get("spawn_y");
			int[][] worldTiles = new int[(int)height][(int)width];
			if(base_tile != 0){
				for(int c = 0; c < worldTiles.length; c++){
					for(int r = 0; r < worldTiles[c].length; r++){
						if(worldTiles[c][r] == 0){
							worldTiles[c][r] = (int)base_tile;
						}
					}
				}
			}
			handler.setMap(new Map(handler, filepath, name, map_id, (int)height, (int)width, (int)spawn_x, (int)spawn_y, worldTiles));
		}catch(Exception e){
			System.out.println("Error Message: MapRead_loadBasic SOMETHING WENT WRONG.");
			e.printStackTrace();
		}
	}
	
	public static void loadStatics(Handler handler, JSONObject json){
		JSONArray arrays = (JSONArray)json.get("arrays");
		
		JSONArray warpArray = (JSONArray) ((JSONObject)arrays.get(0)).get("warp_portal");
		// warp portals
		for(Object o : warpArray){
			long x, y, tempX, tempY;
			String mapfile = (String)((JSONObject)o).get("map_json");
			x = (Long)((JSONObject)o).get("x");
			y = (Long)((JSONObject)o).get("y");
			tempX = (Long)((JSONObject)o).get("spawn_x");
			tempY = (Long)((JSONObject)o).get("spawn_y");
			try{
				handler.getEntityManager().addStatic(new Statics(handler, (int)x, (int)y, 64, 64, "warp_portal", mapfile, (int)tempX, (int)tempY, Assets.warp_portal_images, false));
			}catch(NullPointerException e){
				e.printStackTrace();
				System.out.println("Error Message: MapRead_loadStatics Something is null! - mapfile = " + mapfile + ", x= " + x + ", y= " + y + ", tempX= " + tempX + ", tempY= " + tempY);
			}
		}
		JSONArray staticArray = (JSONArray) ((JSONObject)arrays.get(1)).get("statics");
		// statics
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
						handler.getMap().setTile((int)x, (int)y, (int)idnum);
					}catch(NullPointerException e){
						e.printStackTrace();
						System.out.println("Error Message: MapRead_loadStatics One of these static json objects are null");
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
						handler.getEntityManager().addEntity(new Statics(handler, (float)x, (float)y, image));
					}catch(NullPointerException e){
						e.printStackTrace();
						System.out.println("Error Message: MapRead_loadStatics One of the static type json objects is null");
					}
				}
				break;
			}
		}
	}
	
	
	public static void loadEnemies(Handler handler, JSONObject json){
		// monsters
		
		System.out.println("Message: MapRead_loadEnemies LOADING ENEMIES START");
		
		Random gen = new Random(37274);
		JSONArray arrays = (JSONArray)json.get("arrays");
		JSONArray monsterArray = (JSONArray) ((JSONObject)arrays.get(2)).get("monsters");
		for(Object o : monsterArray){
			String mon_name = (String)((JSONObject)o).get("name");
			int mon_width = (int)(long)((Long)((JSONObject)o).get("width"));
			int mon_height = (int)(long)((Long)((JSONObject)o).get("height"));
			String enemy_id = (String)((JSONObject)o).get("enemy_id");
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
					System.out.println("Error Message: MapRead_loadEnemies some json object is null or couldn't find the item");
				}
			}
			Attack attack = new Attack(0, 0, null, 0, null);
			JSONArray attacks = (JSONArray)((JSONObject)o).get("attack");
			for(Object a : attacks){
				int speed = (int)(long)((Long)((JSONObject)a).get("speed"));
				int dmgMod = (int)(long)((Long)((JSONObject)a).get("dmg_mod"));
				int attack_range = (int)(long)((Long)((JSONObject)a).get("range"));
				String attack_name = (String)((JSONObject)a).get("name");
				attack = new Attack(speed, dmgMod*level + level*((dmgMod*2)/100), null, attack_range, attack_name);
			}
			Rectangle rect = new Rectangle(0, 0, 0, 0);
			JSONArray region = (JSONArray)((JSONObject)o).get("region");
			for(Object r : region){
				int x = (int)(long)((Long)((JSONObject)r).get("x")) * Tile.TILE_WIDTH;
				int y = (int)(long)((Long)((JSONObject)r).get("y")) * Tile.TILE_HEIGHT;
				int rect_height = (int)(long)((Long)((JSONObject)r).get("height")) * Tile.TILE_HEIGHT;
				int rect_width = (int)(long)((Long)((JSONObject)r).get("width")) * Tile.TILE_WIDTH;
				rect = new Rectangle(x, y, rect_width, rect_height);
			}
			try{
				int tempX = 0;
				int tempY = 0;
				for(int k = 0; k < amount; k++){
					System.out.println("Message: MapRead_loadEnemies k count is: " + k);
					boolean isEmpty = true;
					do{
						tempX += ((gen.nextInt(5) * Tile.TILE_WIDTH) + rect.x);
						if(tempX >= rect.width){
							tempX = gen.nextInt(5) * Tile.TILE_WIDTH;
						}
						
						tempY += ((gen.nextInt(3) * Tile.TILE_HEIGHT) + rect.y);
						if(tempY >= rect.height){
							tempY = gen.nextInt(3) * Tile.TILE_HEIGHT;
						}
				
						for(Entity e : handler.getEntityManager().getEntities()){
							if((new Rectangle(tempX, tempY, mon_width, mon_height)).intersects(
									new Rectangle((int)e.getX(), (int)e.getY(), e.getWidth(), e.getHeight()))){
								System.out.println("Message: MapRead_loadEnemies tempx: " + tempX + ", tempy: " + tempY);
								isEmpty = false;
								break;
							}
						}
						
					}while(!isEmpty);
					
					Soldier entity = new Soldier(handler, mon_name, enemy_id, tempX, tempY, mon_height, mon_width, EnemySpecies.valueOf(enemy_species), level, dropList);
					entity.getStats().setMaxHealth(entity.getStats().getMaxHealth() + 2 * level);
					if(entity.getEnemyID().equals("S_002")){
						entity.setDefaultAttack(attack);
					}
					entity.getStats().setArmorRating(2*level);
					handler.getEntityManager().addEntity(entity);
					System.out.println("Update Message: MapRead_loadEnemies Monster spawn at: " + entity.getX() + ", " + entity.getY());
				}
			}catch(NullPointerException e){
				e.printStackTrace();
				System.out.println("Error Message: MapRead_loadEnemies Something went wrong with the spawning.");
			}
		}
	}
	
	
	public static void loadNPCs(Handler handler, JSONObject json){
		ArrayList<String> npcIDs = new ArrayList<String>();
		JSONArray ids = (JSONArray)json.get("npc_ids");
		for(Object id : ids){
			String text = (String)id;
			npcIDs.add(text);
		}
		loadNPCs(handler, npcIDs);
	}
	
	/**
	 * Creates NPC objects from the Map's NPC JSON file.
	 * @param filepath Filepath of the NPC JSON file.
	 */
	private static void loadNPCs(Handler handler, ArrayList<String> npcIDs){
		try{
			for(NPC n : DatabaseConnect.createNPC(handler, npcIDs)){
				handler.getEntityManager().addEntity(n);
			}
		}catch(NullPointerException k){
			System.out.println("Error Message: MapRead_loadNPCs NPC Creation NULL value.");
			k.printStackTrace();
		}catch(Exception e){
			System.out.println("Error Message: MapRead_loadNPCs NPC creation or adding error.");
			e.printStackTrace();
		}
	}

}
