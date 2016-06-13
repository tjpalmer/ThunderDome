package virassan.items;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import virassan.entities.creatures.Player;

public class Inventory{
	
	public static final int MAX_STACK = 30;
	
	private Player player;
	HashMap<Item, Integer> counts;
	
	private Comparator<Item> sorter = new Comparator<Item>(){
		@Override
		public int compare(Item a, Item b) {
			return a.getName().compareTo(a.getName());
		}
		
	};
	
	
	// Item lists
	private ArrayList<Item> items;

	/**
	 * Creates an Inventory for the Player
	 * @param handler the Game's Handler for all the shit
	 */
	public Inventory(Player player) {
		this.player = player;
		items = new ArrayList<Item>();
		counts = new HashMap<Item, Integer>();
		initMap();
	}
	
	
	/**
	 * Adds an item count for each item that can be stacked 
	 */
	public void initMap(){
		for(Item i : Item.values()){
			if(i.getStack() > 1){
				counts.put(i, 0);
			}
		}
	}
	
	/**
	 * Renders the Inventory
	 * @param g
	 */
	public void render(Graphics g){
		//TODO Fix this bullshit lol
		int x = 80;
		int y = 100;
		g.setColor(Color.GRAY);
		g.fillRect(100, 100, 250, 250);
		for(Item item: items){
			x += 25;
			if(x > 330){
				x = 105;
				y += 25;
			}
			g.drawImage(item.getImage(), x, y, null);
		}
	}
	
	
	/**
	 * Adds an Item to the Inventory and Updates its stack count
	 * @param item the Item to be added
	 */
	public void addItems(Item item){
		items.add(item);
		counts.put(item, counts.get(item) +1);
	}
	
	public ArrayList<Item> getItems(){
		return items;
	}
	
	public Comparator<Item> getSorter(){
		return sorter;
	}
}
