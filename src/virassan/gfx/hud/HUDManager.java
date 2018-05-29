package virassan.gfx.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.CopyOnWriteArrayList;

import virassan.entities.creatures.player.Player;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.items.ItemManager;
import virassan.main.Display;
import virassan.main.Game;
import virassan.main.Handler;
import virassan.utils.Utils;

/**
 * Heads Up Display Manager - HUD is supposed to manage display things that the Player CANNOT change
 * @author Virassan
 *
 */
public class HUDManager {
	
	private Handler handler;
	private Player player;
	private KeyInput keyInput;
	private MouseInput mouseInput;
	private ItemManager itemManager;
	
	private CopyOnWriteArrayList<SkillText> skillList = new CopyOnWriteArrayList<>();
	private String skillName;
	private float waitTime = 300;
	private long lastTime;
	private long timer = 0;
	private boolean isDragged;
	
	private final Rectangle exitButton;
	
	private float dmgScaleIncr = 0.01f;
	
	public static long MENUTIMER, MENULAST;
	public static final long MENUWAIT = 200;
	
	public HUDManager(Handler handler){
		this.handler = handler;
		keyInput = handler.getKeyInput();
		mouseInput = handler.getMouseInput();
		itemManager = new ItemManager(handler);
		exitButton = new Rectangle(handler.getWidth() - 55, 70, 25, 25);
	}

	
	public void tick(double delta){
		player = handler.getPlayer();
		boolean pause = handler.getEntityManager().getPaused();
		if(!pause){
			itemManager.tick(delta);
			if(keyInput.Z){
				Utils.saveGame(handler);
			}
			if(player.getActiveSkill() != null){
				SkillTracker s = player.getActiveSkill();
				float curAmt = 0;
				switch(s.getSkillCostType()){
				case "stam": curAmt = player.getStats().getStamina(); break;
				case "mana": curAmt = player.getStats().getMana(); break;
				case "heal": curAmt = player.getStats().getHealth(); break;
				}
				if((curAmt - s.getCost()) < 0){
					player.setSkillActive(false);
					skillList.add(new SkillText(handler, "Not enough " + s.getSkillCostType(), Handler.LAVENDER, (int)player.getX(), (int)player.getY() - player.getHeight()-10));
				}
			}
		}
		for(SkillText text : skillList){
			if(text.isLive()){
				text.tick(delta);
			}else{
				skillList.remove(text);
			}
		}
		player.getStats().tick(delta);
		leftClick();
		rightClick();
	}
	
	public void render(Graphics g){
		itemManager.render(g);
		if(!handler.getEntityManager().getPaused()){
			for(SkillText text : skillList){
				if(text.isLive()){
					text.render(g);
				}else{
					skillList.remove(text);
				}
			}
		}
		renderExitButton(g);
		renderSkillBar(g);
		renderHealthBar(g);
		renderStaminaBar(g);
		renderManaBar(g);
		renderExpBar(g);
		// Bouncy Text
		handler.getPlayer().getStats().render(g);
		// Mouse Stuff
		g.setColor(Color.BLUE);
		g.fillRect((int)handler.getMouseInput().getMouseBounds().getX(), (int)handler.getMouseInput().getMouseBounds().getY(), (int)handler.getMouseInput().getMouseBounds().getWidth()+1, (int)handler.getMouseInput().getMouseBounds().getHeight()+1);
		if(skillName != null){
			g.setColor(Color.BLACK);
			g.setFont(new Font("Verdana", Font.BOLD, 18));
			g.drawString(skillName, handler.getMouseInput().getMouseX(), handler.getMouseInput().getMouseY());
		}
		if(handler.getPlayer().getSkillActive()){
			g.setColor(Color.MAGENTA);
			g.fillRect(handler.getMouseInput().getMouseX(), handler.getMouseInput().getMouseY(), 10, 10);
		}
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		g.setColor(Color.WHITE);
		g.drawString("Ticks: " + Game.TICK, handler.getWidth() - 70, 20);
		g.drawString(handler.getPlayer().getX() + " " + handler.getPlayer().getY(), handler.getWidth() - 90, 35);
		g.drawString("" + handler.getEntityManager().getPaused(), handler.getWidth() - 50, 50);
		g.drawString(handler.getGameCamera().getxOffset() + " " + handler.getGameCamera().getyOffset(), handler.getWidth() - 90, 65);
	}
	
