package virassan.entities.creatures.enemies;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import virassan.entities.creatures.Attack;
import virassan.gfx.Animation;
import virassan.gfx.Assets;
import virassan.items.Drop;
import virassan.main.Handler;

public class Soldier extends Enemy{
	
	private BufferedImage sprite;
	private float spawnX, spawnY;
	
	public Soldier(Handler handler, String name, String enemyID, float x, float y, int height, int width, EnemySpecies species, int level) {
		super(handler, name, enemyID, x, y, level, species);
		this.enemyID = enemyID;
		aggroDistance = (int)(Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2))) * 3;
		stats.setEntity(this);
		stats.setMaxHealth(70 + (level * 5));
		stats.setLevel(level);
		stats.setArmorRating(2 * level);
		stats.setCritChance(0.00001F * level);
		stats.setCritMult(0.005F * level);
		damaged = false;
		isMoving = false;
		speed = 5.0F;
		
		ranInterval = new Random().nextInt(200)+300;
		direction = new Random().nextInt(4);
		walking = new Animation[8];
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
		int animeSpeed = 200;
		walkLeft = new Animation(animeSpeed, entitySprites.getWalkingLeft());
		walkRight = new Animation(animeSpeed, entitySprites.getWalkingRight());
		walkUp = new Animation(animeSpeed, entitySprites.getWalkingUp());
		walkDown = new Animation(animeSpeed, entitySprites.getWalkingDown());
		walking[0] = walkRight;
		walking[1] = walkRight;
		walking[2] = walkUp;
		walking[3] = walkLeft;
		walking[4] = walkLeft;
		walking[5] = walkLeft;
		walking[6] = walkDown;
		walking[7] = walkRight;
		animation = walkDown;
		
		defaultAttack = new Attack(900, 3 * level, animation, 55, "Default");
		
		//Moving Bounds
		walkBounds.x = (int)(spawnX  - handler.getGameCamera().getxOffset()- type.getWidth()*5);
		walkBounds.y = (int)(spawnY  - handler.getGameCamera().getyOffset()- type.getHeight()*5);
		walkBounds.width =  type.getWidth()*20;
		walkBounds.height = type.getHeight()*20;
	}
	
	public Soldier(Handler handler, String name, String enemyID, float x, float y, int height, int width, EnemySpecies species, int level, Drop[] drops){
		this(handler, name, enemyID, x, y, height, width, species, level);
		this.drops = drops;
	}
	
	public Soldier(Handler handler, String name, String enemyID, float x, float y, EnemySpecies species, int level, Drop[] drops) {
		this(handler, name, enemyID, x, y, 32, 32, species, level, drops);
	}

	public Soldier(Handler handler, String name, String enemyID, float x, float y, int height, int width, EnemySpecies species, int level, ArrayList<Drop> drops){
		this(handler, name, enemyID, x, y, height, width, species, level);
		this.drops =  new Drop[drops.size()];
		for(int i = 0; i < drops.size(); i++){
			this.drops[i] = drops.get(i);
		}
		
	}
	
	@Override
	public void tick(double delta) {
		move(delta);
		attack();
		if(isMoving){
			animation.setAnimationLoop(true);
		}else{
			animation.setAnimationLoop(false);
		}
		animation.tick(delta);
		stats.tick(delta);
	}

	@Override
	public void render(Graphics g) {
		float xrel = vector.normalize().dX ;//* handler.getGameCamera().getWidth();
		float yrel = vector.normalize().dY ;//* handler.getGameCamera().getHeight();
		
		if(animation.getCurrentFrame() != null){
			g.drawImage(animation.getCurrentFrame(), (int) (xrel - handler.getGameCamera().getxOffset()), (int) (yrel - handler.getGameCamera().getyOffset()), null);
		}else
			g.drawImage(sprite, (int) (xrel - handler.getGameCamera().getxOffset()), (int) (yrel - handler.getGameCamera().getyOffset()), null);
		stats.render(g);
		//Display's the coords of the entity above their head
		//g.setColor(Color.BLACK);
		//g.drawString(x + " " + y, (int)(x - handler.getGameCamera().getxOffset())-14, (int)(y - handler.getGameCamera().getyOffset()) - 5);
		
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		g.drawString(name, (int)(xrel - handler.getGameCamera().getxOffset()), (int)(yrel - handler.getGameCamera().getyOffset()+2));
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

	@Override
	public Class findClass() {
		return getClass().getSuperclass();
	}
	
}
