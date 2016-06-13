package virassan.entities.creatures.enemies;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import virassan.entities.creatures.Attack;
import virassan.entities.creatures.Stats;
import virassan.gfx.Animation;
import virassan.gfx.Assets;
import virassan.items.Drop;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.main.ID;

public class Soldier extends Enemy{
	
	private BufferedImage sprite;
	private float spawnX, spawnY;
	
	public Soldier(Handler handler, float x, float y, ID id, EnemyType type, int level) {
		super(handler, x, y, level, id, type);
		aggroDistance = (int)(Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2))) * 3;
		this.maxHealth = 50;
		stats = new Stats(this, maxHealth);
		damaged = false;
		isMoving = false;
		
		direction = new Random().nextInt(4);
		walking = new Animation[5];
		drops = new Drop[]{new Drop(Item.APPLE, 0.98), new Drop(Item.TOAST, 0.55)};
		this.spawnX = x;
		this.spawnY = y;		
		
		//Collision Bounds
		bounds.x = 2;
		bounds.y = 20;
		bounds.width = 20;
		bounds.height = 15;
		
		//Animation Shit
		Assets entitySprites = new Assets(type.getFilepath());
		sprite = entitySprites.getFront_1();
		
		//Animation Shtuff
		walkLeft = new Animation(200, entitySprites.getWalkingLeft());
		walkRight = new Animation(200, entitySprites.getWalkingRight());
		walkUp = new Animation(200, entitySprites.getWalkingUp());
		walkDown = new Animation(200, entitySprites.getWalkingDown());
		walking[0] = walkUp;
		walking[1] = walkDown;
		walking[2] = walkRight;
		walking[3] = walkLeft;
		animation = walkDown;
		
		defaultAttack = new Attack(700, 10, animation, 25, 25, "Default");
		
		//Moving Bounds
		walkBounds.x = (int)(spawnX  - handler.getGameCamera().getxOffset()- type.getWidth()*5);
		walkBounds.y = (int)(spawnY  - handler.getGameCamera().getyOffset()- type.getHeight()*5);
		walkBounds.width =  type.getWidth()*20;
		walkBounds.height = type.getHeight()*20;
	}

	@Override
	public void tick() {
		move();
		if(isMoving){
			animation.setAnimationLoop(true);
		}else{
			animation.setAnimationLoop(false);
		}
		animation.tick();
		stats.tick();
	}

	@Override
	public void render(Graphics g) {
		if(animation.getCurrentFrame() != null){
			g.drawImage(animation.getCurrentFrame(), (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), null);
		}else
			g.drawImage(sprite, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), null);
		stats.render(g);
		g.setColor(Color.CYAN);
		g.drawString(x + " " + y, (int)(x - handler.getGameCamera().getxOffset())-14, (int)(y - handler.getGameCamera().getyOffset()) - 5);
	}

	
	public float getSpawnX() {
		return spawnX;
	}

	public float getSpawnY() {
		return spawnY;
	}

	@Override
	public void resetTimer() {
		timer = 0;
		
	}

}
