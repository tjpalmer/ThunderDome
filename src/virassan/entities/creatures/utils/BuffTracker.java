package virassan.entities.creatures.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;

import virassan.entities.creatures.Creature;

public class BuffTracker {

	private boolean isLive;
	private Creature target;
	private String name, effect, type;
	private BufferedImage image;
	private int time;
	private int count;
	private long timerSec, lastSec;
	private int effectAmt;
	private Buff buff;
	
	/**
	 * 
	 * @param target
	 * @param type
	 * @param name
	 * @param effect
	 * @param effectAmt
	 * @param time - In Seconds
	 */
	public BuffTracker(Creature target, BufferedImage image, String type, String name, String effect, int effectAmt, int time){
		this.target = target;
		this.image = image;
		this.name = name;
		this.effect = effect;
		this.effectAmt = effectAmt;
		this.time = time;
		this.type = type;
		isLive = true;
	}

	public BuffTracker(Creature target, Buff buff){
		this(target, buff.getImage(), buff.getType(), buff.getName(), buff.getEffect(), buff.getEffectAmt(), buff.getTime());
		this.buff = buff;
	}

	public void tick(double delta){
		// 1 second = 1,000 milliseconds
		if(isLive){
			timerSec += (System.currentTimeMillis() - lastSec);
			lastSec = System.currentTimeMillis();
			if(timerSec >= 1000){
				if(type.equals("buff")){
					switch(effect){
					case "heal": target.getStats().heal(effectAmt / time); break;
					}
				}else if(type.equals("debuff")){
					switch(effect){
					case "dmg": target.getStats().damage(effectAmt / time); break;
					}
				}
				count++;
				timerSec = 0;
			}
			if(count > time){
				isLive = false;
				System.out.println("Message: BuffTracker_tick Buff Dead");
			}
		}
	}
	
	public Buff getBuff(){
		return buff;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public boolean getLive(){
		return isLive;
	}
	
	public void setLive(boolean b){
		isLive = b;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public long getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public long getTimer() {
		timerSec += System.currentTimeMillis() - lastSec;
		lastSec = System.currentTimeMillis();
		return timerSec;
	}

	public void setTimer(long timer) {
		this.timerSec = timer;
	}

	public long getLast() {
		timerSec += System.currentTimeMillis() - lastSec;
		lastSec = System.currentTimeMillis();
		return lastSec;
	}

	public void setLast(long last) {
		this.lastSec = last;
	}

	public int getEffectAmt() {
		return effectAmt;
	}

	public void setEffectAmt(int effectAmt) {
		this.effectAmt = effectAmt;
	}
	
	public void setTarget(Creature target){
		this.target = target;
	}
}
