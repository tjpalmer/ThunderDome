package virassan.items;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import virassan.entities.creatures.utils.Move;
import virassan.gfx.Assets;
import virassan.gfx.hud.ItemPickUp;
import virassan.main.Display;
import virassan.main.Handler;

public class ItemManager{

	private CopyOnWriteArrayList<ItemPickUp> itemsPicked = new CopyOnWriteArrayList<>();
	
	private ArrayList<Drop> items;
	public static final int PICKUP_DIST = 100;
	private Handler handler;
	
	private long timer, lastTime;
	private final int interval = 450;
	private int speed;
	
	public ItemManager(Handler handler) {
		items = new ArrayList<Drop>();
		this.handler = handler;
		timer = 0;
		speed = 2;
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
	
	public void tick(double delta){
		for(ItemPickUp item : itemsPicked){
			if(item.isLive()){
				item.tick(delta);
			}else{
				itemsPicked.remove(item);
			}
		}
		timer += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		if(timer >= interval){
			for(int i = 0; i < items.size(); i++){
				double playerDist = Math.sqrt((double)(Math.pow(items.get(i).getCenter().dX - handler.getPlayer().getCenter().dX, 2) + 
						Math.pow(items.get(i).getCenter().dY - handler.getPlayer().getCenter().dY, 2)));
				if(playerDist <= PICKUP_DIST){
					Move.move(items.get(i), speed, -1, (float)delta);
				}
				//TODO: not picking them up proper - has been picking them up super quick
				if(Move.isHit(items.get(i).getCenter())){
					handler.getPlayer().getInventory().addItems(items.get(i).getItem(), true);
					items.remove(items.get(i));
					i--;
				}
			}
		}
	
	}
	
	public void render(Graphics g){
		for(Drop i : items){
			g.drawImage(i.getItem().getImage(), (int)(i.getPos().dX - handler.getGameCamera().getxOffset()), (int)(i.getPos().dY - handler.getGameCamera().getyOffset()), null);
		}
		int x = handler.getWidth() - Assets.ITEM_WIDTH - 5;
		int y = handler.getHeight() - Assets.ITEM_HEIGHT - 5;
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
