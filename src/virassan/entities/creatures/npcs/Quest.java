package virassan.entities.creatures.npcs;

import java.util.ArrayList;
import java.util.HashMap;

import virassan.entities.creatures.enemies.Enemy;
import virassan.entities.creatures.enemies.EnemySpecies;
import virassan.entities.creatures.enemies.EnemyType;
import virassan.entities.creatures.player.Player;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.utils.Utils;

public class Quest {

	private final HashMap<Object, Object> reqs;
	private final HashMap<Object, Integer> rewards;
	private final String name, desc;
	private final String QUEST_ID;
	private final ArrayList<String> npcIDs;
	private final String startDia, incompDia, compDia;
	private Handler handler;
	
	private HashMap<EnemySpecies, Integer> speciesCounts;
	private HashMap<String, Integer> enemyIDCounts;
	
	/**
	 * Creates a Quest object
	 * @param handler
	 * @param ID
	 * @param name
	 * @param description
	 * @param requirements
	 * @param rewards
	 * @param npcIDs
	 * @param sDia - Contains the DialogID for the Quest's starting dialog
	 * @param iDia - Contains actual text to display as dialog if quest is active but incomplete
	 * @param cDia - Contains actual text to display as dialog if quest is completed (not previously complete, but "turned in")
	 */
	public Quest(Handler handler, String ID, String name, String description, HashMap<Object, Object> requirements, HashMap<Object, Integer> rewards, ArrayList<String> npcIDs, String sDia, String iDia, String cDia) {
		QUEST_ID = ID;
		this.handler = handler;
		this.name = name;
		desc = description;
		reqs = requirements;
		this.rewards = rewards;
		this.npcIDs = npcIDs;
		startDia = sDia;
		incompDia = iDia;
		compDia = cDia;
	}

	public void start(Player player){
		enemyIDCounts = new HashMap<>();
		speciesCounts = new HashMap<>();
		for(Object obj : reqs.keySet()){
			if(obj instanceof String){
				if(handler.getPlayer().getKillEnemyID().containsKey((String)obj)){
					enemyIDCounts.put((String)obj, handler.getPlayer().getKillEnemyID().get((String)obj));
				}else{
					enemyIDCounts.put((String)obj, 0);
				}
			}else if(obj instanceof EnemySpecies){
				if(handler.getPlayer().getKillEnemySpecies().containsKey((EnemySpecies)obj)){
					speciesCounts.put((EnemySpecies)obj, handler.getPlayer().getKillEnemySpecies().get((EnemySpecies)obj));
				}else{
					speciesCounts.put((EnemySpecies)obj, 0);
				}
			}
		}
	}
	
	public void complete(){
		if(!giveRewards()){
			System.out.println("Error Message: Quest_complete error with giveRewards()");
		}
		if(!removeItems()){
			System.out.println("Error Message: Quest_complete error with removeItems()");
		}
	}
	
