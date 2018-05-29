package virassan.entities.statics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import virassan.entities.creatures.player.Player;
import virassan.gfx.Animation;
import virassan.gfx.Assets;
import virassan.main.Handler;

public class Statics extends StaticEntity{
	
	private Animation anime;
	private Image image;
	private String name, newMapID;
	private String idname = "";
	private boolean animated;
	private Rectangle eventBounds;
	private int spawn_x, spawn_y;

	public Statics(Handler handler, float x, float y, Image image, String entityName) {
		super(handler, x, y, Assets.IMAGE_WIDTH, Assets.IMAGE_HEIGHT, image, entityName);
		bounds.x = width/2 - 2;
		bounds.y = height - (height/3);
		bounds.width = width/3;
		bounds.height = height/3;
		this.image = image;
		name = entityName;
		animated = false;
	}
	
	public Statics(Handler handler, float x, float y, Image image){
		this(handler, x, y, image, null);
	}
	
	public Statics(Handler handler, float x, float y, int xBound, int yBound, int widthBound, int heightBound, Image image, String entityName) {
		this(handler, x, y, image, entityName);
		bounds.x = xBound;
		bounds.y = yBound;
		bounds.width = widthBound;
		bounds.height = heightBound;
	}
	
	public Statics(Handler handler, float x, float y, int xBound, int yBound, Image image, String entityName) {
		this(handler, x, y, image, entityName);
		bounds.x = xBound;
		bounds.y = yBound;
	}
	
	public Statics(Handler handler, float x, float y, Image image, int width, int height, String entityName){
		this(handler, x, y, image, entityName);
		this.width = width;
		this.height = height;
	}
	
	/**
	 * For animated Statics
	 * @param handler
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param images
	 */
	public Statics(Handler handler, float x, float y, int width, int height, Image[] images){
		super(handler, x, y, width, height);
		eventBounds = new Rectangle(0, 0, 0, 0);
		anime = new Animation(200, images);
		animated = true;
		anime.setAnimationLoop(true);
	}
	
	/**
	 * For animated statics that need an idname for distinguish themselves from others because of some special thing they do.
	 * @param handler
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param idname
	 * @param images
	 * @param dimension True means that when the player goes behind the static object, it will "disappear". False means it will "lay flat" against the background
	 */
	public Statics(Handler handler, float x, float y, int width, int height, String idname, Image[] images, boolean dimension){
		this(handler, x, y, width, height, images);
		this.idname = idname;
		dimensioned = dimension;
		eventBounds.x = (int)x + 5;
		eventBounds.y = (int)y + 5;
		eventBounds.width = width - 10;
		eventBounds.height = height - 10;
	}
	
	/**
	 * For Warp Portals
	 * @param handler
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param idname
	 * @param newMap
	 * @param spawn_x
	 * @param spawn_y
	 * @param images
	 * @param dimension
	 */
	public Statics(Handler handler, float x, float y, int width, int height, String idname, String newMapID, 
			int spawn_x, int spawn_y, Image[] images, boolean dimension){
		this(handler, x, y, width, height, idname, images, dimension);
		this.idname = idname;
		this.newMapID = newMapID;
		this.spawn_x = spawn_x;
		this.spawn_y = spawn_y;
	}
	
	public void tick(double delta) {
		if(animated){
			anime.tick(delta);
		}
		switch(idname){
		case "warp_portal": eventCollision(); break;
		}
	}

	@Override
	public void render(Graphics g) {
		float xrel = vector.normalize().dX ;//* handler.getGameCamera().getWidth();
		float yrel = vector.normalize().dY ;//* handler.getGameCamera().getHeight();
		
		if(animated){
			g.drawImage(anime.getCurrentFrame(), (int)(xrel - handler.getGameCamera().getxOffset()), (int)(yrel - handler.getGameCamera().getyOffset()), width, height, null);	
			g.setColor(Color.BLACK);
			g.drawRect(getCollisionBounds().x, getCollisionBounds().y, getCollisionBounds().width, getCollisionBounds().height);
		}else{
			g.drawImage(image, (int)(xrel - handler.getGameCamera().getxOffset()), (int)(yrel - handler.getGameCamera().getyOffset()), width, height, null);
		}
	}

	/**
	 * For use of checking if Player collides with a Warp Portal
	 * @return
	 */
	public boolean eventCollision(){
		Player player = handler.getPlayer();
		if(player.getCollisionBounds(-handler.getGameCamera().getxOffset(), -handler.getGameCamera().getyOffset()).intersects(getCollisionBounds())){
			Handler.LOADMAP.setNewSpawn(spawn_x, spawn_y);
			handler.setMapID(newMapID);
			System.out.println("Update Message: Statics_eventCollision WARP PORTAL");
			return true;
		}
		return false;
	}
	
	public Rectangle getCollisionBounds(){
		return new Rectangle((int)(eventBounds.x - handler.getGameCamera().getxOffset()), (int)(eventBounds.y - handler.getGameCamera().getyOffset()), eventBounds.width, eventBounds.height);
	}
	
	//GETTERS AND SETTERS
	
	public void setX(int x){
		vector.dX = x;
	}
	
	public void setY(int y){
		vector.dY = y;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public float getX() {
		return vector.dX;
	}

	public float getY() {
		return vector.dY;
	}
	
	public String getName(){
		if(name != null){
			return name;
		}else{
			return "";
		}
	}
	
	@Override
	public void unPause() {
	}

	@Override
	public Class findClass() {
		return super.getClass();
	}
}
