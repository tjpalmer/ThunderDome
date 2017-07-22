package virassan.entities.creatures.player;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import virassan.entities.creatures.utils.BuffTracker;
import virassan.gfx.Assets;
import virassan.items.Item;
import virassan.items.ItemType;
import virassan.utils.Utils;

public class Inventory{
	
	public static final int MAX_STACK = 30;
	
	private Player player;
	private HashMap<Item, Integer> counts;
	
	private Comparator<Item> sorter = new Comparator<Item>(){
		@Override
		public int compare(Item a, Item b) {
			return a.getName().compareTo(a.getName());
		}
		
	};
	
	// Item lists
	
	private Rectangle[][] slots;
	private Rectangle head, chest, legs, feet, mainHand, offHand, shoulders, hands, acc1, acc2;
	private Rectangle[] equipSlots;
	private Item[][] foodSlots, weapSlots, armSlots, junkSlots, miscSlots;

	/**
	 * Creates an Inventory for the Player
	 * @param handler the Game's Handler for all the shit
	 */
	public Inventory(Player player) {
		this.player = player;
		/*
		weapon = new ArrayList<Item>();
		armor = new ArrayList<Item>();
		junk = new ArrayList<Item>();
		food = new ArrayList<Item>();
		*/
		counts = new HashMap<Item, Integer>();
		//TODO add a misc item
		// curItemList = food;
		slots = new Rectangle[8][5];
		float x = 48;
		int side = 64;
		float xspace = 16.5F;
		int yspace = 25;
		for(int i = 0; i < slots.length; i++){
			int y = 145;
			for(int k = 0; k < slots[i].length; k++){
				slots[i][k] = new Rectangle((int)x, y, side, side);
				y += side + yspace;
			}
			x += side + xspace;
		}
		foodSlots = new Item[8][5];
		weapSlots = new Item[8][5];
		armSlots = new Item[8][5];
		junkSlots = new Item[8][5];
		miscSlots = new Item[8][5];
		head = new Rectangle(733, 148, side, side);
		chest = new Rectangle(733, 238, side, side);
		legs = new Rectangle(733, 328, side, side);
		feet = new Rectangle(733, 418, side, side);
		shoulders = new Rectangle(1153, 148, side, side);
		hands = new Rectangle(1153, 238, side, side);
		acc1 = new Rectangle(1153, 327, side, side);
		acc2 = new Rectangle(1153, 418, side, side);
		mainHand = new Rectangle(900, 498, side, side);
		offHand = new Rectangle(990, 498, side, side);
		equipSlots = new Rectangle[]{mainHand, offHand, head, chest, legs, feet, shoulders, hands, acc1, acc2};
	}
	
	public void tick(){
	}
	
	/**
	 * Renders the Inventory
	 * @param g
	 */
	public void render(Graphics g){
	}
	
	/**
	 * Adds an Item to the Inventory and Updates its stack count
	 * @param item the Item to be added
	 */
	public void addItems(Item item, boolean show){
		if(show){
			player.getHandler().getItemManager().addItem(item.getImage());
		}
		boolean isFound = false;
		if(counts.containsKey(item)){
			counts.replace(item, counts.get(item)+1);
		}else{
			counts.put(item, 1);
		}
		Item[][] items = getItemSlots(item.getItemType());
		if(items != null){
			outer: {
				for(int i = 0; i < items.length; i++){
					for(Item obj : items[i]){
						if(obj == item){
							isFound = true;
							break outer;
						}
					}
				}
			}
			if(!isFound){
				found : {
					for(int i = 0; i < items.length; i++){
						for(int k = 0; k < items[i].length; k++){
							for(int p = 0; p < items.length; p++){
								if(items[p][k] == null){
									items[p][k] = item;
									break found;
								}
							}
						}
					}
				}
				switch(item.getItemType()){
				case WEAPON: weapSlots = items; break;
				case ARMOR: armSlots = items; break;
				case FOOD: foodSlots = items; break;
				case JUNK: junkSlots = items; break;
				case MISC: miscSlots = items; break;
				}
			}
		}
	}
	
	public void addItems(Item item, int amount, boolean show){
		for(int i = 0; i < amount; i++){
			addItems(item, show);
		}
	}
	
	public ArrayList<Item> getItems(){
		ArrayList<Item> temp = new ArrayList<Item>();
		for(Item i : counts.keySet()){
			if(counts.get(i) > 0){
				temp.add(i);
			}
		}
		return temp;
	}
	
