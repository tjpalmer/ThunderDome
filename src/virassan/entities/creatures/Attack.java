package virassan.entities.creatures;

import virassan.gfx.Animation;

public class Attack {

	private float speed, damage;
	private int range;
	private String name;
	private Animation animation;
	
	public Attack(float speed, float damage, Animation animation, int range, String name) {
		this.speed = speed;
		this.damage = damage;
		this.animation = animation;
		this.range = range;
		this.name = name;
	}
	
	

	//GETTERS AND SETTERS
	
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
}
