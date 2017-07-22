package virassan.items;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import virassan.gfx.Assets;
import virassan.gfx.hud.ItemPickUp;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.main.Vector2F;

public class ItemManager{

	private CopyOnWriteArrayList<ItemPickUp> itemsPicked = new CopyOnWriteArrayList<>();
	
	private ArrayList<Drop> items;
	private final int PICKUP_DIST = 100;
	private Handler handler;
	
	private long timer, lastTime;
	private final int speed = 450;
	
	public ItemManager(Handler handler) {
		items = new ArrayList<Drop>();
		this.handler = handler;
		timer = 0;
	}
	
	public void addItem(Drop item){
		items.add(item);
	}
	
	public void removeItem(Drop item){
		for(int i = 0; i < items.size(); i++){
			if(items.get(i) == item){
				items.remove(i);
				return;
			}
		}
	}
	
	public void tick(){
		for(ItemPickUp item : itemsPicked){
			if(item.isLive()){
				item.tick();
			}else{
				itemsPicked.remove(item);
			}
		}
		timer += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		if(timer >= speed){
			for(int i = 0; i < items.size(); i++){
				double playerDist = Math.sqrt((double)(Math.pow((items.get(i).getPos().xPos + Assets.ITEM_WIDTH/2) - (handler.getPlayer().getX() + handler.getPlayer().getWidth()/2), 2) + Math.pow((items.get(i).getPos().yPos + Assets.ITEM_HEIGHT/2) - (handler.getPlayer().getY() + handler.getPlayer().getHeight()/2), 2)));
				if(playerDist <= PICKUP_DIST){
					int velX = 0;
					int velY = 0;
					if(handler.getPlayer().getX() + handler.getPlayer().getWidth()/2 > items.get(i).getPos().xPos + Assets.ITEM_WIDTH/2){
						velX += 1;
					}else if(handler.getPlayer().getX() + handler.getPlayer().getWidth()/2 < items.get(i).getPos().xPos + Assets.ITEM_WIDTH/2){
						velX -= 1;
					}
					if(handler.getPlayer().getY() + handler.getPlayer().getHeight()/2 > items.get(i).getPos().yPos + Assets.ITEM_HEIGHT/2){
						velY += 1;
					}else if(handler.getPlayer().getY() + handler.getPlayer().getHeight()/2 < items.get(i).getPos().yPos + Assets.ITEM_HEIGHT/2){
						velY -= 1;
					}
					items.get(i).setPos(new Vector2F(items.get(i).getPos().xPos + velX, items.get(i).getPos().yPos + velY));
				}
				if(playerDist <= 5){
					//TODO : add item to itemspickedup list
					handler.getPlayer().getInventory().addItems(items.get(i).getItem(), true);
					items.remove(items.get(i));
					i--;
				}
			}
		}
	
	}
	
	public void render(Graphics g){
		for(Drop i : items){
			g.drawImage(i.getItem().getImage(), (int)(i.getPos().xPos - handler.getGameCamera().getxOffset()), (int)(i.getPos().yPos - handler.getGameCamera().getyOffset()), null);
		}
		int x = Display.WIDTH - Assets.ITEM_WIDTH - 5;
		int y = Display.HEIGHT - Assets.ITEM_HEIGHT - 5;
		for(ItemPickUp item : itemsPicked){
			if(item.isLive()){
				item.setX(x);
				item.setY(y);
				item.render(g);
				y -= item.getImage().getHeight();
			}
		}
	}
	
	public void addItem(BufferedImage image){
		itemsPicked.add(new ItemPickUp(image));
	}
}
