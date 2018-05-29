package virassan.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import virassan.entities.creatures.enemies.EnemyType;
import virassan.entities.creatures.npcs.Quest;
import virassan.entities.creatures.player.Player;
import virassan.entities.creatures.utils.BuffTracker;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.items.Equip;
import virassan.items.Item;

/**
 * Save is the class that will hold all necessary Save data that can then be Serialized and Written to a file (elsewhere)
 * @author Virassan
 *
 */
public class Save {

	private int level, currentExp;
	private String name, mapID;
	private float x, y, health, mana, stamina, gold;
	
	private int charisma, strength, resilience, dexterity, intelligence;
	
	private HashMap<String, Boolean> quests;
	
	private ArrayList<SkillTracker> skillList;
	private SkillTracker[] skillBar;
	
	//private CopyOnWriteArrayList<BuffTracker> buffs;
	
	private HashMap<EnemyType, Integer> killList;
	
	// Retrieved via Player's Stats
	private HashMap<Equip, Item> equips;
	private HashMap<Item, Integer> items;

	public Save(Player player) {
		//TODO: finish this
		
		// BASIC INFO
		level = player.getStats().getLevel();
		gold = player.getGold();
		currentExp = player.getStats().getExperience();
		name = player.getName();
		mapID = player.getHandler().getMapID();
		x = player.getX();
		y = player.getY();
		health = player.getStats().getHealth();
		mana = player.getStats().getMana();
		
		// TRAITS
		stamina = player.getStats().getStamina();
		charisma = player.getTraits().getCharisma();
		strength = player.getTraits().getStrength();
		resilience = player.getTraits().getResilience();
		dexterity = player.getTraits().getDexterity();
		intelligence = player.getTraits().getIntelligence();
		
		// QUESTS
		quests = new HashMap<>();
		for(Quest q : player.getQuestLog().getAllQuests().keySet()){
			quests.put(q.getQUEST_ID(), player.getQuestLog().getAllQuests().get(q));
		}
		
		// ITEMS
		equips = player.getStats().getEquip();
		items = player.getInventory().getItemMap();
		
	}

	
	
}
