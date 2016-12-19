package virassan.gfx.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import virassan.entities.Entity;
import virassan.entities.creatures.npcs.Merchant;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.creatures.player.Inventory;
import virassan.entities.creatures.player.Player;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.gfx.Assets;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.items.Equip;
import virassan.items.Item;
import virassan.items.ItemManager;
import virassan.items.ItemType;
import virassan.main.Display;
import virassan.main.Game;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.quests.QuestTracker;
import virassan.utils.ScrollPanel;
import virassan.utils.Utils;

/**
 * Heads Up Display Manager - HUD is supposed to manage display things that the Player CANNOT change
 * @author Virassan
 *
 */
public class HUDManager {
	
	private CopyOnWriteArrayList<SkillText> skillList = new CopyOnWriteArrayList<>();
	
	private ItemManager itemManager;
	
	private ScrollPanel itemScroll, questScroll, helpScroll;
	private boolean isScroll;
	
	private ArrayList<QuestTracker> quests, npcQuests;
	private int curQuestIndex;
	private Rectangle[] questMenu;
	private Rectangle questMenuSel;
	
	private String skillName;
	
	private Rectangle foodItemMenu, weapItemMenu, armorItemMenu, junkItemMenu, miscItemMenu;
	
	private float waitTime = 300;
	private long lastTime;
	private long timer = 0;
	private Handler handler;
	private Player player;
	private boolean isInv, isNPC, isCharacter, isQuestLog, isHelpMenu, isSkillbook, isMerchant;
	private boolean displayItemMenu, equipItemMenu, displayItemInfo, isDragged;
	private boolean isQuestMenu;
	private Point curPoint;
	private Item curItem;
	private String itemName;
	private Rectangle curMenu, selection;
	private Rectangle[] itemMenu;
	private KeyInput keyInput;
	private MouseInput mouseInput;
	private float dmgScaleIncr = 0.01f;
	
	private long menuTimer, menuLast, menuWait = 200;
	
	// Merchant Things
	private int mercCurX = 210, mercCurY;
	private int mercMinY = 195, mercMaxY = 625;
	private final int mercSelHeight = 25;
	private int[] mercY;
	
	//Inventory Things
	private Rectangle playerPortrait = new Rectangle(815, 145, 320, 340);
	private ItemType curTab;
	private Inventory inv;
	private final ItemType tabArray[] = {ItemType.FOOD, ItemType.WEAPON, ItemType.ARMOR, ItemType.JUNK, ItemType.MISC};
	
