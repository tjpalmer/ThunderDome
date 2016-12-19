package virassan.entities.creatures;

import java.awt.Rectangle;
import java.util.Random;

import virassan.entities.Entity;
import virassan.entities.creatures.enemies.Enemy;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.entities.creatures.utils.Stats;
import virassan.gfx.hud.EventText;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.utils.Utils;
import virassan.world.maps.Tile;

public abstract class Creature extends Entity{

	private Random gen = new Random();
	public static final int DEFAULT_HEALTH = 10,
							DEFAULT_MAXHEALTH = 10;
	public static final float DEFAULT_SPEED = 4.0f;
	public static final int DEFAULT_CREATURE_WIDTH = 32, 
							DEFAULT_CREATURE_HEIGHT = 32;
	
	protected String name;
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
	public Creature(Handler handler, String name, float x, float y, int width, int height, int level, ID id) {
		super(handler, x, y, width, height, id);
		this.name = name;
		speed = DEFAULT_SPEED;
		velX = 0;
		velY = 0;
		stats =  new Stats(this, 100, 50, 50, level);
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
	
	public boolean restore(SkillTracker skill, Creature entity){
		switch(skill.getSkillType().getEffectType()){
		case "stam": entity.getStats().setStamina(Utils.clamp((float)(entity.getStats().getStamina() + skill.getSkillType().getEffectAmt()), 0F, entity.getStats().getMaxStam())); break;
		case "health": entity.getStats().heal(skill.getSkillType().getEffectAmt()); break;
		case "mana": entity.getStats().setMana(Utils.clamp((float)(entity.getStats().getMana() + skill.getSkillType().getEffectAmt()), 0F, entity.getStats().getMaxMana())); break;
		}
		switch(skill.getSkillCostType()){
			case "stam": stats.setStamina(Utils.clamp((int)(stats.getStamina() - skill.getCost()), 0, stats.getMaxStam())); break;
			case "mana": stats.setMana(Utils.clamp(stats.getMana() - skill.getCost(), 0, stats.getMaxMana())); break;
			case "heal": stats.setHealth(Utils.clamp(stats.getHealth() - skill.getCost(), 0, stats.getMaxHealth())); break;
		}
		return true;
	}
	
	public boolean attack(SkillTracker skill, Creature entity, String typeOfDamage){
		// entity.getStats().damage((float)skill.getSkillType().getEffectAmt());
		switch(typeOfDamage){
		case "basic": entity.getStats().damage((float)skill.getSkillType().getEffectAmt() - (entity.getStats().getArmorRating() * entity.getStats().getArmorPer())); break;
		case "special": break;
		case "magic": break;
		}
		String skillCost = skill.getSkillCostType();
		switch(skillCost){
		case "stam": stats.setStamina(Utils.clamp((int)(stats.getStamina() - skill.getCost()), 0, stats.getMaxStam())); break;
		case "mana": stats.setMana(Utils.clamp(stats.getMana() - skill.getCost(), 0, stats.getMaxMana())); break;
		case "heal": stats.setHealth(Utils.clamp(stats.getHealth() - skill.getCost(), 0, stats.getMaxHealth())); break;
		}
		if(entity instanceof Enemy){
			NPCDeath((Enemy)entity);
		}
		entity.getStats().setDamaged(true);
		entity.getStats().setAggro(true);
		return true;
	}
	
	public boolean attack(Attack attack){
		float crit = ((float)gen.nextInt(100))/ 100F;
		if(attackBounds != null){
			attackBounds.width = attack.getWidth();
			attackBounds.height = attack.getHeight();
			for(Entity e : handler.getWorld().getMap().getEntityManager().getEntities()){
				if(id == e.getId()){
					continue;
				}
				if(e instanceof Creature){
					if(!(e instanceof NPC)){
						Creature entity = (Creature)e;
						if(collisionAttack(e)){
							float temp = attack.getDamage() + (float)(attack.getDamage() * stats.getDmgMod());
							float lvlDiff = entity.getStats().getArmorPer() * (stats.getLevel() - entity.stats.getLevel());
							if(crit < stats.getCritChance()){
								float critAmt = (temp * stats.getCritMult());
								temp += critAmt;
								if(entity instanceof Enemy){
									stats.getEventList().add(new EventText(this, handler, "CRITICAL HIT", (int)x, (int)y));
								}
							}
							temp += stats.getWeapDmg();
							if(Math.abs(stats.getLevel() - entity.stats.getLevel()) >= 5){
								entity.getStats().damage(temp - (entity.getStats().getArmorRating() * entity.getStats().getArmorPer()) + lvlDiff);
							}else{
								entity.getStats().damage(temp - (entity.getStats().getArmorRating() * entity.getStats().getArmorPer()));
							}
							if(entity instanceof Enemy){
								NPCDeath((Enemy)entity);
							}
							entity.setVelX(0);
							entity.setVelY(0);
							return true;
						}
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
	
	public void skillAction(ID target, SkillTracker skill, Entity e){
		if(e.getId() == target){
			skill.action((Creature)e, this);
		}
	}
	
	private boolean collisionWithTile(int x, int y){
		return handler.getWorld().getMap().getTile(x, y).isSolid();
	}
	
	private float collisionDamage(int x, int y){
		return handler.getWorld().getMap().getTile(x, y).getCollisionDamage();
	}
	
	public void NPCDeath(Enemy e){
		if(e.stats.getHealth() <=0){
			e.isDead(true);
			// TODO: move the below shit to the enemy class bro
			e.droppedItems();
			e.gainExp();
		}
	}
	
	
	//GETTERS AND SETTERS
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
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
}
