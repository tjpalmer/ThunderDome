package virassan.entities.creatures.player;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import virassan.entities.creatures.Creature;
import virassan.entities.creatures.enemies.Enemy;
import virassan.entities.creatures.enemies.EnemySpecies;
import virassan.entities.creatures.npcs.Merchant;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.creatures.player.traits.Traits;
import virassan.entities.creatures.utils.Move;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.entities.creatures.utils.Stats;
import virassan.gfx.Animation;
import virassan.gfx.Assets;
import virassan.input.KeyInput;
import virassan.input.MouseInput;
import virassan.main.Handler;
import virassan.utils.Utils;
import virassan.world.maps.Tile;

public class Player extends Creature{
	
	private float gold;
	
	//Skill stuff
	private SkillTracker[] skillBar;
	private ArrayList<SkillTracker> skillList;
	private SkillTracker activeSkill;
	private boolean skillActive;
	private int curSkillIndex;
	
	// Traits stuff
	private Traits traits;
	
	// NPC stuff
	private NPC currentNPC;
	private Merchant currentMerchant;
	private HashMap<String, ArrayList<Boolean>> npcMetLiked; //<npcID, [met bool, liked bool]>
	
	
	private String name;
	private boolean isPaused;
	
	// Timers
	private long timer, lastTime, otherTime, timerTwo;
	private float damageTime = 20;

	// Inventory stuff
	private Inventory inventory;
	
	// Quest crap
	private QuestLog questLog;
	
	// Extra Player Info
	private HashMap<EnemySpecies, Integer> killEnemySpecies;
	private HashMap<String, Integer> killEnemyID;
	
	//Animation crap
	private Animation walkLeft, walkRight, walkUp, walkUpRight, walkUpLeft, walkDown, walkDownRight, walkDownLeft, animation;
	private Animation[] walking;
	
	// Key Input
	KeyInput keyInput;
	MouseInput mouseInput;
	
	/**
	 * Constructor for creating Player object before loading from Save file.
	 * @param handler
	 * @param id
	 */
	public Player(Handler handler){
		this(handler, 0, 0, 32, 32, "/textures/entities/player.png");
	}
	
	/**
	 * Constructs the Player
	 * @param handler the game's Handler
	 * @param x The spawn x coord of the Player
	 * @param y The spawn y coord of the Player
	 * @param id Id Enum
	 * @param filepath File containing the Player's SpriteSheet
	 */
	public Player(Handler handler, float x, float y, int width, int height, String filepath) {
		super(handler, "Default", x, y, width, height, 1);
		keyInput = handler.getKeyInput();
		mouseInput = handler.getMouseInput();
		this.name = "Player";
		stats = new Stats(this, 150, 75, 75, 1);
		stats.setDmgMod(0.02f);
		stats.setArmorRating(1);
		damaged = false;
		npc = false;
		npcMetLiked = new HashMap<>();
		killEnemySpecies = new HashMap<EnemySpecies, Integer>();
		killEnemyID = new HashMap<String, Integer>();
		inventory = new Inventory(this);
		bounds.x = (int)(x + width/6);
		bounds.y = (int)(y + 3*height/4);
		bounds.width = 20;
		bounds.height = height/4 - 1;
		direction = 1;
		gold = 50;
		
		speed = 7.0F;
		
		questLog = new QuestLog(this);
		
		//Animation Shtuff
		initAnimation(filepath);
		
		//Traits
		traits = new Traits(this, 3, 5, 3, 5, 2);
		
		//Skills
		skillBar = new SkillTracker[5];
		skillList = new ArrayList<SkillTracker>();
		skillActive = false;
	}

	private void initAnimation(String filepath){
		walking = new Animation[8];
		Assets playerSprites = new Assets(filepath);
		int animeSpeed = 800;
		walkLeft = new Animation(animeSpeed, playerSprites.getWalkingLeft());
		walkRight = new Animation(animeSpeed, playerSprites.getWalkingRight());
		walkUp = new Animation(animeSpeed, playerSprites.getWalkingUp());
		walkUpRight = new Animation(animeSpeed, playerSprites.getWalkingUpRight());
		walkUpLeft = new Animation(animeSpeed, playerSprites.getWalkingUpLeft());
		walkDown = new Animation(animeSpeed, playerSprites.getWalkingDown());
		walkDownRight = new Animation(animeSpeed, playerSprites.getWalkingDownRight());
		walkDownLeft = new Animation(animeSpeed, playerSprites.getWalkingDownLeft());
		animation = walkDown;
		walking[0] = walkRight;
		walking[1] = walkUpRight;
		walking[2] = walkUp;
		walking[3] = walkUpLeft;
		walking[4] = walkLeft;
		walking[5] = walkDownLeft;
		walking[6] = walkDown;
		walking[7] = walkDownRight;
	}
	