	public HUDManager(Handler handler){
		this.handler = handler;
		itemManager = new ItemManager(handler);
		keyInput = handler.getKeyInput();
		mouseInput = handler.getMouseInput();
		isInv = false;
		isNPC = false;
		curTab = ItemType.FOOD;
		mercY = new int[14];
		for(int i = 0; i < mercY.length; i++){
			if(i == 0){
				mercY[i] = mercMinY;
			}else{
				mercY[i] = mercY[i-1] + mercSelHeight;
			}
		}
		foodItemMenu = new Rectangle(0, 0, 80, 50); 
		weapItemMenu = foodItemMenu; 
		armorItemMenu = weapItemMenu;
		junkItemMenu = new Rectangle(0, 0, 80, 30);
		miscItemMenu = junkItemMenu;
	}

	
	public void tick(){
		player = handler.getPlayer();
		inv = player.getInventory();
		boolean pause = handler.getEntityManager().getPaused();
		
		// Mouse Input
		drag();
		if(!mouseInput.getLeftClicks().isEmpty()){
			leftClick();
		}
		if(!mouseInput.getRightClicks().isEmpty()){
			rightClick();
		}
		hover();
		
		menuTimer += System.currentTimeMillis() - menuLast;
		menuLast = System.currentTimeMillis();
		
		// If NOT paused
		if(!pause){
			itemManager.tick();
			if(menuTimer > menuWait){
				// Inventory
				if(keyInput.I){
					isInv = true;
					handler.getEntityManager().setPaused(true);
					menuTimer = 0;
				} // Character Sheet
				else if(keyInput.C){
					//TODO: possibly add a detailed Character Info Page with stuff like "Slimes killed: 20" etc
					isCharacter = true;
					menuTimer = 0;
					handler.getEntityManager().setPaused(true);
				} // Quest Log
				else if(keyInput.L){
					isQuestLog = true;
					menuTimer = 0;
					handler.getEntityManager().setPaused(true);
				} // Help Menu
				else if(keyInput.esc){
					isHelpMenu = true;
					menuTimer = 0;
					handler.getEntityManager().setPaused(true);
				} // Skillbook
				else if(keyInput.K){
					menuTimer = 0;
					isSkillbook = true;
					handler.getEntityManager().setPaused(true);
				} // NPC Interaction/Dialog
				else if(keyInput.F){
					outer: {
						for(Entity e : handler.getWorld().getMap().getEntityManager().getEntities()){
							if(e instanceof NPC){
								NPCInteract((NPC)e);
								break outer;
							}else if(e instanceof Merchant){
								merchantInteract((Merchant)e);
								break outer;
							}
						}
						menuTimer = 0;
					}
				}else if(keyInput.space){
					menuTimer = 0;
					handler.getEntityManager().setPaused(true);
				}else if(keyInput.one || keyInput.two || keyInput.three || keyInput.four || keyInput.five){
					int index = 0;
					if(keyInput.two){
						index = 1;
					}else if(keyInput.three){
						index = 2;
					}else if(keyInput.four){
						index = 3;
					}else if(keyInput.five){
						index = 4;
					}
					if(index < player.getSkills().size()){
						SkillTracker s = player.getSkillBar()[index];
						float curAmt = 0;
						switch(s.getSkillCostType()){
						case "stam": curAmt = player.getStats().getStamina(); break;
						case "mana": curAmt = player.getStats().getMana(); break;
						case "heal": curAmt = player.getStats().getHealth(); break;
						}
						if((curAmt - s.getCost()) >= 0){
							player.setSkillActive(true);
							player.setActiveSkill(s);
						}else{
							skillList.add(new SkillText(handler, "Not enough " + s.getSkillCostType(), Handler.LAVENDER, (int)player.getX(), (int)player.getY() - player.getHeight()-10));
						}
					}
					menuTimer = 0;
				}else if(keyInput.Z){
					Utils.saveGame(handler);
				}
			}
		} // If Paused
		else{
			if(isCharacter){
				tickCharacter();
			}else if(isSkillbook){
				tickSkillbook();
			}else if(isInv){
				inv.tick();
				tickInventory();
			}else if(isHelpMenu){
				tickHelpMenu();
			}else if(isQuestLog){
				tickQuestLog();
			}else if(isNPC){
				tickNPC();
			}else if(isMerchant){
				tickMerchant();
			}else if(keyInput.space){
				if(menuTimer > menuWait){
					menuTimer = 0;
					handler.getEntityManager().setPaused(false);
				}
			}
		}
		player.getStats().tick();
		for(SkillText text : skillList){
			if(text.isLive()){
				text.tick();
			}else{
				skillList.remove(text);
			}
		}
	}
	
	public void render(Graphics g){
		if(!handler.getEntityManager().getPaused()){
			itemManager.render(g);
			for(SkillText text : skillList){
				if(text.isLive()){
					text.render(g);
				}else{
					skillList.remove(text);
				}
			}
		}
		renderSkillBar(g);
		renderHealthBar(g);
		renderStaminaBar(g);
		renderManaBar(g);
		// Bouncy Text
		handler.getPlayer().getStats().render(g);
		// Quest Log
		if(isQuestLog){
			renderQuestLog(g);
			renderMenuTabs(g);
		} // Help Menu
		else if(isHelpMenu){
			renderHelpMenu(g);
		} // Inventory
		else if(isInv){
			renderInv(g);
			renderMenuTabs(g);
		} // Skillbook
		else if(isSkillbook){
			renderSkillbook(g);
			renderMenuTabs(g);
		} // NPC
		else if(isNPC){
			renderNPC(g);
		}else if(isMerchant){
			renderMerchant(g);
		} // Mouse Stuff
		g.setColor(Color.BLUE);
		g.fillRect((int)handler.getMouseInput().getMouseBounds().getX(), (int)handler.getMouseInput().getMouseBounds().getY(), (int)handler.getMouseInput().getMouseBounds().getWidth()+1, (int)handler.getMouseInput().getMouseBounds().getHeight()+1);
		if(skillName != null){
			g.setColor(Color.BLACK);
			g.setFont(new Font("Verdana", Font.BOLD, 18));
			g.drawString(skillName, handler.getMouseInput().getMouseX(), handler.getMouseInput().getMouseY());
		}
		if(handler.getPlayer().getSkillActive()){
			g.setColor(Color.MAGENTA);
			g.fillRect(handler.getMouseInput().getMouseX(), handler.getMouseInput().getMouseY(), 10, 10);
		}
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		g.setColor(Color.WHITE);
		g.drawString("Ticks: " + Game.TICK, Display.WIDTH - 70, 20);
		g.drawString(handler.getPlayer().getX() + " " + handler.getPlayer().getY(), Display.WIDTH - 90, 35);
		g.drawString("" + handler.getEntityManager().getPaused(), Display.WIDTH - 50, 50);
	}
	
