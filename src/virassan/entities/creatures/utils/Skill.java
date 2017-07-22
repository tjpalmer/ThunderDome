package virassan.entities.creatures.utils;

import java.awt.image.BufferedImage;

import virassan.gfx.Animation;
import virassan.gfx.Assets;
import virassan.main.ID;

public enum Skill{
	
	HEAL_1("Heal", "restore", ID.Player, "health", 4, 0, "mana", 25, 20, Assets.heal_icon),
	CHOP("Chop", "dmg", ID.Enemy, "basic", 4, 200, "stam", 25, 60, Assets.chop_icon),
	STAB("Stab", "dmg", ID.Enemy, "basic", 2, 100, "stam", 5, 25, Assets.stab_icon),
	SLASH("Slash", "dmg", ID.Enemy, "basic", 5, 300, "stam", 15, 50, Assets.slash_icon);
	
	
	private int range;
	private int cooldown;
	private String name;
	private int cost;
	private ID target;
	private String costType;
	private String type;
	private Animation skillAnimation;
	private BufferedImage icon;
	private Buff buff;
	private int effectAmt;
	private String effectType;
	
	private Skill(String name, String type, ID target, int cooldown, int range, String costType, int cost, BufferedImage icon){
		this.name = name;
		this.type = type;
		this.cooldown = cooldown;
		this.costType = costType;
		this.cost = cost;
		this.icon = icon;
		this.range = range;
		this.target = target;
	}
	
	/**
	 * For damage/heal skilltypes. one hits
	 * @param name Name of the Skill
	 * @param type Type of skill (damage, buff/debuff, summon)
	 * @param cooldown Wait time until the entity can perform the skill again
	 * @param costType Health/Stamina/Mana
	 * @param cost Amount needed to use the skill
	 * @param effectAmt Amount of effect (ie how much the heal/damage)
	 * @param icon Picture!
	 */
	private Skill(String name, String type, ID target, String effectType, int cooldown, int range, String costType, int cost, int effectAmt, BufferedImage icon){
		this(name, type, target, cooldown, range, costType, cost, icon);
		this.effectAmt = effectAmt;
		this.effectType = effectType;
	}
	
	/**
	 * For skills that create a buff or debuff
	 * @param name
	 * @param type
	 * @param cooldown
	 * @param costType
	 * @param cost
	 * @param buff Create a new Buff/debuff
	 * @param icon
	 */
	private Skill(String name, String type, ID target, int cooldown, int range, String costType, int cost, Buff buff, BufferedImage icon){
		this(name, type, target, cooldown, range, costType, cost, icon);
		this.buff = buff;
	}
	
	public int getRange(){
		return range;
	}
	
	public int getEffectAmt(){
		return effectAmt;
	}
	
	public ID getTarget(){
		return target;
	}
	
	public Buff getBuff(){
		return buff;
	}
	
	public Animation getSkillAnimation(){
		return skillAnimation;
	}
	
	public BufferedImage getIcon(){
		return icon;
	}
	
	public int getCooldown() {
		return cooldown;
	}

	public String getName() {
		return name;
	}

	public String getEffectType(){
		return effectType;
	}
	
	public int getCost() {
		return cost;
	}

	public String getCostType() {
		return costType;
	}

	public String getType() {
		return type;
	}
	
	public String toString(){
		return name;
	}
}
