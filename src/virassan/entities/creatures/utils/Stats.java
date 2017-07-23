package virassan.entities.creatures.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import virassan.entities.creatures.Creature;
import virassan.entities.creatures.enemies.Enemy;
import virassan.entities.creatures.player.Player;
import virassan.gfx.hud.BouncyText;
import virassan.gfx.hud.EventText;
import virassan.items.Equip;
import virassan.items.Item;
import virassan.main.ID;
import virassan.utils.Utils;

public class Stats {
	
	private CopyOnWriteArrayList<BouncyText> list = new CopyOnWriteArrayList<BouncyText>();
	private CopyOnWriteArrayList<EventText> eventList = new CopyOnWriteArrayList<EventText>();
	private CopyOnWriteArrayList<BuffTracker> buffs = new CopyOnWriteArrayList<>();
	private HashMap<Equip, Item> equip;
	
	private int[] levelExp;
	private final int MAX_LEVEL = 40;
	private final float ARMOR_PER = 0.08F;
	
	private long aggroTimer, aggroLast;
	private boolean isDamaged, aggro;
	private long damageTimer, damageLast, damageWait = 1200, healTimer, healLast;
	
	// Skill stuff
	private float healRate, manaRate, stamRate, critChance, critMult, armorRating, persuasion, barter;
	
	// Equip
	private Item mainH, offH, head, chest, legs, feet, shoulders, hands, acc1, acc2;
	private double weapDmg;
	
	private float mana, maxMana, stamina, maxStam;
	private float dmgMod, health, maxHealth, healthScale, dmgScale;
	private Creature entity;
	private int experience, maxExperience, level;
	
	/**
	 * Constructor for NPCs and Enemies
	 * @param entity the Creatures object is using these Stats
	 * @param maxHealth the maximum possible health for this Creatures
	 */
	public Stats(Creature entity, float maxHealth, float maxStamina, float maxMana){
		this.entity = entity;
		this.maxHealth = maxHealth;
		health = maxHealth;
		this.maxStam = maxStamina;
		this.maxMana = maxMana;
		stamina = maxStam;
		mana = maxMana;
		dmgMod = 0.001f;
		healthScale = this.health/this.maxHealth;
		dmgScale = this.health/this.maxHealth;
		equip = new HashMap<Equip, Item>();
		level = 1;
		healRate = 5;
		stamRate = 2;
		manaRate = 2;
	}
	
	/**
	 * Constructor all entities
	 * @param entity the Creature whom the stats belong to
	 * @param maxHealth the maximum possible health
	 * @param the starting level of the Creature
	 */
	public Stats(Creature entity, float maxHealth, float maxStamina, float maxMana, int level){
		this(entity, maxHealth, maxStamina, maxMana);
		this.level = level;
		if(entity instanceof Player){
			for(Equip slot : Equip.values()){
				equip.put(slot, null);
			}
			levelExp = new int[MAX_LEVEL + 1];
			for(float i = 0; i <= MAX_LEVEL; i++){
				float num = (float)Math.pow(i, 4) + 1;
				levelExp[(int)i] = (int)((i * 500) + num*(i/2));
			}
			this.maxExperience = levelExp[level];
			/*
			for(int i = 1; i <= MAX_LEVEL; i++){
				System.out.println(levelExp[i]);
			}
			*/
		}
	}
	
	
	public void tick(double delta){
		if(!entity.getHandler().getEntityManager().getPaused()){
			for(BuffTracker buff : buffs){
				if(buff.getLive()){
					buff.tick(delta);
				}else{
					buffs.remove(buff);
				}
			}
		}
		for(EventText text : eventList){
			if(text.isLive()){
				text.tick(delta);
			}else{
				eventList.remove(text);
			}
		}
		for(BouncyText text : list){
			if(text.getLive()){
				text.tick(delta);
			}else{
				list.remove(text);
			}
		}
		if(!entity.getHandler().getEntityManager().getPaused()){
			damageTimer += (System.currentTimeMillis() - damageLast) * delta;
			damageLast = System.currentTimeMillis() * (long)delta;
			if(isDamaged){
				if(damageTimer > damageWait){
					damageTimer = 0;
					isDamaged = false;
				}
			}else{
				if(aggro){
					if(entity instanceof Enemy){
						aggroTimer += (System.currentTimeMillis() - aggroLast) * delta;
						aggroLast = System.currentTimeMillis() * (long)delta;
						if(aggroTimer > 1400){
							int playerDist = (int)Math.sqrt((double)(Math.pow(entity.getX() - entity.getHandler().getPlayer().getX(), 2) + Math.pow(entity.getY() - entity.getHandler().getPlayer().getY(), 2)));
							if(playerDist > ((Enemy)entity).getAggroDist()){
								aggro = false;
							}
							aggroTimer = 0;
						}
					}
				}else{
					healTimer += (System.currentTimeMillis() - healLast) * delta;
					healLast = System.currentTimeMillis() * (long)delta;
					if(healTimer > 800){
						if(health < maxHealth){
							health = Utils.clamp(health + healRate, 0, maxHealth);
							healthScale = health / maxHealth;
						}
						if(stamina < maxStam){
							stamina = Utils.clamp(stamina + stamRate, 0, maxStam);
						}
						if(mana < maxMana){
							mana = Utils.clamp(mana + manaRate, 0, maxMana);
						}
						healTimer = 0;
					}
				}
			}
		}
	}
	
