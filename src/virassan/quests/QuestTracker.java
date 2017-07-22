package virassan.quests;

import java.util.HashMap;

import virassan.entities.creatures.enemies.EnemySpecies;
import virassan.entities.creatures.enemies.EnemyType;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.utils.Utils;

/**
 * Tracks the Player's progression of a Quest
 * @author Virassan
 *
 */
public class QuestTracker {

	public static Handler handler;
	private Quest quest;
	private HashMap<Object, Integer> reqs;
	private HashMap<EnemyType, Integer> enemyCount;
	private HashMap<EnemySpecies, Integer> speciesCount;
	// Object = int amount of experience or health or item, String = "item", "exp", "health"
	private HashMap<Object, String> reward;
	private boolean isComplete;
	
	public QuestTracker(Quest quest) {
		this.quest = quest;
		isComplete = false;
		reqs = new HashMap<Object, Integer>();
		enemyCount = new HashMap<EnemyType, Integer>();
		speciesCount = new HashMap<>();
		for(Object key : quest.getHashMap().keySet()){
			reqs.put(key, 0);
			if(key instanceof EnemyType){
				enemyCount.put((EnemyType)key, 0);
			}else if(key instanceof EnemySpecies){
				speciesCount.put((EnemySpecies)key, 0);
			}
		}
		reward = quest.getRewards();
	}
	
	public HashMap<Object, String> getRewardsList(){
		return reward;
	}
	
	/**
	 * Gives the Quest rewards out to the Player
	 * Does NOT check if the Quest requirements are met
	 */
	//TODO: Later make allowances for choosing between reward items
	public void giveRewards(){
		for(Object o : reward.keySet()){
			if(o instanceof Integer){
				int amount = (Integer)o;
				switch(reward.get(o)){
				case "health": handler.getPlayer().getStats().heal(amount);break;
				case "exp": handler.getPlayer().getStats().addExperience(amount);break;
				case "gold": handler.getPlayer().addGold(amount);break;
				}
			}else if(o instanceof Item){
				handler.getPlayer().getInventory().addItems((Item)o, true);
			}
		}
	}
	
	/**
	 * Returns true if the specific requirement is fully met for the Quest
	 * @param questReq
	 * @return
	 */
	public boolean isReqMet(Object questReq){
		boolean b = false;
		if(questReq instanceof Item){
			if(handler.getPlayer().getInventory().getItems().contains((Item)questReq)){
				reqs.replace((Item)questReq, Utils.clamp(handler.getPlayer().getInventory().getItemMap().get((Item)questReq), 0, quest.getHashMap().get(questReq)));
			}
		}else if(questReq instanceof EnemyType){
			reqs.replace((EnemyType)questReq, Utils.clamp(enemyCount.get((EnemyType)questReq), 0, quest.getHashMap().get((EnemyType)questReq)));
		}else if(questReq instanceof EnemySpecies){
			//TODO: DO THIS THING
			reqs.replace((EnemySpecies)questReq, Utils.clamp(speciesCount.get((EnemySpecies)questReq), 0, quest.getHashMap().get((EnemySpecies)questReq)));
		}
		if(reqs.containsKey(questReq)){
			if(reqs.get(questReq) >= quest.getReqAmt(questReq)){
				b = true;
			}
		}
		return b;
	}
	
	/**
	 * Returns the Player's current amount completed of a requirement
	 * @param questReq The Quest requirement
	 * @return current amount
	 */
	public int getCurAmt(Object questReq){
		isReqMet(questReq);
		return reqs.get(questReq);
	}
	
	/**
	 * Adds 1 to the Player's current amount of a species killed since the start of the Quest
	 * @param species The species the player has slain
	 * @return The current amount of that species that the player has slain for this quest
	 */
	public int addEnemyCount(EnemySpecies species){
		speciesCount.replace(species, speciesCount.get(species) + 1);
		return speciesCount.get(species);
	}
	
	/**
	 * Adds 1 to the Player's current amount of an enemy type that the Player has slain
	 * @param enemy Type of enemy to increment
	 * @return The player's current amount of slain enemies of this type
	 */
	public int addEnemyCount(EnemyType enemy){
		enemyCount.replace(enemy, enemyCount.get(enemy) + 1);
		return enemyCount.get(enemy);
	}
	
	/**
	 * Checks if this Quest is Active
	 * @return Returns true if the Quest is Active, false if not.
	 */
	public boolean isActive(){
		for(QuestTracker q : handler.getPlayer().getQuestLog().getActive()){
			if(quest == q.getQuest()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if this Quest has been Completed Previously
	 * @return Returns true if it has been completed, false if not.
	 */
	public boolean getComplete(){
		for(QuestTracker q : handler.getPlayer().getQuestLog().getComplete()){
			if(quest == q.getQuest()){
				isComplete = true;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the Quest is currently Complete, using isReqMet() to check if all Quest requirements are met.
	 * @return Returns true if all Quest requirements are met, false if not.
	 */
	public boolean isComplete(){
		for(Object o : reqs.keySet()){
			boolean b = isReqMet(o);
			if(!b){
				isComplete = false;
				return isComplete;
			}
		}
		isComplete = true;
		return isComplete;
	}
	
	/**
	 * Returns -1 if the quest doesn't have that requirement
	 * @param req the requirement wanted to find the amount of
	 * @return amount so far that the player has accomplished
	 */
	public int getReqAmt(Object req){
		if(reqs.containsKey(req)){
			if(req instanceof Item){
				return Utils.clamp(handler.getPlayer().getInventory().getItemMap().get((Item)req), 0, quest.getHashMap().get(req));
			}else if(req instanceof EnemyType){
				return Utils.clamp(enemyCount.get(req), 0, quest.getHashMap().get(req));
			}else if(req instanceof EnemySpecies){
				return Utils.clamp(speciesCount.get(req), 0, quest.getHashMap().get(req));
			}else{
				///TODO: Add other types of quest requirements here bro
			}
			return reqs.get(req);
		}
		return -1;
	}
	
	/**
	 * Sets the current progress amount of a Quest requirement - for loading from a save.
	 * @param req The Quest requirement object
	 * @param amount The amount completed
	 */
	public void setReqAmt(Object req, int amount){
		if(reqs.containsKey(req)){
			reqs.replace(req, amount);
			if(req instanceof EnemyType){
				enemyCount.put((EnemyType)req, amount);
			}else if(req instanceof EnemySpecies){
				speciesCount.put((EnemySpecies)req, amount);
			}
		}
	}
	
	public HashMap<EnemySpecies, Integer> getSpeciesList(){
		return speciesCount;
	}
	
	public HashMap<EnemyType, Integer> getEnemyList(){
		return enemyCount;
	}
	
	public int getSpeciesCount(EnemySpecies species){
		if(speciesCount.containsKey(species)){
			return speciesCount.get(species);
		}
		return -1;
	}
	
	public int getEnemyCount(EnemyType enemy){
		if(enemyCount.containsKey(enemy)){
			return enemyCount.get(enemy);
		}
		return -1;
	}
	
	public Quest getQuest(){
		return quest;
	}
	
	public String toString(){
		return quest.toString();
	}
}
