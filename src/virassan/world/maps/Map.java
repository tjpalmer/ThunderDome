package virassan.world.maps;

import java.awt.Graphics;

import virassan.entities.EntityManager;
import virassan.gfx.Assets;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.utils.Vector2F;


/**
 * Creates a Map Object and loads Map from JSON file. Handles rendering and ticking of EntityManager.
 * @author Virassan
 *
 */
public class Map{
	
	public static Tile grass, water;
	
	private Handler handler;
	private String filepath;
	private String mapName;
	private String mapID;
	private EntityManager entityManager;
	
	private int width, height, spawn_x, spawn_y;
	private int[][] worldTiles;
	
	
	/**
	 * Creates the Map
	 * @param handler Pass the game's Handler through
	 * @param filepath Filepath of the map JSON
	 */
	public Map(Handler handler, String filepath, String mapName, String mapID, int height, int width, int spawn_x, int spawn_y, int[][] worldTiles) {
		this.handler = handler;
		this.filepath = filepath;
		this.mapName = mapName;
		this.mapID = mapID;
		this.height = height;
		this.width = width;
		this.spawn_x = spawn_x;
		this.spawn_y = spawn_y;
		this.worldTiles = worldTiles;
		entityManager = new EntityManager(handler);
		if(handler.getPlayer() == null)
			System.out.println("Error Message: Map_MapConstructor Player in Handler is null.");
		entityManager.setPlayer(handler.getPlayer());
	}
	
	public void render(Graphics g){
		int xStart = (int)Math.max(0, handler.getGameCamera().getxOffset() / Tile.TILE_WIDTH);
		int xEnd = (int)Math.min(getWidth(), (handler.getGameCamera().getxOffset() + handler.getWidth())/ Tile.TILE_WIDTH + 3);
		int yStart = (int)Math.max(0, handler.getGameCamera().getyOffset() / Tile.TILE_HEIGHT);
		int yEnd = (int)Math.min(getHeight(), (handler.getGameCamera().getyOffset() + handler.getHeight())/ Tile.TILE_HEIGHT + 3);
		for(int y = yStart; y < yEnd; y++){
			for(int x = xStart; x < xEnd; x++){
				getTile(x, y).render(g, (int) (x*Tile.TILE_WIDTH - handler.getGameCamera().getxOffset()), (int) (y*Tile.TILE_HEIGHT - handler.getGameCamera().getyOffset()));
			}
		}
		entityManager.render(g);
	}

	public void tick(double delta){
		entityManager.tick(delta);
	}
	
	/**
	 * Returns the tile at the column and row given.
	 * @param x Column index number of Tile - ie the Array's first dimension. If passing the pixel coordinate, first divide by the Tile width.
	 * @param y Row index number of Tile - ie the Array's second dimension. If passing the pixel coordinate, first divide by the Tile height.
	 * @return the Tile object at that location.
	 */
	public Tile getTile(int x, int y){
		if(x < 0 || y <0 || x >= width || y >= height){
			return Tile.tiles[0];
		}
		Tile t = Tile.tiles[worldTiles[x][y]];
		if(t == null){
			return Tile.tiles[1];
		}
		return t;
	}
	
	/**
	 * Initializes Tiles 
	 */
	public static void init(){
		grass = new Tile(Assets.grass, 0, false);
		water = new Tile(Assets.water, 1, true);
	}
	
	public int[][] getWorldTiles(){
		return worldTiles;
	}
	
	public void setWorldTiles(int[][] worldTiles){
		this.worldTiles = worldTiles;
	}
	
	public void setTile(int x, int y, int num){
		worldTiles[x][y] = num;
	}
	
	public String getMapName(){
		return mapName;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public EntityManager getEntityManager(){
		return entityManager;
	}
	
	/**
	 * Returns the filepath of the Map - mostly for saving and loading saves
	 * @return
	 */
	public String getFilepath(){
		return filepath;
	}
	
	public String getMapID(){
		return mapID;
	}
	
	public Vector2F getSpawn(){
		return new Vector2F(spawn_x, spawn_y);
	}
}
