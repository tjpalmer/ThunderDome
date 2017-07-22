package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import virassan.entities.creatures.player.Inventory;
import virassan.entities.creatures.player.Player;
import virassan.gfx.Assets;
import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.items.Equip;
import virassan.items.Item;
import virassan.items.ItemType;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.utils.ScrollPanel;
import virassan.utils.Utils;

public class MenuInventory {

	private final ItemType tabArray[] = {ItemType.FOOD, ItemType.WEAPON, ItemType.ARMOR, ItemType.JUNK, ItemType.MISC};
	private KeyInput keyInput;
	private MouseInput mouseInput;
	private Handler handler;
	private Player player;
	private Inventory inv;
	
	private ItemType curTab;
	private Item curItem;
	
	private Rectangle[] itemMenu;
	private String itemName;
	private ScrollPanel itemScroll;
	private boolean displayItemMenu, equipItemMenu, displayItemInfo, isDragged, isScroll;
	private Rectangle curMenu, selection;
	private Rectangle playerPortrait = new Rectangle(815, 145, 320, 340);
	private Point curPoint;
	
	
	public MenuInventory(Handler handler) {
		this.handler = handler;
		keyInput = handler.getKeyInput();
		mouseInput = handler.getMouseInput();
		curTab = ItemType.FOOD;
	}

