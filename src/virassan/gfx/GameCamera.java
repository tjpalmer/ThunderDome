package virassan.gfx;

import virassan.entities.Entity;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.utils.Utils;
import virassan.world.maps.Tile;

/**
 * Handles the GameCamera
 * @author Virassan
 *
 */
public class GameCamera {

	private float xOffset, yOffset;
	private float x, y;
	private float height, width;
	private Handler handler;
	
	public GameCamera(Handler handler, float xOffset, float yOffset){
		this.handler = handler;
		setCameraResolution();
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public void setCameraResolution(){
		width = handler.getWidth();
		//TODO: wait to use until asset scaling is set up
		//height = width * (9.0f / 16.0f);
		height = handler.getHeight();
		System.out.println("GameCamera_setCameraResolution height is: " + height + ", width: " + width);
	}
	
	/**
	 * Checks to see if at the edges of the world/map
	 */
	public void checkBlankSpace(){
		xOffset = Utils.clamp((int)xOffset, 0, handler.getMap().getWidth() * Tile.TILE_WIDTH - handler.getWidth());
		yOffset = Utils.clamp((int)yOffset, 0, handler.getMap().getHeight() * Tile.TILE_HEIGHT - handler.getHeight());
	}
	
	/**
	 * Centers the Camera on the entity passed
	 * @param e Entity to be centered on
	 */
	public void centerOnEntity(Entity e){
		xOffset = e.getX() - handler.getWidth() / 2 + e.getWidth() / 2;
		yOffset = e.getY() - handler.getHeight() / 2 + e.getHeight() / 2;
		checkBlankSpace();
	}
	
	/**
	 * Moves the Game Camera
	 * @param xAmt horizontal amount to move
	 * @param yAmt vertical amount to move
	 */
	public void move (float xAmt, float yAmt){
		xOffset += xAmt;
		yOffset += yAmt;
		checkBlankSpace();
	}
	
	//GETTERS AND SETTERS
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}

	public float getxOffset() {
		return xOffset;
	}
	
	public void setxOffset(float xOffset) {
		this.xOffset = xOffset;
	}

	public float getyOffset() {
		return yOffset;
	}

	public void setyOffset(float yOffset) {
		this.yOffset = yOffset;
	}
	
}
