package virassan.entities.creatures.npcs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import virassan.items.Item;
import virassan.main.Game;
import virassan.main.Handler;
import virassan.main.states.States;

public class Dialog {

	private boolean reqsMet;
	
	private final String DIALOG_ID;
	private final String text;
	private final ArrayList<Dialog> responses;
	private final HashMap<String, ArrayList<Object>> requirements;
	private ArrayList<Object> currentEventInfo;
	private final HashMap<String, ArrayList<Object>> dialogEvents;
	private final HashMap<String, Runnable> events = new HashMap<>();
	private String nextDialogID; //TODO: do i want to store a Dialog here or just the ID and create the Dialog later??
	private String nextDialogNPCID;
	
	{
		events.put("give_item", () -> giveItem());
		events.put("dialog", () -> dialog());
		events.put("quest", () -> quest());
		events.put("heal", () -> heal());
		events.put("merchant", () -> merchant());
		events.put("cinematic", () -> cinematic());
		events.put("teleport", () -> teleport());
		events.put("blacksmith", () -> blacksmith());
		events.put("is_liked", () -> liked());
	}
	
	private final HashMap<String, Runnable> reqs = new HashMap<>();
	
	{
		reqs.put("is_met", () -> isMet());
		reqs.put("is_quest", () -> isQuest());
		reqs.put("quest_comp", () -> questComp());
		reqs.put("is_liked", () -> isLiked());
	}
	
	public Dialog(){
		this(null, "");
	}
	
	public Dialog(String text){
		this(null, text);
	}
	
	/**
	 * creates a Dialog object with default values - no responses, no requirements
	 * @param dialog_id the dialog id
	 * @param text the actual dialog text
	 */
	public Dialog(String dialog_id, String text) {
		reqsMet = true;
		DIALOG_ID = dialog_id;
		this.text = text;
		responses = new ArrayList<>();
		requirements = new HashMap<>();
		dialogEvents = new HashMap<>();
		currentEventInfo = new ArrayList<>();
	}

	public Dialog(String dialog_id, String text, ArrayList<Dialog> responses, 
			HashMap<String, ArrayList<Object>> reqs, HashMap<String, ArrayList<Object>> dialogEvents){
		DIALOG_ID = dialog_id;
		this.text = text;
		this.responses = responses;
		requirements = reqs;
		reqsMet = true;
		this.dialogEvents = dialogEvents;
	}
	
	public void run(){
		for(String a : dialogEvents.keySet()){
			if(a != null){
				currentEventInfo = dialogEvents.get(a);
				//System.out.println("Message: Dialog_run dialogEvents key: " + a);
				events.get(a).run();
			}
		}
	}
	
	public void blacksmith(){
		//TODO: blacksmith stuff
	}
	
