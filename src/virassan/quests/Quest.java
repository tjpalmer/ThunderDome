package virassan.quests;
import java.util.HashMap;

import virassan.entities.creatures.enemies.EnemySpecies;
import virassan.entities.creatures.enemies.EnemyType;
import virassan.items.Item;

public enum Quest {
	
	QUEST_1(Item.APPLE, 5, "Apples for Jimmy", "Bring Jimmy 5 Apples.", new String[]{"I am absolutely starving. Will you bring me some apples?"}, new String[]{"I'm withering away to nothing, where are the apples?"}, new String[]{"Apples, yay!!!"}),
	QUEST_2(new Object[]{EnemySpecies.SLIME}, new int[]{5}, "Slimey Pests", "Kill 5 Slimes and to Jimmy for your reward.", new String[]{"Eeek, these Slimes are everywhere! Will you kill a few?"}, new String[]{"Ugh. Slimes"}, new String[]{"Thank you SO much!"}),
	QUEST_3(new Object[]{Item.TOAST, EnemyType.CAPTAIN}, new int[]{5, 5}, new Object[]{550, Item.SWORD, 50}, new String[]{"exp", "", "gold"}, "Breakfast!", "Kill 10 Slimes and Bring 5 pieces of Toast to Jimmy.", new String[]{"It's breakfast time and the Slimes stole all my Toast!", "Bring me back my Toast?"}, new String[]{"Did you find all my Toast?"}, new String[]{"My toast!!! Thank you!"});

	
	HashMap<Object, Integer> reqs;
	HashMap<Object, String> rewards;
	HashMap<Object, String> defaultReward;
	String name, desc;
	String[] bDialog, aDialog, cDialog;
	
	/**
	 * Basic Constructor - initializes HashMaps for the Quest requirements and rewards and creates a default reward
	 * @param name The Name of the Quest
	 * @param description The Quest Description
	 * @param beginDia The dialog when the Quest is neither active nor completed
	 * @param activeDia The dialog when the Quest is active
	 * @param completeDia The dialog when the Quest is completed
	 */
	Quest(String name, String description, String[] beginDia, String[] activeDia, String[] completeDia){
		this.name= name;
		desc = description;
		bDialog = beginDia;
		aDialog = activeDia;
		cDialog = completeDia;
		reqs = new HashMap<Object, Integer>();
		rewards = new HashMap<Object, String>();
		defaultReward = new HashMap<Object, String>();
		defaultReward.put(400, "exp");
		rewards = defaultReward;
	}
	
	/**
	 * Constructor for Quest with only 1 Item object requirement and uses default award.
	 * @param itemReq1 The Item object requirement
	 * @param req1 The amount of the requirement necessary to complete the Quest
	 * @param name The name of the Quest
	 * @param description The description of the Quest
	 * @param beginDia The dialog when the Quest is neither active nor completed
	 * @param activeDia The dialog when the Quest is active
	 * @param completeDia The dialog when the Quest is completed
	 */
	Quest(Item itemReq1, int req1, String name, String description, String[] beginDia, String[] activeDia, String[] completeDia) {
		this(name, description, beginDia, activeDia, completeDia);
		reqs.put(itemReq1, req1);
	}
	
	/**
	 * Constructor for an array of requirements and uses default reward
	 * @param reqObj An array of requirements.
	 * @param reqInt An array of the amount needed to complete of the requirements. The indeces should match with the reqObj array.
	 * @param name The name of the Quest
	 * @param description The description of the Quest
	 * @param beginDia The dialog when the Quest is neither active nor completed
	 * @param activeDia The dialog when the Quest is active
	 * @param completeDia The dialog when the Quest is completed
	 */
	Quest(Object[] reqObj, int[] reqInt, String name, String description, String[] beginDia, String[] activeDia, String[] completeDia){
		this(name, description, beginDia, activeDia, completeDia);
		for(int i = 0; i < reqObj.length; i++){
			reqs.put(reqObj[i], reqInt[i]);
		}
	}
	
	/**
	 * Constructor for Quest with only 1 EnemyType object requirement and uses default reward.
	 * @param enemy1 The EnemyType object requirement
	 * @param req1 Amount of EnemyType objects to be slain to complete Quest
	 * @param name The name of the Quest
	 * @param description The description of the Quest
	 * @param beginDia The dialog when the Quest is neither active nor completed
	 * @param activeDia The dialog when the Quest is active
	 * @param completeDia The dialog when the Quest is completed
	 */
	Quest(EnemyType enemy1, int req1, String name, String description, String[] beginDia, String[] activeDia, String[] completeDia){
		this(name, description, beginDia, activeDia, completeDia);
		reqs.put(enemy1, req1);
	}
	
	/**
	 * Constructor for multiple requirements and rewards.
	 * @param reqObj An array of requirements.
	 * @param reqInt An array of the amount needed to complete of the requirements. The indeces should match with the reqObj array.
	 * @param rewardObj An array of Objects or integers. If an integer, is the amount to be added to the Player variable indicated by the String at the corresponding index of rewardStr.
	 * @param rewardStr If the reward at the index is an Object, leave an empty string. Otherwise the Strings accepted are: "exp", "gold"
	 * @param name The name of the Quest
	 * @param description The description of the Quest
	 * @param beginDia The dialog when the Quest is neither active nor completed
	 * @param activeDia The dialog when the Quest is active
	 * @param completeDia The dialog when the Quest is completed
	 */
	Quest(Object[] reqObj, int[] reqInt, Object[] rewardObj, String[] rewardStr, String name, String description, String[] beginDia, String[] activeDia, String[] completeDia){
		this(reqObj, reqInt, name, description, beginDia, activeDia, completeDia);
		for(int i = 0; i < rewardObj.length; i++){
			rewards.put(rewardObj[i], rewardStr[i]);
		}
	}
	
	/**
	 * Returns the required amount for the Quest requirement passed. Returns -1 if the requirement doesn't exist for this Quest.
	 * @param itemReq
	 * @return The requirement's needed amount. Returns -1 if the requirement doesn't exist.
	 */
	public int getReqAmt(Object questReq){
		if(reqs.containsKey(questReq)){
			return reqs.get(questReq);
		}
		return -1;
	}
	
	public HashMap<Object, String> getRewards(){
		return rewards;
	}
	
	public String getDescription(){
		return desc;
	}
	
	public String getName(){
		return name;
	}
	
	public HashMap<Object, Integer> getHashMap(){
		return reqs;
	}
	
	public String[] getActiveDialog(){
		return aDialog;
	}
	
	public String[] getCompleteDialog(){
		return cDialog;
	}
	
	public String[] getBeginDialog(){
		return bDialog;
	}
	
	public String toString(){
		return name;
	}

}
