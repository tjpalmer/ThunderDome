package virassan.gfx;

import java.awt.image.BufferedImage;

import virassan.world.maps.Map;
import virassan.world.maps.Tile;

/**
 * Loads, divvies up SpriteSheets, and saves BufferedImages to variables
 * @author Virassan
 *
 */

public class Assets {
	
	public static final int IMAGE_WIDTH = 32, IMAGE_HEIGHT = 32;
	public static final int ITEM_WIDTH = 16, ITEM_HEIGHT = 16;
	
	private BufferedImage front_1, front_Up, front_3, 
		left_1, left_Up, left_3, 
		right_1, right_Up, right_3, 
		back_1, back_Up, back_3,
		fleft_1, fleft_Up, fleft_3,
		fright_1, fright_Up, fright_3,
		bleft_1, bleft_Up, bleft_3,
		bright_1, bright_Up, bright_3;
	public static BufferedImage lava, water, idk, grass, flowers_1, flowers_2, stone_rock, animeDudeStanding, button_blue, button_green, button_pink, button_red;
	
	
	//menu GUI
	public static BufferedImage menu, invCharMenu, questMenu, skillMenu, invItemInfo;
	public static BufferedImage mainInv, activeTab, inactiveTab, invFood, invWeapons, invArmor, invJunk;
	public static BufferedImage dialogBox, button_a, button_b;
	public static BufferedImage scrollButtonUp, scrollButtonDown;
	public static BufferedImage defaultHands, defaultFeet, defaultShoulders, defaultAcc1, defaultAcc2, defaultMain, defaultOff, defaultHead, defaultChest, defaultLegs;
	public static BufferedImage invEmptySlot;
	
	// Skill Icons
	public static BufferedImage skill_border, slash_icon, heal_icon, chop_icon, stab_icon;
	
	// Buff Icons
	public static BufferedImage buff001, buff002;
	
	// Items
	private static final SpriteSheet food = new SpriteSheet(ImageLoader.loadImage("/textures/items/food.png"));
	private static final SpriteSheet weapons = new SpriteSheet(ImageLoader.loadImage("/textures/items/weapons.png"));
	private static final SpriteSheet armor = new SpriteSheet(ImageLoader.loadImage("/textures/items/armor.png"));
	private static final SpriteSheet junk = new SpriteSheet(ImageLoader.loadImage("/textures/items/junk.png"));
	
	// ITEMS YO
	public static BufferedImage apple = food.sprite(0, 0, ITEM_WIDTH, ITEM_HEIGHT),
	green_apple = food.sprite(ITEM_WIDTH, 0, ITEM_WIDTH, ITEM_HEIGHT),
	toast = food.sprite(ITEM_WIDTH * 2, 0, ITEM_WIDTH, ITEM_HEIGHT),
	cherry = food.sprite(ITEM_WIDTH * 3, 0, ITEM_WIDTH, ITEM_HEIGHT),
	// TODO: draw cherry
	
	slime = junk.sprite(0, 0, ITEM_WIDTH, ITEM_HEIGHT),
	bandaid = junk.sprite(ITEM_WIDTH, 0, ITEM_WIDTH, ITEM_HEIGHT),
	tire = junk.sprite(ITEM_WIDTH * 2, 0, ITEM_WIDTH, ITEM_HEIGHT),
	feather = junk.sprite(ITEM_WIDTH * 3, 0, ITEM_WIDTH, ITEM_HEIGHT),
	
	sword = weapons.sprite(0, 0, ITEM_WIDTH, ITEM_HEIGHT),
	broad_sword = weapons.sprite(ITEM_WIDTH, 0, ITEM_WIDTH, ITEM_HEIGHT),
	dagger = weapons.sprite(ITEM_WIDTH * 2, 0, ITEM_WIDTH, ITEM_HEIGHT),
	staff = weapons.sprite(0, ITEM_WIDTH, ITEM_WIDTH, ITEM_HEIGHT),
	woodcutter_axe = weapons.sprite(0, ITEM_WIDTH * 2, ITEM_WIDTH, ITEM_HEIGHT),
	
