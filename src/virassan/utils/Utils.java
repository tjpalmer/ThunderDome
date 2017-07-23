package virassan.utils;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import virassan.entities.creatures.enemies.EnemySpecies;
import virassan.entities.creatures.enemies.EnemyType;
import virassan.entities.creatures.player.Player;
import virassan.entities.creatures.utils.Buff;
import virassan.entities.creatures.utils.BuffTracker;
import virassan.entities.creatures.utils.Skill;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.items.Equip;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.quests.Quest;
import virassan.quests.QuestTracker;
import virassan.world.World;

/**
 * Nifty Utility Stuff
 * @author Virassan
 *
 */
public class Utils {
	
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
	
	public static Map toMap(Item item){
		HashMap<String, String> map = new HashMap<>();
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
		//TODO: later add Player's Image
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
		obj.put("x", new Integer((int)handler.getPlayer().getX()));
		obj.put("y", new Integer((int)handler.getPlayer().getY()));
		obj.put("map", handler.getMap().getFilepath());
		JSONArray stuff = new JSONArray();
		
		// EQUIP
		JSONObject equips = new JSONObject();
		JSONArray equip = new JSONArray();
		for(Equip slot : player.getStats().getEquip().keySet()){
			equip.add(toMap(player.getStats().getEquip().get(slot)));
		}
		equips.put("equip", equip);
		stuff.add(equips);
		
		// INVENTORY
		JSONObject inv = new JSONObject();
		JSONArray inventory = new JSONArray();
		for(Item item : player.getInventory().getItemMap().keySet()){
			inventory.add(toMap(item, player.getInventory().getItemMap().get(item)));
		}
		inv.put("inventory", inventory);
		stuff.add(inv);
		
		// SKILL BAR
		JSONObject skillbar = new JSONObject();
		JSONArray skillBar = new JSONArray();
		for(int i = 0; i < player.getSkillBar().length; i++){
			skillBar.add(toMap(player.getSkillBar()[i], i));
		}
		skillbar.put("skillbar", skillBar);
		stuff.add(skillbar);
		
		// ACTIVE QUESTS
		JSONObject aQuests = new JSONObject();
		JSONArray activeQuests = new JSONArray();
		for(QuestTracker quest : player.getQuestLog().getActive()){
			activeQuests.add(toMap(quest));
			JSONObject reqObj = new JSONObject();
			JSONArray reqs = new JSONArray();
			for(Object req : quest.getQuest().getHashMap().keySet()){
				reqs.add(toMap(quest.getCurAmt(req), req));
			}
			reqObj.put("reqs", reqs);
			activeQuests.add(reqObj);
		}
		aQuests.put("active_quests", activeQuests);
		stuff.add(aQuests);
		
		// COMPLETED QUESTS
		JSONObject cQuests = new JSONObject();
		JSONArray completedQuests = new JSONArray();
		for(QuestTracker quest : player.getQuestLog().getComplete()){
			completedQuests.add(toMap(quest));
		}
		cQuests.put("completed_quests", completedQuests);
		stuff.add(cQuests);
		
		// BUFFS
		JSONObject bList = new JSONObject();
		JSONArray buffs = new JSONArray();
		for(BuffTracker buff : player.getStats().getBuffs()){
			if(buff.getBuff() != null){
				buffs.add(toMap(buff));
			}
		}
		bList.put("buffs", buffs);
		stuff.add(bList);
		
		// SKILL LIST
		JSONObject sList = new JSONObject();
		JSONArray skills = new JSONArray();
		for(SkillTracker skill : player.getSkills()){
			skills.add(toMap(skill));
		}
		sList.put("skills", skills);
		stuff.add(sList);
		obj.put("arrays", stuff);
		//TODO: add itemmanager items array, player kill list
	
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
		//TODO: instead of setting variables for the Player - create a NEW Player object with these values
		Player player = new Player(handler, ID.Player);
		handler.setWorld(new World(handler, player));
		JSONObject jsonObject;
		//TODO: later add Player's Image
		long playerLevel = 0, playerExp = 0, charisma = 0, resilience = 0, strength = 0, intelligence = 0, dexterity = 0, x = 0, y = 0;
		String map = null, playerName = null;
		String[] skillBar = new String[5];
		HashMap<String, Long> inventory = new HashMap<>();
		ArrayList<String> equip = new ArrayList<>();
		HashMap<String, Long> buffs = new HashMap<>();
		HashMap<String, HashMap<String, Long>> activeQuests = new HashMap<>();
		ArrayList<String> completedQuests = new ArrayList<>();
		ArrayList<String> skills = new ArrayList<>();
		try{
			jsonObject = (JSONObject)JSONValue.parse(new FileReader(filepath));
			playerName = (String)jsonObject.get("player_name");
			playerLevel = (Long)jsonObject.get("player_level");
			playerExp = (Long)jsonObject.get("player_exp");
			charisma = (Long)jsonObject.get("char");
			resilience = (Long)jsonObject.get("res");
			strength = (Long)jsonObject.get("str");
			intelligence = (Long)jsonObject.get("int");
			dexterity = (Long)jsonObject.get("dex");
			x = (Long)jsonObject.get("x");
			y = (Long)jsonObject.get("y");
			map = (String)jsonObject.get("map");
			JSONArray arrays = (JSONArray)jsonObject.get("arrays");
			for(int i = 0; i < arrays.size(); i++){
				switch(i){
				case 0: 
					JSONObject eqObj = (JSONObject)arrays.get(i);
					JSONArray eqArray = (JSONArray)eqObj.get("equip");
					for(Object o : eqArray){
						JSONObject obj = (JSONObject)o;
						String item = (String)obj.get("item");
						equip.add(item);
					}
					break;
				case 1:
					JSONObject invObj = (JSONObject)arrays.get(i);
					JSONArray invArray = (JSONArray)invObj.get("inventory");
					for(Object o : invArray){
						JSONObject obj = (JSONObject)o;
						String item = (String)obj.get("item");
						Long stack = (Long)obj.get("stack");
						inventory.put(item, stack);
					}
					break;
				case 2:
					JSONObject skillbarObj = (JSONObject)arrays.get(i);
					JSONArray skillbarArray = (JSONArray)skillbarObj.get("skillbar");
					for(Object o : skillbarArray){
						JSONObject obj = (JSONObject)o;
						String skill = (String)obj.get("skill");
						Long index = (Long)obj.get("index");
						skillBar[index.intValue()] = skill;
					}
					break;
				case 3:
					HashMap<String, Long> reqs = new HashMap<>();
					JSONObject activeObj = (JSONObject)arrays.get(i);
					JSONArray activeArray = (JSONArray)activeObj.get("active_quests");
					String prev = "";
					for(Object o : activeArray){
						if(((JSONObject)o).get("quest") != null){
							String quest = (String)((JSONObject)o).get("quest");
							prev = quest;
						}else if(((JSONObject)o).get("reqs") != null){
							JSONArray reqArray = (JSONArray)((JSONObject)o).get("reqs");
							for(Object array : reqArray){
								JSONObject arrayObj = (JSONObject)array;
								String req = (String)arrayObj.get("req");
								Long amount = (Long)arrayObj.get("amt");
								reqs.put(req, amount);
							}
							activeQuests.put(prev, reqs);
						}
					}
					break;
				case 4:
					JSONObject completeObj = (JSONObject)arrays.get(i);
					JSONArray completeArray = (JSONArray)completeObj.get("completed_quests");
					for(Object o : completeArray){
						JSONObject obj = (JSONObject)o;
						String quest = (String)obj.get("quest");
						completedQuests.add(quest);
					}
					break;
				case 5:
					JSONObject buffObj = (JSONObject)arrays.get(i);
					JSONArray buffArray = (JSONArray)buffObj.get("buffs");
					for(Object o : buffArray){
						JSONObject obj = (JSONObject)o;
						String buff = (String)obj.get("buff");
						Long buffTime = (Long)obj.get("buff_time");
						buffs.put(buff, buffTime);
					}
					break;
				case 6:
					JSONObject skillObj = (JSONObject)arrays.get(i);
					JSONArray skillArray = (JSONArray)skillObj.get("skills");
					for(Object o : skillArray){
						JSONObject obj = (JSONObject)o;
						String skill = (String)obj.get("skill");
						skills.add(skill);
					}
					break;
				}
			}
		}catch (IOException e){
			e.printStackTrace();
			System.out.println(filepath);
		}
		try{
			player.setName(playerName);
			Handler.WORLD.setMap(map);
		}catch(NullPointerException e){
			if(playerName == null){
				player.setName("Default");
				System.out.println("----in loadGame playerName is null. Using Default");
			}if(map == null){
				Handler.WORLD.setMap("res/worlds/maps/hajime_village.json");
				System.out.println("----in loadGame map is null. Using Default");
			}
		}
		if(playerLevel == 0){
			player.getStats().setLevel(1);
		}else{
			for(int i = 1; i < (int)playerLevel; i++){
				player.getStats().levelUp();
			}
		}
		System.out.println("Player is: " + handler.getPlayer());
		player.getStats().setExperience((int)playerExp);
		player.setX((float)x);
		player.setY((float)y);
		if(charisma == 0){
			player.getTraits().levelChar(1);
		}else{
			player.getTraits().levelChar((int)charisma);
		}if(resilience == 0){
			player.getTraits().levelRes(1);
		}else{
			player.getTraits().levelRes((int)resilience);
		}if(strength == 0){
			player.getTraits().levelStr(1);
		}else{
			player.getTraits().levelStr((int)strength);
		}if(dexterity == 0){
			player.getTraits().levelDex(1);
		}else{
			player.getTraits().levelDex((int)dexterity);
		}if(intelligence == 0){
			player.getTraits().levelInt(1);
		}else{
			player.getTraits().levelInt((int)intelligence);
		}
		for(String str : skills){
			player.addSkill(new SkillTracker(player, Skill.valueOf(str), player.getAnimation()));
		}
		for(int i = 0; i < skillBar.length; i++){
			if(!skillBar[i].equals("")){
				for(SkillTracker skill : player.getSkills()){
					if(skill.getSkillType() == Skill.valueOf(skillBar[i])){
						player.setSkillBar(skill, i);
					}
				}
			}
		}
		for(String str : completedQuests){
			player.getQuestLog().addComplete(new QuestTracker(Quest.valueOf(str)));
		}
		for(String str : buffs.keySet()){
			player.getStats().addBuff(new BuffTracker(player, Buff.valueOf(str)));
		}
		for(String str : inventory.keySet()){
			player.getInventory().addItems(Item.valueOf(str), inventory.get(str).intValue(), false);
		}
		for(String str : equip){
			if(!str.equals("")){
				player.getStats().equip(Item.valueOf(str));
			}
		}
		for(String str : activeQuests.keySet()){
			QuestTracker quest = new QuestTracker(Quest.valueOf(str));
			for(String r : activeQuests.get(str).keySet()){
				Object test;
				try{
					test = EnemyType.valueOf(r);
				}catch(IllegalArgumentException e){
					try{
						test = EnemySpecies.valueOf(r);
					}catch(IllegalArgumentException a){
						try{
							test = Item.valueOf(r);
						}catch(IllegalArgumentException k){
							System.out.println("Cannot Find ENUM");
							test = null;
							k.printStackTrace();
						}
					}
				}
				if(test != null){
					quest.setReqAmt(test, activeQuests.get(str).get(r).intValue());
				}
			}
		}
		
		
	}
}
