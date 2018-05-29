package virassan.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import virassan.entities.creatures.npcs.Quest;
import virassan.entities.creatures.player.Player;
import virassan.entities.creatures.utils.Buff;
import virassan.entities.creatures.utils.BuffTracker;
import virassan.entities.creatures.utils.Skill;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.items.Item;
import virassan.main.Handler;

public class SaveRead {

	public static JSONObject loadFile(String filepath){
		try{
			JSONObject jsonObject = (JSONObject)JSONValue.parse(new FileReader(filepath));
			return jsonObject;
		}catch(FileNotFoundException f){
			System.out.println("Error Message: SaveRead_loadFile FILE NOT FOUND " + filepath);
			f.printStackTrace();
		}catch(IOException i){
			System.out.println("Error Message: SaveRead_loadFile IOEXCEPTION " + filepath);
			i.printStackTrace();
		}
		return null;
	}

	public static String loadMapID(JSONObject json){
		try{
			return (String)json.get("map");
		}catch(Exception e){
			System.out.println("Error Message: SaveRead_loadMapID EXCEPTION");
			e.printStackTrace();
			return null;
		}
	}
	
	public static Player loadBasicPlayer(Handler handler, JSONObject jsonObject){
		Player player = new Player(handler);
		long playerLevel = 0, playerExp = 0, 
				charisma = 0, resilience = 0, strength = 0, intelligence = 0, dexterity = 0, 
				x = 0, y = 0;
		String map = null, playerName = null;
		try{
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
		}catch (Exception e){
			System.out.println("Error Message: SaveRead_loadPlayer Something Went Wrong.");
			e.printStackTrace();
		}
		// Set Values
		try{
			player.setName(playerName);
			handler.setMapID(map);
		}catch(NullPointerException e){
			if(playerName.equals("null") || playerName == null){
				player.setName("Default");
				System.out.println("Error Message: SaveRead_loadPlayer playerName is null. Using Default");
			}if(map == null){
				handler.setMapID("haj_vi_01");
				System.out.println("Error Message: SaveRead_loadPlayer map is null. Using Default");
			}
		}
		if(playerLevel == 0){
			player.getStats().setLevel(1);
		}else{
			for(int i = 1; i < (int)playerLevel; i++){
				player.getStats().levelUp();
			}
		}
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
		return player;
	}
	
