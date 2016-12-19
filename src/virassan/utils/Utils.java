package virassan.utils;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import virassan.entities.creatures.enemies.EnemyType;
import virassan.entities.creatures.player.Player;
import virassan.entities.creatures.utils.BuffTracker;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.items.Equip;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.quests.QuestTracker;

/**
 * Nifty Utility Stuff
 * @author Virassan
 *
 */
public class Utils {

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String loadFileAsString(String path){
		StringBuilder builder = new StringBuilder();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line;
			while((line = br.readLine()) != null)
				builder.append(line+ "\n");
			br.close();
		}catch(IOException e){
			e.printStackTrace();
			//TODO: error message
		}
		return builder.toString();
	}
	
	/**
	 * Converts String to an int
	 * @param number
	 * @return the int - defaults at 0 if the string passed isn't a pure number
	 */
	public static int parseInt(String number){
		try{
			return Integer.parseInt(number);
		}catch(NumberFormatException e){
			e.printStackTrace();
			//TODO: error message
			return 0;
		}
	}
	
	/**
	 * Keeps the variable given from going below the min or above the max
	 * @param var the variable to be checked
	 * @param min the minimum that variable can be
	 * @param max the maxiumum that variable can be
	 * @return if above the max, returns the max, if below the min, returns the min, if neither it returns the variable unchanged
	 */
	public static int clamp(int var, int min, int max){
		if(var >= max)
			return var = max;
		else if(var <= min)
			return var = min;
		else
			return var;
	}

	/**
	 * Keeps the variable given from going below the min or above the max
	 * @param var the variable to be checked
	 * @param min the minimum that variable can be
	 * @param max the maxiumum that variable can be
	 * @return if above the max, returns the max, if below the min, returns the min, if neither it returns the variable unchanged
	 */
	public static float clamp(float var, float min, float max){
		if(var >= max)
			return var = max;
		else if(var <= min)
			return var = min;
		else
			return var;
	}
	
	/**
	 * Takes the image and removes the alpha amount from its opacity
	 * @param image The image wanted to become transparent
	 * @param alpha The numerical value to remove from the image's current opacity.
	 * @return A copied transparent image
	 */
	public static BufferedImage transparency(BufferedImage image, int alpha){
		BufferedImage ugh = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		//TODO: get 2d array of pixels, and convert the alpha/opacity of each
		for(int y = 0; y < image.getHeight(); y++){
			for(int x = 0; x < image.getWidth(); x++){
				int rgb = image.getRGB(x, y);
				Color color = new Color(rgb, true);
				int red = color.getRed();
				int blue = color.getBlue();
				int green = color.getGreen();
				int a = color.getAlpha();
				if(red == 255 && blue == 255 && green == 255){
					ugh.setRGB(x, y, rgb);
				}else{
					Color target = new Color(red, green, blue, clamp(a-alpha, 0, 255));
					ugh.setRGB(x, y, target.getRGB());
				}
				// System.out.println("New RGB: " + ugh.getRGB(x, y) + "  Old RGB: " + image.getRGB(x, y));
			}
		}
		return ugh;
	}
	
	/**
	 * Takes the incoming String and wraps it to a new "line" if its length goes beyond the width
	 * @param text String to wrap
	 * @param width Width of area the String is allowed to be before it wraps
	 * @param textMetrics The FontMetrics of the Graphics, necessary to find the text's string width
	 * @return an Array of Strings of the text wrapped
	 */
	public static ArrayList<String> wrapText(String text, int width, FontMetrics textMetrics){
		ArrayList<String> map = new ArrayList<String>();
		String textToDraw = text;
		String[] arr =  textToDraw.split(" ");
		int nIndex = 0;
		while(nIndex < arr.length){
			String line = arr[nIndex++];
			while(nIndex < arr.length && textMetrics.stringWidth(line + " " + arr[nIndex]) < width){
				line = line + " " + arr[nIndex];
				nIndex++;
			}
			map.add(line);
		}
		return map;
	}
	
	/**
	 * For Mapping Quest Requirement current Amounts
	 * @param amount
	 * @param req
	 * @return
	 */
	public static Map toMap(int amount, Object req){
		HashMap<String, Object> map = new HashMap<>();
		String name;
		if(req instanceof Enum){
			name = ((Enum) req).name();
		}else{
			name = req.toString();
		}
		map.put("req", name);
		map.put("amt", amount);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Map toMap(QuestTracker quest){
		HashMap<String, Object> map = new HashMap<>();
		map.put("quest", quest.getQuest().name());
		return map;
	}
	
	public static Map toMap(EnemyType type, int amount){
		HashMap<String, Object> map = new HashMap<>();
		map.put("enemy_type", type.name());
		map.put("num", amount);
		return map;
	}
	
	public static Map toMap(BuffTracker buff){
		HashMap<String, Object> map = new HashMap<>();
		map.put("buff", buff.getBuff().name());
		int timer = 0;
		if(buff.getTimer() >= 1000){
			timer = 1000;
		}else{
			timer = (int)buff.getTimer();
		}
		map.put("buff_time", timer);
		return map;
	}
	
	public static Map toMap(SkillTracker skill){
		HashMap<String, String> map = new HashMap<>();
		map.put("skill", skill.getSkillType().name());
		return map;
	}
	
	/**
	 * Used for SkillBar
	 * @param skill the skill 
	 * @param index what slot on the skillbar it's in
	 * @return
	 */
	public static Map toMap(SkillTracker skill, int index){
		HashMap<String, Object> map = new HashMap<>();
		if(skill != null){
			map.put("skill", skill.getSkillType().name());
		}else{
			map.put("skill", "");
		}
		map.put("index", index);
		return map;
	}
	
	public static Map toMap(Equip equip, Item item){
		HashMap<String, Object> map = new HashMap<>();
		map.put("slot", equip.name());
		if(item != null){
			map.put("item", item.name());
		}else{
			map.put("item", "");
		}
		return map;
	}
	
	public static Map toMap(Item item, int stack){
		HashMap<String, Object> map = new HashMap<>();
		map.put("item", item.name());
		map.put("stack", stack);
		return map;
	}
	
	/**
	 * Saves important Game Info into a JSON file
	 * @param handler The Game's Handler
	 */
	@SuppressWarnings("unchecked")
	public static void saveGame(Handler handler){
		Player player = handler.getPlayer();
		JSONObject obj = new JSONObject();
		obj.put("player_name", player.getName());
		obj.put("player_level", new Integer(player.getStats().getLevel()));
		obj.put("player_exp", new Integer(player.getStats().getExperience()));
		obj.put("player_gold", new Integer((int)player.getGold()));
		obj.put("char", new Integer(player.getTraits().getCharisma()));
		obj.put("dex", new Integer(player.getTraits().getDexterity()));
		obj.put("int", new Integer(player.getTraits().getIntelligence()));
		obj.put("str", new Integer(player.getTraits().getStrength()));
		obj.put("res", new Integer(player.getTraits().getResilience()));
		obj.put("xOffset", new Integer((int)handler.getGameCamera().getxOffset()));
		obj.put("yOffset", new Integer((int)handler.getGameCamera().getyOffset()));
		obj.put("map_name", handler.getMap().getMapName());
		obj.put("map", handler.getMap().getFilepath());
		
		JSONArray equip = new JSONArray();
		for(Equip slot : player.getStats().getEquip().keySet()){
			equip.add(toMap(slot, player.getStats().getEquip().get(slot)));
		}
		obj.put("equip", equip);
		JSONArray inventory = new JSONArray();
		for(Item item : player.getInventory().getItemMap().keySet()){
			inventory.add(toMap(item, player.getInventory().getItemMap().get(item)));
		}
		obj.put("inventory", inventory);
		JSONArray skillBar = new JSONArray();
		for(int i = 0; i < player.getSkillBar().length; i++){
			skillBar.add(toMap(player.getSkillBar()[i], i));
		}
		obj.put("skillbar", skillBar);
		JSONArray activeQuests = new JSONArray();
		for(QuestTracker quest : player.getQuestLog().getActive()){
			activeQuests.add(toMap(quest));
			JSONArray reqs = new JSONArray();
			for(Object req : quest.getQuest().getHashMap().keySet()){
				reqs.add(toMap(quest.getCurAmt(req), req));
			}
			activeQuests.add(reqs);
		}
		obj.put("active_quests", activeQuests);
		JSONArray completedQuests = new JSONArray();
		for(QuestTracker quest : player.getQuestLog().getComplete()){
			completedQuests.add(toMap(quest));
		}
		obj.put("completed_quests", completedQuests);
		JSONArray buffs = new JSONArray();
		for(BuffTracker buff : player.getStats().getBuffs()){
			if(buff.getBuff() != null){
				buffs.add(toMap(buff));
			}
		}
		obj.put("player_buffs", buffs);
		JSONArray skills = new JSONArray();
		for(SkillTracker skill : player.getSkills()){
			skills.add(toMap(skill));
		}
		obj.put("player_skills", skills);
		
		//TODO: add itemmanager items array, player kill list
		
		
		/*
		JSONArray list = new JSONArray();
		list.add("Greetings traveler!");
		list.add("Welcome to our kingdom!");
		list.add("Good day, wanderer!");
		obj.put("greetings", list);
		*/

		try {
			FileWriter file = new FileWriter("c:\\Users\\Virassan\\Documents\\!ThunderDome\\ThunderDome\\ThunderDome\\res\\saves\\testsave.json");
			file.write(obj.toJSONString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the JSON file and implements the data into the game
	 * @param handler The Handler
	 * @param filepath The filepath of the save file
	 */
	public static void loadGame(Handler handler, String filepath){
		JSONObject jsonObject;
		long playerLevel = 0, playerExp = 0, charisma = 0, resilience = 0, strength = 0, intelligence = 0, dexterity = 0, xOffset = 0, yOffset = 0;
		String map = null, mapName = null, playerName = null;
		SkillTracker[] skillBar = new SkillTracker[5];
		HashMap<String, Integer> inventory = new HashMap<>();
		HashMap<String, String> equip = new HashMap<>();
		//TODO: add activequests, completedquests, buffs, skills
		//JSONParser parser = new JSONParser();
		try{
			//jsonObject = (JSONObject)parser.parse(new FileReader(filepath));
			jsonObject = (JSONObject)JSONValue.parse(new FileReader(filepath));
			playerName = (String)jsonObject.get("player_name");
			playerLevel = (Long)jsonObject.get("player_level");
			playerExp = (Long)jsonObject.get("player_exp");
			charisma = (Long)jsonObject.get("char");
			resilience = (Long)jsonObject.get("res");
			strength = (Long)jsonObject.get("str");
			intelligence = (Long)jsonObject.get("int");
			dexterity = (Long)jsonObject.get("dex");
			xOffset = (Long)jsonObject.get("xOffset");
			yOffset = (Long)jsonObject.get("yOffset");
			map = (String)jsonObject.get("map");
			mapName = (String)jsonObject.get("map_name");
			System.out.println(playerName + ", " + playerLevel + ", " + intelligence + ", " + mapName + ", " + playerExp);
			//TODO: read the arrays of arrays!
			JSONArray skillList = (JSONArray)jsonObject.get("player_skills");
			JSONArray inv = (JSONArray)jsonObject.get("inventory");
			JSONArray skillbar = (JSONArray)jsonObject.get("skillbar");
			JSONArray eq = (JSONArray)jsonObject.get("equip");
			JSONArray aq = (JSONArray)jsonObject.get("active_quests");
			//TODO: buffs, then completed quests
			
			
			
			
			// SKILL LIST
			for(Object o : skillList){
				JSONObject object = (JSONObject)o;
				String skill = (String)object.get("skill");
				System.out.println("Skill: " + skill);
			}
			
			// INVENTORY
			for(Object o : inv){
				JSONObject array = (JSONObject)o;
				String item = (String)array.get("item");
				Long stack = (Long)array.get("stack");
				System.out.println(item + ": " + stack);
			}
			
			// SKILLBAR
			for(Object o : skillbar){
				JSONObject array = (JSONObject)o;
				String test = (String)array.get("skill");
				System.out.println(test);
				Long test2 = (Long)array.get("index");
				System.out.println(test2);
			}
		
			// EQUIP
			for(Object o : eq){
				JSONObject array = (JSONObject)o;
				String item = (String)array.get("item");
				String slot = (String)array.get("slot"); //TODO: change "equip" to "slot"
				System.out.println(slot + ": " + item);
			}
			
			
			//TODO: BUFFS
			/*
			JSONArray playerBuffs = (JSONArray)jsonObject.get("player_buffs");
			Iterator<Object> buffIt = playerBuffs.iterator();
			while(buffIt.hasNext()){
				JSONObject array = (JSONObject)buffIt.next();
				String buff = (String)array.get("buff");
				Long buffTimer = (Long)array.get("buff_time");
				System.out.println(buff + ": " + buffTimer);
			}*/
			
			//TODO: COMPLETED QUESTS
			
			// ACTIVE QUESTS
			
			Iterator<Object> aqIterator = aq.iterator();
			while(aqIterator.hasNext()){
				/*
				JSONArray reqs = (JSONArray)aqIterator.next();
				Iterator<Object> reqIterator = reqs.iterator();
				while(reqIterator.hasNext()){
					JSONObject reqObj = (JSONObject)reqIterator.next();
					String req = (String)reqObj.get("req");
					Long amount = (Long)reqObj.get("amt");
					System.out.println(req + ": " + amount);
				}
				*/
			}
			
			/*
			JSONArray msg = (JSONArray) jsonObject.get("greetings");
			Iterator<String> iterator = msg.iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}
			 */
			
			
		}catch (FileNotFoundException e){
			e.printStackTrace();
			System.out.println(filepath);
		}catch (IOException e){
			e.printStackTrace();
			System.out.println(filepath);
		}
		try{
			handler.getPlayer().setName(playerName);
			handler.getWorld().setMap(map);
			handler.getWorld().getMap().setMapName(mapName);
		}catch(NullPointerException e){
			if(playerName == null){
				handler.getPlayer().setName("Default");
			}if(map == null){
				handler.getWorld().setMap("res/worlds/maps/world3.txt");
			}
		}
		if(playerLevel == 0){
			handler.getPlayer().getStats().setLevel(1);
		}else{
			for(int i = 0; i < (int)playerLevel; i++){
				handler.getPlayer().getStats().levelUp();
			}
		}
		handler.getPlayer().getStats().setExperience((int)playerExp);
		handler.getGameCamera().setxOffset((float)xOffset);
		handler.getGameCamera().setyOffset((float)yOffset);
		if(charisma == 0){
			handler.getPlayer().getTraits().levelChar(1);
		}else{
			handler.getPlayer().getTraits().levelChar((int)charisma);
		}if(resilience == 0){
			handler.getPlayer().getTraits().levelRes(1);
		}else{
			handler.getPlayer().getTraits().levelRes((int)resilience);
		}if(strength == 0){
			handler.getPlayer().getTraits().levelStr(1);
		}else{
			handler.getPlayer().getTraits().levelStr((int)strength);
		}if(dexterity == 0){
			handler.getPlayer().getTraits().levelDex(1);
		}else{
			handler.getPlayer().getTraits().levelDex((int)dexterity);
		}if(intelligence == 0){
			handler.getPlayer().getTraits().levelInt(1);
		}else{
			handler.getPlayer().getTraits().levelInt((int)intelligence);
		}
	}
	
	/**
	 * Reads the JSON file containing NPC info and creates the NPC
	 * @param filepath the npc JSON file
	 */
	public static void npcReader(String filepath){
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(new FileReader(filepath));
			JSONObject jsonObject = (JSONObject) obj;
			String name = (String) jsonObject.get("name");
			long level = (Long) jsonObject.get("level");
			JSONArray msg = (JSONArray) jsonObject.get("greetings");
			Iterator<String> iterator = msg.iterator();
			String[] greetings = new String[1];
			int count = 0;
			while (iterator.hasNext()){
				if(count >= greetings.length){
					String[] temp = new String[greetings.length+1];
					System.arraycopy(greetings, 0, temp, 0, greetings.length);
					greetings = temp;
				}
				greetings[count] = iterator.next();
				count++;
			}	
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}catch (ParseException e){
			e.printStackTrace();
		}
		//TODO: Create new NPC object with above values
	}
}