	public void render(Graphics g){
		for(EventText text : eventList){
			text.render(g);
		}
		for(BouncyText text : list){
			text.render(g);
		}
		int x = 10;
		int y = 85;
		for(BuffTracker buff : buffs){
			if(buff.getLive()){
				g.drawImage(buff.getImage(), x, y, null);
				y += buff.getImage().getHeight() + 5;
			}
		}
	}
	
	public void equip(Item item){
		switch(item.getEquip()){
			case MAINHAND: 
				if(mainH != null){
					unEquip(item.getEquip());
				}
				mainH = item; 
				weapDmg = mainH.getDmgAmt();
				break;
			case OFFHAND: 
				if(offH != null){
					unEquip(item.getEquip());
				}
				offH = item; 
				break;
			case HEAD: 
				if(head != null){
					unEquip(item.getEquip());
				}
				head = item; 
				armorRating = head.getArmorAmt(); 
				break;
			case CHEST: 
				if(chest != null){
					unEquip(item.getEquip());
				}
				chest = item; 
				armorRating = chest.getArmorAmt(); 
				break;
			case LEGS: 
				if(legs != null){
					unEquip(item.getEquip());
				}
				legs = item; 
				armorRating = legs.getArmorAmt();
				break;
			case FEET: 
				if(feet != null){
					unEquip(item.getEquip());
				}
				feet = item; 
				armorRating = feet.getArmorAmt();
				break;
			case SHOULDERS:
				if(shoulders != null){
					unEquip(item.getEquip());
				}
				shoulders = item; 
				armorRating = shoulders.getArmorAmt();
				break;
			case HANDS: 
				if(hands != null){
					unEquip(item.getEquip());
				}
				hands = item; 
				armorRating = hands.getArmorAmt();
				break;
			case ACCESS1: 
				if(acc1 != null){
					unEquip(item.getEquip());
				}
				acc1 = item;
				break;
			case ACCESS2: 
				if(acc2 != null){
					unEquip(item.getEquip());
				}
				acc2 = item; 
				break;
		}
		equip.replace(item.getEquip(), item);
	}
	
	public void unEquip(Equip slot){
		switch(slot){
			case MAINHAND: weapDmg -= mainH.getDmgAmt(); ((Player)entity).getInventory().addItems(mainH, false); mainH = null; break;
			case OFFHAND: ((Player)entity).getInventory().addItems(offH, false); offH = null ;break;
			case HEAD: armorRating -= head.getArmorAmt(); ((Player)entity).getInventory().addItems(head, false); head = null; break;
			case CHEST: armorRating -= chest.getArmorAmt(); ((Player)entity).getInventory().addItems(chest, false); chest = null; break;
			case LEGS: armorRating -= legs.getArmorAmt(); ((Player)entity).getInventory().addItems(legs, false); legs = null; break;
			case FEET: armorRating -= feet.getArmorAmt(); ((Player)entity).getInventory().addItems(feet, false); feet = null; break;
			case SHOULDERS: armorRating -= shoulders.getArmorAmt(); ((Player)entity).getInventory().addItems(shoulders, false); shoulders = null; break;
			case HANDS: armorRating -= hands.getArmorAmt(); ((Player)entity).getInventory().addItems(hands, false); hands = null; break;
			case ACCESS1: ((Player)entity).getInventory().addItems(acc1, false); acc1 = null; break;
			case ACCESS2: ((Player)entity).getInventory().addItems(acc2, false); acc2 = null; break;
		}
		equip.replace(slot, null);
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
		if(!entity.getHandler().getEntityManager().getPaused()){
			list.add(new BouncyText(entity.getHandler(), String.valueOf((int)amount), Color.WHITE, (int)(entity.getX()) + entity.getWidth()/2, (int)(entity.getY()) + entity.getHeight()/2));
		}
	}
	
	public boolean isLevelUp(){
		if(level < 40){
			if(entity.getId() == ID.Player){
				entity = (Player)entity;
				if(getMaxExperience() <= experience){
					return true;
				}
			}
		}
		return false;
	}
	
	public void levelUp(){
		level++;
		maxHealth += 50;
		health = maxHealth;
		dmgMod += 0.05F;
		healthScale = health/maxHealth;
		dmgScale = health/maxHealth;
		
		// Testing purposes
		((Player)entity).getTraits().levelStr(1);
		((Player)entity).getTraits().levelDex(1);
		String text = "Level Up! " + String.valueOf(level);
		eventList.add(new EventText(entity, entity.getHandler(), text, (int)entity.getX(), (int)entity.getY()));
		
		/*
		list.add(new BouncyText(entity.getHandler(), text, Color.YELLOW, (int)(entity.getX()), (int)(entity.getY() + 5)));
		*/
	}
	
