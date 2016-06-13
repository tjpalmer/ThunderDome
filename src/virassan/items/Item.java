package virassan.items;

import java.awt.image.BufferedImage;

import virassan.gfx.Assets;


public enum Item{
	
	// ORDER by Alphabet!
	APPLE("Apple", ItemType.FOOD, Inventory.MAX_STACK, "A tasty red apple!", 10, Assets.apple),
	BANDAID("Bandaid", ItemType.JUNK, 15, "Ew, a used bandaid.", 0, Assets.bandaid),
	SLIME("Green Jelly Slime", ItemType.JUNK, Inventory.MAX_STACK, "Gross.", 0, Assets.slime),
	TOAST("Toast", ItemType.FOOD, Inventory.MAX_STACK, "Oh no, I'm late!", 12, Assets.toast);
	
	
	private String name, description;
	private ItemType itemType;
	private BufferedImage image;
	private int price;
	private int stack;

	/**
	 * Creates an Item object
	 * @param name Name of the Item
	 * @param itemType Type of Item: Weapon, Armor, Food, Junk
	 * @param description Item description
	 * @param buyPrice The base buying price of the Item
	 * @param image The image used for rendering the Item
	 */
	private Item(String name, ItemType itemType, int stack, String description, int buyPrice, BufferedImage image) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.description = description;
		this.itemType = itemType;
		this.image = image;
		this.price = buyPrice;
		this.stack = stack;
	}
	
	
	//GETTERS

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public int getPrice(){
		return price;
	}
	
	public int getStack(){
		return stack;
	}
}
