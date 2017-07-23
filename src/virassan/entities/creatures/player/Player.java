package virassan.entities.creatures.player;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import virassan.entities.creatures.Creature;
import virassan.entities.creatures.enemies.EnemyType;
import virassan.entities.creatures.npcs.Merchant;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.creatures.player.traits.Traits;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.entities.creatures.utils.Stats;
import virassan.gfx.Animation;
import virassan.gfx.Assets;
import virassan.input.KeyInput;
import virassan.input.MouseInput;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.quests.QuestTracker;
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

	private String name;
	private boolean isPaused;
	
	// Timers
	private long timer, lastTime, otherTime, timerTwo;
	private float damageTime = 20;

	// Inventory stuff
	private Inventory inventory;
	
	// Quest crap
	private QuestLog questLog;
	private HashMap<EnemyType, Integer> killList;
	
	//Animation crap
	private Animation walkLeft, walkRight, walkUp, walkDown, animation;
	private Animation[] walking;
	
	// Key Input
	KeyInput keyInput;
	MouseInput mouseInput;
	
	/**
	 * Constructor for creating Player object before loading from Save file.
	 * @param handler
	 * @param id
	 */
	public Player(Handler handler, ID id){
		this(handler, 0, 0, id, 32, 32, "/textures/entities/static/testing_anime_dude.png");
	}
	
	/**
	 * Constructs the Player
	 * @param handler the game's Handler
	 * @param x The spawn x coord of the Player
	 * @param y The spawn y coord of the Player
	 * @param id Id Enum
	 * @param filepath File containing the Player's SpriteSheet
	 */
	public Player(Handler handler, float x, float y, ID id, int width, int height, String filepath) {
		super(handler, "Default", x, y, width, height, 1, id);
		keyInput = handler.getKeyInput();
		mouseInput = handler.getMouseInput();
		QuestTracker.handler = handler;
		this.name = "Player";
		stats = new Stats(this, 150, 75, 75, 1);
		stats.setDmgMod(0.02f);
		stats.setArmorRating(1);
		damaged = false;
		npc = false;
		killList = new HashMap<EnemyType, Integer>();
		inventory = new Inventory(this);
		bounds.x = (int)(x + width/6);
		bounds.y = (int)(y + 3*height/4);
		bounds.width = 20;
		bounds.height = height/4 - 1;
		direction = 1;
		gold = 50;
		
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
		walking = new Animation[5];
		Assets playerSprites = new Assets(filepath);
		walkLeft = new Animation(350, playerSprites.getWalkingLeft());
		walkRight = new Animation(350, playerSprites.getWalkingRight());
		walkUp = new Animation(350, playerSprites.getWalkingUp());
		walkDown = new Animation(350, playerSprites.getWalkingDown());
		animation = walkDown;
		walking[0] = walkUp;
		walking[1] = walkDown;
		walking[2] = walkRight;
		walking[3] = walkLeft;
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
			
			move(delta);
			animation.tick(delta);
			y = Utils.clamp((int)y, 0, Handler.WORLD.getMap().getHeight() * Tile.TILE_HEIGHT - height);
			x = Utils.clamp((int)x, 0, Handler.WORLD.getMap().getWidth() * Tile.TILE_WIDTH - width);
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
				timerTwo += (System.currentTimeMillis() - otherTime) * delta;
				otherTime = System.currentTimeMillis() * (long)delta;
				if(timerTwo > 100){
					stats.heal(5);
					timerTwo = 0;
				}
			}
			else if(keyInput.Q){
				timer += (System.currentTimeMillis() - lastTime) * delta;
				lastTime = System.currentTimeMillis() * (long)delta;
				if(timer > 100){
					stats.damage(2);
					timer = 0;
				}
			}
			else if(keyInput.R){
				timerTwo += (System.currentTimeMillis() - otherTime) * delta;
				otherTime = System.currentTimeMillis() * (long)delta;
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
		if(animation.getCurrentFrame() != null){
			g.drawImage(animation.getCurrentFrame(), (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), null);
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
		//TODO: Cap it when pressing up/down and left/right
		float xmin = 0;
		float ymin = 0;
		velY = 0;
		velX = 0;
		if(down){
			direction = 1;
			velY += speed;
			ymin = 0;
		}
		if(left){
			direction = 3;
			velX -= speed;
			xmin = -speed;
		}
		if(right){
			direction = 2;
			velX += speed;
			xmin = 0;
		}
		if(up){
			direction = 0;
			velY -= speed;
			ymin = -speed;
		}
		animation = walking[direction];
		animation.start();
		animation.setAnimationLoop(true);
		animation.setTimeShared(true);
		if(!up && !down && !right && !left)
			animation.setAnimationLoop(false);
		velY = Utils.clamp((float)(velY*delta), ymin, ymin + speed);
		velX = Utils.clamp((float)(velX*delta), xmin, xmin + speed);
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

	public HashMap<EnemyType, Integer> getKillList() {
		return killList;
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
}