	public void addExperience(int amount){
		experience += amount;
		list.add(new BouncyText(entity.getHandler(), String.valueOf((int)amount), Color.MAGENTA, (int)(entity.getX() + entity.getWidth()/2), (int)(entity.getY())));
		if(isLevelUp()){
			getMaxExperience();
			levelUp();
		}
	}
	
	public int getMaxExperience(){
		return getLevelExperience(level);
	}
	
	public int getLevelExperience(int level){
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
		isDamaged = true;
		if(entity instanceof Enemy){
			aggro = true;
		}
		damageTimer = 0;
		amount = Utils.clamp(amount, 0F, maxHealth);
		Color myColor = Color.CYAN;
		if(entity.getId() == ID.Player){
			myColor = Color.RED;
		}
		if(health > 0){
			health -= amount;
			health = Utils.clamp(health, 0, maxHealth);
			healthScale = health/maxHealth;
			list.add(new BouncyText(entity.getHandler(), String.valueOf((int)amount), myColor, (int)(entity.getX() + entity.getWidth()/2), (int)(entity.getY() + entity.getHeight()/2)));
		}
	}

	public void addEventText(String text){
		eventList.add(new EventText(entity, entity.getHandler(), text, (int)entity.getX(), (int)entity.getY() - 15));
	}
	
	public void addBuff(BuffTracker buff){
		buffs.add(buff);
	}
	
	public CopyOnWriteArrayList<BuffTracker> getBuffs(){
		return buffs;
	}
	
	//GETTERS AND SETTERS
	public int getExperience(){
		return experience;
	}
	
	public void setExperience(int exp){
		experience = exp;
	}
	
	public void setEntity(Creature entity) {
		this.entity = entity;
	}
	
	public Creature getEntity(){
		return entity;
	}
	
	public float getMana() {
		return mana;
	}

	public void addMaxMana(float amt){
		maxMana += amt;
	}
	
	public void setMana(float mana) {
		this.mana = mana;
	}

	public float getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(float maxMana) {
		this.maxMana = maxMana;
	}

	public void addMaxStam(float amt){
		maxStam += amt;
	}
	
	public float getMaxStam() {
		return maxStam;
	}

	public void setMaxStam(float maxStam) {
		this.maxStam = maxStam;
	}

	public Item getMainH() {
		return mainH;
	}

	public Item getOffH() {
		return offH;
	}

	public Item getHead() {
		return head;
	}

	public Item getChest() {
		return chest;
	}

	public Item getLegs() {
		return legs;
	}

	public Item getHands() {
		return hands;
	}

	public Item getFeet() {
		return feet;
	}

	public Item getShoulders(){
		return shoulders;
	}
	
	public Item getAcc1() {
		return acc1;
	}

	public Item getAcc2() {
		return acc2;
	}
	
	public void setHealth(float amount){
		health = amount;
	}
	
	public float getHealth(){
		return health;
	}
	
	public void addMaxHealth(float amt){
		maxHealth += amt;
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
	
	public void setDmgMod(float amt){
		dmgMod = amt;
	}
	
	public void addDmgMod(float amt){
		dmgMod += amt;
	}
	
	public double getWeapDmg(){
		return weapDmg;
	}
	
	public void setArmorRating(float amt){
		armorRating = amt;
	}
	
	public float getArmorRating(){
		return armorRating;
	}
	
	public void addArmorRating(float amt){
		armorRating += amt;
	}
	
	public void addCritMult(float amt){
		critMult += amt;
	}
	
	public void setCritMult(float amt){
		critMult = amt;
	}
	
	public float getCritMult(){
		return critMult;
	}
	
	public float getBarter(){
		return barter;
	}
	
	public void addBarter(float amt){
		barter += amt;
	}
	
	public float getPersuasion(){
		return persuasion;
	}
	
	public void addPersuasion(float amt){
		persuasion += amt;
	}
	
	public void addCritChance(float amt){
		critChance += amt;
	}
	
	public void setCritChance(float amt){
		critChance = amt;
	}
	
	public float getCritChance(){
		return critChance;
	}
	
	public void addHealRate(float hRate){
		healRate += hRate;
	}
	
	public float getHealRate(){
		return healRate;
	}
	
	public void addManaRate(float mRate){
		manaRate += mRate;
	}
	
	public float getManaRate(){
		return manaRate;
	}
	
	public void addStamRate(float sRate){
		stamRate += sRate;
	}
	
	public float getStamRate(){
		return stamRate;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public float getStamina(){
		return stamina;
	}
	
	public void setStamina(float stam) {
		stamina = stam;
	}
	
	public float getArmorPer(){
		return ARMOR_PER;
	}
	
	public boolean isDamaged(){
		return isDamaged;
	}
	
	public void setDamaged(boolean b){
		isDamaged = b;
	}
	
	public void setAggro(boolean b){
		aggro = b;
	}
	
	public boolean getAggro(){
		return aggro;
	}
	
	public HashMap<Equip, Item> getEquip(){
		return equip;
	}
	
	public CopyOnWriteArrayList<EventText> getEventList(){
		return eventList;
	}
	
	public CopyOnWriteArrayList<BouncyText> getList(){
		return list;
	}
}