	public void renderMerchant(Graphics g){
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
	
	public void renderMenuTabs(Graphics g){
		g.setFont(new Font("Verdana", Font.BOLD, 32));
		String inventory = "Inventory";
		String questlog = "Quest Log";
		String skillbook = "Skillbook";
		g.setColor(Color.WHITE);
		if(isInv){
			g.setColor(Handler.BLUE_VIOLET);
		}
		g.drawString(inventory, 125, 50);
		g.setColor(Color.WHITE);
		if(isSkillbook){
			g.setColor(Handler.BLUE_VIOLET);
		}
		g.drawString(skillbook, 640 - (g.getFontMetrics().stringWidth(skillbook) / 2), 50);
		g.setColor(Color.WHITE);
		if(isQuestLog){
			g.setColor(Handler.BLUE_VIOLET);
		}
		g.drawString(questlog, 950, 50);
	}
	
	public void renderSkillbook(Graphics g){
		//TODO: actually render skillbook stuff
		g.drawImage(Assets.skillMenu, 0, 0, null);
		float y = 138;
		float x = 768;
		for(SkillTracker skill : player.getSkillBar()){
			if(skill != null){
				g.drawImage(skill.getSkillType().getIcon(), (int)x, (int)y, null);
				g.drawImage(Assets.skill_border,(int)x, (int)y, null);
				x += 74F;
			}
		}
	}
	
	public void renderInv(Graphics g){
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
		} // Item Info
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
	}

	public void renderNPC(Graphics g){
		int x = (int)((Display.WIDTH % 1040) / 2);
		int y = Display.HEIGHT - 200;
		g.drawImage(Assets.dialogBox, x, y, null);
		g.setColor(Color.PINK);
		g.setFont(new Font("Gentium Basic", Font.PLAIN, 32));
		if(!isQuestMenu){
			g.drawString(player.getCurrentNPC().getCurDialog(), x + 15, y + 35);
			g.drawImage(Assets.button_a, x + 995, y + 105, null);
		}else{
			for(QuestTracker quest : npcQuests){
				g.setColor(Color.PINK);
				g.drawString(quest.getQuest().getName(), questMenu[npcQuests.indexOf(quest)].x + 15, questMenu[npcQuests.indexOf(quest)].y + g.getFontMetrics().getHeight());
				g.setColor(Handler.SELECTION_HIGHLIGHT);
				g.fillRect(questMenuSel.x, questMenuSel.y, questMenuSel.width, questMenuSel.height);
			}
		}
	}
	
	public void renderQuestLog(Graphics g){
		g.drawImage(Assets.questMenu, 0, 0, null);
		int x = 50;
		int y = 150;
		Font questFont = new Font("Verdana", Font.PLAIN, 18);
		questScroll = new ScrollPanel(new Rectangle(x, y, 505, 600));
		quests = player.getQuestLog().getActive();
		ArrayList<String> questNames = new ArrayList<>();
		for(QuestTracker quest : quests){
			questNames.add(quest.getQuest().getName());
		}
		if(!quests.isEmpty()){
			questScroll.addStrings(questNames, questFont, g.getFontMetrics(questFont), Color.WHITE);
			questScroll.render(g);
			g.setColor(Handler.SELECTION_HIGHLIGHT);
			g.fillRect(x - 5, questScroll.getDrawPoints().get(curQuestIndex).y - 17, 491, 21);
			//TODO: Finish Quest Log
			g.setColor(Color.WHITE);
			g.setFont(questFont);
			x = 600;
			y = 150;
			g.drawString(quests.get(curQuestIndex).getQuest().getDescription(), x, y);
			for(Object obj : quests.get(curQuestIndex).getQuest().getHashMap().keySet()){
				y += 20;
				g.drawString(obj + ": " + quests.get(curQuestIndex).getReqAmt(obj) + "/" + quests.get(curQuestIndex).getQuest().getReqAmt(obj), x + 150, y);
			}
		}
	}
	