	//TODO: finish method, complete switch cases
	public void useItem(Item item){
		// TODO complete the rest of the types of items
		Item[][] items = getItemSlots(item.getItemType());
		Point target = null;
		for(int i = 0; i < items.length; i++){
			for(int k = 0; k < items[i].length; k++){
				if(items[i][k] == item){
					target = new Point(i, k);
				}
			}
		}
		switch(item.getItemType()){
		case FOOD:
			if(item.getType() != null){
				if(counts.get(item) > 0){
					switch(item.getType()){
						case "rHealth": 
							player.getStats().heal((float)item.getAddAmt());
							removeItem(target, foodSlots); System.out.println("HEALED FOR: " + item.getAddAmt());
								break;
						case "mExp":
							//TODO: do stuff
							break;
						case "mDmg":
							//TODO
							break;
						case "tHealth":
							//TODO
							player.getStats().addBuff(new BuffTracker(player, Assets.buff001, "buff", item.getName(), "heal", (int)item.getAddAmt(), item.getAddTimer()));
							removeItem(target, foodSlots);
							break;
						case "tMana":
							// TODO
							break;
						case "tStam":
							//TODO
							break;
						case "rStam":
							player.getStats().setStamina(Utils.clamp((float)(player.getStats().getStamina() + item.getAddAmt()), 0F, player.getStats().getMaxStam()));
							removeItem(target, foodSlots); break;
						case "rMana":
							player.getStats().setMana(Utils.clamp((float)(player.getStats().getMana() + item.getAddAmt()), 0F, player.getStats().getMaxMana()));
							removeItem(target, foodSlots); break;
					}
				}
			}break;
		case WEAPON:
			//TODO: implement equip crap here~
			if(counts.get(item) > 0){
				removeItem(target, weapSlots);
				player.getStats().equip(item);
			}break;
		case ARMOR:
			if(counts.get(item) > 0){
				removeItem(target, armSlots);
				player.getStats().equip(item);
			}
			break;
		case JUNK:
			break;
		case MISC:
			break;
		}
	}
	
	public Comparator<Item> getSorter(){
		return sorter;
	}
	
	public Item getItemEquip(int index){
		switch(index){
		case 0: return player.getStats().getMainH();
		case 1: return player.getStats().getOffH();
		case 2: return player.getStats().getHead();
		case 3: return player.getStats().getChest();
		case 4: return player.getStats().getLegs();
		case 5: return player.getStats().getFeet();
		case 6: return player.getStats().getShoulders();
		case 7: return player.getStats().getHands();
		case 8: return player.getStats().getAcc1();
		case 9: return player.getStats().getAcc2();
		}
			
		return null;
	}
	
	public Point getItemSlotIndex(Item item){
		Item[][] items = getItemSlots(item.getItemType());
		Point temp = null;
		for(int i = 0; i < items.length; i++){
			for(int k = 0; k < items[i].length; k++){
				if(item == items[i][k]){
					return new Point(k, i);
				}
			}
		}
		return temp;
	}
	
	public Item[][] getItemSlots(ItemType item){
		Item[][] items = new Item[8][5];
		switch(item){
		case WEAPON: items = weapSlots; break;
		case ARMOR: items = armSlots; break;
		case FOOD: items = foodSlots; break;
		case JUNK: items = junkSlots; break;
		case MISC: items = miscSlots; break;
		}
		return items;
	}
	
	public Rectangle[] getEquipSlots(){
		return equipSlots;
	}
	
	public Rectangle[][] getSlots(){
		return slots;
	}
	
	public HashMap<Item, Integer> getItemMap(){
		return counts;
	}

	public boolean removeItems(Point target, Item[][] items, int amount){
		for(int i = 0; i < amount; i++){
			if(removeItem(target, items)){
				return true;
			}
		}
		return false;
	}
	
	public boolean removeItems(Item item, int amount){
		for(int i = 0; i < amount; i++){
			if(removeItem(item)){
				return true;
			}
		}
		return false;
	}
	
	public boolean removeItem(Point target, Item[][] items){
		if(target != null){
			System.out.println("Before - " + counts.get(items[target.x][target.y]));
			counts.replace(items[target.x][target.y], counts.get(items[target.x][target.y]) - 1);
			System.out.println("After - " + counts.get(items[target.x][target.y]));
			if(counts.get(items[target.x][target.y]) <= 0){
				counts.remove(items[target.x][target.y]);
				items[target.x][target.y] = null;
			}
			return true;
		}
		return false;
	}
	
	public boolean removeItem(Item item) {
		Item[][] items = getItemSlots(item.getItemType());
		Point temp = new Point(0, 0);
		for(int i = 0; i < items.length; i++){
			for(int k = 0; k < items[i].length; k++){
				if(item == items[i][k]){
					temp = new Point(i, k);
					switch(item.getItemType()){
					case WEAPON: removeItem(temp, weapSlots); break;
					case ARMOR: removeItem(temp, armSlots); break;
					case FOOD: removeItem(temp, foodSlots); break;
					case JUNK: removeItem(temp, junkSlots); break;
					case MISC: removeItem(temp, miscSlots); break;
					}
					if(counts.get(item) <= 0){
						counts.remove(item);
					}
					return true;
				}
			}
		}
		return false;
	}
}