	// TODO: draw these armor items yo
	iron_boots = armor.sprite(0, 0, ITEM_WIDTH, ITEM_HEIGHT),
	iron_cuirass = armor.sprite(ITEM_WIDTH, 0, ITEM_WIDTH, ITEM_HEIGHT),
	iron_gauntlet = armor.sprite(ITEM_WIDTH * 2, 0, ITEM_WIDTH, ITEM_HEIGHT),
	iron_greaves = armor.sprite(ITEM_WIDTH * 3, 0, ITEM_WIDTH, ITEM_HEIGHT),
	iron_helm = armor.sprite(ITEM_WIDTH * 4, 0, ITEM_WIDTH, ITEM_HEIGHT);
	
	
	public static SpriteSheet warp_portal = new SpriteSheet(ImageLoader.loadImage("/textures/tiles/statics/warp_portal_spritesheet.png"));
	public static BufferedImage[] warp_portal_images = new BufferedImage[]{Assets.warp_portal.sprite(0, 0, 64, 64), Assets.warp_portal.sprite(64, 0, 64, 64), Assets.warp_portal.sprite(64*2, 0, 64, 64), Assets.warp_portal.sprite(0, 64, 64, 64), Assets.warp_portal.sprite(64, 64, 64, 64), Assets.warp_portal.sprite(64*2, 64, 64, 64)};
	
	
	private BufferedImage[] walkingLeft, 
			walkingRight, 
			walkingUp, 
			walkingUpLeft,
			walkingUpRight,
			walkingDown, 
			walkingDownLeft,
			walkingDownRight,
			standing;
	
