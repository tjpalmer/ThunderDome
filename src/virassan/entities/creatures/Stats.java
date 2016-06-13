package virassan.entities.creatures;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.CopyOnWriteArrayList;

import virassan.gfx.hud.BouncyText;
import virassan.main.ID;
import virassan.utils.Utils;

public class Stats {
	
	private CopyOnWriteArrayList<BouncyText> list = new CopyOnWriteArrayList<BouncyText>();
	
	private int[] levelExp;
	private final int MAX_LEVEL = 40;
	
	private float dmgMod;
	private float health;
	private float maxHealth;
	private float healthScale;
	private float dmgScale;
	private Creature entity;
	private int experience, maxExperience;
	
	/**
	 * Constructor for NPCs and Enemies
	 * @param entity the Creatures object is using these Stats
	 * @param maxHealth the maximum possible health for this Creatures
	 */
	public Stats(Creature entity, float maxHealth){
		this.entity = entity;
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		dmgMod = 0.001f;
		healthScale = this.health/this.maxHealth;
		dmgScale = this.health/this.maxHealth;
	}
	
	/**
	 * Constructor for Player
	 * @param entity the Player object
	 * @param maxHealth the maximum possible health for the Player
	 * @param the Level of the Player
	 */
	public Stats(Creature entity, float maxHealth, int level){
		this.entity = entity;
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.experience = 0;
		healthScale = this.health/this.maxHealth;
		dmgScale = this.health/this.maxHealth;
		
		levelExp = new int[MAX_LEVEL];
		for(int i = 1; i <= MAX_LEVEL - 1; i++){
			levelExp[i] = i * 500 + 2 * i/3 + i/7;
		}
		this.maxExperience = levelExp[level];
	}
	
	
	public void tick(){
		//TODO
		for(BouncyText text : list){
			text.tick();
		}
		entity.setHealth((int)this.health);
	}
	
	public void render(Graphics g){
		for(BouncyText text : list){
			text.render(g);
		}
	}
	
	/**
	 * Heals the character for the amount
	 * @param amount amount to add to player health
	 */
	public void heal(float amount){
		if(amount + health > maxHealth){
			amount = maxHealth - health;
		}
		health += amount;
		health = Utils.clamp(health, 0, maxHealth);
		healthScale = health/maxHealth;
		dmgScale = health/maxHealth;
		list.add(new BouncyText(entity.getHandler(), String.valueOf((int)amount), Color.WHITE, (int)(entity.getX()) + entity.getWidth()/2, (int)(entity.getY()) + entity.getHeight()/2));
		System.out.println("Healed!");
	}
	
	public boolean isLevelUp(){
		if(entity.getId() == ID.Player){
			entity = (Player)entity;
			if(getMaxExperience(entity.getLevel()) <= experience){
				return true;
			}
		}
		return false;
	}
	
	public void levelUp(){
		entity.setLevel(entity.getLevel() + 1);
		maxHealth += 50;
		health = maxHealth;
		dmgMod += 0.10;
		healthScale = health/maxHealth;
		dmgScale = health/maxHealth;
		String text = "Level Up! " + String.valueOf(entity.getLevel());
		list.add(new BouncyText(entity.getHandler(), text, Color.YELLOW, (int)(entity.getX()), (int)(entity.getY() + 5)));
	}
	
	public void addExperience(int amount){
		experience += amount;
		list.add(new BouncyText(entity.getHandler(), String.valueOf((int)amount), Color.MAGENTA, (int)(entity.getX() + entity.getWidth()/2), (int)(entity.getY())));
		if(isLevelUp()){
			experience = 0;
			getMaxExperience(entity.getLevel());
			levelUp();
		}
	}
	
	public int getMaxExperience(int level){
		this.maxExperience = levelExp[level];
		return maxExperience;
	}
	
	public float getHealthScale() {
		return healthScale;
	}

	public void setHealthScale(float healthScale) {
		this.healthScale = healthScale;
	}

	public float getDmgScale() {
		return dmgScale;
	}

	public void setDmgScale(float dmgScale) {
		this.dmgScale = dmgScale;
	}

	/**
	 * Damages the character for the amount
	 * @param amount amount to subtract from player health
	 */
	public void damage(float amount){
		if(health > 0){
			health -= amount;
			health = Utils.clamp(health, 0, maxHealth);
			healthScale = health/maxHealth;
			entity.setDamaged(true);
			list.add(new BouncyText(entity.getHandler(), String.valueOf((int)amount), Color.RED, (int)(entity.getX() + entity.getWidth()/2), (int)(entity.getY() + entity.getHeight()/2)));
			System.out.println("HIT!");
		}
	}

	
	//GETTERS AND SETTERS
	
	public void setHealth(float amount){
		health = amount;
	}
	
	public float getHealth(){
		return health;
	}
	
	public void setMaxHealth(float amount){
		maxHealth = amount;
	}
	
	public float getMaxHealth(){
		return maxHealth;
	}

	public float getDmgMod() {
		return dmgMod;
	}
	
	public void setDmgMod(float dmgMod){
		this.dmgMod = dmgMod;
	}
	
	public CopyOnWriteArrayList<BouncyText> getList(){
		return list;
	}
	
}