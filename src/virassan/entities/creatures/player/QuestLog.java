package virassan.entities.creatures.player;

import java.util.ArrayList;
import java.util.HashMap;

import virassan.entities.creatures.npcs.Quest;
import virassan.utils.DatabaseConnect;

public class QuestLog {

	private Player player;
	private HashMap<Quest, Boolean> allQuests;
	//TODO: Saves don't save the whole quest, just the questID and boolean and turns questIDs into Quests on load up
	
	public QuestLog(Player player) {
		this.player = player;
		allQuests = new HashMap<>();
	}

	/**
	 * Replaces all player quest data with the hashmap passed
	 * @param quests
	 * @return
	 */
	public boolean addQuests(HashMap<Quest, Boolean> quests){
		allQuests = quests;
		return true;
	}
	
	public boolean addQuest(String questID){
		return addQuest(DatabaseConnect.getQuest(player.getHandler(), questID));
	}
	
	public boolean addQuest(Quest quest){
		try{
			allQuests.put(quest, false);
			quest.start(player);
			System.out.println("Update Message: QuestLog_addQuest Quest added: " + quest.getName());
			return true;
		}catch(Exception e){
			System.out.println("Error Message: QuestLog_addQuest Error with adding Quest: " + quest.getName());
			e.printStackTrace();
			return false;
		}
	}
	
	public void addQuest(ArrayList<Quest> quests){
		for(Quest q : quests){
			addQuest(q);
		}
	}
	
	public void setAllQuests(HashMap<Quest, Boolean> allquests){
		this.allQuests = allquests;
	}
	
	public boolean completeQuest(String questID){
		return completeQuest(getActiveQuest(questID));
	}
	
	/**
	 * Moves the quest from the active list, to the completed list.
	 * @param quest the quest object that has been completed
	 */
	public boolean completeQuest(Quest quest){
		if(quest.isAllReqMet()){
			for(Quest q : getActiveQuests()){
				if(quest.getQUEST_ID().equals(q.getQUEST_ID())){ //checks that quest in questlog is same as the quest passed
					quest.complete(); //gives rewards, removes necessary items, etc
					allQuests.replace(q, true); //changes from active to completed
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList<String> getCompletedQuestIDs(){
		ArrayList<String> completed = new ArrayList<>();
		for(Quest q : getCompletedQuests()){
			completed.add(q.getQUEST_ID());
		}
		return completed;
	}
	
	public ArrayList<Quest> getCompletedQuests(){
		ArrayList<Quest> completedQuests = new ArrayList<>();
		for(Quest q : allQuests.keySet()){
			if(allQuests.get(q) == true){
				completedQuests.add(q);
			}
		}
		return completedQuests;
	}
	
	public ArrayList<String> getActiveQuestIDs(){
		ArrayList<String> active = new ArrayList<>();
		for(Quest q : getActiveQuests()){
			active.add(q.getQUEST_ID());
		}
		return active;
	}
	
	public boolean isActiveQuest(String questID){
		if(getActiveQuestIDs().contains(questID)){
			return true;
		}
		return false;
	}
	
	public ArrayList<Quest> getActiveQuests(){
		ArrayList<Quest> activeQuests = new ArrayList<>();
		for(Quest q : allQuests.keySet()){
			if(allQuests.get(q) == false){
				activeQuests.add(q);
			}
		}
		return activeQuests;
	}
	
	public ArrayList<String> getAllQuestIDs(){
		ArrayList<String> quests = new ArrayList<>();
		quests.addAll(getActiveQuestIDs());
		quests.addAll(getCompletedQuestIDs());
		return quests;
	}
	
	public HashMap<Quest, Boolean> getAllQuests(){
		return allQuests;
	}
	
	/**
	 * Searches quests in questlog and returns whether the quest is complete or not
	 * @param questID
	 * @return returns true if the quest is completed, returns false if the quest is still active/incomplete
	 */
	public boolean isQuestComplete(String questID){
		for(String q : getCompletedQuestIDs()){
			if(q.equals(questID)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isQuest(String questID){
		ArrayList<String> questIDs = getAllQuestIDs();
		for(String q : questIDs){
			if(q.equals(questID)){
				return true;
			}
		}
		return false;
	}
	
	public Quest getActiveQuest(String questID){
		for(Quest q : getActiveQuests()){
			if(q.getQUEST_ID().equals(questID)){
				return q;
			}
		}
		return null;
	}
	
	public Quest getQuest(String questID){
		for(Quest q : allQuests.keySet()){
			if(q.getQUEST_ID().equals(questID)){
				return q;
			}
		}
		return null;
	}
	
}