	/**
	 * Ticks the Player - checking KeyInput, animation, movement, and GameCamera
	 */
	public void tick(double delta) {
		isPaused = handler.getEntityManager().getPaused();
		getInput(delta);
		if(!isPaused){
			for(SkillTracker s : skillList){
				s.tick(delta);
			}
			animation.tick(delta);
			vector.dY = Utils.clamp((int)vector.dY, 0, handler.getMap().getHeight() * Tile.TILE_HEIGHT - height);
			vector.dX = Utils.clamp((int)vector.dX, 0, handler.getMap().getWidth() * Tile.TILE_WIDTH - width);
			handler.getGameCamera().centerOnEntity(this);
			if(stats.isDamaged()){
				if(damageTime > 0){
					damageTime--;
				}
				if(damageTime <= 0){
					stats.setDamaged(false);
					damageTime = 20;
				}
			}
		}
	}

	/**
	 * Checks player Input via Keyboard and adjusts movement and animation
	 */
	private void getInput(double delta){
		if(!isPaused){
			// Movement
			movement(delta, keyInput.W, keyInput.S, keyInput.A, keyInput.D);
			
			// Special Commands
			if(keyInput.E){
				timerTwo += (System.currentTimeMillis() - otherTime);
				otherTime = System.currentTimeMillis();
				if(timerTwo > 100){
					stats.heal(5);
					timerTwo = 0;
				}
			}
			else if(keyInput.Q){
				timer += (System.currentTimeMillis() - lastTime);
				lastTime = System.currentTimeMillis();
				if(timer > 100){
					stats.damage(2);
					timer = 0;
				}
			}
			else if(keyInput.R){
				timerTwo += (System.currentTimeMillis() - otherTime);
				otherTime = System.currentTimeMillis();
				if(timerTwo > 100){
					stats.addExperience(100);
					timerTwo = 0;
				}
			}
		}
	}
	
	/**
	 * Renders the player
	 * @param g The Graphics for the Game
	 */
	public void render(Graphics g) {
		float xrel = vector.normalize().dX ;//* handler.getGameCamera().getWidth();
		float yrel = vector.normalize().dY ;//* handler.getGameCamera().getHeight();
		
		if(animation.getCurrentFrame() != null){
			g.drawImage(animation.getCurrentFrame(), (int) (xrel - handler.getGameCamera().getxOffset()), (int) (yrel - handler.getGameCamera().getyOffset()), null);
			g.setColor(Color.RED);
			g.drawRect(getCollisionBounds(-handler.getGameCamera().getxOffset(), -handler.getGameCamera().getyOffset()).x, getCollisionBounds(-handler.getGameCamera().getxOffset(), -handler.getGameCamera().getyOffset()).y, getCollisionBounds(handler.getGameCamera().getxOffset(), handler.getGameCamera().getyOffset()).width, getCollisionBounds(handler.getGameCamera().getxOffset(), handler.getGameCamera().getyOffset()).height);
		}else{
			//g.drawImage(sprite, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), null);
		}
	}
	
	
	/**
	 * If an arrow key is pressed, changes Player velocity
	 * @param b if any arrow key is pressed
	 */
	private void movement(double delta, boolean up, boolean down, boolean left, boolean right){
		if(up && right){
			direction = 1;
		}
		else if(up && left){
			direction = 3;
		}
		else if(down && right){
			direction = 7;
		}
		else if(down && left){
			direction = 5;
		}
		else if(down){
			direction = 6;
		}
		else if(left){
			direction = 4;
		}
		else if(right){
			direction = 0;
		}
		else if(up){
			direction = 2;
		}else{
			direction = -1;
		}
		isMoving = Move.move(this, speed, direction, (float)delta);
		if(isMoving){
			animation = walking[direction];
			animation.start();
			animation.setAnimationLoop(true);
			animation.setTimeShared(true);
		}else{
			animation.setAnimationLoop(false);
		}
		
	}
	
	/**
	 * Sets the timer variable to 0
	 */
	public void resetTimer(){
		timer = 0;
	}
	