	public void renderHelpMenu(Graphics g){
		g.drawImage(Assets.menu, 0, 0, null);
		//TODO : Finish Help Menu render
		g.setColor(Handler.BLUE_VIOLET);
		g.setFont(new Font("Verdana", Font.BOLD, 32));
		g.drawString("Help Menu", 550, 40);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		//TODO: render words
		ArrayList<String> words = new ArrayList<>();
		words.add("You are a Slime, and you're killing other Slimes. The main difference in appearance is the Enemies will have a name printed above them, whereas you do Not.");
		words.add("The NPCs are the Stick figures.");
		words.add("Captains are stronger than Slimes, and Bosses are even stronger. The stronger they are, the better loot they drop");
		words.add("CONTROLS - ");
		words.add("SPACE: Pauses/Unpauses the Game");
		words.add("W: Moves the Character Up");
		words.add("A: Moves the Character Left");
		words.add("S: Moves the Character Down");
		words.add("D: Moves the Character Right");
		words.add("F: Interacts with nearby NPC");
		words.add("K: Opens/Closes the Skillbook");
		words.add("L: Opens/Closes the Quest Log");
		words.add("I: Opens/Closes the Inventory/Character Page");
		words.add("Escape: Opens/Closes this Help Menu");
		words.add("SKILLS - ");
		words.add("Clicking the Icon of a Skill or Pressing its number hot-key will Activate it, shown by a pink box under your mouse.");
		words.add("Each skill has a maximum range and uses either Stamina or Mana");
		words.add("If a skill's icon is grey'd out, it means the skill is still in cooldown");
		words.add("You'll be alerted if the target is out of range - or if you lack the cost of stamina/mana - or if the skill isn't ready yet");
		words.add("NPCs - ");
		words.add("E: Buys or Sells and Item when Talking to a Merchant");
		words.add("A: Furthers the dialog when Talking to an NPC");
		words.add("If you have more than one active Quest with an NPC, you must select which Quest you'd like to talk about - use the Mouse to do that");
		words.add("The Mouse hasn't been implemented into the Merchant page. Use W and S to scroll through items");
		words.add("INVENTORY - ");
		words.add("Right Click: Opens Item Menu for Item Selected");
		//TODO: change double click back to quick use of item and add Item Info to the Item Menu
		words.add("Double Click: Opens Item Information for Item Selected");
		words.add("Drag an Equipable Item to the Player Portrait to auto-equip it");
		words.add("Drag an Item to the Trashcan Icon to destory it Forever - COMING SOON");
		words.add("Use A and D to switch between Inventory Tab");
		words.add("Use TAB to switch to the next Menu Tab");
		words.add("QUEST LOG - ");
		words.add("Click on a Quest in the list to the left to have its information and your progress displayed on the right.");
		words.add("SKILLBOOK - ");
		words.add("Double Click: Opens Skill Information for the Skill Selected - COMING SOON");
		words.add("Drag a Skill to the skillbar to put it in that slot - COMING SOON");
		ArrayList<String> temp = new ArrayList<>();
		for(String text : words){
			for(String display : Utils.wrapText(text, Display.WIDTH - 150, g.getFontMetrics())){
				temp.add(display);
			}
		}
		helpScroll = new ScrollPanel(new Rectangle(60, 100, Display.WIDTH - 100, 600));
		helpScroll.addStrings(temp, g.getFont(), g.getFontMetrics(), g.getColor());
		helpScroll.render(g);
	}
	
	public void renderSkillBar(Graphics g){
		g.setColor(Color.GRAY);
		int rectx = (Display.WIDTH/2) - 80;
		int recty = 10;
		g.fillRect((Display.WIDTH/2) - 80, 10, 320, 64);
		int tempx = (Display.WIDTH/2) - 80;
		int numx = (Display.WIDTH/2) - 60;
		for(SkillTracker s : handler.getPlayer().getSkillBar()){
			if(s != null){
				s.drawIcon(g, tempx, recty);
				tempx += 64;
			}
		}
		g.setColor(Color.BLACK);
		for(int i = 0; i < 5; i++){
			g.drawRect(rectx, recty, 64, 64);
			rectx += 64;
		}
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		for(int i = 0; i < 5; i++){
			g.drawString("" + (i + 1), numx, recty + 80);
			numx += 64;
		}
	}
	
	public void renderManaBar(Graphics g){
		g.setColor(Handler.MANA_BLUE);
		g.fillRect(10, 60, (int)(150 * (handler.getPlayer().getStats().getMana() / handler.getPlayer().getStats().getMaxMana())), 15);
		g.setColor(Color.BLACK);
		g.drawRect(9, 59, 30, 16);
		g.drawRect(39, 59, 30, 16);
		g.drawRect(69, 59, 30, 16);
		g.drawRect(99, 59, 30, 16);
		g.drawRect(129, 59, 30, 16);
		Color bar = new Color(3, 233, 249);
		g.setColor(bar);
		g.fillRect((int)(150 * (handler.getPlayer().getStats().getMana() / handler.getPlayer().getStats().getMaxMana())) + 7, 60, 3, 15);
		g.setColor(Handler.BARELY_GRAY);
		g.setFont(new Font("Consolas", Font.BOLD, 14));
		String manaDisplay = (int)handler.getPlayer().getStats().getMana() + " / " + (int)handler.getPlayer().getStats().getMaxMana();
		g.drawString(manaDisplay, 115 - g.getFontMetrics().stringWidth(manaDisplay), 72);
	}
	