	public void renderExitButton(Graphics g){
		g.setColor(Color.BLACK);
		g.setFont(new Font("Verdana", Font.PLAIN, 20));
		g.drawString("X", handler.getWidth() - 50, 90);
		g.setColor(Color.RED);
		g.drawRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
	}
	
	public void renderSkillBar(Graphics g){
		g.setColor(Color.GRAY);
		int rectx = (handler.getWidth() / 2) - 80;
		int recty = 10;
		g.fillRect((handler.getWidth() / 2) - 80, 10, 320, 64);
		int tempx = (handler.getWidth() / 2) - 80;
		int numx = (handler.getWidth() / 2) - 60;
		for(SkillTracker s : handler.getPlayer().getSkillBar()){
			if(s != null){
				s.drawIcon(g, tempx, recty);
				tempx += 64;
			}
		}
		g.setColor(Color.BLACK);
		for(int i = 0; i < 5; i++){
			g.drawRect(rectx, recty, 64, 64);
			rectx += 64;
		}
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		for(int i = 0; i < 5; i++){
			g.drawString("" + (i + 1), numx, recty + 80);
			numx += 64;
		}
	}
	
	public void renderExpBar(Graphics g){
		float maxExp = handler.getPlayer().getStats().getMaxExperience();
		float exp = handler.getPlayer().getStats().getLevelExperience(handler.getPlayer().getStats().getLevel() - 1);
		float playerExp = handler.getPlayer().getStats().getExperience() - exp;
		g.setColor(Handler.GOLD);
		g.fillRect(10, 80, (int)(150 * ((playerExp) / (maxExp - exp))), 15);
		g.setColor(Color.BLACK);
		g.drawRect(9, 79, 30, 16);
		g.drawRect(39, 79, 30, 16);
		g.drawRect(69, 79, 30, 16);
		g.drawRect(99, 79, 30, 16);
		g.drawRect(129, 79, 30, 16);
		g.setColor(Handler.BARELY_GRAY);
		g.setFont(new Font("Consolas", Font.BOLD, 14));
		String expDisplay = playerExp + " / " + (maxExp - exp);
		g.drawString(expDisplay, 120 - g.getFontMetrics().stringWidth(expDisplay), 92);
	}
	
	public void renderManaBar(Graphics g){
		g.setColor(Handler.MANA_BLUE);
		g.fillRect(10, 60, (int)(150 * (handler.getPlayer().getStats().getMana() / handler.getPlayer().getStats().getMaxMana())), 15);
		g.setColor(Color.BLACK);
		g.drawRect(9, 59, 30, 16);
		g.drawRect(39, 59, 30, 16);
		g.drawRect(69, 59, 30, 16);
		g.drawRect(99, 59, 30, 16);
		g.drawRect(129, 59, 30, 16);
		Color bar = new Color(3, 233, 249);
		g.setColor(bar);
		g.fillRect((int)(150 * (handler.getPlayer().getStats().getMana() / handler.getPlayer().getStats().getMaxMana())) + 7, 60, 3, 15);
		g.setColor(Handler.BARELY_GRAY);
		g.setFont(new Font("Consolas", Font.BOLD, 14));
		String manaDisplay = (int)handler.getPlayer().getStats().getMana() + " / " + (int)handler.getPlayer().getStats().getMaxMana();
		g.drawString(manaDisplay, 115 - g.getFontMetrics().stringWidth(manaDisplay), 72);
	}
	
	public void renderStaminaBar(Graphics g){
		g.setColor(Handler.STAMINA_GREEN);
		g.fillRect(10, 40, (int)(150 * (handler.getPlayer().getStats().getStamina() / handler.getPlayer().getStats().getMaxStam())), 15);
		g.setColor(Color.BLACK);
		g.drawRect(9, 39, 30, 16);
		g.drawRect(39, 39, 30, 16);
		g.drawRect(69, 39, 30, 16);
		g.drawRect(99, 39, 30, 16);
		g.drawRect(129, 39, 30, 16);
		Color bar = new Color(6, 248, 80);
		g.setColor(bar);
		g.fillRect((int)(150 * (handler.getPlayer().getStats().getStamina() / handler.getPlayer().getStats().getMaxStam())) + 7, 40, 3, 15);
		g.setColor(Handler.BARELY_GRAY);
		g.setFont(new Font("Consolas", Font.BOLD, 14));
		String stamDisplay = (int)handler.getPlayer().getStats().getStamina() + " / " + (int)handler.getPlayer().getStats().getMaxStam();
		g.drawString(stamDisplay, 122 - g.getFontMetrics().stringWidth(stamDisplay), 52);
	}
	
