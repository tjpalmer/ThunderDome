package virassan.entities.creatures.player;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import virassan.entities.creatures.Creature;
import virassan.entities.creatures.enemies.EnemyType;
import virassan.entities.creatures.npcs.Merchant;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.creatures.player.traits.Traits;
import virassan.entities.creatures.utils.Skill;
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
		System.out.println(stats.getMaxStam());
		stats.setDmgMod(0.02f);
		stats.setArmorRating(1);
		damaged = false;
		npc = false;
		killList = new HashMap<EnemyType, Integer>();
		inventory = new Inventory(this);
		walking = new Animation[5];
		bounds.x = (int)(x + width/6);
		bounds.y = (int)(y + 3*height/4);
		bounds.width = 20;
		bounds.height = height/4 - 1;
		direction = 1;
		gold = 50;
		
		questLog = new QuestLog(this);
		
		//Animation Shtuff
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
		
		//Traits
		traits = new Traits(this, 3, 5, 3, 5, 2);
		
		//Skills
		skillBar = new SkillTracker[5];
		skillList = new ArrayList<SkillTracker>();
		/*
		skillList.add(new SkillTracker(this, Skill.STAB, animation));
		skillList.add(new SkillTracker(this, Skill.SLASH, animation));
		skillList.add(new SkillTracker(this, Skill.HEAL_1, animation));
		skillList.add(new SkillTracker(this, Skill.CHOP, animation));
		for(int i = 0; i < skillBar.length; i++){
			if(i < skillList.size()){
				skillBar[i] = skillList.get(i);
			}
		}*/
		skillActive = false;
	}

	/**
	 * Ticks the Player - checking KeyInput, animation, movement, and GameCamera
	 */
	public void tick() {
		isPaused = handler.getEntityManager().getPaused();
		getInput();
		if(!isPaused){
			for(SkillTracker s : skillList){
				s.tick();
			}
			
			move();
			animation.tick();
			y = Utils.clamp((int)y, 0, handler.getWorld().getMap().getHeight() * Tile.TILE_HEIGHT - height);
			x = Utils.clamp((int)x, 0, handler.getWorld().getMap().getWidth() * Tile.TILE_WIDTH - width);
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
	private void getInput(){
		if(!isPaused){
			// Movement
			movement(keyInput.W, keyInput.S, keyInput.A, keyInput.D);
			
			// Special Commands
			if(keyInput.E){
				timerTwo += System.currentTimeMillis() - otherTime;
				otherTime = System.currentTimeMillis();
				if(timerTwo > 100){
					stats.heal(5);
					timerTwo = 0;
				}
			}
			else if(keyInput.Q){
				timer += System.currentTimeMillis() - lastTime;
				lastTime = System.currentTimeMillis();
				if(timer > 100){
					stats.damage(2);
					timer = 0;
				}
			}
			else if(keyInput.R){
				timerTwo += System.currentTimeMillis() - otherTime;
				otherTime = System.currentTimeMillis();
				if(timerTwo > 100){
					stats.addExperience(100);
					timerTwo = 0;
				}
			}
			
			/*
			//Attacking
			attacking(keyInput.A, attackA);
			attacking(keyInput.W, attackW);
			attacking(keyInput.S, attackS);
			attacking(keyInput.D, attackD);

			// Skills Bar
			
			if(skillActive){
				if(mouseInput.isLeftClicked()){
					System.out.println("Skill active and gonna try the thing");
					skillAction(activeSkill.getTarget(), activeSkill);
					skillActive = false;
					System.out.println("Skill deactive");
				}
			}else if(mouseInput.isLeftClicked()){
				if(!skillActive){
					for(Skill s : skillBar){
						if(s != null){
							if(mouseInput.getMouseBounds().intersects(s.getBounds())){
								System.out.println("Skill Active!");
								skillActive = true;
								activeSkill = s;
							}
						}
					}
				}
			}*/
		}
	}
	
	/**
	 * Renders the player
	 * @param g The Graphics for the Game
	 */
	public void render(Graphics g) {
		if(animation.getCurrentFrame() != null){
			g.drawImage(animation.getCurrentFrame(), (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), null);
		}else{
			//g.drawImage(sprite, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), null);
		}
	}
	
	
	/**
	 * If an arrow key is pressed, changes Player velocity
	 * @param b if any arrow key is pressed
	 */
	private void movement(boolean up, boolean down, boolean left, boolean right){
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
		velY = Utils.clamp(velY, ymin, ymin + speed);
		velX = Utils.clamp(velX, xmin, xmin + speed);
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
}