	public void heal(){
		try{
			int amount = (int)currentEventInfo.get(1);
			Game.handler.getPlayer().getStats().heal((float)amount);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void merchant(){
		try{
			String mercID = (String)currentEventInfo.get(0);
			if(mercID.equals(Game.handler.getPlayer().getCurrentMerchant().getNpcID())){
				Game.handler.setState(States.NPCShop);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void cinematic(){
		//TODO: trigger cinematic shtuff
		
	}
	
	public void teleport(){
		try{
			String mapID = (String)currentEventInfo.get(0);
			int coordX = (int)currentEventInfo.get(1);
			int coordY = (int)currentEventInfo.get(2);
			if(mapID.equals(Game.handler.getMap().getMapID())){
				Game.handler.getPlayer().setX((float)coordX);
				Game.handler.getPlayer().setY((float)coordY);
			}
			else{
				Game.handler.setMapID(mapID);
				Handler.LOADMAP.setNewSpawn(coordX, coordY);
				Game.handler.setState(States.LoadMap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void liked(){
		try{
			
			if(currentEventInfo.get(3) instanceof Boolean){
				if(!(Boolean)currentEventInfo.get(3)){
					Game.handler.getPlayer().setNPCLiked(Game.handler.getPlayer().getCurrentNPC().getNPCID(), false);
				}else{
					Game.handler.getPlayer().setNPCLiked(Game.handler.getPlayer().getCurrentNPC().getNPCID(), true);
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs the quest dialog
	 */
	public void quest(){
		if(currentEventInfo.get(0) instanceof String){
			String quest_id = (String)currentEventInfo.get(0);
			String dialog = "";
			if(!Game.handler.getPlayer().getQuestLog().isQuest(quest_id)){
				Game.handler.getPlayer().getQuestLog().addQuest(quest_id);
				dialog = Game.handler.getPlayer().getQuestLog().getQuest(quest_id).getStartDia();
			}else{
				dialog = Game.handler.getPlayer().getQuestLog().getQuest(quest_id).questDialog();
			}
			Handler.NPCDIALOG.setNextDialog(dialog);
		}
	}
	
	/**
	 * Specifically for when the next dialog is spoken by a different NPC. Changes the current NPC and 
	 */
	public void dialog(){
		try{
			nextDialogID = null;
			if(currentEventInfo != null){
				for(Object o : currentEventInfo){
					if(o != null){
						String[] varchar = ((String)currentEventInfo.get(0)).split(", ");
						nextDialogID = varchar[0];
						if(varchar.length > 1){
							nextDialogNPCID = varchar[1];
						}
						Handler.NPCDIALOG.setNextDialog(Handler.NPCDIALOG.getDialog(nextDialogID));
						//TODO: add the stuff for nextDialogNPCID
					}
				}
			}
		}catch(NullPointerException k){
			System.out.println("Error Message: Dialog_dialog something is NULL.");
			System.out.println("Current Event Info: " + currentEventInfo);
			k.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void giveItem(){
		try{
			String itemID = (String)currentEventInfo.get(1);
			Game.handler.getPlayer().getInventory().addItems(Item.valueOf(itemID), (int)currentEventInfo.get(2), false);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void isMet(){
		// in requirements key = isMet, in arraylist<object> index 0 is the ID and index 1 is the value/amount
		// so requirements = isMet, ArrayList=A_001,0
		String key = "is_met";
		try{
			String npcID = (String)requirements.get(key).get(0);
			int bool = Integer.parseInt((String)requirements.get(key).get(1));
			boolean b = Game.handler.getPlayer().isNPCMet(npcID);
				if(bool == 0){
					// if npc is NOT met then reqsMet = reqsMet && true
					// if npc is met then reqsMet = false
					reqsMet = !b && reqsMet;
				}else if(bool == 1){
					//if npc is NOT met then reqsMet = false
					//if npc is met then reqsMet = reqsMet && true
					reqsMet = b && reqsMet;
				}
		}catch(NullPointerException e){
			System.out.println("Error Message: Dialog_isMet_NULLPOINTEREXCEPTION");
		}catch(Exception k){
			System.out.println("Error Message: Dialog_isMet Unknown Exception");
			System.out.println("Index 0: " + requirements.get(key).get(0));
			System.out.println("Index 1: " + requirements.get(key).get(1));
			//print stack trace
		}
	}
	
	public void isQuest(){
		String key = "is_quest";
		// ("is_quest", ArrayList(Quest_ID, true/false))
		try{
			String questID = (String)requirements.get(key).get(0);
			int bool = Integer.parseInt((String)requirements.get(key).get(1));
			boolean b;
			if(bool == 0){
				b =  Game.handler.getPlayer().getQuestLog().isQuest(questID);
				reqsMet = !b && reqsMet;
			}else if(bool == 1){
				b =  Game.handler.getPlayer().getQuestLog().isActiveQuest(questID);
				reqsMet = b && reqsMet;
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: Dialog_isQuest_NULLPOINTEREXCEPTION, DialogID: " + DIALOG_ID);
			e.printStackTrace();
		}catch(Exception k){
			System.out.println("Error Message: Dialog_isQuest Unknown Exception");
			System.out.println("DialogID: " + DIALOG_ID + "Key: " + key + ", Index 0: " + requirements.get(key).get(0) + ", Index 1: " + requirements.get(key).get(1));
			k.printStackTrace();
		}//Jesse was here
	}
	
	public void isLiked(){
		// in requirements key = is_liked, in arraylist<object> index 0 is the ID and index 1 is the value/amount
		// so requirements = is_liked, ArrayList=A_001,0
		String key = "is_liked";
		try{
			String npcID = (String)requirements.get(key).get(0);
			int bool = Integer.parseInt((String)requirements.get(key).get(1));
			boolean b = Game.handler.getPlayer().isNPCLiked(npcID);
			System.out.println("Message: Dialog_isLiked bool is: " + bool);
			System.out.println("Message: Dialog_isLiked NPC liked: " + b);
			if(bool == 0){
				// if npc is NOT met then reqsMet = reqsMet && true
				// if npc is met then reqsMet = false
				reqsMet = !b && reqsMet;
			}else if(bool == 1){
				//if npc is NOT met then reqsMet = false
				//if npc is met then reqsMet = reqsMet && true
				reqsMet = b && reqsMet;
				}
		}catch(NullPointerException e){
			System.out.println("Error Message: Dialog_isLiked_NULLPOINTEREXCEPTION, DialogID: " + DIALOG_ID);
			e.printStackTrace();
		}catch(Exception k){
			System.out.println("DialogID: " + DIALOG_ID + "Key: " + key + ", Index 0: " + requirements.get(key).get(0) + ", Index 1: " + requirements.get(key).get(1));
			k.printStackTrace();
		}
	}
	
	public void questComp(){
		String key = "quest_comp";
		try{
			String questID = (String)requirements.get(key).get(0);
			int bool = Integer.parseInt((String)requirements.get(key).get(1));
			boolean b = Game.handler.getPlayer().getQuestLog().isQuestComplete(questID);
			if(bool == 0){
				reqsMet = !b && reqsMet;
			}else if(bool == 1){
				reqsMet = b && reqsMet;
			}
		}catch(NullPointerException e){
			System.out.println("Error Message: Dialog_questComp_NULLPOINTEREXCEPTION, DialogID: " + DIALOG_ID);
			e.printStackTrace();
		}catch(Exception k){
			System.out.println("Error Message: Dialog_questComp Unknown Exception");
			System.out.println("DialogID: " + DIALOG_ID + "Key: " + key + ", Index 0: " + requirements.get(key).get(0) + ", Index 1: " + requirements.get(key).get(1));
			k.printStackTrace();
		}
	}
	
	public String getNextDialogID(){
		for(String e : dialogEvents.keySet()){
			if(e.equals("dialog")){
				return (String)dialogEvents.get(e).get(0);
			}
		}
		return "";
	}
	
	public String getDIALOG_ID() {
		return DIALOG_ID;
	}

	public String getText() {
		return text;
	}
	
	public Set<String> getEvents(){
		return dialogEvents.keySet();
	}
	
	public HashMap<String, ArrayList<Object>> getReqs(){
		return requirements;
	}
	
	public ArrayList<Dialog> getResponses(){
		return responses;
	}
	
	public boolean isMetReq(){
		
		for(String a : requirements.keySet()){
			if( a != null){
				if(a.equals("is_met")){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean areReqsMet(){
		reqsMet = true;
		for(String a : requirements.keySet()){
			if(a != null){
				reqs.get(a).run(); //TODO: throws a nullpointerexception when trying to finish a quest - reqs is prob null
			}
		}
		return reqsMet;
	}
	
	public String toString(){
		return DIALOG_ID;
	}
	
}
