package virassan.items;

import java.util.Random;

import virassan.utils.Vector2F;

public class Drop implements Comparable{

	private Item item;
	private double chance;
	private Vector2F pos;
	
	public Drop(Item item, double chance) {
		this.item = item;
		this.chance = chance;
	}
	
	public boolean isDropped(){
		double ran = new Random().nextDouble();
		if(ran <= chance){
			return true;
		}
		return false;
	}

	public void setPos(Vector2F pos){
		this.pos = pos;
	}
	
	public Vector2F getPos(){
		return pos;
	}
	
	public Vector2F getCenter(){
		return (pos.add(new Vector2F(item.getWidth()/2, item.getHeight()/2)));
	}
	
	public void setChance(double chance){
		this.chance = chance;
	}
	
	public double getChance(){
		return chance;
	}
	
	public Item getItem(){
		return item;
	}

	@Override
	public int compareTo(Object o) {
		Drop item = (Drop)o;
		return this.item.getName().compareTo(item.getItem().getName());
	}
	
}