	public void renderStaminaBar(Graphics g){
		g.setColor(Handler.STAMINA_GREEN);
		g.fillRect(10, 40, (int)(150 * (handler.getPlayer().getStats().getStamina() / handler.getPlayer().getStats().getMaxStam())), 15);
		g.setColor(Color.BLACK);
		g.drawRect(9, 39, 30, 16);
		g.drawRect(39, 39, 30, 16);
		g.drawRect(69, 39, 30, 16);
		g.drawRect(99, 39, 30, 16);
		g.drawRect(129, 39, 30, 16);
		Color bar = new Color(6, 248, 80);
		g.setColor(bar);
		g.fillRect((int)(150 * (handler.getPlayer().getStats().getStamina() / handler.getPlayer().getStats().getMaxStam())) + 7, 40, 3, 15);
		g.setColor(Handler.BARELY_GRAY);
		g.setFont(new Font("Consolas", Font.BOLD, 14));
		String stamDisplay = (int)handler.getPlayer().getStats().getStamina() + " / " + (int)handler.getPlayer().getStats().getMaxStam();
		g.drawString(stamDisplay, 122 - g.getFontMetrics().stringWidth(stamDisplay), 52);
	}
	
	public void renderHealthBar(Graphics g){
		//Red Removed Health bar
		if(!handler.getPlayer().getStats().isDamaged()){
			timer += System.currentTimeMillis() - lastTime;
			if(handler.getPlayer().getStats().getDmgScale() > handler.getPlayer().getStats().getHealthScale()){
				if(timer > waitTime){
					player.getStats().setDmgScale(handler.getPlayer().getStats().getDmgScale() - dmgScaleIncr);
					if(Math.abs((double)handler.getPlayer().getStats().getDmgScale() - (double)handler.getPlayer().getStats().getHealthScale()) / handler.getPlayer().getStats().getHealthScale() < 0.3){
						waitTime -= waitTime*0.02f;
						dmgScaleIncr += 0.0004f;
					}else{
						waitTime -= waitTime*0.5f;
					}
					//waitTime -= waitTime*0.5f;
					if(waitTime <= 0){
						waitTime = 300;
					}
					lastTime = System.currentTimeMillis();
				}	
			}
		}else{
			waitTime = 300;
		}
		g.setColor(Handler.HEALTH_ORANGE);
		g.fillRect(10, 10, (int)(150 * handler.getPlayer().getStats().getDmgScale()), 25);
		//Health Bar
		g.setColor(Handler.HEALTH_RED);
		g.fillRect(10, 10, (int)(150 * handler.getPlayer().getStats().getHealthScale()), 25);
		g.setColor(Color.BLACK);
		g.drawRect(9, 9, 30, 26);
		g.drawRect(39, 9, 30, 26);
		g.drawRect(69, 9, 30, 26);
		g.drawRect(99, 9, 30, 26);
		g.drawRect(129, 9, 30, 26);
		Color bar = new Color(251, 196, 5);
		g.setColor(bar);
		g.fillRect((int)(150 * handler.getPlayer().getStats().getHealthScale()) + 7, 10, 3, 25);
		g.setColor(new Color(235, 235, 235));
		g.setFont(new Font("Consolas", Font.BOLD, 16));
		String healthDisplay = (int)handler.getPlayer().getStats().getHealth() + " / " + (int)handler.getPlayer().getStats().getMaxHealth();
		g.drawString(healthDisplay, 125 - g.getFontMetrics().stringWidth(healthDisplay), 27);
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(handler.getEntityManager().getPaused()){
			//TODO : add all menu actions for left click
			if(isInv){
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
			}else if(isQuestLog){
				if(clicks.element() != null){
					outer: {
						while(clicks.element() != null){
							Point head = clicks.poll().getObject();
							double x = head.getX();
							double y = head.getY();
							if(new Rectangle((int)x, (int)y, 1, 1).intersects(questScroll.getButtonUp())){
								questScroll.move(5);
								System.out.println("UP");
								break outer;
							}else if(new Rectangle((int)x, (int)y, 1, 1).intersects(questScroll.getButtonDown())){
								questScroll.move(-5);
								System.out.println("DOWN");
								break outer;
							}else{
								for(Object obj : questScroll.getDrawObjects()){
									if(obj instanceof String){
										if(mouseInput.getMouseBounds().intersects(new Rectangle(questScroll.getDrawPoints().get(questScroll.getDrawObjects().indexOf(obj)).x, questScroll.getDrawPoints().get(questScroll.getDrawObjects().indexOf(obj)).y - questScroll.getFontHeight() + 2, 500, 22))){
											if(questScroll.getDrawPoints().get(questScroll.getDrawObjects().indexOf(obj)).y >= 150 && questScroll.getDrawPoints().get(questScroll.getDrawObjects().indexOf(obj)).y <= (150+600)){
												curQuestIndex = questScroll.getDrawObjects().indexOf(obj);
												break outer;
											}
										}
									}
								}
							}
						}
					}
				}
			}else if(isNPC){
				if(isQuestMenu){
				//TODO: use the mouse for the questMenu
					if(clicks.element() != null){
						outer :{
							while(clicks.element() != null){
								Point head = clicks.poll().getObject();
								double x = head.getX();
								double y = head.getY();
								//TODO: do a for loop of the size of npcQuests and figure out its Rectangle
								for(int i = 0; i < questMenu.length; i++){
									if(new Rectangle((int)x, (int)y, 1, 1).intersects(questMenu[i])){
										player.getCurrentNPC().setDialog(npcQuests.get(i));
										isQuestMenu = false;
										break outer;
									}
								}
							}
						}
					}
				}
			}else if(isHelpMenu){
				if(clicks.element() != null){
					outer: {
						while(clicks.element() != null){
							Point head = clicks.poll().getObject();
							double x = head.getX();
							double y = head.getY();
							//TODO: not moving properly
							if(new Rectangle((int)x, (int)y, 1, 1).intersects(helpScroll.getButtonUp())){
								helpScroll.move(2);
								System.out.println("UP");
								break outer;
							}else if(new Rectangle((int)x, (int)y, 1, 1).intersects(helpScroll.getButtonDown())){
								helpScroll.move(-2);
								System.out.println("DOWN");
								break outer;
							}
						}
					}
				}
			}
		}else{
			if(player.getSkillActive()){
				if(clicks.element() != null){
					outer :{
						while(clicks.element() != null){
							Point head = clicks.poll().getObject();
							double x = head.getX();
							double y = head.getY();
							ID target = player.getActiveSkill().getTarget();
							for(Entity e : handler.getEntityManager().getEntities()){
								if(e.getId() == target){
									if(new Rectangle((int)x, (int)y, 1, 1).intersects(new Rectangle((int)(e.getX() - handler.getGameCamera().getxOffset()), (int)(e.getY() - handler.getGameCamera().getyOffset()), e.getWidth(), e.getHeight()))){
										player.skillAction(player.getActiveSkill().getTarget(), player.getActiveSkill(), e);
										break outer;
									}
								}
							}
						}
					}
					player.setSkillActive(false);
				}
			}else if(!player.getSkillActive()){
				outer :{
					while(clicks.element() != null){
						Point head = clicks.poll().getObject();
						double x = head.getX();
						double y = head.getY();
						for(Entity e : handler.getEntityManager().getEntities()){
							if(e instanceof NPC){
								if(new Rectangle((int)x, (int)y, 1, 1).intersects(new Rectangle((int)e.getX(), (int)e.getY(), e.getWidth(), e.getHeight()))){
									NPCInteract((NPC)e);
									break outer;
								}
							}else if(e instanceof Merchant){
								if(new Rectangle((int)x, (int)y, 1, 1).intersects(new Rectangle((int)(e.getX() - handler.getGameCamera().getxOffset()), (int)(e.getY() - handler.getGameCamera().getyOffset()), e.getWidth(), e.getHeight()))){
									merchantInteract((Merchant)e);
									break outer;
								}
							}
						}
						for(SkillTracker s : player.getSkillBar()){
							if(s != null){
								if(new Rectangle((int)x, (int)y, 1, 1).intersects(s.getBounds())){
									float curAmt = 0;
									switch(s.getSkillCostType()){
									case "stam": curAmt = player.getStats().getStamina(); break;
									case "mana": curAmt = player.getStats().getMana(); break;
									case "heal": curAmt = player.getStats().getHealth(); break;
									}
									if((curAmt - s.getCost()) >= 0){
										player.setSkillActive(true);
										player.setActiveSkill(s);
									}else{
										skillList.add(new SkillText(handler, "Not enough " + s.getSkillCostType(), Handler.LAVENDER, (int)x, (int)y));
									}
									break outer;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void rightClick(){
		// TODO : add right click actions!
		LinkedQueue clicks = mouseInput.getRightClicks();
		if(handler.getEntityManager().getPaused()){
			if(isInv){
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
		}
	}
	
	public void drag(){
		//TODO: finish drag implementations
		if(mouseInput.isDragged()){
			isDragged = true;
			Rectangle startRect =  new Rectangle(mouseInput.getStartDrag().x, mouseInput.getStartDrag().y, 1, 1);
			if(isInv){
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
			}
		}else{
			if(isDragged){
				Rectangle endRect = new Rectangle(mouseInput.getEndDrag().x, mouseInput.getEndDrag().y, 1, 1);
				Rectangle startRect =  new Rectangle(mouseInput.getStartDrag().x, mouseInput.getStartDrag().y, 1, 1);
				if(isInv){
					if(startRect.intersects(new Rectangle(715, 125, 525, 455))){ //intersected the Equip area
						if(endRect.intersects(new Rectangle(40, 125, 645, 465))){ //intersects the Inventory area
								player.getStats().unEquip(curItem.getEquip());
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
		if(handler.getEntityManager().getPaused()){
			if(isInv){
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
			}else if(isNPC){
				if(isQuestMenu){
					for(Rectangle rect : questMenu){
						if(mouseInput.getMouseBounds().intersects(rect)){
							questMenuSel = rect;
						}
					}
				}
			}
		}else{
			//TODO: hover over skillbar
			if(player.getSkillBar() != null){
				outer: {
					for(SkillTracker skill : player.getSkillBar()){
						if(skill != null){
							if(mouseInput.getMouseBounds().intersects(skill.getBounds())){
								skillName = skill.getSkillType().getName();
								break outer;
							}
						}
					}
					skillName = null;
				}
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
	
	public void NPCInteract(NPC entity){
		player.setCurrentNPC(entity);
		int playerDist = (int)(Math.sqrt(Math.pow(player.getX() - entity.getX(), 2)+Math.pow(player.getY() - entity.getY(),2)));
		if(playerDist <= entity.getInteractDist()){
			entity.setInteract(true);
			isNPC = true;
			npcQuests = new ArrayList<>();
			ArrayList<QuestTracker> active = handler.getPlayer().getQuestLog().getActive();
			for(QuestTracker q : entity.getQuests()){
				if(active.size() > 0){
					for(QuestTracker a : active){
						if(a.getQuest() == q.getQuest()){
							npcQuests.add(a);
						}
					}
				}
			}
			if(!npcQuests.isEmpty()){
				if(npcQuests.size() > 1){
					//TODO: choose which quest to talk about - make a list
					questMenu = new Rectangle[npcQuests.size()];
					int x = 300;
					int y = Display.HEIGHT - 190;
					for(int i = 0; i < questMenu.length; i++){
						if(i != 0 && i%2 == 0){
							x += 255;
						}
						questMenu[i] = new Rectangle(x, y, 250, 50);
						y += 55;
					}
					questMenuSel = questMenu[0];
					isQuestMenu = true;
				}else{
					entity.setDialog(npcQuests.get(0));
				}
			}else{
				entity.setDialog();
			}
			handler.getEntityManager().setPaused(true);
		}
	}
	
	public void merchantInteract(Merchant entity){
		player.setCurrentMerchant(entity);
		int playerDist = (int)(Math.sqrt(Math.pow(player.getX() - entity.getX(), 2)+Math.pow(player.getY() - entity.getY(),2)));
		if(playerDist <= entity.getInteractDist()){
			entity.setInteract(true);
			isMerchant = true;
			handler.getEntityManager().setPaused(true);
		}
	}
	
	public void tickMerchant(){
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
		if(menuTimer > menuWait){
			if(keyInput.F){
				isMerchant = false;
				menuTimer = 0;
				handler.getEntityManager().setPaused(false);
			}
			if(keyInput.W){
				if(mercMinY < mercCurY){
					mercCurY -= mercSelHeight;
				}
				menuTimer = 0;
			}else if(keyInput.S){
				if(mercMaxY > mercCurY){
					mercCurY = Utils.clamp(mercCurY + mercSelHeight, mercMinY, mercY[itemIndex]);
				}
				menuTimer = 0;
			}else if(keyInput.D || keyInput.A){
				if(curMerc.getCurList() == curMerc.getBuyList()){
					curMerc.setCurList(curMerc.getSellList());
				}else if(curMerc.getCurList() == curMerc.getSellList()){
					curMerc.setCurList(curMerc.getBuyList());
				}
				mercCurY = mercMinY;
				menuTimer = 0;
			}else if(keyInput.E){
				int selIndex = 0;
				for(int i = 0; i < mercY.length; i++){
					if(mercCurY == mercY[i]){
						selIndex = i;
					}
				}
				curMerc.useItem(selIndex);
				menuTimer = 0;
			}
		}
	}
	
	public void setQuestList(boolean b){
		isQuestMenu = b;
	}
	
	public boolean isQuestMenu(){
		return isQuestMenu;
	}
	
	public void tickNPC(){
		NPC curNPC = player.getCurrentNPC();
		if(!isQuestMenu){
			if(menuTimer > menuWait){
				if(keyInput.A){
					if(curNPC.getDialognum() < curNPC.getDialog().size()-1){
						curNPC.setDialognum(curNPC.getDialognum()+1);
					}else if(curNPC.getDialognum() >= curNPC.getDialog().size()-1){
						curNPC.setInteract(false);
						isNPC = false;
						curNPC.setDialognum(0);
						player.setCurrentNPC(null);
						handler.getEntityManager().setPaused(false);
					}
					menuTimer = 0;
				}
			}
		}
	}
	
	public void tickCharacter(){
		//TODO: character detail page
	}
	
	public void tickSkillbook(){
		if(menuTimer > menuWait){
			if(keyInput.K || keyInput.I || keyInput.L || keyInput.esc){
				isSkillbook = false;
				menuTimer = 0;
				if(keyInput.K){
					handler.getEntityManager().setPaused(false);
				}else if(keyInput.I){
					isInv = true;
				}else if(keyInput.esc){
					isHelpMenu = true;
				}else if(keyInput.L){
					isQuestLog = true;
				}
			}
		}
	}
	
	public void tickHelpMenu(){
		if(menuTimer > menuWait){
			if(keyInput.esc || keyInput.I || keyInput.L || keyInput.K){
				isHelpMenu = false;
				menuTimer = 0;
				if(keyInput.esc){
					handler.getEntityManager().setPaused(false);
				}else if(keyInput.I){
					isInv = true;
				}else if(keyInput.L){
					isQuestLog = true;
				}else if(keyInput.K){
					isSkillbook = true;
				}
			}
		}
	}
	
	public void tickQuestLog(){
		if(menuTimer > menuWait){
			if(keyInput.esc || keyInput.I || keyInput.L || keyInput.K){
				isQuestLog = false;
				menuTimer = 0;
				curQuestIndex = 0;
				if(keyInput.L){
					handler.getEntityManager().setPaused(false);
				}else if(keyInput.I){
					isInv = true;
				}else if(keyInput.esc){
					isHelpMenu = true;
				}else if(keyInput.K){
					isSkillbook = true;
				}
			}
		}
	}
	
	public void tickInventory(){
		if(menuTimer > menuWait){
			if(keyInput.esc || keyInput.I || keyInput.L || keyInput.K){
				isInv = false;
				displayItemInfo = false;
				itemName = null;
				displayItemMenu = false;
				menuTimer = 0;
				if(keyInput.I){
					handler.getEntityManager().setPaused(false);
				}else if(keyInput.L){
					isQuestLog = true;
				}else if(keyInput.esc){
					isHelpMenu = true;
				}else if(keyInput.K){
					isSkillbook = true;
				}
			}
			if(keyInput.W){ // UP
					//TODO: make it select the menu tabs
					
					menuTimer = 0;
			}else if(keyInput.S){ // DOWN
					//TODO: make it select the menu tabs
					
					menuTimer = 0;
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
				menuTimer = 0;
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
				menuTimer = 0;
			}
		}
	}
	// GETTERS AND SETTERS
	public ItemType getCurrentTab(){
		return curTab;
	}
	
	public Handler getHandler(){
		return handler;
	}
	
	public boolean isQuestLog(){
		return isQuestLog;
	}
	
	public void setQuestLog(boolean b){
		isQuestLog = b;
	}
	
	public boolean isInv(){
		return isInv;
	}
	
	public void setInv(boolean b){
		isInv = b;
	}
	
	public void setNPC(boolean b){
		isNPC = b;
	}
	
	public void setCharacter(boolean b){
		isCharacter = b;
	}
	
	public boolean isCharacter(){
		return isCharacter;
	}
	
	public boolean isNPC(){
		return isNPC;
	}
	
	public ItemManager getItemManager(){
		return itemManager;
	}
	
	public void addSkillList(SkillText text){
		skillList.add(text);
	}
}