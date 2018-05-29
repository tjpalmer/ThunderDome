package virassan.entities;

import java.awt.Graphics;
import java.awt.Rectangle;

import virassan.main.Handler;
import virassan.utils.Vector2F;

/**
 * Creates Entities, creates bounds, and checks collision
 * @author Virassan
 *
 */
public abstract class Entity {

	protected Handler handler;
	protected Vector2F vector;
	protected float velX, velY; //speed
	protected int width, height;
	protected boolean isDead;
	
	protected Rectangle bounds;
	
	/**
	 * Constructs the Entity
	 * @param handler the handler
	 * @param x x-coord
	 * @param y y-coord
	 * @param width 
	 * @param height
	 */
	public Entity(Handler handler, float x, float y, int width, int height){
		this.handler = handler;
		vector = new Vector2F(x,y);
		this.width = width;
		this.height = height;
		bounds = new Rectangle(0,0, width, height);
		isDead = false;
	}
	
	/**
	 * Returns the rectangle of the entity's collision bounds
	 * @param xOffset x coord
	 * @param yOffset y coord
	 * @return rectangle the collision box
	 */
	public Rectangle getCollisionBounds(float xOffset, float yOffset){
		return new Rectangle((int) (vector.dX + bounds.x + xOffset), (int) (vector.dY + bounds.y + yOffset), bounds.width, bounds.height);
	}
	
	/**
	 * Returns if collision or not
	 * @param xOffset 
	 * @param yOffset 
	 * @return true is collision, false if not
	 */
	public boolean checkEntityCollisions(float xOffset, float yOffset){
		for(Entity e : handler.getMap().getEntityManager().getEntities()){
			if(e.equals(this)){
				continue;
			}
			if(e.getCollisionBounds(0f, 0f).intersects(this.getCollisionBounds(xOffset, yOffset))){
				return true;
			}
		}
		return false;
	}

	public abstract void tick(double delta);
	public abstract void render(Graphics g);
	public abstract void unPause();
	
	
	//GETTERS AND SETTERS
	
	
	public Rectangle getBounds(){
		return bounds;
	}
	
	/**
	 * Returns the width of the entity
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width of the entity
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns the Height of the entity
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height of the entity
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Sets the x-coord of the entity
	 * @param x
	 */
	public void setX(float x) {
		vector.dX = x;
	}

	/**
	 * Sets the y-coord of the entity
	 * @param y
	 */
	public void setY(float y) {
		vector.dY = y;
	}
	
	public Vector2F getCenter(){
		return new Vector2F(vector.dX + (width/2), vector.dY + (height/2));
	}
	
	/**
	 * Sets the x Velocity of the Entity
	 * @param velX
	 */
	public void setVelX(int velX)
	{
		this.velX = velX;
	}
	
	/**
	 * Sets the y Velocity of the Entity
	 * @param velY
	 */
	public void setVelY(int velY)
	{
		this.velY = velY;
	}
	
	/**
	 * Returns the x-coord of the entity
	 * @return
	 */
	public float getX()
	{
		return vector.dX;
	}
	
	/**
	 * Returns the y-coord of the entity
	 * @return
	 */
	public float getY()
	{
		return vector.dY;
	}
	
	/**
	 * Returns the x Velocity of the Entity
	 * @return
	 */
	public float getVelX()
	{
		return velX;
	}
	
	/**
	 * Returns the y Velocity of the Entity
	 * @return
	 */
	public float getVelY(){
		return velY;
	}
	
	public Handler getHandler(){
		return handler;
	}
	
	public boolean isDead(){
		return isDead;
	}
	
	public void isDead(boolean b){
		isDead = b;
	}	
	
	abstract public Class findClass();
	
}
