package virassan.entities.creatures;

import java.awt.Rectangle;
import java.util.Random;

import virassan.entities.Entity;
import virassan.entities.creatures.enemies.Enemy;
import virassan.entities.creatures.player.Player;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.entities.creatures.utils.Stats;
import virassan.gfx.hud.EventText;
import virassan.gfx.hud.SkillText;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.utils.Utils;

public abstract class Creature extends Entity{

	private Random gen = new Random();
	public static final int DEFAULT_HEALTH = 10,
							DEFAULT_MAXHEALTH = 10;
	public final float DEFAULT_SPEED ;
	public static final int DEFAULT_CREATURE_WIDTH = 32, 
							DEFAULT_CREATURE_HEIGHT = 32;
	
	protected String name;
	protected float speed;
	protected boolean damaged;
	protected Stats stats;
	protected int direction; // 0 = up, 1 = down, 2 = right, 3 = left
	
	//Attacking
	protected float attackSpeed, attackDmg;
	protected int attackRange;
	protected String attackName;
	
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
	public Creature(Handler handler, String name, float x, float y, int width, int height, int level) {
		super(handler, x, y, width, height);
		this.name = name;
		DEFAULT_SPEED = 0.10f * (float)handler.getWidth();
		speed = DEFAULT_SPEED;
		velX = 0;
		velY = 0;
		stats =  new Stats(this, 100, 50, 50, level);
		walkBounds = new Rectangle((int)x*5, (int)y*5, width*2, height*2);
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
	
	public boolean attack(Item weapon, Creature entity){
		float crit = ((float)gen.nextInt(100))/ 100F;
		int range = weapon.getWeapRange();
		double distance = Math.sqrt(Math.pow((vector.dX + (width/2)) - (entity.getX() + (entity.getWidth()/2)), 2) + 
				Math.pow((vector.dY + (height/2)) - (entity.getY()+ (entity.getHeight()/2)), 2));
		if(distance <= range){
			float temp = weapon.getDmgAmt() + (float)(weapon.getDmgAmt() * stats.getDmgMod());
			if(crit < stats.getCritChance()){
				float critAmt = (temp * stats.getCritMult());
				temp += critAmt;
				if(entity instanceof Enemy){
					stats.getEventList().add(new EventText(this, handler, "CRITICAL HIT", (int)vector.dX, (int)vector.dY));
				}
			}
			entity.getStats().damage(temp - (entity.getStats().getArmorRating() * entity.getStats().getArmorPer()));
			if(entity instanceof Enemy){
				((Enemy)entity).EnemyDeath();
			}
			entity.setVelX(0);
			entity.setVelY(0);
			return true;
		}else{
			Handler.GAMESTATE.getHUD().addSkillList(new SkillText(handler, "Out of Range", Handler.LAVENDER, (int)entity.getX()-10, (int)entity.getY()-10));
		}
		return false;
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
			((Enemy)entity).EnemyDeath();
		}
		entity.getStats().setDamaged(true);
		entity.getStats().setAggro(true);
		return true;
	}
	
	/**
	 * Attacking for Enemies
	 * @param attack the attack the Enemy uses
	 * @return true if the attack hits
	 */
	public boolean attack(Attack attack){
		float crit = ((float)gen.nextInt(100))/ 100F;
		if(attackRange > 0){
			for(Entity e : handler.getMap().getEntityManager().getEntities()){
				if(e instanceof Creature){
					if((e instanceof Player)){
						Player entity = (Player)e;
						if(collisionAttack(e)){
							float temp = attack.getDamage() + (float)(attack.getDamage() * stats.getDmgMod());
							if(crit < stats.getCritChance()){
								float critAmt = (temp * stats.getCritMult());
								temp += critAmt;
							}
							entity.getStats().damage(temp - (entity.getStats().getArmorRating() * entity.getStats().getArmorPer()));
							return true;
						}
					}
				}
			}
		}else{
			System.out.println("Error Message: Creature_attack attackRange is <= 0: " + attackRange);
		}
		return false;
	}
	
	public boolean collisionAttack(Entity e){
		double distance = Math.sqrt( Math.pow( (e.getX() +( e.getWidth() / 2 ) ) - ( vector.dX + ( this.width / 2 ) ), 2 ) + 
				Math.pow( ( e.getY() + ( e.getHeight() / 2 ) ) - ( vector.dY + ( this.height / 2 ) ), 2 ) );
		if(distance <= attackRange){
			return true;
		}
		return false;
	}
	
	public void skillAction(Class target, SkillTracker skill, Entity e){
		if(e.findClass() == target){ 
			skill.action((Creature)e, this);
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
	
	/**
	 * TODO: doesn't seem to be implemented in merchant and others
	 */
	public abstract void resetTimer();
	
	public void setNPCTimer(Creature e){
		e.resetTimer();
	}
}
