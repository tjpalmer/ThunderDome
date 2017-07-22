package virassan.entities.statics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import virassan.entities.creatures.player.Player;
import virassan.gfx.Animation;
import virassan.gfx.Assets;
import virassan.gfx.SpriteSheet;
import virassan.main.Game;
import virassan.main.Handler;

public class Statics extends StaticEntity{
	
	private Animation anime;
	private BufferedImage image;
	private String name, newMap;
	private String idname = "";
	private boolean animated;
	private Rectangle eventBounds;
	private int spawn_x, spawn_y;

	public Statics(Handler handler, float x, float y, BufferedImage image, String entityName) {
		super(handler, x, y, Assets.IMAGE_WIDTH, Assets.IMAGE_HEIGHT, image, entityName);
		bounds.x = width/2 - 2;
		bounds.y = height - (height/3);
		bounds.width = width/3;
		bounds.height = height/3;
		this.image = image;
		name = entityName;
		animated = false;
	}
	
	public Statics(Handler handler, float x, float y, BufferedImage image){
		this(handler, x, y, image, null);
	}
	
	public Statics(Handler handler, float x, float y, int xBound, int yBound, int widthBound, int heightBound, BufferedImage image, String entityName) {
		this(handler, x, y, image, entityName);
		bounds.x = xBound;
		bounds.y = yBound;
		bounds.width = widthBound;
		bounds.height = heightBound;
	}
	
	public Statics(Handler handler, float x, float y, int xBound, int yBound, BufferedImage image, String entityName) {
		this(handler, x, y, image, entityName);
		bounds.x = xBound;
		bounds.y = yBound;
	}
	
	public Statics(Handler handler, float x, float y, BufferedImage image, int width, int height, String entityName){
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
	public Statics(Handler handler, float x, float y, int width, int height, BufferedImage[] images){
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
	public Statics(Handler handler, float x, float y, int width, int height, String idname, BufferedImage[] images, boolean dimension){
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
	public Statics(Handler handler, float x, float y, int width, int height, String idname, String newMap, int spawn_x, int spawn_y, BufferedImage[] images, boolean dimension){
		this(handler, x, y, width, height, idname, images, dimension);
		this.idname = idname;
		this.newMap = newMap;
		this.spawn_x = spawn_x;
		this.spawn_y = spawn_y;
		System.out.println(eventBounds.x + ", " + eventBounds.y + " - " + eventBounds.width + " X " + eventBounds.height);
	}
	
	public void tick() {
		if(animated){
			anime.tick();
		}
		switch(idname){
		case "warp_portal": eventCollision(); break;
		}
	}

	@Override
	public void render(Graphics g) {
		if(animated){
			g.drawImage(anime.getCurrentFrame(), (int)(x - handler.getGameCamera().getxOffset()), (int)(y - handler.getGameCamera().getyOffset()), width, height, null);	
			g.setColor(Color.BLACK);
			g.drawRect(getCollisionBounds().x, getCollisionBounds().y, getCollisionBounds().width, getCollisionBounds().height);
		}else{
			g.drawImage(image, (int)(x - handler.getGameCamera().getxOffset()), (int)(y - handler.getGameCamera().getyOffset()), width, height, null);
		}
	}

	/**
	 * For use of checking if Player collides with a Warp Portal
	 * @return
	 */
	public boolean eventCollision(){
		Player player = handler.getPlayer();
		if(player.getCollisionBounds(-handler.getGameCamera().getxOffset(), -handler.getGameCamera().getyOffset()).intersects(getCollisionBounds())){
			Handler.WORLD.setMap(newMap);
			handler.getPlayer().setX(spawn_x);
			handler.getPlayer().setY(spawn_y);
			System.out.println("WARP PORTAL");
			return true;
		}
		return false;
	}
	
	public Rectangle getCollisionBounds(){
		return new Rectangle((int)(eventBounds.x - handler.getGameCamera().getxOffset()), (int)(eventBounds.y - handler.getGameCamera().getyOffset()), eventBounds.width, eventBounds.height);
	}
	
	//GETTERS AND SETTERS
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
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
		// TODO Auto-generated method stub
		
	}
}
