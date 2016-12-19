package virassan.entities.statics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import virassan.gfx.Assets;
import virassan.main.Game;
import virassan.main.Handler;

public class Statics extends StaticEntity{
	
	public static Statics[] statics = new Statics[300];
	
	private BufferedImage image;
	private String name;
	private int idNum;

	public Statics(Handler handler, int idNum, BufferedImage image, String entityName) {
		super(handler, 0, 0, Assets.IMAGE_WIDTH, Assets.IMAGE_HEIGHT, image, entityName);
		bounds.x = width/2 - 2;
		bounds.y = height - (height/3);
		bounds.width = width/3;
		bounds.height = height/3;
		this.image = image;
		statics[idNum] = this;
		name = entityName;
		this.idNum = idNum;
	}
	
	public Statics(Handler handler, int idNum, BufferedImage image) {
		super(handler, 0, 0, Assets.IMAGE_WIDTH, Assets.IMAGE_HEIGHT, image, null);
		bounds.x = width/2 - 2;
		bounds.y = height - (height/3);
		bounds.width = width/3;
		bounds.height = height/3;
		this.image = image;
		statics[idNum] = this;
		this.idNum = idNum;
	}
	
	public Statics(Handler handler, int idNum, int xBound, int yBound, int widthBound, int heightBound, BufferedImage image, String entityName) {
		super(handler, 0, 0, Assets.IMAGE_WIDTH, Assets.IMAGE_HEIGHT, image, entityName);
		bounds.x = xBound;
		bounds.y = yBound;
		bounds.width = widthBound;
		bounds.height = heightBound;
		this.image = image;
		statics[idNum] = this;
		name = entityName;
		this.idNum = idNum;
	}
	
	public Statics(Handler handler, int idNum, int widthBound, int heightBound, BufferedImage image, String entityName) {
		super(handler, 0, 0, Assets.IMAGE_WIDTH, Assets.IMAGE_HEIGHT, image, entityName);
		bounds.x = width;
		bounds.y = height;
		bounds.width = widthBound;
		bounds.height = heightBound;
		this.image = image;
		statics[idNum] = this;
		name = entityName;
		this.idNum = idNum;
	}
	
	public Statics(Handler handler, int idNum, int widthBound, int heightBound, BufferedImage image) {
		super(handler, 0, 0, Assets.IMAGE_WIDTH, Assets.IMAGE_HEIGHT, image, null);
		bounds.x = width;
		bounds.y = height;
		bounds.width = widthBound;
		bounds.height = heightBound;
		this.image = image;
		statics[idNum] = this;
		this.idNum = idNum;
	}
	
	public Statics(Handler handler, int idNum, int xBound, int yBound, int widthBound, int heightBound, BufferedImage image) {
		super(handler, 0, 0, Assets.IMAGE_WIDTH, Assets.IMAGE_HEIGHT, image, null);
		bounds.x = xBound;
		bounds.y = yBound;
		bounds.width = widthBound;
		bounds.height = heightBound;
		this.image = image;
		statics[idNum] = this;
		this.idNum = idNum;
	}
	
	public static void init(){
		new Statics(Game.handler, 0, 0, 0, Assets.flowers_1, "flowers_1");
		new Statics(Game.handler, 1, 0, 0, Assets.flowers_2, "flowers_2");
		new Statics(Game.handler, 2, Assets.animeDudeStanding, "Zane");
		new Statics(Game.handler, 3, Assets.stone_rock, "The Rock");
	}
	
	
	public void tick() {
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(image, (int)(x- handler.getGameCamera().getxOffset()), (int)(y - handler.getGameCamera().getyOffset()), width, height, null);
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
	
	public int getidNum(){
		return idNum;
	}

	@Override
	public void unPause() {
		// TODO Auto-generated method stub
		
	}
}
