package virassan.entities.creatures.utils;

import java.awt.image.BufferedImage;

import virassan.gfx.Assets;

public enum Buff{

	CURSE_1("debuff", "Curse", "dmg", 21, 6000, Assets.buff001),
	RESTORE_1("buff", "Restore", "heal", 50, 8000, Assets.buff002);
	
	private String name, effect, type;
	private BufferedImage image;
	private int effectAmt;
	private int time;
	
	private Buff(String type, String name, String effect, int effectAmt, int time, BufferedImage image){
		this.name = name;
		this.image = image;
		this.effect = effect;
		this.effectAmt = effectAmt;
		this.time = time;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public String getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public String getEffect(){
		return effect;
	}
	
	public int getEffectAmt(){
		return effectAmt;
	}
	
	public int getTime(){
		return time;
	}
	
	public String toString(){
		return name;
	}
}