	@Override
	public void unPause() {
		otherTime = System.currentTimeMillis();
		lastTime = System.currentTimeMillis();
		
	}

	// GETTERS AND SETTERS
	public Inventory getInventory(){
		return inventory;
	}
	
	public QuestLog getQuestLog(){
		return questLog;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String text){
		name = text;
	}

	public NPC getCurrentNPC(){
		return currentNPC;
	}
	
	public void setCurrentNPC(NPC e) {
		currentNPC = e;
	}
	
	public Traits getTraits(){
		return traits;
	}
	
	public void setTraits(int speech, int end, int res, int dex, int intel){
		traits = new Traits(this, speech, end, res, dex, intel);
	}

	public HashMap<String, Integer> getKillEnemyID(){
		return killEnemyID;
	}
	
	public HashMap<EnemySpecies, Integer> getKillEnemySpecies() {
		return killEnemySpecies;
	}
	
	public void addKillList(Enemy enemy){
		//TODO: wtf is the point of the Soldier class? Maybe for different behaviors? 
		if(killEnemySpecies.containsKey(enemy.getSpecies())){
			killEnemySpecies.replace(enemy.getSpecies(), killEnemySpecies.get(enemy.getSpecies()) + 1);
		}else{
			killEnemySpecies.put(enemy.getSpecies(), 1);
		}
		if(killEnemyID.containsKey(enemy.getEnemyID())){
			killEnemyID.replace(enemy.getEnemyID(), killEnemyID.get(enemy.getEnemyID()) + 1);
		}else{
			killEnemyID.put(enemy.getEnemyID(), 1);
		}
		
	}
	
	public String toString(){
		return name;
	}
	
	public SkillTracker[] getSkillBar(){
		return skillBar;
	}
	
	public boolean getSkillActive(){
		return skillActive;
	}
	
	public void setSkillActive(boolean b){
		skillActive = b;
	}

	public SkillTracker getActiveSkill(){
		return activeSkill;
	}
	
	public void setActiveSkill(SkillTracker skill){
		activeSkill = skill;
	}
	
	public void setCurrentMerchant(Merchant e){
		currentMerchant = e;
	}
	
	public Merchant getCurrentMerchant(){
		return currentMerchant;
	}
	
	public float getGold(){
		return gold;
	}
	
	public Animation getAnimation(){
		return animation;
	}
	
	public void addGold(float amt){
		gold = Utils.clamp(gold + amt, 0F, 999999999F);
	}
	
	public void addSkill(SkillTracker skill){
		skillList.add(skill);
	}
	
	public void setSkillBar(SkillTracker skill, int index){
		skillBar[index] = skill;
	}
	
	public ArrayList<SkillTracker> getSkills(){
		return skillList;
	}
	
	public void setCurSkillIndex(int index){
		curSkillIndex = index;
	}
	
	public int getCurSkillIndex(){
		return curSkillIndex;
	}

	public void setNPCMetLiked(HashMap<String, ArrayList<Boolean>> hash){
		npcMetLiked = hash;
	}
	
	public HashMap<String, ArrayList<Boolean>> getNpcMetLiked(){
		return npcMetLiked;
	}
	
	public void setNPCMet(String npcID){
		if(npcMetLiked.containsKey(npcID)){
			System.out.println("Error Message: Player_setNPCMet NPC_ALREADY_MET");
		}else{
			npcMetLiked.put(npcID, new ArrayList<Boolean>(Arrays.asList(true, true)));
		}
	}
	
	public void setNPCLiked(String npcID, boolean liked){
		if(npcMetLiked.containsKey(npcID)){
			npcMetLiked.replace(npcID, new ArrayList<Boolean>(Arrays.asList(npcMetLiked.get(npcID).get(0), liked)));
		}else{
			npcMetLiked.put(npcID, new ArrayList<Boolean>(Arrays.asList(true, liked)));
		}
	}
	
	public boolean isNPCMet(String npcID){
		if(npcMetLiked.containsKey(npcID)){
			return npcMetLiked.get(npcID).get(0).booleanValue();
		}
		return false;
	}
	
	public boolean isNPCLiked(String npcID){
		if(npcMetLiked.containsKey(npcID)){
			return npcMetLiked.get(npcID).get(1).booleanValue();
		}
		return false;
	}
	
	@Override
	public Class findClass() {
		return getClass();
	}
}
