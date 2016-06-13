package virassan.entities.creatures;

import java.awt.Rectangle;

import virassan.entities.Entity;
import virassan.entities.creatures.enemies.Enemy;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.utils.Utils;
import virassan.world.maps.Tile;

public abstract class Creature extends Entity{

	
	public static final int DEFAULT_HEALTH = 10,
							DEFAULT_MAXHEALTH = 10;
	public static final float DEFAULT_SPEED = 4.0f;
	public static final int DEFAULT_CREATURE_WIDTH = 32, 
							DEFAULT_CREATURE_HEIGHT = 32;
	
	protected int maxHealth;
	protected int health;
	protected int level;
	protected float speed;
	protected boolean damaged;
	protected Stats stats;
	protected int direction; // 0 = up, 1 = down, 2 = right, 3 = left
	
	//Attacking
	protected float attackSpeed, attackDmg;
	protected int attackWidth, attackHeight;
	protected String attackName;
	protected Rectangle attackBounds;
	
	
	//NPC Shit
	protected boolean npc;
	protected boolean isMoving;
	protected int aggro_dist;
	protected Rectangle walkBounds;
	
	/**
	 * Creates a Creature at the specified coords
	 * @param handler
	 * @param x 
	 * @param y
	 * @param width
	 * @param height
	 */
	public Creature(Handler handler, float x, float y, int width, int height, int level, ID id) {
		super(handler, x, y, width, height, id);
		speed = DEFAULT_SPEED;
		velX = 0;
		velY = 0;
		this.level = level;
		walkBounds = new Rectangle((int)x*5, (int)y*5, width*2, height*2);
		attackBounds = new Rectangle();
	}

	
	public void move(){
		if(!isDead()){
			if(!checkEntityCollisions(velX, 0f)){
				moveX();
			}
			if(!checkEntityCollisions(0f, velY)){
				moveY();
			}
		}
	}
	
	public void moveX(){
		if(velX > 0){ //moving right
			int tempX = (int)(x + velX + bounds.x + bounds.width)/ Tile.TILE_WIDTH;
			if(!collisionWithTile(tempX, (int)(y + bounds.y)/ Tile.TILE_HEIGHT) && 
					!collisionWithTile(tempX, (int)(y + bounds.y + bounds.height)/ Tile.TILE_HEIGHT)){
				if(npc){
					if(Utils.clamp(x + velX, walkBounds.x, walkBounds.x + walkBounds.width) == x + velX){
						x = Utils.clamp(velX+x, 0, handler.getWorld().getMap().getWidth()*64);
					}else{
						setisMoving(false);
					}
				}else{
					x += velX;
				}
			}else{
				x = (tempX * Tile.TILE_WIDTH - bounds.x - bounds.width -1);
				if(npc){
					setisMoving(false);
				}
				//TODO only care if Player ID goes collides with bad shit
				//if(ID == Player)
				if(collisionDamage(tempX, (int)(y + bounds.y)/ Tile.TILE_HEIGHT) > 0){
					stats.damage(collisionDamage(tempX, (int)(y + bounds.y)/ Tile.TILE_HEIGHT));
				}
				else if(collisionDamage(tempX, (int)(y + bounds.y + bounds.height)/ Tile.TILE_HEIGHT) > 0){
					stats.damage(collisionDamage(tempX, (int)(y + bounds.y + bounds.height)/ Tile.TILE_HEIGHT));
				}
			}
			direction = 2;
		}else if(velX < 0){ //moving left
			int tempX = (int) (x + velX + bounds.x)/ Tile.TILE_WIDTH;
			if(!collisionWithTile(tempX, (int)(y + bounds.y)/ Tile.TILE_HEIGHT) && !collisionWithTile(tempX, (int)(y+bounds.y + bounds.height)/ Tile.TILE_HEIGHT)){
				if(npc){
					if(Utils.clamp(x + velX, walkBounds.x, walkBounds.x + walkBounds.width) == x + velX){
						x = Utils.clamp(velX+x, 0, handler.getWorld().getMap().getWidth()*64);
					}else{
						setisMoving(false);
					}
				}else{
					x += velX;
				}
			}else{
				x = (tempX * Tile.TILE_WIDTH + Tile.TILE_WIDTH - bounds.x);
				if(npc){
					setisMoving(false);
				}
				if(collisionDamage(tempX, (int)(y + bounds.y)/ Tile.TILE_HEIGHT) > 0){
					stats.damage(collisionDamage(tempX, (int)(y + bounds.y)/ Tile.TILE_HEIGHT));
				}
				else if(collisionDamage(tempX, (int)(y + bounds.y + bounds.height)/ Tile.TILE_HEIGHT) > 0){
					stats.damage(collisionDamage(tempX, (int)(y + bounds.y + bounds.height)/ Tile.TILE_HEIGHT));
				}
			}
			direction = 3;
		}
	}
	
