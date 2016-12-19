package virassan.main;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.LinkedList;

import virassan.entities.Entity;
import virassan.entities.EntityManager;
import virassan.entities.creatures.player.Player;
import virassan.gfx.GameCamera;
import virassan.input.KeyInput;
import virassan.input.MouseInput;
import virassan.items.ItemManager;
import virassan.states.StateManager;
import virassan.utils.Utils;
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

	
	private Game game;
	private World world;
	
	private boolean isNaming, isSave;
	
	public LinkedList<Entity> object = new LinkedList<Entity>();
	
	/**
	 * Contsructs the Handler
	 * @param game Game object - ie main gameloop
	 */
	public Handler(Game game){
		this.game = game;
		isNaming = true;
		isSave = true;
	}
	
	/**
	 * Ticks the World
	 */
	public void tick()
	{
		//TODO: changed isNaming to false to skip it for testing purposes
		isNaming = false;
		if(!isNaming){
			world.tick();
		}else if(isNaming){
			getKeyInput().tick();
		}
		//TODO: figure out how to see if there's any JSON files in the "/saves/" folder
		if(isSave){
			boolean saveFile = new File("c:\\Users\\Virassan\\Documents\\!ThunderDome\\ThunderDome\\ThunderDome\\res\\saves\\testsave.json").isFile();
			if(saveFile){
				getKeyInput().tick();
				//TODO: do the loadGame thing
				if(getKeyInput().enter){
					isSave = false;
				}else if(getKeyInput().space){
					Utils.loadGame(this, "c:\\Users\\Virassan\\Documents\\!ThunderDome\\ThunderDome\\ThunderDome\\res\\saves\\testsave.json");
					isSave = false;
				}
			}else{
				isSave = false;
			}
		}
	}
	
	/**
	 * Renders the World
	 * @param g
	 */
	public void render(Graphics g)
	{
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
		}else if(isSave){
			g.setColor(Color.BLACK);
			g.drawString("To Start a New Game press ENTER", 200, 190);
			g.drawString("To Load the Last Save press SPACE", 200, 220);
		}
		else{
			world.render(g);
		}
	}	
	
	// GETTERS AND SETTERS

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
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
	
	public StateManager getStateManager(){
		return game.getStateManager();
	}

	public Player getPlayer() {
		return world.getPlayer();
	}
	
	public ItemManager getItemManager(){
		return world.getItemManager();
	}
	
	public EntityManager getEntityManager(){
		return this.getMap().getEntityManager();
	}
	
	public Map getMap(){
		return world.getMap();
	}
}