	/**
	 * Initializes publically accessed images
	 */
	public static void init(){
		SpriteSheet testing = new SpriteSheet(ImageLoader.loadImage("/textures/tiles/TextureSpriteSheet.png"));
		// SpriteSheet tiles = new SpriteSheet(ImageLoader.loadImage("/textures/tiles/TileSpriteSheet.png"));
		SpriteSheet animeDude = new SpriteSheet(ImageLoader.loadImage("/textures/entities/static/testing_anime_dude.png"));
		SpriteSheet skills = new SpriteSheet(ImageLoader.loadImage("/textures/skills/skill01_sheet.png"));
		button_blue = ImageLoader.loadImage("/textures/buttons/Button_blue.png");		
		button_green = ImageLoader.loadImage("/textures/buttons/Button_green.png");
		button_pink = ImageLoader.loadImage("/textures/buttons/Button_pink.png");
		button_red = ImageLoader.loadImage("/textures/buttons/Button_red.png");
		
		// Inventory GUI tabs and text = 125wX50h, mainBG = 500wX350h
		
		// mainInv = ImageLoader.loadImage("/textures/gui/inventory/mainBackground.png");
		mainInv = ImageLoader.loadImage("/textures/gui/inventory/Inventory.png");
		invFood = ImageLoader.loadImage("/textures/gui/inventory/foodTab.png");
		invWeapons = ImageLoader.loadImage("/textures/gui/inventory/weaponsTab.png");
		invArmor = ImageLoader.loadImage("/textures/gui/inventory/armorTab.png");
		invJunk = ImageLoader.loadImage("/textures/gui/inventory/junkTab.png");
		inactiveTab = ImageLoader.loadImage("/textures/gui/inventory/activeTab.png");
		activeTab = ImageLoader.loadImage("/textures/gui/inventory/inactiveTab.png");
		
		// Menu GUI
		menu = ImageLoader.loadImage("/textures/gui/menus/menu.png");
		invCharMenu = ImageLoader.loadImage("/textures/gui/menus/inventory - character.png");
		questMenu = ImageLoader.loadImage("/textures/gui/menus/questlog.png");
		skillMenu = ImageLoader.loadImage("/textures/gui/menus/skillbook.png");
		invItemInfo = ImageLoader.loadImage("/textures/gui/menus/itemInfoBox.png");
		
		
		scrollButtonUp = ImageLoader.loadImage("/textures/gui/menus/scrollButtonUp.png");
		scrollButtonDown = ImageLoader.loadImage("/textures/gui/menus/scrollButtonDown.png");
		
		SpriteSheet defaultEquip = new SpriteSheet(ImageLoader.loadImage("/textures/gui/menus/defaultEquip.png"));
		defaultHead = defaultEquip.sprite(0, 0, 64, 64);
		defaultChest = defaultEquip.sprite(64, 0, 64, 64);
		defaultLegs = defaultEquip.sprite(0, 64, 64, 64);
		defaultFeet = defaultEquip.sprite(64, 64, 64, 64);
		defaultShoulders = defaultEquip.sprite(0, 128, 64, 64);
		defaultHands = defaultEquip.sprite(64, 128, 64, 64);
		defaultAcc1 = defaultEquip.sprite(0, 192, 64, 64);
		defaultAcc2 = defaultEquip.sprite(64, 192, 64, 64);
		defaultMain = defaultEquip.sprite(0, 256, 64, 64);
		defaultOff = defaultEquip.sprite(64, 256, 64, 64);
		invEmptySlot = ImageLoader.loadImage("/textures/gui/menus/inv_empty_slot.png");
		
		dialogBox = ImageLoader.loadImage("/textures/gui/npc/npc_dialog_box.png");
		button_a = ImageLoader.loadImage("/textures/gui/npc/dialog_button_a.png");
		button_b = ImageLoader.loadImage("/textures/gui/npc/dialog_button_b.png");
		
		animeDudeStanding = animeDude.sprite(32, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		
		//lava = testing.sprite(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		grass = testing.sprite(0, 0, Tile.TILE_WIDTH, Tile.TILE_WIDTH);
		water = testing.sprite(Tile.TILE_WIDTH, 0, Tile.TILE_WIDTH, Tile.TILE_WIDTH);
		
		flowers_1 = testing.sprite(Tile.TILE_WIDTH * 3, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
		flowers_2 = testing.sprite(Tile.TILE_WIDTH * 4, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
		stone_rock = testing.sprite(Tile.TILE_WIDTH * 2, 0, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
	
		// TILES BRO
		Map.init();
		
		// ITEMS YO
		apple = food.sprite(0, 0, ITEM_WIDTH, ITEM_HEIGHT);
		green_apple = food.sprite(ITEM_WIDTH, 0, ITEM_WIDTH, ITEM_HEIGHT);
		toast = food.sprite(ITEM_WIDTH * 2, 0, ITEM_WIDTH, ITEM_HEIGHT);
		cherry = food.sprite(ITEM_WIDTH * 3, 0, ITEM_WIDTH, ITEM_HEIGHT);
		// TODO: draw cherry
		
		slime = junk.sprite(0, 0, ITEM_WIDTH, ITEM_HEIGHT);
		bandaid = junk.sprite(ITEM_WIDTH, 0, ITEM_WIDTH, ITEM_HEIGHT);
		tire = junk.sprite(ITEM_WIDTH * 2, 0, ITEM_WIDTH, ITEM_HEIGHT);
		feather = junk.sprite(ITEM_WIDTH * 3, 0, ITEM_WIDTH, ITEM_HEIGHT);
		
		sword = weapons.sprite(0, 0, ITEM_WIDTH, ITEM_HEIGHT);
		broad_sword = weapons.sprite(ITEM_WIDTH, 0, ITEM_WIDTH, ITEM_HEIGHT);
		dagger = weapons.sprite(ITEM_WIDTH * 2, 0, ITEM_WIDTH, ITEM_HEIGHT);
		staff = weapons.sprite(0, ITEM_WIDTH, ITEM_WIDTH, ITEM_HEIGHT);
		woodcutter_axe = weapons.sprite(0, ITEM_WIDTH * 2, ITEM_WIDTH, ITEM_HEIGHT);
		
		// TODO: draw these armor items yo
		iron_boots = armor.sprite(0, 0, ITEM_WIDTH, ITEM_HEIGHT);
		iron_cuirass = armor.sprite(ITEM_WIDTH, 0, ITEM_WIDTH, ITEM_HEIGHT);
		iron_gauntlet = armor.sprite(ITEM_WIDTH * 2, 0, ITEM_WIDTH, ITEM_HEIGHT);
		iron_greaves = armor.sprite(ITEM_WIDTH * 3, 0, ITEM_WIDTH, ITEM_HEIGHT);
		iron_helm = armor.sprite(ITEM_WIDTH * 4, 0, ITEM_WIDTH, ITEM_HEIGHT);
		
		// TODO: Skill Icons!
		skill_border = ImageLoader.loadImage("/textures/skills/border.png");
		slash_icon = skills.sprite(0, 0, 64, 64);
		heal_icon = skills.sprite(64, 0, 64, 64);
		chop_icon = skills.sprite(128, 0, 64, 64);
		stab_icon = skills.sprite(192, 0, 64, 64);
		
		// TODO: Buff Icons
		buff001 = ImageLoader.loadImage("/textures/buffs/buff001.png");
	}
	
	public Assets(String filepath, int height, int width){
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage(filepath));
		
		front_1 = sheet.sprite(0, 0, height, width);
		front_Up = sheet.sprite(width, 0, width, height);
		front_3 = sheet.sprite(width*2, 0, width, height);
		left_1 = sheet.sprite(0, height, width, height);
		left_Up = sheet.sprite(width, height, width, height);
		left_3 = sheet.sprite(width*2, height, width, height);
		right_1 = sheet.sprite(0, height*2, width, height);
		right_Up = sheet.sprite(width, height*2, width, height);
		right_3 = sheet.sprite(width*2, height*2, width, height);
		back_1 = sheet.sprite(0, height*3, width, height);
		back_Up = sheet.sprite(width, height*3, width, height);
		back_3 = sheet.sprite(width*2, height*3, width, height);
		fleft_1 = sheet.sprite(width*3, 0, width, height);
		fleft_Up = sheet.sprite(width*4, 0, width, height);
		fleft_3 = sheet.sprite(width*5, 0, width, height);
		bleft_1 = sheet.sprite(width*3, height, width, height);
		bleft_Up = sheet.sprite(width*4, height, width, height);
		bleft_3 = sheet.sprite(width*5, height, width, height);
		fright_1 = sheet.sprite(width*3, height*2, width, height);
		fright_Up = sheet.sprite(width*4, height*2, width, height);
		fright_3 = sheet.sprite(width*5, height*2, width, height);
		bright_1 = sheet.sprite(width*3, height*3, width, height);
		bright_Up = sheet.sprite(width*4, height*3, width, height);
		bright_3 = sheet.sprite(width*5, height*3, width, height);
		
		walkingLeft = new BufferedImage[3];
		walkingRight = new BufferedImage[3];
		walkingUp = new BufferedImage[3];
		walkingUpRight = new BufferedImage[3];
		walkingUpLeft = new BufferedImage[3];
		walkingDown = new BufferedImage[3];
		walkingDownRight = new BufferedImage[3];
		walkingDownLeft = new BufferedImage[3];
		standing = new BufferedImage[1];
		
		walkingLeft[0] = left_1;
		walkingLeft[1] = left_Up;
		walkingLeft[2] = left_3;
		walkingRight[0] = right_1;
		walkingRight[1] = right_Up;
		walkingRight[2] = right_3;
		walkingUp[0] = back_1;
		walkingUp[1] = back_Up;
		walkingUp[2] = back_3;
		walkingUpRight[0] = bright_1;
		walkingUpRight[1] = bright_Up;
		walkingUpRight[2] = bright_3;
		walkingUpLeft[0] = bleft_1;
		walkingUpLeft[1] = bleft_Up;
		walkingUpLeft[2] = bleft_3;
		walkingDown[0] = front_1;
		walkingDown[1] = front_Up;
		walkingDown[2] = front_3;
		walkingDownRight[0] = fright_1;
		walkingDownRight[1] = fright_Up;
		walkingDownRight[2] = fright_3;
		walkingDownLeft[0] = fleft_1;
		walkingDownLeft[1] = fleft_Up;
		walkingDownLeft[2] = fleft_3;
		standing[0] = front_1;
	}
	
	/**
	 * Constructs a new Asset - used for walking Animation
	 * @param filepath the walking Animation SpriteSheet file
	 */
	public Assets(String filepath){
		this(filepath, IMAGE_HEIGHT, IMAGE_WIDTH);
	}
	
	public Assets(String filepath, int numberOfFrames, int imageWidth, int imageHeight){
		walkingLeft = new BufferedImage[numberOfFrames];
		walkingRight = new BufferedImage[numberOfFrames];
		walkingUp = new BufferedImage[numberOfFrames];
		walkingDown = new BufferedImage[numberOfFrames];
		
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage(filepath));
		for(int k = 0; k < 4; k++){
			for(int i = 0; i < numberOfFrames; i++){
				if(k == 0){
					walkingUp[i] = sheet.sprite(i * imageWidth, k * imageHeight, imageWidth, imageHeight);
				}else if(k == 1){
					walkingDown[i] = sheet.sprite(i * imageWidth, k * imageHeight, imageWidth, imageHeight);
				}else if(k == 2){
					walkingRight[i] = sheet.sprite(i * imageWidth, k * imageHeight, imageWidth, imageHeight);
				}else if(k == 3){
					walkingLeft[i] = sheet.sprite(i * imageWidth, k * imageHeight, imageWidth, imageHeight);
				}
			}
		}
		/*
		for(int j = 0; j < 4; j++){
			if(j == 0){
				walkingUp[0] = sheet.sprite(j * imageWidth, 4 * imageHeight, imageWidth, imageHeight);
			}else if(j == 1){
				walkingDown[0] = sheet.sprite(j * imageWidth, 4 * imageHeight, imageWidth, imageHeight);
			}else if(j == 2){
				walkingRight[0] = sheet.sprite(j * imageWidth, 4 * imageHeight, imageWidth, imageHeight);
			}else if(j == 3){
				walkingLeft[0] = sheet.sprite(j * imageWidth, 4 * imageHeight, imageWidth, imageHeight);
			}
		}
		*/
	}


	// GETTERS AND SETTERS
		
	
	public BufferedImage getFront_1() {
		return front_1;
	}

	public void setFront_1(BufferedImage front_1) {
		this.front_1 = front_1;
	}

	public BufferedImage getFront_Up() {
		return front_Up;
	}

	public void setFront_Up(BufferedImage front_Up) {
		this.front_Up = front_Up;
	}

	public BufferedImage getFront_3() {
		return front_3;
	}

	public void setFront_3(BufferedImage front_3) {
		this.front_3 = front_3;
	}

	public BufferedImage getLeft_1() {
		return left_1;
	}

	public void setLeft_1(BufferedImage left_1) {
		this.left_1 = left_1;
	}

	public BufferedImage getLeft_Up() {
		return left_Up;
	}

	public void setLeft_Up(BufferedImage left_Up) {
		this.left_Up = left_Up;
	}

	public BufferedImage getLeft_3() {
		return left_3;
	}

	public void setLeft_3(BufferedImage left_3) {
		this.left_3 = left_3;
	}

	public BufferedImage getRight_1() {
		return right_1;
	}

	public void setRight_1(BufferedImage right_1) {
		this.right_1 = right_1;
	}

	public BufferedImage getRight_Up() {
		return right_Up;
	}

	public void setRight_Up(BufferedImage right_Up) {
		this.right_Up = right_Up;
	}

	public BufferedImage getRight_3() {
		return right_3;
	}

	public void setRight_3(BufferedImage right_3) {
		this.right_3 = right_3;
	}

	public BufferedImage getBack_1() {
		return back_1;
	}

	public void setBack_1(BufferedImage back_1) {
		this.back_1 = back_1;
	}

	public BufferedImage getBack_Up() {
		return back_Up;
	}

	public void setBack_Up(BufferedImage back_Up) {
		this.back_Up = back_Up;
	}

	public BufferedImage getBack_3() {
		return back_3;
	}

	public void setBack_3(BufferedImage back_3) {
		this.back_3 = back_3;
	}

	public BufferedImage getFleft_1() {
		return fleft_1;
	}

	public void setFleft_1(BufferedImage fleft_1) {
		this.fleft_1 = fleft_1;
	}

	public BufferedImage getFleft_Up() {
		return fleft_Up;
	}

	public void setFleft_Up(BufferedImage fleft_Up) {
		this.fleft_Up = fleft_Up;
	}

	public BufferedImage getFleft_3() {
		return fleft_3;
	}

	public void setFleft_3(BufferedImage fleft_3) {
		this.fleft_3 = fleft_3;
	}

	public BufferedImage getFright_1() {
		return fright_1;
	}

	public void setFright_1(BufferedImage fright_1) {
		this.fright_1 = fright_1;
	}

	public BufferedImage getFright_Up() {
		return fright_Up;
	}

	public void setFright_Up(BufferedImage fright_Up) {
		this.fright_Up = fright_Up;
	}

	public BufferedImage getFright_3() {
		return fright_3;
	}

	public void setFright_3(BufferedImage fright_3) {
		this.fright_3 = fright_3;
	}

	public BufferedImage getBleft_1() {
		return bleft_1;
	}

	public void setBleft_1(BufferedImage bleft_1) {
		this.bleft_1 = bleft_1;
	}

	public BufferedImage getBleft_Up() {
		return bleft_Up;
	}

	public void setBleft_Up(BufferedImage bleft_Up) {
		this.bleft_Up = bleft_Up;
	}

	public BufferedImage getBleft_3() {
		return bleft_3;
	}

	public void setBleft_3(BufferedImage bleft_3) {
		this.bleft_3 = bleft_3;
	}

	public BufferedImage getBright_1() {
		return bright_1;
	}

	public void setBright_1(BufferedImage bright_1) {
		this.bright_1 = bright_1;
	}

	public BufferedImage getBright_Up() {
		return bright_Up;
	}

	public void setBright_Up(BufferedImage bright_Up) {
		this.bright_Up = bright_Up;
	}

	public BufferedImage getBright_3() {
		return bright_3;
	}

	public void setBright_3(BufferedImage bright_3) {
		this.bright_3 = bright_3;
	}

	public BufferedImage[] getWalkingLeft() {
		return walkingLeft;
	}

	public void setWalkingLeft(BufferedImage[] walkingLeft) {
		this.walkingLeft = walkingLeft;
	}

	public BufferedImage[] getWalkingRight() {
		return walkingRight;
	}

	public void setWalkingRight(BufferedImage[] walkingRight) {
		this.walkingRight = walkingRight;
	}

	public BufferedImage[] getWalkingUp() {
		return walkingUp;
	}

	public void setWalkingUp(BufferedImage[] walkingUp) {
		this.walkingUp = walkingUp;
	}

	public BufferedImage[] getWalkingUpLeft() {
		return walkingUpLeft;
	}

	public void setWalkingUpLeft(BufferedImage[] walkingUpLeft) {
		this.walkingUpLeft = walkingUpLeft;
	}

	public BufferedImage[] getWalkingUpRight() {
		return walkingUpRight;
	}

	public void setWalkingUpRight(BufferedImage[] walkingUpRight) {
		this.walkingUpRight = walkingUpRight;
	}

	public BufferedImage[] getWalkingDown() {
		return walkingDown;
	}

	public void setWalkingDown(BufferedImage[] walkingDown) {
		this.walkingDown = walkingDown;
	}

	public BufferedImage[] getWalkingDownLeft() {
		return walkingDownLeft;
	}

	public void setWalkingDownLeft(BufferedImage[] walkingDownLeft) {
		this.walkingDownLeft = walkingDownLeft;
	}

	public BufferedImage[] getWalkingDownRight() {
		return walkingDownRight;
	}

	public void setWalkingDownRight(BufferedImage[] walkingDownRight) {
		this.walkingDownRight = walkingDownRight;
	}

	public BufferedImage[] getStanding() {
		return standing;
	}

	public void setStanding(BufferedImage[] standing) {
		this.standing = standing;
	}

}
	