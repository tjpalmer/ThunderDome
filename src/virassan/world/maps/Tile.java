package virassan.world.maps;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import virassan.gfx.Assets;

/**
 * Creates Map Tile objects
 * @author Virassan
 *
 */
public class Tile {
	
	public static Tile grass, water;
	public static Tile[] tiles = new Tile[256];
	public static final int TILE_WIDTH = 64, 
							TILE_HEIGHT = 64;
	protected Image texture;
	protected final int idNum;
	
	private int width, height;
	private boolean isSolid;
	private float collisionDmg;
	
	/**
	 * Creates the Tile object
	 * @param texture the BufferedImage from Assets
	 * @param idNum Desired ID Number
	 * @param isSolid true if solid, false if not solid
	 */
	public Tile(Image texture, int idNum, boolean isSolid) {
		this.texture = texture;
		this.idNum = idNum;
		this.isSolid = isSolid;
		tiles[idNum] = this;
		width = TILE_WIDTH;
		height = TILE_HEIGHT;
		collisionDmg = 0;
	}

	/**
	 * For Tiles that do damage to the player when touched
	 * @param texture the BufferedImage from Assets
	 * @param idNum Desired ID Number 
	 * @param isSolid true is solid, false if not
	 * @param collisionDmg amount of damage done per second when player intersects
	 */
	public Tile(BufferedImage texture, int idNum, boolean isSolid, float collisionDmg) {
		this.texture = texture;
		this.idNum = idNum;
		this.isSolid = isSolid;
		tiles[idNum] = this;
		width = TILE_WIDTH;
		height = TILE_HEIGHT;
		this.collisionDmg = collisionDmg;
	}
	
	/**
	 * Creates the Tile object - for Tiles with different dimensions than standard
	 * @param texture the BufferedImage from Assets
	 * @param idNum the Desired ID Number
	 * @param isSolid true if solid, false if not solid
	 * @param width the width
	 * @param height the height
	 */
	public Tile(BufferedImage texture, int idNum, boolean isSolid, int width, int height){
		this.texture = texture;
		this.idNum = idNum;
		this.isSolid = isSolid;
		tiles[idNum] = this;
		this.width = width;
		this.height = height;
	}
	
	
	public void render(Graphics g, int x, int y) {
		g.drawImage(texture, x, y, TILE_WIDTH, TILE_HEIGHT, null);
	}
	
	// GETTERS AND SETTERS
	public boolean isSolid(){
		return isSolid;
	}
	
	public int getIDNum(){
		return idNum;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public float getCollisionDamage(){
		return collisionDmg;
	}
}
