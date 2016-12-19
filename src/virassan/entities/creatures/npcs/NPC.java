package virassan.entities.creatures.npcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import virassan.entities.creatures.Creature;
import virassan.gfx.ImageLoader;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.quests.Quest;
import virassan.quests.QuestTracker;

public class NPC extends Creature{
	private BufferedImage image;
	private int interactDist;
	private boolean isInteract;
	private ArrayList<String> defaultDialog;
	private int dialognum;
	private ArrayList<String> dialog;
	private ArrayList<QuestTracker> quests;

	public NPC(Handler handler, float x, float y, int width, int height, String name, String filepath) {
		super(handler, name, x, y, width, height, 1, ID.NPC);
		// TODO Add Portrait
		defaultDialog = new ArrayList<String>(Arrays.asList("Hello, Stranger!", "My name is " + name + ".", "Thanks for stopping by!"));
		image = ImageLoader.loadImage(filepath);
		interactDist = (int)(Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
		bounds.x += 22;
		bounds.width -= 46;
		bounds.y += 54;
		bounds.height -= 60;
		isInteract = false;
		dialognum = 0;
		dialog = defaultDialog;
	}
	
	public NPC(Handler handler, float x, float y, int width, int height, String name, String filepath, ArrayList<Quest> quests){
		this(handler, x, y, width, height, name, filepath);
		this.quests = new ArrayList<QuestTracker>();
		for(Quest q : quests){
			this.quests.add(new QuestTracker(q));
		}
	}

	@Override
	public void resetTimer() {
		// No Timers here to reset
	}

	
	public void setDialog(QuestTracker target){
		if(target == null){
			setDialog();
		}else{
			if(!target.isComplete()){
				System.out.println("in active");
				dialog = new ArrayList<String>();
				for(String txt : target.getQuest().getActiveDialog()){
					dialog.add(txt);
				}
			}else{
				System.out.println("in complete");
				dialog = new ArrayList<String>();
				for(String txt : target.getQuest().getCompleteDialog()){
					dialog.add(txt);
				}
				//TODO move this player quest stuff to the HUD
				handler.getPlayer().getQuestLog().completeQuest(target);
				target.giveRewards();
			}
		}
	}
	
	public void setDialog(){
		//TODO move the player quest parts to the HUD - KEEP the dialog update parts
		ArrayList<QuestTracker> complete = handler.getPlayer().getQuestLog().getComplete();
		ArrayList<Boolean> temp = new ArrayList<Boolean>();
		QuestTracker done = null;
		for(QuestTracker q : quests){
			if(complete.size() > 0){
				if(q.getComplete()){
					temp.add(true);
					done = q;
				}else{
					temp.add(false);
				}
			}else{
				temp.add(false);
			}
		}
		if(temp.contains(false)){
			int questNum = 0;
			if(done != null){
				if(quests.indexOf(done)+1 < quests.size()){
					questNum = quests.indexOf(done)+1;
				}
			}
			System.out.println("in begin");
			dialog = new ArrayList<String>(Arrays.asList(defaultDialog.get(0), defaultDialog.get(1)));
			for(String txt : quests.get(questNum).getQuest().getBeginDialog()){
				dialog.add(txt);
			}
			handler.getPlayer().getQuestLog().addQuest(quests.get(questNum));
		}else{
			dialog = defaultDialog;
		}
	}
	
	public ArrayList<QuestTracker> NPCQuestMenu(ArrayList<QuestTracker> quests){
		return quests;
	}
	
	@Override
	public void tick() {
		//TODO: Add animation and possible movement to NPC when Not Paused
	}

	@Override
	public void render(Graphics g) {
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		g.drawImage(image, (int)(x-handler.getGameCamera().getxOffset()), (int)(y-handler.getGameCamera().getyOffset()), null);
		g.setColor(Color.WHITE);
		g.drawString(name, (int)(x-handler.getGameCamera().getxOffset()+2), (int)(y-handler.getGameCamera().getyOffset()-5));
		int temp = (int)(Math.sqrt(Math.pow(x-handler.getPlayer().getX(), 2)+Math.pow(y-handler.getPlayer().getY(),2)));
		if(temp <= interactDist){
			g.drawString("F", (int)(x-handler.getGameCamera().getxOffset()+(width/2)-5), (int)(y-handler.getGameCamera().getyOffset()+height+10));
		}
	}
	
	//GETTERS AND SETTERS
	public void addQuest(Quest quest){
		quests.add(new QuestTracker(quest));
	}
	
	public ArrayList<QuestTracker> getQuests(){
		return quests;
	}
	
	public void setDialognum(int i){
		dialognum = i;
	}
	
	public String getCurDialog(){
		return dialog.get(dialognum);
	}
	
	public ArrayList<String> getDialog(){
		return dialog;
	}
	public boolean getInteract(){
		return isInteract;
	}
	
	public void setInteract(boolean b){
		isInteract = b;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getInteractDist() {
		return interactDist;
	}

	public void setInteractDist(int interactDist) {
		this.interactDist = interactDist;
	}

	@Override
	public void unPause() {}

	public int getDialognum() {
		return dialognum;
	}

	
}