	public void moveY(){
		if(velY < 0){ //moving Up
			int tempY = (int)(y + velY + bounds.y) / Tile.TILE_HEIGHT;
			if(!collisionWithTile((int)((x + bounds.x) / Tile.TILE_WIDTH), tempY) && 
					!collisionWithTile((int)((x + bounds.x + bounds.width) / Tile.TILE_WIDTH), tempY)){
				if(npc){
					if(Utils.clamp(y + velY, walkBounds.y, walkBounds.y + walkBounds.height) == y + velY){
						y = Utils.clamp(velY+y, 0, handler.getWorld().getMap().getHeight()*64);
					}else{
						setisMoving(false);
					}
				}else{
					y += velY;
				}
			}else{
				y = (tempY *Tile.TILE_HEIGHT + Tile.TILE_HEIGHT - bounds.y);
				if(npc){
					setisMoving(false);
				}
				if(collisionDamage((int)((x + bounds.x) / Tile.TILE_WIDTH), tempY) > 0){ 
					stats.damage(collisionDamage(((int)(x + bounds.x) / Tile.TILE_WIDTH), tempY));
				}
				else if(collisionDamage((int)((x + bounds.x + bounds.width) / Tile.TILE_WIDTH), tempY) > 0){
					stats.damage(collisionDamage((int)((x + bounds.x + bounds.width) / Tile.TILE_WIDTH), tempY));
				}
			}
			direction = 0;
		}else if(velY > 0){ // moving Down
			int tempY = (int)(y + velY + bounds.y + bounds.height) / Tile.TILE_HEIGHT;
			if(!collisionWithTile((int) (x + bounds.x) / Tile.TILE_WIDTH, tempY) && 
					!collisionWithTile((int) (x + bounds.x + bounds.width) / Tile.TILE_WIDTH, tempY)){
				if(npc){
					if(Utils.clamp(y + velY, walkBounds.y, walkBounds.y + walkBounds.height) == y + velY){
						y = Utils.clamp(velY+y, 0, handler.getWorld().getMap().getHeight()*64);
					}else{
						if(walkBounds.y <= 0 || walkBounds.height <= 0){
						}
						setisMoving(false);
					}
				}else{
					y += velY;
				}
			}else{
				y = (tempY *Tile.TILE_HEIGHT - bounds.y - bounds.height -1);
				if(npc){
					setisMoving(false);
				}
				if(collisionDamage((int)((x + bounds.x) / Tile.TILE_WIDTH), tempY) > 0){ 
					stats.damage(collisionDamage(((int)(x + bounds.x) / Tile.TILE_WIDTH), tempY));
				}
				else if(collisionDamage((int)((x + bounds.x + bounds.width) / Tile.TILE_WIDTH), tempY) > 0){
					stats.damage(collisionDamage((int)((x + bounds.x + bounds.width) / Tile.TILE_WIDTH), tempY));
				}
			}
			direction = 1;
		}
	}
	
	
	public boolean attack(Attack attack){
		if(attackBounds != null){
			attackBounds.width = attack.getWidth();
			attackBounds.height = attack.getHeight();
			for(Entity e : handler.getWorld().getMap().getEntityManager().getEntities()){
				if(e.equals(this)){
					continue;
				} 
				if(e.getId() == ID.Enemy){
					Enemy entity = (Enemy)e;
					if(collisionAttack(entity)){
						System.out.println("You hit Something!");
						entity.getStats().damage(attack.getDamage() + (float)(attack.getDamage() * stats.getDmgMod()));
						NPCDeath(entity);
						entity.setVelX(0);
						entity.setVelY(0);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean collisionAttack(Entity e){
		if(direction == 0){ // UP
			attackBounds.y = (int)y - attackBounds.height;
			attackBounds.x = (int)x;
		}else if(direction == 1){ // DOWN
			attackBounds.y = (int)y + height;
			attackBounds.x = (int)x;
		}else if(direction == 2){ // RIGHT
			attackBounds.x = (int)x + width;
			attackBounds.y = (int)y;
		}else if(direction == 3){ // LEFT
			attackBounds.x = (int)x - attackBounds.width;
			attackBounds.y = (int)y;
		}
		if(new Rectangle((int)e.getX(), (int)e.getY(), e.getWidth(), e.getHeight()).intersects(attackBounds)){
			return true;
		}
		return false;
	}
	
	
	private boolean collisionWithTile(int x, int y){
		return handler.getWorld().getMap().getTile(x, y).isSolid();
	}
	
	private float collisionDamage(int x, int y){
		return handler.getWorld().getMap().getTile(x, y).getCollisionDamage();
	}
	
	public void NPCDeath(Enemy e){
		if(e.health <=0){
			System.out.println("DEAD!");
			e.isDead(true);
			// TODO: move the below shit to the enemy class bro
			e.droppedItems();
			e.gainExp();
		}
	}
	
	
	//GETTERS AND SETTERS
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public float getVelX() {
		return velX;
	}

	public void setSpeed(float speed){
		this.speed = speed;
	}
	
	public float getSpeed(){
		return speed;
	}
	
	public void setVelX(float velX) {
		this.velX = velX;
	}

	public float getVelY() {
		return velY;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}

	public boolean isDamaged(){
		return damaged;
	}
	
	public void setDamaged(boolean damaged){
		this.damaged = damaged;
	}
	
	public boolean isNPC(){
		return npc;
	}
	
	public void setNPC(boolean b){
		this.npc = b;
	}
	
	public boolean getisMoving(){
		return isMoving;
	}
	public void setisMoving(boolean b){
		isMoving = b;
	}
	
	public Stats getStats(){
		return stats;
	}
	
	public Rectangle getWalkBounds(){
		return walkBounds;
	}
	
	public void setAttackBoundsX(int x){
		attackBounds.x = x;
	}
	
	public void setAttackBoundsY(int y){
		attackBounds.y = y;
	}
	
	public abstract void resetTimer();
	
	public void setNPCTimer(Creature e){
		e.resetTimer();
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
