package virassan.quests;

import java.util.ArrayList;
import java.util.HashMap;

import virassan.entities.creatures.enemies.Enemy;
import virassan.entities.creatures.enemies.EnemySpecies;
import virassan.entities.creatures.enemies.EnemyType;
import virassan.entities.creatures.enemies.Soldier;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.utils.Utils;

public class QuestTracker {

	public static Handler handler;
	private Quest quest;
	private HashMap<Object, Integer> reqs;
	private HashMap<EnemyType, Integer> enemyCount;
	private HashMap<EnemySpecies, Integer> speciesCount;
	// Object = int amount of experience or health or item, String = "item", "exp", "health"
	private HashMap<Object, String> reward;
	private boolean isComplete;
	private boolean isActive;
	
	public QuestTracker(Quest quest) {
		this.quest = quest;
		isComplete = false;
		isActive = false;
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
				handler.getPlayer().getInventory().addItems((Item)o);
			}
		}
	}
	
	/**
	 * Returns true if the specific requirement is met for the Quest
	 * For use in checking off items in a todo list (in game)
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
		}
		//TODO: Search through hashmap requirements for the argument and then check if the # are the same
		if(reqs.containsKey(questReq)){
			if(reqs.get(questReq) >= quest.getReqAmt(questReq)){
				b = true;
			}
		}
		return b;
	}
	
	public int getCurAmt(Object questReq){
		isReqMet(questReq);
		return reqs.get(questReq);
	}
	
	public int addEnemyCount(EnemySpecies species){
		speciesCount.replace(species, speciesCount.get(species) + 1);
		return speciesCount.get(species);
	}
	
	public int addEnemyCount(EnemyType enemy){
		enemyCount.replace(enemy, enemyCount.get(enemy) + 1);
		return enemyCount.get(enemy);
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
	
	public boolean isActive(){
		if(handler.getPlayer().getQuestLog().getActive().contains(this)){
			isActive = true;
		}else{
			isActive = false;
		}
		return isActive;
	}
	
	public boolean getComplete(){
		for(QuestTracker q : handler.getPlayer().getQuestLog().getComplete()){
			if(quest == q.getQuest()){
				return true;
			}
		}
		return false;
	}
	
	public boolean isComplete(){
		ArrayList<Boolean> temp = new ArrayList<Boolean>();
		for(Object o : reqs.keySet()){
			temp.add(isReqMet(o));
		}
		isComplete = !temp.contains(false);
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
	
	public String toString(){
		return quest.toString();
	}
}
