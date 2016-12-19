package virassan.entities.creatures.player.traits;
import virassan.entities.creatures.player.Player;
import virassan.utils.Utils;

public class Traits {
	private Player player;
	private int charisma, strength, resilience, dexterity, intelligence;
	private final int MAX_LEVEL = 25;
	private final float PERSUASION = 0.02F, BARTER = 0.002F, ARMOR = 1F, HEALTH = 10, CRIT_MULT = 0.01F, 
						CRIT_CHANCE = 0.02F, STAMINA = 5, STAM_RATE = 0.005F, DMG_MULT = 0.01F;
	private final float MANA = 5, HEALTH_RATE = 1; // +1 Health per second
	private final float MANA_RATE = 0.5F; // + .5 Mana per second
	
	public Traits(Player player) {
		this.player = player;
		levelRes(1);
		levelChar(1);
		levelStr(1);
		levelInt(1);
		levelDex(1);
	}
	
	public Traits(Player player, int charisma, int strength, int resilience, int dexterity, int intelligence){
		this(player);
		levelRes(resilience-1);
		levelChar(charisma-1);
		levelStr(strength-1);
		levelDex(dexterity-1);
		levelInt(intelligence-1);
	}
	
	//GETTERS AND SETTERS
	public void levelChar(int amt){
		for(int i = 0; i < amt; i++){
			this.charisma = Utils.clamp(charisma + 1, 1, MAX_LEVEL);
			player.getStats().addPersuasion(PERSUASION);
			player.getStats().addBarter(BARTER);
		}
	}
	
	public void levelStr(int amt){
		for(int i = 0; i < amt; i++){
			this.strength = Utils.clamp(strength + 1, 1, MAX_LEVEL);
			player.getStats().addArmorRating(ARMOR);
			player.getStats().addMaxStam(STAMINA);
			player.getStats().addDmgMod(DMG_MULT);
			player.getStats().setStamina(player.getStats().getMaxStam());
		}
	}
	
	public void levelRes(int amt){
		for(int i = 0; i < amt; i++){
			this.resilience = Utils.clamp(resilience + 1, 1, MAX_LEVEL);
			player.getStats().addMaxHealth(HEALTH);
			player.getStats().addHealRate(HEALTH_RATE);
			player.getStats().setHealth(player.getStats().getMaxHealth());
		}
	}
	
	public void levelDex(int amt){
		for(int i = 0; i < amt; i++){
			this.dexterity = Utils.clamp(dexterity + 1, 1, MAX_LEVEL);
			player.getStats().addCritMult(CRIT_MULT);
			player.getStats().addCritChance(CRIT_CHANCE);
			player.getStats().addStamRate(STAM_RATE);
		}
	}
	
	public void levelInt(int amt){
		for(int i = 0; i < amt; i++){
			this.intelligence = Utils.clamp(intelligence + 1, 1, MAX_LEVEL);
			player.getStats().addMaxMana(MANA);
			player.getStats().addManaRate(MANA_RATE);
			player.getStats().setMana(player.getStats().getMaxMana());
		}
	}
	
	public void unLevelChar(int amt){
		for(int i = 0; i < amt; i++){
			this.charisma = Utils.clamp(charisma - 1, 1, MAX_LEVEL);
			player.getStats().addPersuasion(-PERSUASION);
			player.getStats().addBarter(-BARTER);
		}
	}
	
	public void unLevelStr(int amt){
		for(int i = 0; i < amt; i++){
			this.strength = Utils.clamp(strength - 1, 1, MAX_LEVEL);
			player.getStats().addArmorRating(-ARMOR);
			player.getStats().addMaxStam(-STAMINA);
			player.getStats().addDmgMod(-DMG_MULT);
		}
	}
	
	public void unLevelRes(int amt){
		for(int i = 0; i < amt; i++){
			this.resilience = Utils.clamp(resilience - 1, 1, MAX_LEVEL);
			player.getStats().addMaxHealth(-HEALTH);
			player.getStats().addHealRate(-HEALTH_RATE);
		}
	}
	
	public void unLevelDex(int amt){
		for(int i = 0; i < amt; i++){
			this.dexterity = Utils.clamp(dexterity - 1, 1, MAX_LEVEL);
			player.getStats().addCritMult(-CRIT_MULT);
			player.getStats().addCritChance(-CRIT_CHANCE);
			player.getStats().addStamRate(-STAM_RATE);
		}
	}
	
	public void unLevelInt(int amt){
		for(int i = 0; i < amt; i++){
			this.intelligence = Utils.clamp(intelligence - 1, 1, MAX_LEVEL);
			player.getStats().addMaxMana(-MANA);
			player.getStats().addManaRate(-MANA_RATE);
		}
	}
	
	public int getCharisma(){
		return charisma;
	}
	
	public int getStrength() {
		return strength;
	}

	public int getResilience() {
		return resilience;
	}

	public int getDexterity() {
		return dexterity;
	}

	public int getIntelligence() {
		return intelligence;
	}

	public Player getPlayer() {
		return player;
	}
}
