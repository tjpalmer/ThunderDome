package virassan.world;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import virassan.entities.Entity;
import virassan.entities.creatures.Creature;
import virassan.entities.creatures.enemies.Enemy;
import virassan.entities.creatures.npcs.Merchant;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.creatures.player.Player;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.items.ItemManager;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.main.states.States;
import virassan.world.maps.Map;
import virassan.world.maps.Tile;


/**
 * Creates the world!
 * @author Virassan
 *
 */
public class World {

	private Handler handler;
	private Player player;
	private HUDManager hud;
	private Map map;
	private MouseInput mouseInput;
	private KeyInput keyInput;

	private boolean isDragged;
	private long attackTimer, attackLast, attackWait = 200;
	
	public World(Handler handler){
		this.handler = handler;
		this.handler.setWorld(this);
		player = new Player(handler, ID.Player);
		hud = new HUDManager(handler);
		map = new Map(handler, "");
		keyInput = handler.getKeyInput();
		mouseInput = handler.getMouseInput();
		isDragged = false;
	}
	
	public World(Handler handler, Player player){
		this(handler);
		this.player = player;
	}
	
	public void tick(){
		map.tick();
		hud.tick();
		//TODO: move any user input from HUD while game is not paused, into HERE
		// Mouse Input
		drag();
		if(!mouseInput.getLeftClicks().isEmpty()){
			leftClick();
		}
		if(!mouseInput.getRightClicks().isEmpty()){
			rightClick();
		}
		hover();
		HUDManager.MENUTIMER += System.currentTimeMillis() - HUDManager.MENULAST;
		HUDManager.MENULAST = System.currentTimeMillis();
		boolean pause = handler.getEntityManager().getPaused();
		// If NOT paused
		if(!pause){
			if(HUDManager.MENUTIMER > HUDManager.MENUWAIT){
				// Inventory
				if(keyInput.I){
					handler.getEntityManager().setPaused(true);
					handler.setState(States.MenuInventory);
					HUDManager.MENUTIMER = 0;
				} // Character Sheet
				else if(keyInput.C){
					//TODO: possibly add a detailed Character Info Page with stuff like "Slimes killed: 20" etc
					HUDManager.MENUTIMER = 0;
					handler.getEntityManager().setPaused(true);
					handler.setState(States.MenuCharacter);
				} // Quest Log
				else if(keyInput.L){
					HUDManager.MENUTIMER = 0;
					handler.getEntityManager().setPaused(true);
					handler.setState(States.MenuQuest);
				} // Help Menu
				else if(keyInput.esc){
					HUDManager.MENUTIMER = 0;
					handler.getEntityManager().setPaused(true);
					handler.setState(States.MenuSettings);
				} // Skillbook
				else if(keyInput.K){
					HUDManager.MENUTIMER = 0;
					handler.getEntityManager().setPaused(true);
					handler.setState(States.MenuSkills);
				} // Level Up Menu
				else if(keyInput.T){
					HUDManager.MENUTIMER = 0;
					handler.getEntityManager().setPaused(true);
					handler.setState(States.MenuLevelUp);
				} // NPC Interaction/Dialog
				else if(keyInput.F){
					outer: {
						for(Entity e : map.getEntityManager().getEntities()){
							if(e instanceof NPC){
								HUDManager.MENUTIMER = 0;
								handler.getEntityManager().setPaused(true);
								handler.setState(States.NPCDialog);
								Handler.NPCDIALOG.NPCInteract((NPC)e);
								break outer;
							}else if(e instanceof Merchant){
								HUDManager.MENUTIMER = 0;
								handler.getEntityManager().setPaused(true);
								handler.setState(States.NPCShop);
								Handler.NPCSHOP.merchantInteract((Merchant)e);
								break outer;
							}
						}
					}
				}else if(keyInput.space){
					HUDManager.MENUTIMER = 0;
					handler.getEntityManager().setPaused(true);
				}else if(keyInput.one || keyInput.two || keyInput.three || keyInput.four || keyInput.five){
					int index = 0;
					if(keyInput.two){
						index = 1;
					}else if(keyInput.three){
						index = 2;
					}else if(keyInput.four){
						index = 3;
					}else if(keyInput.five){
						index = 4;
					}
					player.setSkillActive(true);
					player.setActiveSkill(player.getSkillBar()[index]);
					HUDManager.MENUTIMER = 0;
				}
			}
		}else if(keyInput.space){
			HUDManager.MENUTIMER = 0;
			handler.getEntityManager().setPaused(false);
		}
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(player.getSkillActive()){
			if(clicks.element() != null){
				outer :{
					while(clicks.element() != null){
						Point head = clicks.poll().getObject();
						double x = head.getX();
						double y = head.getY();
						ID target = player.getActiveSkill().getTarget();
						for(Entity e : handler.getEntityManager().getEntities()){
							if(e.getId() == target){
								if(new Rectangle((int)x, (int)y, 1, 1).intersects(new Rectangle((int)(e.getX() - handler.getGameCamera().getxOffset()), (int)(e.getY() - handler.getGameCamera().getyOffset()), e.getWidth(), e.getHeight()))){
									player.skillAction(player.getActiveSkill().getTarget(), player.getActiveSkill(), e);
									break outer;
								}
							}
						}
					}
				}
				player.setSkillActive(false);
			}
		}else if(!player.getSkillActive()){
			outer :{
				while(clicks.element() != null){
					Point head = clicks.poll().getObject();
					double x = head.getX();
					double y = head.getY();
					for(Entity e : handler.getEntityManager().getEntities()){
						if(e instanceof NPC){
							if(new Rectangle((int)x, (int)y, 1, 1).intersects(new Rectangle((int)e.getX(), (int)e.getY(), e.getWidth(), e.getHeight()))){
								Handler.NPCDIALOG.NPCInteract((NPC)e);
								handler.setState(States.NPCDialog);
								break outer;
							}
						}else if(e instanceof Merchant){
							if(new Rectangle((int)x, (int)y, 1, 1).intersects(new Rectangle((int)(e.getX() - handler.getGameCamera().getxOffset()), (int)(e.getY() - handler.getGameCamera().getyOffset()), e.getWidth(), e.getHeight()))){
								handler.setState(States.NPCShop);
								Handler.NPCSHOP.merchantInteract((Merchant)e);
								break outer;
							}
						}else if(e instanceof Enemy){
							if(new Rectangle((int)x, (int)y, 1, 1).intersects(new Rectangle((int)(e.getX() - handler.getGameCamera().getxOffset()), (int)(e.getY() - handler.getGameCamera().getyOffset()), e.getWidth(), e.getHeight()))){
								//TODO: do the attack thing with weapon
								attackTimer += System.currentTimeMillis() - attackLast;
								if(attackTimer > attackWait){
									try{
										player.attack(player.getStats().getMainH(), (Creature)e);
									}catch(NullPointerException n){
										if(player.getStats().getMainH() == null){
											player.getStats().addEventText("No Weapon Equiped");
										}
									}
									attackTimer = 0;
									attackLast = System.currentTimeMillis();
								}
								break outer;
							}
						}
					}
					for(SkillTracker s : player.getSkillBar()){
						if(s != null){
							if(new Rectangle((int)x, (int)y, 1, 1).intersects(s.getBounds())){
								player.setSkillActive(true);
								player.setActiveSkill(s);
								break outer;
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
	
	/**
	 * Renders the World 
	 * @param g
	 */
	public void render(Graphics g){
		int xStart = (int)Math.max(0, handler.getGameCamera().getxOffset() / Tile.TILE_WIDTH);
		int xEnd = (int)Math.min(map.getWidth(), (handler.getGameCamera().getxOffset() + handler.getWidth())/ Tile.TILE_WIDTH + 3);
		int yStart = (int)Math.max(0, handler.getGameCamera().getyOffset() / Tile.TILE_HEIGHT);
		int yEnd = (int)Math.min(map.getHeight(), (handler.getGameCamera().getyOffset() + handler.getHeight())/ Tile.TILE_HEIGHT + 3);
		for(int y = yStart; y < yEnd; y++){
			for(int x = xStart; x < xEnd; x++){
				map.getTile(x, y).render(g, (int) (x*Tile.TILE_WIDTH - handler.getGameCamera().getxOffset()), (int) (y*Tile.TILE_HEIGHT - handler.getGameCamera().getyOffset()));
			}
		}
		map.render(g);
		hud.render(g);
	}
	
	/**
	 * Sets the current map
	 * @param filepath The filepath of the map JSON file.
	 */
	public void setMap(String filepath){
		map = new Map(handler, filepath);
		System.out.println("Loading Map!");
		map.loadMap(filepath);
	}
	
	public Player getPlayer(){
		return player;
	}

	public void setPlayer(Player player){
		this.player = player;
	}
	
	public ItemManager getItemManager(){
		return hud.getItemManager();
	}
	
	public void setHUD(HUDManager hud){
		this.hud = hud;
	}
	
	public HUDManager getHUD(){
		return hud;
	}

	public Map getMap() {
		return map;
	}
}