	public static void loadEquip(Player player, JSONObject jsonObject){
		ArrayList<String> equip = new ArrayList<>();
		try{
			JSONArray arrays = (JSONArray)jsonObject.get("arrays");
			JSONObject eqObj = (JSONObject)arrays.get(0);
			JSONArray eqArray = (JSONArray)eqObj.get("equip");
			for(Object o : eqArray){
				JSONObject obj = (JSONObject)o;
				String item = (String)obj.get("item");
				equip.add(item);
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: SaveRead_loadEquip something is null");
			e.printStackTrace();
		}
		// Set Values
		try{
			for(String str : equip){
				if(!str.equals("")){
					player.getStats().equip(Item.valueOf(str));
				}
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: SaveRead_loadEquip something is null");
			e.printStackTrace();
		}
	}
	
	public static void loadInventory(Player player, JSONObject jsonObject){
		HashMap<String, Long> inventory = new HashMap<>();
		try{
			JSONArray arrays = (JSONArray)jsonObject.get("arrays");
			JSONObject invObj = (JSONObject)arrays.get(1);
			JSONArray invArray = (JSONArray)invObj.get("inventory");
			for(Object o : invArray){
				JSONObject obj = (JSONObject)o;
				String item = (String)obj.get("item");
				Long stack = (Long)obj.get("stack");
				inventory.put(item, stack);
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: SaveRead_loadEquip something is null");
			e.printStackTrace();
		}
		// Set values
		try{
			for(String str : inventory.keySet()){
				player.getInventory().addItems(Item.valueOf(str), inventory.get(str).intValue(), false);
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: SaveRead_loadEquip something is null");
			e.printStackTrace();
		}
	}
	
	public static void loadSkillBar(Player player, JSONObject jsonObject){
		String[] skillBar = new String[5];
		try{
			JSONArray arrays = (JSONArray)jsonObject.get("arrays");
			JSONObject skillbarObj = (JSONObject)arrays.get(2);
			JSONArray skillbarArray = (JSONArray)skillbarObj.get("skillbar");
			for(Object o : skillbarArray){
				JSONObject obj = (JSONObject)o;
				String skill = (String)obj.get("skill");
				Long index = (Long)obj.get("index");
				skillBar[index.intValue()] = skill;
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: SaveRead_loadEquip something is null");
			e.printStackTrace();
		}
		// Set Values
		for(int i = 0; i < skillBar.length; i++){
			if(!skillBar[i].equals("")){
				for(SkillTracker skill : player.getSkills()){
					if(skill.getSkillType() == Skill.valueOf(skillBar[i])){
						player.setSkillBar(skill, i);
					}
				}
			}
		}
	}
	
	public static void loadQuests(Player player, JSONObject jsonObject){
		HashMap<String, Boolean> questIDs = new HashMap<>();
		try{
			JSONArray arrays = (JSONArray)jsonObject.get("arrays");
			JSONObject questObj = (JSONObject)arrays.get(3);
			JSONArray questArray = (JSONArray)questObj.get("quests");
			for(Object o : questArray){
				JSONObject obj = (JSONObject)o;
				String quest = (String)obj.get("quest_id");
				boolean b = (boolean)obj.get("quest_bool");
				questIDs.put(quest, b);
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: SaveRead_loadEquip something is null");
			e.printStackTrace();
		}
		// Set Values
		HashMap<Quest, Boolean> quests = new HashMap<>();
		for(String id : questIDs.keySet()){
			quests.put(DatabaseConnect.getQuest(player.getHandler(), id), questIDs.get(id));
		}
		player.getQuestLog().addQuests(quests);
	}
	
	public static void loadBuffs(Player player, JSONObject jsonObject){
		HashMap<String, Long> buffs = new HashMap<>();
		try{
			JSONArray arrays = (JSONArray)jsonObject.get("arrays");
			JSONObject buffObj = (JSONObject)arrays.get(4);
			JSONArray buffArray = (JSONArray)buffObj.get("buffs");
			for(Object o : buffArray){
				JSONObject obj = (JSONObject)o;
				String buff = (String)obj.get("buff");
				Long buffTime = (Long)obj.get("buff_time");
				buffs.put(buff, buffTime);
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: SaveRead_loadEquip something is null");
			e.printStackTrace();
		}
		// Set Values
		for(String str : buffs.keySet()){
			player.getStats().addBuff(new BuffTracker(player, Buff.valueOf(str)));
		}
	}
	
	public static void loadSkills(Player player, JSONObject jsonObject){
		ArrayList<String> skills = new ArrayList<>();
		try{
			JSONArray arrays = (JSONArray)jsonObject.get("arrays");
			JSONObject skillObj = (JSONObject)arrays.get(5);
			JSONArray skillArray = (JSONArray)skillObj.get("skills");
			for(Object o : skillArray){
				JSONObject obj = (JSONObject)o;
				String skill = (String)obj.get("skill");
				skills.add(skill);
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: SaveRead_loadEquip something is null");
			e.printStackTrace();
		}
		// Set Values
		for(String str : skills){
			player.addSkill(new SkillTracker(player, Skill.valueOf(str), player.getAnimation()));
		}
	}
	
	public static void loadNPCs(Player player, JSONObject jsonObject){
		HashMap<String, ArrayList<Boolean>> npcHash = new HashMap<>();
		try{
			JSONArray arrays = (JSONArray)jsonObject.get("arrays");
			JSONObject npcObj = (JSONObject)arrays.get(6);
			JSONArray npcArray = (JSONArray)npcObj.get("npcs");
			for(Object o : npcArray){
				JSONObject obj = (JSONObject)o;
				String npc = (String)obj.get("id");
				boolean met = (boolean)obj.get("met");
				boolean liked = (boolean)obj.get("liked");
				npcHash.put(npc, new ArrayList<Boolean>(Arrays.asList(met, liked)));
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: SaveRead_loadEquip something is null");
			e.printStackTrace();
		}
		// Set Values
		player.setNPCMetLiked(npcHash);
	}
	
}
