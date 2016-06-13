package virassan.main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import virassan.entities.Entity;
import virassan.entities.creatures.Player;
import virassan.gfx.GameCamera;
import virassan.input.KeyInput;
import virassan.items.ItemManager;
import virassan.states.StateManager;
import virassan.world.World;


/**
 * To loop through all objects in game and individually update them and render them to the screen.
 * @author Virassan
 *
 */
public class Handler {

	private Game game;
	private World world;
	
	private boolean isNaming;
	
	public LinkedList<Entity> object = new LinkedList<Entity>();
	
	/**
	 * Contsructs the Handler
	 * @param game Game object - ie main gameloop
	 */
	public Handler(Game game){
		this.game = game;
		isNaming = true;
	}
	
	/**
	 * Ticks the World
	 */
	public void tick()
	{
		if(!isNaming){
			world.tick();
		}
	}
	
	/**
	 * Renders the World
	 * @param g
	 */
	public void render(Graphics g)
	{
		//TODO: changed isNaming to false to skip it for testing purposes
		isNaming = false;
		if(isNaming){
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(200, 200, 200, 20);
			g.setColor(Color.BLACK);
			g.drawString("What would you like to Name your character?", 200, 190);
			getKeyInput().tick();
			String text = getKeyInput().getTyped();
			g.drawString(text, 215, 215);
			if(getKeyInput().enter){
				isNaming = false;
				getPlayer().setName(text);
				getKeyInput().isTyping(false);
			}
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
	
	public StateManager getStateManager(){
		return game.getStateManager();
	}

	public Player getPlayer() {
		return world.getPlayer();
	}
	
	public ItemManager getItemManager(){
		return world.getItemManager();
	}
}