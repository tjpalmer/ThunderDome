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
	
	Quest(Item itemReq1, int req1, String name, String description, String[] beginDia, String[] activeDia, String[] completeDia) {
		this(name, description, beginDia, activeDia, completeDia);
		reqs.put(itemReq1, req1);
	}
	
	Quest(Object[] reqObj, int[] reqInt, String name, String description, String[] beginDia, String[] activeDia, String[] completeDia){
		this(name, description, beginDia, activeDia, completeDia);
		for(int i = 0; i < reqObj.length; i++){
			reqs.put(reqObj[i], reqInt[i]);
		}
	}
	
	Quest(EnemyType enemy1, int req1, String name, String description, String[] beginDia, String[] activeDia, String[] completeDia){
		this(name, description, beginDia, activeDia, completeDia);
		reqs.put(enemy1, req1);
	}
	
	Quest(Object[] reqObj, int[] reqInt, Object[] rewardObj, String[] rewardStr, String name, String description, String[] beginDia, String[] activeDia, String[] completeDia){
		this(reqObj, reqInt, name, description, beginDia, activeDia, completeDia);
		for(int i = 0; i < rewardObj.length; i++){
			rewards.put(rewardObj[i], rewardStr[i]);
		}
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
	
	public int getReqAmt(Object itemReq){
		if(reqs.containsKey(itemReq)){
			return reqs.get(itemReq);
		}
		/*
		if(itemReq instanceof Item){
			return reqs.get((Item)itemReq);
		}else if(itemReq instanceof EnemyType){
			return reqs.get((EnemyType)itemReq);
		}*/
		return -1;
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
