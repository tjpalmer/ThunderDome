package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import virassan.entities.creatures.npcs.Merchant;
import virassan.entities.creatures.player.Inventory;
import virassan.entities.creatures.player.Player;
import virassan.gfx.Assets;
import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.items.Item;
import virassan.items.ItemType;
import virassan.main.Handler;
import virassan.utils.Utils;

public class NPCShop {

	private Handler handler;
	private MouseInput mouseInput;
	private KeyInput keyInput;
	private Player player;
	private Inventory inv;
	
	private boolean isDragged;
	private int mercCurX = 210, mercCurY;
	private int mercMinY = 195, mercMaxY = 625;
	private final int mercSelHeight = 25;
	private int[] mercY;
	
	public NPCShop(Handler handler) {
		this.handler = handler;
		mouseInput = handler.getMouseInput();
		keyInput = handler.getKeyInput();
		mercY = new int[14];
		for(int i = 0; i < mercY.length; i++){
			if(i == 0){
				mercY[i] = mercMinY;
			}else{
				mercY[i] = mercY[i-1] + mercSelHeight;
			}
		}
	}

	public void render(Graphics g){
		//TODO finish rendering merchant properly - it's doing weird stuff with Select
		g.drawImage(Assets.menu, 0, 0, null);
		g.setFont(new Font("Verdana", Font.BOLD, 24));
		g.setColor(Color.WHITE);
		if(player.getCurrentMerchant().getCurList() == player.getCurrentMerchant().getBuyList()){
			g.setColor(Handler.BLUE_VIOLET);
		}
		g.drawString("BUY", 400, 50);
		g.setColor(Color.WHITE);
		if(player.getCurrentMerchant().getCurList() == player.getCurrentMerchant().getSellList()){
			g.setColor(Handler.BLUE_VIOLET);
		}
		g.drawString("SELL", 800, 50);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.PLAIN, 14));
		ArrayList<Item> curList = player.getCurrentMerchant().getCurList();
		HashMap<Item, Integer> hash = null;
		if(curList == player.getCurrentMerchant().getBuyList()){
			hash = player.getCurrentMerchant().getBuyItems();
		}else{
			hash = inv.getItemMap();
		}
		int x = 215;
		int y = 200;
		int yIncr = 25;
		int stringIncr = 13;
		int stringY = 200;
		int xCol1 = 25;
		int xCol2 = xCol1 + 25;
		int xCol3 = xCol2 + 135;
		int xCol4 = xCol3 + 25;
		int xCol5 = xCol4 + 400;
		for(Item key : curList){
			if(key.getStack() == 0){
				// Items with no stack limitations
				int stack = hash.get(key);
				if(stack != 0){
					g.setColor(Color.WHITE);
					g.drawString("" + key.getPrice(), x, stringY + stringIncr);
					g.drawImage(key.getImage(), x + xCol1, y, null);
					g.drawString(key.getName(), x + xCol2, stringY + stringIncr);
					g.drawString("" + stack, x + xCol3, stringY + stringIncr);
					g.drawString(key.getDescription(), x + xCol4, stringY + stringIncr);
					if(key.getItemType() == ItemType.FOOD && !key.getType().equals("")){
						g.drawString("" + (int)key.getAddAmt(), x + xCol5, stringY + stringIncr);
					}
					y += yIncr;
					stringY = y;
				}	
			}else{
				// Items with NO stacking at all
				g.setColor(Color.WHITE);
				g.drawString("" + key.getPrice(), x, stringY + stringIncr);
				g.drawImage(key.getImage(), x + xCol1, y, null);
				g.drawString(key.getName(), x + xCol2, stringY + stringIncr);
				g.drawString("" + 1, x + xCol3, stringY + stringIncr);
				g.drawString(key.getDescription(), x + xCol4, stringY + stringIncr);
				if(key.getItemType() == ItemType.WEAPON){
					g.drawString("" + key.getDmgAmt(), x + xCol5, stringY + stringIncr);
				}else if(key.getItemType() == ItemType.ARMOR){
					g.drawString("" + key.getArmorAmt(), x + xCol5, stringY + stringIncr);
				}
				
				y += yIncr;
				stringY = y;
			}
		}
		g.setColor(Handler.SELECTION_HIGHLIGHT);
		g.fillRect(mercCurX, mercCurY, 800 - 15, mercSelHeight);
	}
	
	public void tick(double delta){
		player = handler.getPlayer();
		inv = player.getInventory();
		Merchant curMerc = player.getCurrentMerchant();
		int itemIndex = curMerc.getCurList().size()-1;
		if(curMerc.getCurList().size() <= 0){
			itemIndex = 0;
		}
		int curIndex = 0;
		for(int i = 0; i < mercY.length; i++){
			if(mercY[i] == mercCurY){
				curIndex = i;
				break;
			}
		}
		if(curIndex >  itemIndex && curIndex != 0){
			mercCurY = mercY[itemIndex];
		}
		HUDManager.MENUTIMER += (System.currentTimeMillis() - HUDManager.MENULAST) * delta;
		HUDManager.MENULAST = System.currentTimeMillis() * (long)delta;
		if(HUDManager.MENUTIMER > HUDManager.MENUWAIT){
			if(keyInput.F){
				HUDManager.MENUTIMER = 0;
				handler.setState(States.World);
				handler.getEntityManager().setPaused(false);
			}
			if(keyInput.W){
				if(mercMinY < mercCurY){
					mercCurY -= mercSelHeight;
				}
				HUDManager.MENUTIMER = 0;
			}else if(keyInput.S){
				if(mercMaxY > mercCurY){
					mercCurY = Utils.clamp(mercCurY + mercSelHeight, mercMinY, mercY[itemIndex]);
				}
				HUDManager.MENUTIMER = 0;
			}else if(keyInput.D || keyInput.A){
				if(curMerc.getCurList() == curMerc.getBuyList()){
					curMerc.setCurList(curMerc.getSellList());
				}else if(curMerc.getCurList() == curMerc.getSellList()){
					curMerc.setCurList(curMerc.getBuyList());
				}
				mercCurY = mercMinY;
				HUDManager.MENUTIMER = 0;
			}else if(keyInput.E){
				int selIndex = 0;
				for(int i = 0; i < mercY.length; i++){
					if(mercCurY == mercY[i]){
						selIndex = i;
					}
				}
				curMerc.useItem(selIndex);
				HUDManager.MENUTIMER = 0;
			}
		}
	}
	
	public void merchantInteract(Merchant entity){
		player = handler.getPlayer();
		player.setCurrentMerchant(entity);
		int playerDist = (int)(Math.sqrt(Math.pow(player.getX() - entity.getX(), 2)+Math.pow(player.getY() - entity.getY(),2)));
		if(playerDist <= entity.getInteractDist()){
			entity.setInteract(true);
			handler.getEntityManager().setPaused(true);
		}
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(clicks.element() != null){
			outer : {
				while(clicks.element() != null){
					
				}
			}
		}
	}
	
	public void rightClick(){
		// TODO : add right click actions!
		LinkedQueue clicks = mouseInput.getRightClicks();
		if(handler.getEntityManager().getPaused()){

		}
	}
	
	public void hover(){
		
	}
	
	public void drag(){
		if(mouseInput.isDragged()){
			isDragged = true;
			Rectangle startRect =  new Rectangle(mouseInput.getStartDrag().x, mouseInput.getStartDrag().y, 1, 1);
		}else{
			if(isDragged){
				Rectangle startRect, endRect;
				try{
					startRect =  new Rectangle(mouseInput.getStartDrag().x, mouseInput.getStartDrag().y, 1, 1);
				}catch(NullPointerException e){
					startRect = new Rectangle(0, 0, 0, 0);
				}try{
					endRect = new Rectangle(mouseInput.getEndDrag().x, mouseInput.getEndDrag().y, 1, 1);
				}catch(NullPointerException e){
					endRect = new Rectangle(0, 0, 0, 0);
				}
			}
			isDragged = false;
		}
	}
}
