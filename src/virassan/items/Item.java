package virassan.items;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import virassan.gfx.Assets;


public enum Item{
	
	// ORDER by Alphabet!
	// A
	APPLE("Apple", ItemType.FOOD, 0, "A tasty red apple!", 10, "rHealth", 10, Assets.apple),
	APPLE_PIE("Apple Pie", ItemType.FOOD, 0, "Delicious apple pie", 25, "rHealth", 10, Assets.apple),
	
	// B
	BANDAID("Bandaid", ItemType.JUNK, 0, "Ew, a used bandaid.", 5, Assets.bandaid),
	BROAD_SWORD("Broad Sword", ItemType.WEAPON, 1, "A wide pointy stick", Equip.MAINHAND, 35, 20, Assets.broad_sword),
	
	// C
	CHERRY("Cherries", ItemType.FOOD, 0, "Unfortunately, not the kind soaked in vodka", 10, "tHealth", 20, 10, Assets.cherry),
	
	// D
	DAGGER("Dagger", ItemType.WEAPON, 1, "Just a dagger", Equip.MAINHAND, 15, 8, Assets.dagger),
	
	// E
	
	// F
	FEATHER("Feather", ItemType.JUNK, 0, "Light as a feather, but is it stiff like a board?", 0, Assets.feather),
	
	// G
	GREEN_APPLE("Green Apple", ItemType.FOOD, 0, "A delightfully tart green apple. Yum!", 15, "rStam", 5, Assets.green_apple),
	SLIME("Green Jelly Slime", ItemType.JUNK, 0, "Gross.", 0, Assets.slime),
	
	// H
	
	// I
	IRON_BOOTS("Iron Boots", ItemType.ARMOR, 1, 2, "Careful not to step on anyone's toes", Equip.FEET, 10, Assets.iron_boots),
	IRON_CUIRASS("Iron Cuirass", ItemType.ARMOR, 1, 10, "A simple but sturdy breastplate.", Equip.CHEST, 25, Assets.iron_cuirass),
	IRON_GAUNTLETS("Iron Gauntlets", ItemType.ARMOR, 1, 2, "Nothing like metal gloves to keep your hands warm", Equip.HANDS, 10, Assets.iron_gauntlet),
	IRON_GREAVES("Iron Greaves", ItemType.ARMOR, 1, 5, "Gotta protect them thick thighs", Equip.LEGS, 15, Assets.iron_greaves),
	IRON_HELM("Iron Helm", ItemType.ARMOR, 1, 2, "A metal bucket to keep the rain off your head.", Equip.HEAD, 10, Assets.iron_helm),
	
	// J
	
	// K
	
	// L
	
	// M
	
	// N
	
	// O
	
	// P
	
	// Q
	
	// R
	
	// S
	STAFF("Staff", ItemType.WEAPON, 1, "Hit them on the head with this big stick", Equip.MAINHAND, 45, 9, Assets.staff),
	SWORD("Sword", ItemType.WEAPON, 1, "The pointy end goes in the Other guy.", Equip.MAINHAND, 25, 10, Assets.sword),
	
	// T
	TIRE("Old Tire", ItemType.JUNK, 0, "You could always make a tire swing", 0, Assets.tire),
	TOAST("Toast", ItemType.FOOD, 0, "Oh no, I'm late!", 12, "rHealth", 10, Assets.toast),
	
	// U
	
	// V
	
	// W
	WOODCUTTER_AXE("Woodcutter's Axe", ItemType.WEAPON, 1, "Chop them into firewood!", Equip.MAINHAND, 35, 20, Assets.woodcutter_axe);
	
	private static final int DEFAULT_RANGE = 40;
	private int weapRange;
	private String name, description, type;
	private ItemType itemType;
	private Equip slot;
	private BufferedImage image;
	// zero stack means no Limit
	private int price, stack, addTimer, dmgAmt, armorAmt;
	private double addAmt;
	private static Comparator<Item> itemSorter = new Comparator<Item>(){
        @Override
        public int compare(Item s1, Item s2){
            return s1.getName().compareToIgnoreCase(s2.getName());
        }};

	/**
	 * Creates an Item object
	 * @param name Name of the Item
	 * @param itemType Type of Item: Weapon, Armor, Food, Junk
	 * @param description Item description
	 * @param buyPrice The base buying price of the Item
	 * @param image The image used for rendering the Item
	 */
	private Item(String name, ItemType itemType, int stack, String description, int buyPrice, BufferedImage image) {
		// Junk Item Constructor and other items that don't add any stats/do anything
		this.name = name;
		this.description = description;
		this.itemType = itemType;
		this.image = image;
		this.price = buyPrice;
		this.stack = stack;
	}
	
