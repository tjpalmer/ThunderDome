package virassan.entities.statics;

import java.awt.Image;
import java.awt.image.BufferedImage;

import virassan.entities.Entity;
import virassan.entities.creatures.utils.Stats;
import virassan.main.Handler;

public abstract class StaticEntity extends Entity{

	protected boolean damaged;
	protected Stats stats;
	protected boolean dimensioned;
	
	public StaticEntity(Handler handler, float x, float y, int width, int height, Image image, String entityName) {
		super(handler, x, y, width, height);
		dimensioned = true;
	}

	public StaticEntity(Handler handler, float x, float y, int width, int height) {
		super(handler, x, y, width, height);
		dimensioned = true;
	}
	
	public boolean isDamaged(){
		return damaged;
	}
	
	public void setDamaged(boolean damaged){
		this.damaged = damaged;
	}
	
	public Stats getStats(){
		return stats;
	}
	
	public boolean getDimension(){
		return dimensioned;
	}
	
}
