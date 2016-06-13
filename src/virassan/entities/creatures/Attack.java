package virassan.entities.creatures;

import virassan.gfx.Animation;

public class Attack {

	private float speed, damage;
	private int width, height;
	private String name;
	private Animation animation;
	
	public Attack(float speed, float damage, Animation animation, int width, int height, String name) {
		// TODO Auto-generated constructor stub
		this.speed = speed;
		this.damage = damage;
		this.animation = animation;
		this.width = width;
		this.height = height;
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
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