	public void renderHealthBar(Graphics g){
		//Red Removed Health bar
		if(!handler.getPlayer().getStats().isDamaged()){
			timer += System.currentTimeMillis() - lastTime;
			if(handler.getPlayer().getStats().getDmgScale() > handler.getPlayer().getStats().getHealthScale()){
				if(timer > waitTime){
					player.getStats().setDmgScale(handler.getPlayer().getStats().getDmgScale() - dmgScaleIncr);
					if(Math.abs((double)handler.getPlayer().getStats().getDmgScale() - (double)handler.getPlayer().getStats().getHealthScale()) / handler.getPlayer().getStats().getHealthScale() < 0.3){
						waitTime -= waitTime*0.02f;
						dmgScaleIncr += 0.0004f;
					}else{
						waitTime -= waitTime*0.5f;
					}
					//waitTime -= waitTime*0.5f;
					if(waitTime <= 0){
						waitTime = 300;
					}
					lastTime = System.currentTimeMillis();
				}	
			}
		}else{
			waitTime = 300;
		}
		g.setColor(Handler.HEALTH_ORANGE);
		g.fillRect(10, 10, (int)(150 * handler.getPlayer().getStats().getDmgScale()), 25);
		//Health Bar
		g.setColor(Handler.HEALTH_RED);
		g.fillRect(10, 10, (int)(150 * handler.getPlayer().getStats().getHealthScale()), 25);
		g.setColor(Color.BLACK);
		g.drawRect(9, 9, 30, 26);
		g.drawRect(39, 9, 30, 26);
		g.drawRect(69, 9, 30, 26);
		g.drawRect(99, 9, 30, 26);
		g.drawRect(129, 9, 30, 26);
		Color bar = new Color(251, 196, 5);
		g.setColor(bar);
		g.fillRect((int)(150 * handler.getPlayer().getStats().getHealthScale()) + 7, 10, 3, 25);
		g.setColor(new Color(235, 235, 235));
		g.setFont(new Font("Consolas", Font.BOLD, 16));
		String healthDisplay = (int)handler.getPlayer().getStats().getHealth() + " / " + (int)handler.getPlayer().getStats().getMaxHealth();
		g.drawString(healthDisplay, 125 - g.getFontMetrics().stringWidth(healthDisplay), 27);
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(clicks.element() != null){
			while(clicks.element() != null){
				Point head = clicks.poll().getObject();
				double x = head.getX();
				double y = head.getY();
				if(exitButton.intersects(x, y, 1, 1)){
					handler.getGame().close();
				}
			}
		}
	}
	
	public void rightClick(){
		// TODO : add right click actions?
		LinkedQueue clicks = mouseInput.getRightClicks();
		if(handler.getEntityManager().getPaused()){
			
		}
	}
	
	public void drag(){
		//TODO: finish drag implementations
		if(mouseInput.isDragged()){
			isDragged = true;
			Rectangle startRect =  new Rectangle(mouseInput.getStartDrag().x, mouseInput.getStartDrag().y, 1, 1);
		}else{
			if(isDragged){
				Rectangle startRect, endRect;
				try{
					startRect =  new Rectangle(mouseInput.getStartDrag().x, mouseInput.getStartDrag().y, 1, 1);
				}catch(NullPointerException e){
					startRect = new Rectangle(0, 0, 0, 0);
				}try{
					endRect = new Rectangle(mouseInput.getEndDrag().x, mouseInput.getEndDrag().y, 1, 1);
				}catch(NullPointerException e){
					endRect = new Rectangle(0, 0, 0, 0);
				}
			}
			isDragged = false;
		}
	}
	
	public void hover(){
		if(handler.getEntityManager().getPaused()){
			
		}else{
			if(player.getSkillBar() != null){
				outer: {
					for(SkillTracker skill : player.getSkillBar()){
						if(skill != null){
							if(mouseInput.getMouseBounds().intersects(skill.getBounds())){
								skillName = skill.getSkillType().getName();
								break outer;
							}
						}
					}
					skillName = null;
				}
			}
		}
	}
	
	// GETTERS AND SETTERS
	public Handler getHandler(){
		return handler;
	}

	public ItemManager getItemManager(){
		return itemManager;
	}
	
	public void addSkillList(SkillText text){
		skillList.add(text);
	}
}