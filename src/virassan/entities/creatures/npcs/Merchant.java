package virassan.entities.creatures.npcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import virassan.entities.creatures.Creature;
import virassan.gfx.ImageLoader;
import virassan.items.Item;
import virassan.main.Handler;

public class Merchant extends Creature{

	private HashMap<Item, Integer> buyItems;
	private final Comparator<Item> itemSorter = Item.getSorter();
	private ArrayList<Item> curList;
	private ArrayList<Item> sellList, buyList;
	private BufferedImage image;
	private final String npcID;
	private int interactDist;
	private boolean isInteract;
	
	public Merchant(Handler handler, String npcID, String name, float x, float y, int width, int height, String filepath) {
		// TODO Add portrait!
		super(handler, name, x, y, width, height, 1);
		this.npcID = npcID;
		image = ImageLoader.loadImage(filepath);
		interactDist = (int)(Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
		bounds.x += 22;
		bounds.width -= 46;
		bounds.y += 54;
		bounds.height -= 60;
		isInteract = false;
		buyItems = new HashMap<Item, Integer>();
		buyList = new ArrayList<Item>();
		//TODO: for testing purposes added a cherry manually
		buyItems.put(Item.CHERRY, 5);
		for(Item item : buyItems.keySet()){
			buyList.add(item);
		}
		buyList.sort(itemSorter);
		
		sellList = new ArrayList<Item>();
		for(Item item : handler.getPlayer().getInventory().getItemMap().keySet()){
			if(handler.getPlayer().getInventory().getItemMap().get(item) > 0){
				sellList.add(item);
				
			}
		}
		sellList.sort(itemSorter);
		curList = buyList;
	}

	public Merchant(Handler handler, String npcID, String name, float x, float y, int width, int height, String filepath, HashMap<Item, Integer> buyItems){
		this(handler, npcID, name, x, y, width, height, filepath);
		this.buyItems = buyItems;
		for(Item item : buyItems.keySet()){
			buyList.add(item);
		}
		buyList.sort(itemSorter);
	}
	
	@Override
	public void resetTimer() {	
	}

	@Override
	public void tick(double delta) {
		if(!handler.getEntityManager().getPaused()){
			// TODO: update the animation/image
		}else{
			for(Item item : handler.getPlayer().getInventory().getItemMap().keySet()){
				if(handler.getPlayer().getInventory().getItemMap().get(item) <= 0){
					sellList.remove(item);
					sellList.sort(itemSorter);
				}else{
					if(!sellList.contains(item)){
						sellList.add(item);
					}
				}
			}
			for(Item item : buyItems.keySet()){
				if(buyItems.get(item) <= 0){
					buyList.remove(item);
					buyList.sort(itemSorter);
				}else{
					if(!buyList.contains(item)){
						buyList.add(item);
					}
				}
			}
		}
	}

	@Override
	public void render(Graphics g) {
		float xrel = vector.normalize().dX ;//* handler.getGameCamera().getWidth();
		float yrel = vector.normalize().dY ;//* handler.getGameCamera().getHeight();
		
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		g.drawImage(image, (int)(xrel - handler.getGameCamera().getxOffset()), (int)(yrel - handler.getGameCamera().getyOffset()), null);
		g.setColor(Color.WHITE);
		g.drawString(name, (int)(xrel - handler.getGameCamera().getxOffset() + 2), (int)(yrel - handler.getGameCamera().getyOffset() - 5));
		int temp = (int)(Math.sqrt(Math.pow(xrel - handler.getPlayer().getX(), 2) + Math.pow(yrel - handler.getPlayer().getY(), 2)));
		if(temp <= interactDist){
			g.drawString("F", (int)(xrel - handler.getGameCamera().getxOffset() + (width / 2) - 5), (int)(yrel - handler.getGameCamera().getyOffset() + height + 10));
		}
	}

	public void useItem(int index){
		Item item = curList.get(index);
		float price = item.getPrice();
		if(curList == buyList){
			if((handler.getPlayer().getGold() - price) >= 0){
				buyItems.replace(item, buyItems.get(item) - 1);
				handler.getPlayer().getInventory().addItems(item, true);
			}
		}else if(curList == sellList){
			price -= (price * 0.20F);
			handler.getPlayer().addGold(price);
			handler.getPlayer().getInventory().removeItem(item);
			//TODO: Add the item sold to Merchant to the Merchant's Buy List
		}
	}
	
	@Override
	public void unPause() {	
	}

	// GETTERS AND SETTERS
	public Image getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public int getInteractDist() {
		return interactDist;
	}

	public void setInteractDist(int interactDist) {
		this.interactDist = interactDist;
	}

	public boolean isInteract() {
		return isInteract;
	}

	public void setInteract(boolean isInteract) {
		this.isInteract = isInteract;
	}
	
	public ArrayList<Item> getCurList(){
		return curList;
	}
	
	public void setCurList(ArrayList<Item> list){
		curList = list;
	}
	
	public ArrayList<Item> getBuyList(){
		return buyList;
	}
	
	public ArrayList<Item> getSellList(){
		return sellList;
	}
	
	public HashMap<Item, Integer> getBuyItems(){
		return buyItems;
	}
	
	public String getNpcID(){
		return npcID;
	}
	
	public String toString(){
		return name;
	}

	@Override
	public Class findClass() {
		return getClass().getSuperclass();
	}
}
