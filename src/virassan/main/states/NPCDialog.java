package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import virassan.entities.creatures.npcs.NPC;
import virassan.entities.creatures.player.Player;
import virassan.gfx.Assets;
import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.quests.QuestTracker;

public class NPCDialog {

	private Handler handler;
	private MouseInput mouseInput;
	private KeyInput keyInput;
	private Player player;
	
	private ArrayList<QuestTracker> npcQuests;
	private Rectangle[] questMenu;
	private Rectangle questMenuSel;
	private boolean isQuestMenu;
	
	public NPCDialog(Handler handler) {
		this.handler = handler;
		mouseInput = handler.getMouseInput();
		keyInput = handler.getKeyInput();
		
	}

	public void render(Graphics g){
		Handler.WORLD.render(g);
		int x = (int)((Display.WIDTH % 1040) / 2);
		int y = Display.HEIGHT - 200;
		g.drawImage(Assets.dialogBox, x, y, null);
		g.setColor(Color.PINK);
		g.setFont(new Font("Gentium Basic", Font.PLAIN, 32));
		if(!isQuestMenu){
			g.drawString(player.getCurrentNPC().getCurDialog(), x + 15, y + 35);
			g.drawImage(Assets.button_a, x + 995, y + 105, null);
		}else{
			for(QuestTracker quest : npcQuests){
				g.setColor(Color.PINK);
				g.drawString(quest.getQuest().getName(), questMenu[npcQuests.indexOf(quest)].x + 15, questMenu[npcQuests.indexOf(quest)].y + g.getFontMetrics().getHeight());
				g.setColor(Handler.SELECTION_HIGHLIGHT);
				g.fillRect(questMenuSel.x, questMenuSel.y, questMenuSel.width, questMenuSel.height);
			}
		}
	}
	
	public void tick(double delta){
		player = handler.getPlayer();
		if(!mouseInput.getLeftClicks().isEmpty()){
			leftClick();
		}
		if(!mouseInput.getRightClicks().isEmpty()){
			rightClick();
		}
		hover();
		HUDManager.MENUTIMER += (System.currentTimeMillis() - HUDManager.MENULAST) * delta;
		HUDManager.MENULAST = System.currentTimeMillis() * (long)delta;
		NPC curNPC = player.getCurrentNPC();
		if(!isQuestMenu){
			if(HUDManager.MENUTIMER > HUDManager.MENUWAIT){
				if(keyInput.A){
					if(curNPC.getDialognum() < curNPC.getDialog().size()-1){
						curNPC.setDialognum(curNPC.getDialognum()+1);
					}else if(curNPC.getDialognum() >= curNPC.getDialog().size()-1){
						handler.setState(States.World);
						curNPC.setInteract(false);
						curNPC.setDialognum(0);
						player.setCurrentNPC(null);
						handler.getEntityManager().setPaused(false);
					}
					HUDManager.MENUTIMER = 0;
				}
			}
		}
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(isQuestMenu){
			//TODO: use the mouse for the questMenu
				if(clicks.element() != null){
					outer :{
						while(clicks.element() != null){
							Point head = clicks.poll().getObject();
							double x = head.getX();
							double y = head.getY();
							//TODO: do a for loop of the size of npcQuests and figure out its Rectangle
							for(int i = 0; i < questMenu.length; i++){
								if(new Rectangle((int)x, (int)y, 1, 1).intersects(questMenu[i])){
									player.getCurrentNPC().setDialog(npcQuests.get(i));
									isQuestMenu = false;
									break outer;
								}
							}
						}
					}
				}
			}
	}
	
	public void rightClick(){
		LinkedQueue clicks = mouseInput.getRightClicks();
		if(handler.getEntityManager().getPaused()){

		}
	}
	
	public void hover(){
		if(isQuestMenu){
			for(Rectangle rect : questMenu){
				if(mouseInput.getMouseBounds().intersects(rect)){
					questMenuSel = rect;
				}
			}
		}
	}
	
	public void NPCInteract(NPC entity){
		player = handler.getPlayer();
		player.setCurrentNPC(entity);
		int playerDist = (int)(Math.sqrt(Math.pow(player.getX() - entity.getX(), 2)+Math.pow(player.getY() - entity.getY(),2)));
		if(playerDist <= entity.getInteractDist()){
			entity.setInteract(true);
			npcQuests = new ArrayList<>();
			ArrayList<QuestTracker> active = handler.getPlayer().getQuestLog().getActive();
			for(QuestTracker q : entity.getQuests()){
				if(active.size() > 0){
					for(QuestTracker a : active){
						if(a.getQuest() == q.getQuest()){
							npcQuests.add(a);
						}
					}
				}
			}
			if(!npcQuests.isEmpty()){
				if(npcQuests.size() > 1){
					//TODO: choose which quest to talk about - make a list
					questMenu = new Rectangle[npcQuests.size()];
					int x = 300;
					int y = Display.HEIGHT - 190;
					for(int i = 0; i < questMenu.length; i++){
						if(i != 0 && i%2 == 0){
							x += 255;
						}
						questMenu[i] = new Rectangle(x, y, 250, 50);
						y += 55;
					}
					questMenuSel = questMenu[0];
					isQuestMenu = true;
				}else{
					entity.setDialog(npcQuests.get(0));
				}
			}else{
				entity.setDialog();
			}
			handler.getEntityManager().setPaused(true);
		}
	}
}
