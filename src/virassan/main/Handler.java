package virassan.main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import virassan.entities.Entity;
import virassan.entities.EntityManager;
import virassan.entities.creatures.player.Player;
import virassan.gfx.GameCamera;
import virassan.input.KeyInput;
import virassan.input.MouseInput;
import virassan.items.ItemManager;
import virassan.main.states.LaunchLoad;
import virassan.main.states.LaunchMenu;
import virassan.main.states.LaunchNew;
import virassan.main.states.MenuCharacter;
import virassan.main.states.MenuInventory;
import virassan.main.states.MenuLevelUp;
import virassan.main.states.MenuMap;
import virassan.main.states.MenuQuest;
import virassan.main.states.MenuSettings;
import virassan.main.states.MenuSkills;
import virassan.main.states.NPCDialog;
import virassan.main.states.NPCShop;
import virassan.main.states.States;
import virassan.world.World;
import virassan.world.maps.Map;

/**
 * To loop through all objects in game and individually update them and render them to the screen.
 * @author Virassan
 *
 */
public class Handler {
	// COLORS
	public static final Color GOLD = new Color(218, 165, 32),
		STAMINA_GREEN = new Color(88, 195, 79),
		MANA_BLUE = new Color(3, 165, 249),
		HEALTH_RED = new Color(223, 4, 4),
		HEALTH_ORANGE = new Color(251, 57, 5),
		BARELY_GRAY = new Color(235, 235, 235),
		PURPLE = new Color(125, 5, 195),
		LAVENDER = new Color(143, 107, 185),
		RED_VIOLET = new Color(149, 9, 147),
		BLUE_VIOLET = new Color(98, 6, 193),
		HOT_PINK = new Color(255, 5, 200),
		SELECTION_HIGHLIGHT = new Color(255, 255, 255, 50),
		SELECTION_LOWLIGHT = new Color(0, 0, 0, 70),
		SELECTION_MIDLIGHT = new Color(235, 235, 235, 30),
		ITEM_MENU = new Color(0, 0, 0, 215);
	// STATES
	public static NPCDialog NPCDIALOG;
	public static NPCShop NPCSHOP;
	public static MenuCharacter MENUCHAR;
	public static MenuInventory MENUINV;
	public static MenuMap MENUMAP;
	public static MenuQuest MENUQUEST;
	public static MenuSettings MENUSET;
	public static MenuSkills MENUSKILLS;
	public static MenuLevelUp MENULVL;
	public static LaunchMenu LAUNCHMENU;
	public static LaunchNew LAUNCHNEW;
	public static LaunchLoad LAUNCHLOAD;
	public static World WORLD;
	
	private Player player;
	private Game game;
	private States curState;
	
	//private boolean isNaming;
	
	public LinkedList<Entity> object = new LinkedList<Entity>();
	
	/**
	 * Contsructs the Handler
	 * @param game Game object - ie main gameloop
	 */
	public Handler(Game game){
		this.game = game;
		//isNaming = true;
		player = new Player(this, ID.Player);
		LAUNCHLOAD = new LaunchLoad(this);
		LAUNCHNEW = new LaunchNew(this);
		LAUNCHMENU = new LaunchMenu(this);
		MENULVL = new MenuLevelUp(this);
		MENUQUEST = new MenuQuest(this);
		MENUSET = new MenuSettings(this);
		MENUSKILLS = new MenuSkills(this);
		MENUINV = new MenuInventory(this);
		MENUMAP = new MenuMap(this);
		MENUCHAR = new MenuCharacter(this);
		NPCDIALOG = new NPCDialog(this);
		NPCSHOP = new NPCShop(this);
		curState = States.LaunchMenu;
	}
	
	/**
	 * Ticks the World
	 * @param delta 
	 */
	public void tick(double delta)
	{
		/*
		isNaming = false;
		if(!isNaming){
			world.tick();
		}else if(isNaming){
			getKeyInput().tick();
		}
		*/
		switch(curState){
		case LaunchLoad:
			LAUNCHLOAD.tick(delta);
			break;
		case LaunchMenu:
			LAUNCHMENU.tick(delta);
			break;
		case LaunchNew:
			LAUNCHNEW.tick(delta);
			break;
		case MenuCharacter:
			MENUCHAR.tick(delta);
			break;
		case MenuInventory:
			MENUINV.tick(delta);
			break;
		case MenuLevelUp:
			MENULVL.tick(delta);
			break;
		case MenuMap:
			MENUMAP.tick(delta);
			break;
		case MenuQuest:
			MENUQUEST.tick(delta);
			break;
		case MenuSettings:
			MENUSET.tick(delta);
			break;
		case MenuSkills:
			MENUSKILLS.tick(delta);
			break;
		case NPCDialog:
			NPCDIALOG.tick(delta);
			break;
		case NPCShop:
			NPCSHOP.tick(delta);
			break;
		case World:
			WORLD.tick(delta);
			break;
		default:
			LAUNCHMENU.tick(delta);
			break;
		}
	}
	
	/**
	 * Renders the World
	 * @param g
	 */
	public void render(Graphics g)
	{
		/*
		if(isNaming){
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(200, 200, 200, 20);
			g.setColor(Color.BLACK);
			g.drawString("What would you like to Name your character?", 200, 190);
			String text = getKeyInput().getTyped();
			g.drawString(text, 215, 215);
			if(getKeyInput().enter){
				isNaming = false;
				getPlayer().setName(text);
				getKeyInput().isTyping(false);
			}
		}
		*/
		switch(curState){
		case LaunchLoad:
			LAUNCHLOAD.render(g);
			break;
		case LaunchMenu:
			LAUNCHMENU.render(g);
			break;
		case LaunchNew:
			LAUNCHNEW.render(g);
			break;
		case MenuCharacter:
			MENUCHAR.render(g);
			break;
		case MenuInventory:
			MENUINV.render(g);
			break;
		case MenuLevelUp:
			MENULVL.render(g);
			break;
		case MenuMap:
			MENUMAP.render(g);
			break;
		case MenuQuest:
			MENUQUEST.render(g);
			break;
		case MenuSettings:
			MENUSET.render(g);
			break;
		case MenuSkills:
			MENUSKILLS.render(g);
			break;
		case NPCDialog:
			NPCDIALOG.render(g);
			break;
		case NPCShop:
			NPCSHOP.render(g);
			break;
		case World:
			WORLD.render(g);
			break;
		default:
			LAUNCHMENU.render(g);
			break;
		}
	}	
	
	// GETTERS AND SETTERS
	public void setWorld(World world){
		WORLD = world;
		player = world.getPlayer();
	}
	
	public void setState(States state){
		curState = state;
	}
	
	public States getState(){
		return curState;
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public int getWidth(){
		return game.getWidth();
	}
	
	public int getHeight(){
		return game.getHeight();
	}
	
	public GameCamera getGameCamera(){
		return game.getGameCamera();
	}
	
	public KeyInput getKeyInput(){
		return game.getKeyInput();
	}
	
	public MouseInput getMouseInput(){
		return game.getMouseInput();
	}

	public Player getPlayer() {
		return player;
	}
	
	public ItemManager getItemManager(){
		return WORLD.getItemManager();
	}
	
	public EntityManager getEntityManager(){
		return this.getMap().getEntityManager();
	}
	
	public Map getMap(){
		return WORLD.getMap();
	}
}