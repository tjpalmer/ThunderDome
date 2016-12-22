package virassan.entities.creatures.player;

import java.util.ArrayList;

import virassan.items.Item;
import virassan.quests.*;

public class QuestLog {

	private Player player;
	private ArrayList<QuestTracker> activeQuests;
	private ArrayList<QuestTracker> completedQuests;
	
	public QuestLog(Player player) {
		this.player = player;
		activeQuests = new ArrayList<QuestTracker>();
		completedQuests = new ArrayList<QuestTracker>();
		addQuest(new QuestTracker(Quest.QUEST_2));
		addQuest(new QuestTracker(Quest.QUEST_3));
	}

	public void addQuest(QuestTracker quest){
		activeQuests.add(quest);
	}
	
	/**
	 * Moves the quest from the active list, to the completed list. To be used AFTER checking if it HAS been completed
	 * @param quest the quest that has been completed
	 */
	public void completeQuest(QuestTracker quest){
		QuestTracker temp = null;
		for(QuestTracker q : activeQuests){
			if(quest.getQuest() == q.getQuest()){
				temp = q;
				completedQuests.add(q);
				for(Object obj : quest.getQuest().getHashMap().keySet()){
					if(obj instanceof Item){
						player.getInventory().removeItems((Item)obj, quest.getQuest().getReqAmt(obj));
					}
				}
				break;
			}
		}
		if(temp != null){
			activeQuests.remove(temp);
			player.getStats().addEventText("Quest Complete!");
			System.out.println(activeQuests);
		}
	}
	
	public ArrayList<QuestTracker> getComplete(){
		return completedQuests;
	}
	
	public void addComplete(QuestTracker quest){
		completedQuests.add(quest);
	}
	
	public ArrayList<QuestTracker> getActive(){
		return activeQuests;
	}
}
