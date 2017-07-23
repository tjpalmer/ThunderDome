package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import virassan.entities.creatures.player.Player;
import virassan.gfx.Assets;
import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.main.Handler;
import virassan.quests.QuestTracker;
import virassan.utils.ScrollPanel;

public class MenuQuest {

	private Handler handler;
	private Player player;
	private MouseInput mouseInput;
	private KeyInput keyInput;
	
	private ArrayList<QuestTracker> quests;
	private int curQuestIndex;
	private ScrollPanel questScroll;
	
	
	public MenuQuest(Handler handler) {
		this.handler = handler;
		mouseInput = handler.getMouseInput();
		keyInput = handler.getKeyInput();
		
	}
	
	public void render(Graphics g){
		player = handler.getPlayer();
		g.drawImage(Assets.questMenu, 0, 0, null);
		int x = 50;
		int y = 150;
		Font questFont = new Font("Verdana", Font.PLAIN, 18);
		questScroll = new ScrollPanel(new Rectangle(x, y, 505, 600));
		quests = player.getQuestLog().getActive();
		ArrayList<String> questNames = new ArrayList<>();
		for(QuestTracker quest : quests){
			questNames.add(quest.getQuest().getName());
		}
		if(!quests.isEmpty()){
			questScroll.addStrings(questNames, questFont, g.getFontMetrics(questFont), Color.WHITE);
			questScroll.render(g);
			g.setColor(Handler.SELECTION_HIGHLIGHT);
			g.fillRect(x - 5, questScroll.getDrawPoints().get(curQuestIndex).y - 17, 491, 21);
			//TODO: Finish Quest Log
			g.setColor(Color.WHITE);
			g.setFont(questFont);
			x = 600;
			y = 150;
			g.drawString(quests.get(curQuestIndex).getQuest().getDescription(), x, y);
			for(Object obj : quests.get(curQuestIndex).getQuest().getHashMap().keySet()){
				y += 20;
				g.drawString(obj + ": " + quests.get(curQuestIndex).getReqAmt(obj) + "/" + quests.get(curQuestIndex).getQuest().getReqAmt(obj), x + 150, y);
			}
		}
		g.setFont(new Font("Verdana", Font.BOLD, 32));
		String inventory = "Inventory";
		String questlog = "Quest Log";
		String skillbook = "Skillbook";
		g.setColor(Color.WHITE);
		g.drawString(inventory, 125, 50);
		g.setColor(Color.WHITE);
		g.drawString(skillbook, 640 - (g.getFontMetrics().stringWidth(skillbook) / 2), 50);
		g.setColor(Handler.BLUE_VIOLET);
		g.drawString(questlog, 950, 50);
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
		if(HUDManager.MENUTIMER > HUDManager.MENUWAIT){
			if(keyInput.esc || keyInput.I || keyInput.L || keyInput.K){
				HUDManager.MENUTIMER = 0;
				curQuestIndex = 0;
				if(keyInput.L){
					handler.setState(States.World);
					handler.getEntityManager().setPaused(false);
				}else if(keyInput.I){
					handler.setState(States.MenuInventory);
				}else if(keyInput.esc){
					handler.setState(States.MenuSettings);
				}else if(keyInput.K){
					handler.setState(States.MenuSkills);
				}
			}
		}
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(handler.getEntityManager().getPaused()){
			if(clicks.element() != null){
				outer: {
					while(clicks.element() != null){
						Point head = clicks.poll().getObject();
						double x = head.getX();
						double y = head.getY();
						if(new Rectangle((int)x, (int)y, 1, 1).intersects(questScroll.getButtonUp())){
							questScroll.move(5);
							System.out.println("UP");
							break outer;
						}else if(new Rectangle((int)x, (int)y, 1, 1).intersects(questScroll.getButtonDown())){
							questScroll.move(-5);
							System.out.println("DOWN");
							break outer;
						}else{
							for(Object obj : questScroll.getDrawObjects()){
								if(obj instanceof String){
									if(mouseInput.getMouseBounds().intersects(new Rectangle(questScroll.getDrawPoints().get(questScroll.getDrawObjects().indexOf(obj)).x, questScroll.getDrawPoints().get(questScroll.getDrawObjects().indexOf(obj)).y - questScroll.getFontHeight() + 2, 500, 22))){
										if(questScroll.getDrawPoints().get(questScroll.getDrawObjects().indexOf(obj)).y >= 150 && questScroll.getDrawPoints().get(questScroll.getDrawObjects().indexOf(obj)).y <= (150+600)){
											curQuestIndex = questScroll.getDrawObjects().indexOf(obj);
											break outer;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void rightClick(){
		// TODO : add right click actions!
		LinkedQueue clicks = mouseInput.getRightClicks();
		if(handler.getEntityManager().getPaused()){

		}
	}
	
	public void hover(){
		
	}
}
