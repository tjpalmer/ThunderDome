package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import virassan.gfx.Assets;
import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.utils.ScrollPanel;
import virassan.utils.Utils;

public class MenuSettings {

	private Handler handler;
	private KeyInput keyInput;
	private MouseInput mouseInput;
	
	private ScrollPanel helpScroll;
	
	public MenuSettings(Handler handler) {
		this.handler = handler;
		keyInput = handler.getKeyInput();
		mouseInput = handler.getMouseInput();
		
	}

	public void render(Graphics g){
		g.drawImage(Assets.menu, 0, 0, null);
		g.setColor(Handler.BLUE_VIOLET);
		g.setFont(new Font("Verdana", Font.BOLD, 32));
		g.drawString("Help Menu", 550, 40);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		ArrayList<String> words = new ArrayList<>();
		words.add("You are a Slime, and you're killing other Slimes. The main difference in appearance is the Enemies will have a name printed above them, whereas you do Not.");
		words.add("The NPCs are the Stick figures.");
		words.add("Captains are stronger than Slimes, and Bosses are even stronger. The stronger they are, the better loot they drop");
		words.add("CONTROLS - ");
		words.add("SPACE: Pauses/Unpauses the Game");
		words.add("W: Moves the Character Up");
		words.add("A: Moves the Character Left");
		words.add("S: Moves the Character Down");
		words.add("D: Moves the Character Right");
		words.add("F: Interacts with nearby NPC");
		words.add("K: Opens/Closes the Skillbook");
		words.add("L: Opens/Closes the Quest Log");
		words.add("I: Opens/Closes the Inventory/Character Page");
		words.add("Escape: Opens/Closes this Help Menu");
		words.add("SKILLS - ");
		words.add("Clicking the Icon of a Skill or Pressing its number hot-key will Activate it, shown by a pink box under your mouse.");
		words.add("Each skill has a maximum range and uses either Stamina or Mana");
		words.add("If a skill's icon is grey'd out, it means the skill is still in cooldown");
		words.add("You'll be alerted if the target is out of range - or if you lack the cost of stamina/mana - or if the skill isn't ready yet");
		words.add("NPCs - ");
		words.add("E: Buys or Sells and Item when Talking to a Merchant");
		words.add("A: Furthers the dialog when Talking to an NPC");
		words.add("If you have more than one active Quest with an NPC, you must select which Quest you'd like to talk about - use the Mouse to do that");
		words.add("The Mouse hasn't been implemented into the Merchant page. Use W and S to scroll through items");
		words.add("INVENTORY - ");
		words.add("Right Click: Opens Item Menu for Item Selected");
		//TODO: change double click back to quick use of item and add Item Info to the Item Menu
		words.add("Double Click: Opens Item Information for Item Selected");
		words.add("Drag an Equipable Item to the Player Portrait to auto-equip it");
		words.add("Drag an Item to the Trashcan Icon to destory it Forever - COMING SOON");
		words.add("Use A and D to switch between Inventory Tab");
		words.add("Use TAB to switch to the next Menu Tab");
		words.add("QUEST LOG - ");
		words.add("Click on a Quest in the list to the left to have its information and your progress displayed on the right.");
		words.add("SKILLBOOK - ");
		words.add("Double Click: Opens Skill Information for the Skill Selected - COMING SOON");
		words.add("Drag a Skill to the skillbar to put it in that slot - COMING SOON");
		ArrayList<String> temp = new ArrayList<>();
		for(String text : words){
			for(String display : Utils.wrapText(text, Display.WIDTH - 150, g.getFontMetrics())){
				temp.add(display);
			}
		}
		helpScroll = new ScrollPanel(new Rectangle(60, 100, Display.WIDTH - 100, 600));
		helpScroll.addStrings(temp, g.getFont(), g.getFontMetrics(), g.getColor());
		helpScroll.render(g);
	}
	
	public void tick(){
		if(!mouseInput.getLeftClicks().isEmpty()){
			leftClick();
		}
		if(!mouseInput.getRightClicks().isEmpty()){
			rightClick();
		}
		HUDManager.MENUTIMER += System.currentTimeMillis() - HUDManager.MENULAST;
		HUDManager.MENULAST = System.currentTimeMillis();
		if(HUDManager.MENUTIMER > HUDManager.MENUWAIT){
			if(keyInput.esc || keyInput.I || keyInput.L || keyInput.K){
				HUDManager.MENUTIMER = 0;
				if(keyInput.esc){
					handler.setState(States.World);
					handler.getEntityManager().setPaused(false);
				}else if(keyInput.I){
					handler.setState(States.MenuInventory);
				}else if(keyInput.L){
					handler.setState(States.MenuQuest);
				}else if(keyInput.K){
					handler.setState(States.MenuSkills);
				}
			}
		}
	}
	
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(clicks.element() != null){
			outer: {
				while(clicks.element() != null){
					Point head = clicks.poll().getObject();
					double x = head.getX();
					double y = head.getY();
					//TODO: not moving properly
					if(new Rectangle((int)x, (int)y, 1, 1).intersects(helpScroll.getButtonUp())){
						helpScroll.move(2);
						System.out.println("UP");
						break outer;
					}else if(new Rectangle((int)x, (int)y, 1, 1).intersects(helpScroll.getButtonDown())){
						helpScroll.move(-2);
						System.out.println("DOWN");
						break outer;
					}
				}
			}
		}
	}
	
	public void rightClick(){
		
	}
	
}
