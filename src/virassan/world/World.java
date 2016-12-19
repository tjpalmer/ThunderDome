package virassan.world;

import java.awt.Graphics;

import virassan.entities.creatures.player.Player;
import virassan.gfx.hud.HUDManager;
import virassan.items.ItemManager;
import virassan.main.Handler;
import virassan.main.ID;
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
	

	public World(Handler handler){
		this.handler = handler;
		player = new Player(handler, 0, 0, ID.Player, 32, 32, "/textures/entities/blob_spritesheet_testing.png");
		hud = new HUDManager(handler);
	}
	
	public World(Handler handler, String mapFilepath){
		this.handler = handler;
		handler.setWorld(this);
		player = new Player(handler, 0, 0, ID.Player, 32, 32, "/textures/entities/blob_spritesheet_testing.png");
		setMap(mapFilepath);
		hud = new HUDManager(handler);
	}
	
	public void tick(){
		map.tick();
		hud.tick();
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
	
	public void setMap(String filepath){
		map = new Map(handler, filepath , "Hajime Village");
		map.loadMap();
	}
	
	public Player getPlayer(){
		return player;
	}

	public ItemManager getItemManager(){
		return hud.getItemManager();
	}
	
	public HUDManager getHUD(){
		return hud;
	}

	public Map getMap() {
		return map;
	}
}