	public boolean removeItems(){
		try{
			for(Object o : reqs.keySet()){
				if(o instanceof Item){
					System.out.println("Message: Quest_removeItems item amount: " + (int)reqs.get(o));
					handler.getPlayer().getInventory().removeItems((Item)o, (int)reqs.get(o));
				}
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Gives the Quest rewards out to the Player
	 * Does NOT check if the Quest requirements are met
	 */
	//TODO: Later make allowances for choosing between reward items
	public boolean giveRewards(){
		try{
			for(Object o : rewards.keySet()){
				if(o instanceof String){
					int amount = (int)rewards.get(o);
					switch((String)o){
					case "health": handler.getPlayer().getStats().heal(amount);break;
					case "exp": handler.getPlayer().getStats().addExperience(amount);break;
					case "gold": handler.getPlayer().addGold(amount);break;
					}
				}else if(o instanceof Item){
					handler.getPlayer().getInventory().addItems((Item)o, (int)rewards.get(o), true);
				}
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Checks if all quest Requirements are met
	 * @return
	 */
	public boolean isAllReqMet(){
		if(reqs.isEmpty()){
			if(npcIDs.get(1).equals(handler.getPlayer().getCurrentNPC().getNPCID())){
				if(handler.getPlayer().getCurrentNPC().getCurrentDialog().equals(compDia)){
					return true;
				}
			}
			return false;
		}else{
			for(Object o : reqs.keySet()){
				if(isReqMet(o) == false){
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Returns true if the specific requirement is fully met
	 * @param questReq
	 * @return
	 */
	public boolean isReqMet(Object questReq){
		if(questReq instanceof Item){
			if(handler.getPlayer().getInventory().getItems().contains((Item)questReq)){
				if(handler.getPlayer().getInventory().getItemMap().get((Item)questReq) >= (int)reqs.get(questReq)){
					return true;
				}
			}
		}else if(questReq instanceof String){
			if(handler.getPlayer().getKillEnemyID().containsKey((String)questReq)){
				if(getCurAmt(questReq) == (int)reqs.get(questReq)){
					return true;
				}
			}
		}else if(questReq instanceof EnemySpecies){
			if(handler.getPlayer().getKillEnemySpecies().containsKey((EnemySpecies)questReq)){
				if(getCurAmt(questReq) == (int)reqs.get(questReq)){
					return true;
				}
			}
		}else if(npcIDs.size() > 1){
			if(handler.getPlayer().getCurrentNPC().getNPCID().equals(npcIDs.get(1))){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the Player's current amount completed of a requirement
	 * @param questReq The Quest requirement
	 * @return current amount
	 */
	public int getCurAmt(Object questReq){
		if(reqs.get(questReq) instanceof Integer){
			if(questReq instanceof Item){
				if(handler.getPlayer().getInventory().getItems().contains((Item)questReq)){
					return Utils.clamp(handler.getPlayer().getInventory().getItemMap().get((Item)questReq), 0, (int)reqs.get(questReq));
				}
			}else if(questReq instanceof String){
				if(handler.getPlayer().getKillEnemyID().containsKey((String)questReq)){
					return Utils.clamp(Utils.positive(enemyIDCounts.get(questReq) - handler.getPlayer().getKillEnemyID().get((String)questReq)), 0, handler.getPlayer().getKillEnemyID().get((String)questReq));
				}
			}else if(questReq instanceof EnemySpecies){
				if(handler.getPlayer().getKillEnemySpecies().containsKey((EnemySpecies)questReq)){
					return Utils.clamp(Utils.positive(speciesCounts.get(questReq) - handler.getPlayer().getKillEnemySpecies().get((EnemySpecies)questReq)), 0, handler.getPlayer().getKillEnemySpecies().get((EnemySpecies)questReq));
				}
			}
		}
		return 0;
	}
	
	/**
	 * Checks if this Quest is Active
	 * @return Returns true if the Quest is Active, false if not.
	 */
	public boolean isActive(){
		for(Quest q : handler.getPlayer().getQuestLog().getActiveQuests()){
			if(q.getQUEST_ID().equals(QUEST_ID)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if this Quest has already been Completed Previously
	 * @return Returns true if it has been completed, false if not.
	 */
	public boolean getComplete(){
		for(Quest q : handler.getPlayer().getQuestLog().getCompletedQuests()){
			if(QUEST_ID == q.getQUEST_ID()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the player currently has the quest with the quest_id passed, and the status of that quest and then executes the proper dialog.
	 * @param quest_id
	 */
	public String questDialog(){
		String dialog = "";
		if(handler.getPlayer().getQuestLog().isQuest(QUEST_ID)){
			if(isAllReqMet()){
				dialog = compDia;
				if(!handler.getPlayer().getQuestLog().completeQuest(this)){
					System.out.println("Error Message: Quest_questDialog quest not completed: " + name);
				}
			}else{
				dialog = incompDia;
			}
		}
		return dialog;
	}
	
	//GETTERS
	
	public HashMap<Object, Object> getReqs() {
		return reqs;
	}

	public HashMap<Object, Integer> getRewards() {
		return rewards;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public String getQUEST_ID() {
		return QUEST_ID;
	}

	public ArrayList<String> getNpcIDs() {
		return npcIDs;
	}

	public String getStartDia() {
		return startDia;
	}

	public String getIncompDia() {
		return incompDia;
	}

	public String getCompDia() {
		return compDia;
	}
	
}