	public void render(Graphics g){
		player = handler.getPlayer();
		inv = player.getInventory();
		Rectangle[][] slots = inv.getSlots();
		g.drawImage(Assets.invCharMenu, 0, 0, null);
		g.setFont(new Font("Verdana", Font.BOLD, 26));
		String food = "Food";
		String weap = "Weapons";
		String arm = "Armor";
		String junk = "Junk";
		String misc = "Misc";
		int spacing = 40;
		int x = 60;
		int y = 635;
		g.setColor(Color.WHITE);
		if(curTab == ItemType.FOOD){
			g.setColor(Handler.LAVENDER);
		}
		g.drawString(food, x, y);
		g.setColor(Color.WHITE);
		if(curTab == ItemType.WEAPON){
			g.setColor(Handler.LAVENDER);
		}
		g.drawString(weap, x + spacing + g.getFontMetrics().stringWidth(food), y);
		g.setColor(Color.WHITE);
		if(curTab == ItemType.ARMOR){
			g.setColor(Handler.LAVENDER);
		}
		g.drawString(arm, x + spacing * 2 + g.getFontMetrics().stringWidth(food) + g.getFontMetrics().stringWidth(weap), y);
		g.setColor(Color.WHITE);
		if(curTab == ItemType.JUNK){
			g.setColor(Handler.LAVENDER);
		}
		g.drawString(junk, x + spacing * 3 + g.getFontMetrics().stringWidth(food) + g.getFontMetrics().stringWidth(weap) + g.getFontMetrics().stringWidth(arm), y);
		g.setColor(Color.WHITE);
		if(curTab == ItemType.MISC){
			g.setColor(Handler.LAVENDER);
		}
		g.drawString(misc, 700 - 40 - g.getFontMetrics().stringWidth(misc), y);
		// CHARACTER CRAP
		int center = (64/2) - (Assets.ITEM_HEIGHT/2);
		for(Equip equip : player.getStats().getEquip().keySet()){
			BufferedImage image = null;
			boolean defaultImage = false;
			if(player.getStats().getEquip().get(equip) != null){
				image = player.getStats().getEquip().get(equip).getImage();
			}else{
				defaultImage = true;
			}
			int tempx = center;
			int tempy = center;
			switch(equip){
			case MAINHAND: tempx += inv.getEquipSlots()[0].x;
							tempy += inv.getEquipSlots()[0].y; break;
			case OFFHAND: tempx += inv.getEquipSlots()[1].x;
							tempy += inv.getEquipSlots()[1].y; break;
			case HEAD: tempx += inv.getEquipSlots()[2].x;
						tempy += inv.getEquipSlots()[2].y; break;
			case CHEST: tempx += inv.getEquipSlots()[3].x;
						tempy += inv.getEquipSlots()[3].y; break;
			case LEGS: tempx += inv.getEquipSlots()[4].x;
						tempy += inv.getEquipSlots()[4].y; break;
			case FEET: tempx += inv.getEquipSlots()[5].x;
						tempy += inv.getEquipSlots()[5].y; break;
			case SHOULDERS: tempx += inv.getEquipSlots()[6].x;
							tempy += inv.getEquipSlots()[6].y; break;
			case HANDS: tempx += inv.getEquipSlots()[7].x;
						tempy += inv.getEquipSlots()[7].y; break;
			case ACCESS1: tempx += inv.getEquipSlots()[8].x;
							tempy += inv.getEquipSlots()[8].y; break;
			case ACCESS2: tempx += inv.getEquipSlots()[9].x;
							tempy += inv.getEquipSlots()[9].y; break;
			}
			if(defaultImage){
				tempx -= center;
				tempy -= center;
				image = equip.getImage();
			}
			if(!defaultImage){
				g.drawImage(Assets.invEmptySlot, tempx - center, tempy - center, null);
			}
			if(!mouseInput.isDragged() || curItem != player.getStats().getEquip().get(equip)){
				g.drawImage(image, tempx, tempy, null);
			}
		}
		g.setColor(Handler.MANA_BLUE);
		g.setFont(new Font("Gentium Basic", Font.BOLD, 32));
		g.drawString(player.getName(), 950 - g.getFontMetrics().stringWidth(player.getName()), 110);
		g.drawString("Level " + player.getStats().getLevel(), 990, 110);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.BOLD, 18));
		String[] charInfo = new String[]{"Char: " + player.getTraits().getCharisma(), "Str: " + player.getTraits().getStrength(), "Res: " + player.getTraits().getResilience(), "Dex: " + player.getTraits().getDexterity(), "Int: " + player.getTraits().getIntelligence(), "Armor: " + player.getStats().getArmorRating(), "Weapon Damage: " + player.getStats().getWeapDmg()};
		int tempy = 620;
		int tempx = 750;
		int secondx = 800;
		for(int i = 0; i < charInfo.length; i++){
			if(i >= charInfo.length-2){
				g.drawString(charInfo[i], secondx, tempy + 40);
				secondx += 30 + g.getFontMetrics().stringWidth(charInfo[i]);
			}else{
				g.drawString(charInfo[i], tempx, tempy);
				tempx += 30 + g.getFontMetrics().stringWidth(charInfo[i]);
			}
		}
		// ITEMS
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.PLAIN, 18));
		g.drawString("Gold: " + (int)player.getGold(), 330, 105);
		g.setFont(new Font("Verdana", Font.PLAIN, 14));
		Item[][] items = inv.getItemSlots(curTab);
		if(items != null){
			for(int i = 0; i < items.length; i++){
				for(int k = 0; k < items[i].length; k++){
					if(items[i][k] != null){
						if(!mouseInput.isDragged() || curItem != items[i][k]){
						g.drawImage(items[i][k].getImage(), slots[i][k].x + (slots[i][k].width/2) - (Assets.ITEM_WIDTH/2), slots[i][k].y + (slots[i][k].height/2) - (Assets.ITEM_HEIGHT/2), null);
						 // FOR WHEN ITEM PIC SIZE IS 64x64 - g.drawImage(items[i][k].getImage(), slots[i][k].x, slots[i][k].y, null);
						g.drawString("" + inv.getItemMap().get(items[i][k]), slots[i][k].x + 40, slots[i][k].y + 50);
						}
					}
				}
			}
		}
		// ITEM DRAGGING
		if(mouseInput.isDragged()){
			if(curItem != null){
				g.drawImage(curItem.getImage(), Utils.clamp(mouseInput.getDragged().x + (curItem.getImage().getWidth()/2), 0, Display.WIDTH - curItem.getImage().getWidth()), Utils.clamp(mouseInput.getDragged().y + (curItem.getImage().getHeight()/2), 0, Display.HEIGHT - curItem.getImage().getHeight()), null);
			}
		}
		// Item Menu Crap
		if(displayItemMenu){
			g.setColor(Handler.ITEM_MENU);
			g.fillRect(curMenu.x, curMenu.y, curMenu.width, curMenu.height);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Verdana", Font.PLAIN, 16));
			if(equipItemMenu){
				g.drawString("UnEquip", curMenu.x + 10, curMenu.y + g.getFontMetrics().getHeight());
				g.drawString("Destroy", curMenu.x + 10, curMenu.y + 2 + 18 + g.getFontMetrics().getHeight());
			}else{
				switch(curItem.getItemType()){
				case FOOD: g.drawString("Use", curMenu.x + 10, curMenu.y + g.getFontMetrics().getHeight());
							g.drawString("Destroy", curMenu.x + 10, curMenu.y + 2 + 18 + g.getFontMetrics().getHeight()); break;
				case WEAPON: g.drawString("Equip", curMenu.x + 10, curMenu.y + g.getFontMetrics().getHeight());
								g.drawString("Destroy", curMenu.x + 10, curMenu.y + 2 + 18 + g.getFontMetrics().getHeight());break;
				case ARMOR: g.drawString("Equip", curMenu.x + 10, curMenu.y + g.getFontMetrics().getHeight());
							g.drawString("Destroy", curMenu.x + 10, curMenu.y + 2 + 18 + g.getFontMetrics().getHeight()); break;
				case JUNK: g.drawString("Destroy", curMenu.x + 10, curMenu.y + g.getFontMetrics().getHeight()); break;
				case MISC: String text = "Use"; if(curItem.getEquip() != null){text = "Equip";}
					g.drawString(text, curMenu.x + 10, curMenu.y + g.getFontMetrics().getHeight());
					g.drawString("Destroy", curMenu.x + 10, curMenu.y + 2 + 18 + g.getFontMetrics().getHeight()); break;
				}
			}
			g.setColor(Handler.SELECTION_HIGHLIGHT);
			g.fillRect(selection.x, selection.y, selection.width, selection.height);
		} 
		// Item Info
		else if(displayItemInfo){
			g.setColor(Handler.ITEM_MENU);
			int infox = curPoint.x + 32;
			int infoy = curPoint.y + 32;
			g.drawImage(Assets.invItemInfo, infox, infoy, null);
			ArrayList<String> info = new ArrayList<>();
			for(String text : curItem.getInfo()){
				for(String display : Utils.wrapText(text, Assets.invItemInfo.getWidth(), g.getFontMetrics())){
					info.add(display);
				}
			}
			itemScroll.addStrings(info, new Font("Calibri Bold", Font.PLAIN, 16), g.getFontMetrics(), Color.WHITE);
			itemScroll.render(g);
		}
		// Display Item Name
		if(itemName != null && !displayItemMenu){
			g.setColor(Handler.SELECTION_LOWLIGHT);
			g.fillRect(mouseInput.getMouseX() - 3, mouseInput.getMouseY() - 17, g.getFontMetrics().stringWidth(itemName) - (g.getFontMetrics().stringWidth(itemName)/5) , g.getFontMetrics().getHeight() + 2);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Calibri Bold", Font.BOLD, 16));
			g.drawString(itemName, mouseInput.getMouseX(), mouseInput.getMouseY());
		}
		g.setColor(Color.RED);
		g.drawRect(playerPortrait.x, playerPortrait.y, playerPortrait.width, playerPortrait.height);
		g.drawRect(40, 125, 645, 465);
		g.drawRect(715, 125, 525, 455);
		// Render Menu Tabs
		g.setFont(new Font("Verdana", Font.BOLD, 32));
		String inventory = "Inventory";
		String questlog = "Quest Log";
		String skillbook = "Skillbook";
		g.setColor(Handler.BLUE_VIOLET);
		g.drawString(inventory, 125, 50);
		g.setColor(Color.WHITE);
		g.drawString(skillbook, 640 - (g.getFontMetrics().stringWidth(skillbook) / 2), 50);
		g.setColor(Color.WHITE);
		g.drawString(questlog, 950, 50);
	}
	
	public void tick(){
		player = handler.getPlayer();
		inv = player.getInventory();
		drag();
		if(!mouseInput.getLeftClicks().isEmpty()){
			leftClick();
		}
		if(!mouseInput.getRightClicks().isEmpty()){
			rightClick();
		}
		hover();
		HUDManager.MENUTIMER += System.currentTimeMillis() - HUDManager.MENULAST;
		HUDManager.MENULAST = System.currentTimeMillis();
		if(HUDManager.MENUTIMER > HUDManager.MENUWAIT){
			if(keyInput.esc || keyInput.I || keyInput.L || keyInput.K){
				displayItemInfo = false;
				itemName = null;
				displayItemMenu = false;
				HUDManager.MENUTIMER = 0;
				if(keyInput.I){
					handler.setState(States.World);
					handler.getEntityManager().setPaused(false);
				}else if(keyInput.L){
					handler.setState(States.MenuQuest);
				}else if(keyInput.esc){
					handler.setState(States.MenuSettings);
				}else if(keyInput.K){
					handler.setState(States.MenuSkills);
				}
			}
			if(keyInput.W){ // UP
				//TODO: make it select the menu tabs
					
				HUDManager.MENUTIMER = 0;
			}else if(keyInput.S){ // DOWN
				//TODO: make it select the menu tabs
					
				HUDManager.MENUTIMER = 0;
			}else if(keyInput.D){
				if(curTab != tabArray[tabArray.length-1]){
					for(int i = 0; i < tabArray.length; i++){
						if(curTab == tabArray[i]){
							curTab = tabArray[i+1];
							break;
						}
					}
				}else{
					curTab = tabArray[0];
				}
				HUDManager.MENUTIMER = 0;
			}else if(keyInput.A){
				if(curTab != tabArray[0]){
					for(int i = 0; i < tabArray.length; i++){
						if(curTab == tabArray[i]){
							curTab = tabArray[i-1];
							break;
						}
					}
				}else{
					curTab = tabArray[tabArray.length -1];
				}
				HUDManager.MENUTIMER = 0;
			}
		}
	}
	
	public void invItemMenu(Point item, Point mouse){
		displayItemMenu = true;
		if(!equipItemMenu){
			curItem = inv.getItemSlots(curTab)[item.x][item.y];
		}else{
			curItem = inv.getItemEquip(item.x);
		}
		curMenu = new Rectangle(mouse.x, mouse.y, 80, 50);
		if(curItem.getItemType() == ItemType.JUNK || curItem.getItemType() == ItemType.MISC){
			curMenu.height -= 20;
		}
		itemMenu = new Rectangle[]{new Rectangle(mouse.x, mouse.y, 80, 25), new Rectangle(mouse.x, mouse.y + 25, 80, 25)};	
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(!mouseInput.isDragged()){
			if(displayItemMenu){
				if(clicks.element() != null){
					outer: {
						while(clicks.element() != null){
							Point head = clicks.poll().getObject();
							double x = head.getX();
							double y = head.getY();
							if(new Rectangle((int)x, (int)y, 1, 1).intersects(selection)){
								if(selection == itemMenu[0]){
									if(equipItemMenu){
										player.getStats().unEquip(curItem.getEquip());
									}else{
										if(curItem.getItemType() == ItemType.JUNK){
											inv.removeItem(curItem);
										}else{
											inv.useItem(curItem);
										}
									}
									break outer;
								}else if(selection == itemMenu[1]){
									inv.removeItem(curItem);
									break outer;
								}
							}
						}
					}
				}
				displayItemMenu = false;
				displayItemInfo = false;
				isScroll = false;
			}
			else{
				if(isScroll){
					if(clicks.element() != null){
						outer: {
							while(clicks.element() != null){
								Point head = clicks.poll().getObject();
								double x = head.getX();
								double y = head.getY();
								if(displayItemInfo){
									if(new Rectangle((int)x, (int)y, 1, 1).intersects(itemScroll.getButtonUp())){
										itemScroll.move(5);
										System.out.println("UP");
										break outer;
									}else if(new Rectangle((int)x, (int)y, 1, 1).intersects(itemScroll.getButtonDown())){
										itemScroll.move(-5);
										System.out.println("DOWN");
										break outer;
									}
								}
							}
							displayItemInfo = false;
							isScroll = false;
						}
					}
				}else if(mouseInput.getDoubleClick()){
					if(clicks.element() != null){
						outer: {
							while(clicks.element() != null){
								Point head = clicks.poll().getObject();
								double x = head.getX();
								double y = head.getY();
								Item[][] items = inv.getItemSlots(curTab);
								if(items != null){
									for(int i = 0; i < items.length; i++){
										for(int k = 0; k < items[i].length; k++){
											if(items[i][k] != null){
												if(inv.getSlots()[i][k].intersects(x, y, 1, 1)){
													displayItemInfo = true;
													curItem = items[i][k];
													curPoint = new Point(inv.getSlots()[i][k].x, inv.getSlots()[i][k].y);
													itemScroll = new ScrollPanel(new Rectangle(curPoint.x + 32, curPoint.y + 50, Assets.invItemInfo.getWidth(), Assets.invItemInfo.getHeight()));
													isScroll = true;
													break outer;
												}
											}
										}
									}
									for(int i = 0; i < inv.getEquipSlots().length; i++){
										if(inv.getEquipSlots()[i].intersects(mouseInput.getMouseBounds())){
											if(inv.getItemEquip(i) != null){
												displayItemInfo = true;
												curItem = inv.getItemEquip(i);
												curPoint = new Point(inv.getEquipSlots()[i].x, inv.getEquipSlots()[i].y);
												itemScroll = new ScrollPanel(new Rectangle(curPoint.x + 32, curPoint.y + 50, Assets.invItemInfo.getWidth(), Assets.invItemInfo.getHeight()));
												isScroll = true;
												break outer;
											}
										}
									}
								}
							}
							displayItemInfo = false;
							isScroll = false;
						}
					}
				}else{
					if(clicks.element() != null){
						outer: {
							while(clicks.element() != null){
								Point head = clicks.poll().getObject();
								double x = head.getX();
								double y = head.getY();
								Item[][] items = inv.getItemSlots(curTab);
								if(items != null){
									for(int i = 0; i < items.length; i++){
										for(int k = 0; k < items[i].length; k++){
											if(items[i][k] != null){
												if(inv.getSlots()[i][k].intersects(x, y, 1, 1)){
													curItem = items[i][k];
													break outer;
												}
											}
										}
									}
									for(int i = 0; i < inv.getEquipSlots().length; i++){
										if(inv.getEquipSlots()[i].intersects(mouseInput.getMouseBounds())){
											if(inv.getItemEquip(i) != null){
												curItem = inv.getItemEquip(i);
												break outer;
											}
										}
									}
								}
							}
						}
					}
				}
				displayItemMenu = false;
			}
		}
	}
	
	public void rightClick(){
		LinkedQueue clicks = mouseInput.getRightClicks();
		if(clicks.element() != null){
			outer: {
				while(clicks.element() != null){
					Point head = clicks.poll().getObject();
					double x = head.getX();
					double y = head.getY();
					Item[][] items = inv.getItemSlots(curTab);
					if(items != null){
						for(int i = 0; i < items.length; i++){
							for(int k = 0; k < items[i].length; k++){
								if(items[i][k] != null){
									if(inv.getSlots()[i][k].intersects(x, y, 1, 1)){
										invItemMenu(new Point(i, k), new Point((int)x, (int)y));
										equipItemMenu = false;
										break outer;
									}
								}
							}
						}
					}
					for(int i = 0; i < inv.getEquipSlots().length; i++){
						if(inv.getEquipSlots()[i].intersects(mouseInput.getMouseBounds())){
							if(inv.getItemEquip(i) != null){
								equipItemMenu = true;
								invItemMenu(new Point(i, i), new Point((int)x, (int)y));
								break outer;
							}
						}
					}
					equipItemMenu = false;
					displayItemMenu = false;
				}
			}
		}
	}
	
	public void drag(){
		if(mouseInput.isDragged()){
			isDragged = true;
			Rectangle startRect =  new Rectangle(mouseInput.getStartDrag().x, mouseInput.getStartDrag().y, 1, 1);
			Item[][] items = inv.getItemSlots(curTab);
			outer: {
				for(int i = 0; i < items.length; i++){
					for(int k = 0; k < items[i].length; k++){
						if(items[i][k] != null){
							if(inv.getSlots()[i][k].intersects(startRect)){
								curItem = items[i][k];
								break outer;
							}
						}
					}
				}
				for(int i = 0; i < inv.getEquipSlots().length; i++){
					if(inv.getEquipSlots()[i].intersects(startRect)){
						if(inv.getItemEquip(i) != null){
							curItem = inv.getItemEquip(i);
							break outer;
						}
					}
				}
			}
			displayItemMenu = false;
			displayItemInfo = false;
			equipItemMenu = false;
			isScroll = false;
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
				if(startRect.intersects(new Rectangle(715, 125, 525, 455))){ //intersected the Equip area
					if(endRect.intersects(new Rectangle(40, 125, 645, 465))){ //intersects the Inventory area
						player.getStats().unEquip(curItem.getEquip());
					}
				}else if(startRect.intersects(new Rectangle(40, 125, 645, 465))){ //intersected the Inv area
					if(endRect.intersects(new Rectangle(715, 125, 525, 455))){ //intersects the Equip area
						if(curItem.getEquip() != null){
							inv.useItem(curItem);
						}
					}else if(endRect.intersects(playerPortrait)){
						if(curItem.getEquip() != null){
							inv.useItem(curItem);
						}
					}
				}
			}
			isDragged = false;
		}
	}
	
	public void hover(){
		if(!mouseInput.isDragged()){
			if(displayItemMenu){
				for(int i = 0; i < itemMenu.length; i++){
					if(mouseInput.getMouseBounds().intersects(itemMenu[i])){
						selection = itemMenu[i];
						break;
					}
				}
			}else{
				Item[][] items = inv.getItemSlots(curTab);
				outer: {
					for(int i = 0; i < items.length; i++){
						for(int k = 0; k < items[i].length; k++){
							if(items[i][k] != null){
								if(inv.getSlots()[i][k].intersects(mouseInput.getMouseBounds())){
									itemName = items[i][k].getName();
									break outer;
								}
							}
						}
					}
					for(int i = 0; i < inv.getEquipSlots().length; i++){
						if(inv.getEquipSlots()[i].intersects(mouseInput.getMouseBounds())){
							if(inv.getItemEquip(i) != null){
								itemName = inv.getItemEquip(i).getName();
								break outer;
							}
						}
					}
				itemName = null;
				}
			}
		}else{
			itemName = null;
		}
	}
}
