package virassan.entities.creatures.enemies;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import virassan.entities.creatures.Attack;
import virassan.gfx.Animation;
import virassan.gfx.Assets;
import virassan.items.Drop;
import virassan.main.Handler;
import virassan.main.ID;

public class Soldier extends Enemy{
	
	private BufferedImage sprite;
	private float spawnX, spawnY;
	
	public Soldier(Handler handler, String name, float x, float y, int height, int width, ID id, EnemyType type, EnemySpecies species, int level) {
		super(handler, name, x, y, level, id, type, species);
		aggroDistance = (int)(Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2))) * 3;
		stats.setEntity(this);
		stats.setMaxHealth(70 + (level * 5));
		stats.setLevel(level);
		stats.setArmorRating(2 * level);
		stats.setCritChance(0.00001F * level);
		stats.setCritMult(0.005F * level);
		damaged = false;
		isMoving = false;
		
		direction = new Random().nextInt(4);
		walking = new Animation[5];
		this.spawnX = x;
		this.spawnY = y;		
		
		//Collision Bounds
		bounds.x = 2;
		bounds.y = (height/2) + 10;
		bounds.width = width - 15;
		bounds.height = (height/2) - 10;
		
		//Animation Shit
		String filepath = species.getFilePath() + type.getFilepath();
		Assets entitySprites = new Assets(filepath, height, width);
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
		
		defaultAttack = new Attack(900, 3 * level, animation, 25, 25, "Default");
		
		//Moving Bounds
		walkBounds.x = (int)(spawnX  - handler.getGameCamera().getxOffset()- type.getWidth()*5);
		walkBounds.y = (int)(spawnY  - handler.getGameCamera().getyOffset()- type.getHeight()*5);
		walkBounds.width =  type.getWidth()*20;
		walkBounds.height = type.getHeight()*20;
	}
	
	public Soldier(Handler handler, String name, float x, float y, int height, int width, ID id, EnemyType type, EnemySpecies species, int level, Drop[] drops){
		this(handler, name, x, y, height, width, id, type, species, level);
		this.drops = drops;
	}
	
	public Soldier(Handler handler, String name, float x, float y, ID id, EnemyType type, EnemySpecies species, int level, Drop[] drops) {
		this(handler, name, x, y, 32, 32, id, type, species, level, drops);
	}

	public Soldier(Handler handler, String name, float x, float y, int height, int width, ID id, EnemyType type, EnemySpecies species, int level, ArrayList<Drop> drops){
		this(handler, name, x, y, height, width, id, type, species, level);
		this.drops =  new Drop[drops.size()];
		for(int i = 0; i < drops.size(); i++){
			this.drops[i] = drops.get(i);
		}
		
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
		/* Display's the coords of the entity above their head
		g.drawString(x + " " + y, (int)(x - handler.getGameCamera().getxOffset())-14, (int)(y - handler.getGameCamera().getyOffset()) - 5);
		*/
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		g.drawString(name, (int)(x - handler.getGameCamera().getxOffset()), (int)(y - handler.getGameCamera().getyOffset()+2));
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

	public String toString(){
		return name;
	}
	
}