	private Item(String name, ItemType itemType, int stack, String description, int buyPrice, String type, double addAmt, BufferedImage image){
		// Items that don't have a timer
		this.name = name;
		this.description = description;
		this.itemType = itemType;
		this.image = image;
		this.price = buyPrice;
		this.stack = stack;
		this.type = type;
		this.addAmt = addAmt;
	}
	
	private Item(String name, ItemType itemType, int stack, String description, int buyPrice, String type, double addAmt, int addTimer, BufferedImage image){
		// Items that have a timer to w/e they add
		this.name = name;
		this.description = description;
		this.itemType = itemType;
		this.image = image;
		this.price = buyPrice;
		this.stack = stack;
		this.type = type;
		this.addAmt = addAmt;
		this.addTimer = addTimer;
	}
	
	private Item(String name, ItemType itemType, int stack, String description, Equip slot, int buyPrice, int dmgAmt, BufferedImage image) {
		// Basic Weapons with no extra modifiers
		this.name = name;
		this.description = description;
		this.itemType = itemType;
		this.image = image;
		this.price = buyPrice;
		this.stack = stack;
		this.dmgAmt = dmgAmt;
		this.slot = slot;
		this.weapRange = DEFAULT_RANGE;
	}
	
	private Item(String name, ItemType itemType, int stack, String description, Equip slot, int range, int buyPrice, int dmgAmt, BufferedImage image){
		this(name, itemType, stack, description, slot, buyPrice, dmgAmt, image);
		this.weapRange = range;
	}
	
	private Item(String name, ItemType itemType, int stack, int armorAmt, String description, Equip slot, int buyPrice, BufferedImage image) {
		// Basic Armor with no extra modifiers
		this.name = name;
		this.description = description;
		this.itemType = itemType;
		this.image = image;
		this.price = buyPrice;
		this.stack = stack;
		this.armorAmt = armorAmt;
		this.slot = slot;
	}
	
	//GETTERS
	public int getWeapRange(){
		return weapRange;
	}
	
	public Equip getEquip(){
		return slot;
	}
	
	public int getArmorAmt(){
		return armorAmt;
	}
	
	public int getDmgAmt(){
		return dmgAmt;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public String getType(){
		return type;
	}
	
	public double getAddAmt(){
		return addAmt;
	}
	
	public int getAddTimer(){
		return addTimer;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public int getWidth(){
		return image.getWidth();
	}
	
	//FIXME: how to get height of Image?
	public int getHeight(){
		return image.getHeight();
	}
	
	public int getPrice(){
		return price;
	}
	
	public int getStack(){
		return stack;
	}
	
	public static Comparator<Item> getSorter(){
		return itemSorter;
	}
	
	public ArrayList<String> getInfo(){
		ArrayList<String> info = new ArrayList<String>();
		info.add(name);
		switch(itemType){
		case ARMOR:
			info.add("Armor: " + (int)armorAmt);
			break;
		case FOOD:
			switch(type){
			case "rHealth": 
				info.add("Restores " + (int)addAmt + " Health.");
				break;
			case "mExp":
				info.add("Multiplies experience gained by " + (int)addAmt + " for " + (int)(addTimer / 100) + "seconds.");
				break;
			case "mDmg":
				info.add("Multiplies damage dealt by " + (int)addAmt + " for " + (int)(addTimer / 100) + "seconds.");
				break;
			case "tHealth":
				info.add("Restores " + (int)addAmt + " Health for " + (int)(addTimer/100) + "seconds.");
				break;
			case "tMana":
				info.add("Restores " + (int)addAmt + " Mana for " + (int)(addTimer/100) + "seconds.");
				break;
			case "tStam":
				info.add("Restores " + (int)addAmt + " Stamina for " + (int)(addTimer/100) + "seconds.");
				break;
			case "rStam":
				info.add("Restores " + (int)addAmt + " Stamina.");
				break;
			case "rMana":
				info.add("Restores " + (int)addAmt + " Mana.");
				break;
			}
			break;
		case JUNK:
			break;
		case MISC:
			break;
		case WEAPON:
			info.add("Damage: " + dmgAmt);
			break;
		}
		info.add(description);
		return info;
	}
	
	public String toString(){
		return name;
	}
}
