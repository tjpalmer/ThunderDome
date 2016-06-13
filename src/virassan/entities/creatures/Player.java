package virassan.entities.creatures;

import java.awt.Color;
import java.awt.Graphics;

import virassan.gfx.Animation;
import virassan.gfx.Assets;
import virassan.gfx.hud.BouncyText;
import virassan.items.Inventory;
import virassan.main.Handler;
import virassan.main.ID;
import virassan.utils.Utils;
import virassan.world.maps.Tile;

public class Player extends Creature{

	private String name;
	
	private long invTimer, invLast;
	private long timer, lastTime, otherTime, timerTwo, attackTimer, attackTime;
	private float damageTime = 20;

	private int invSlots;
	private Inventory inventory;
	
	private Attack attackA, attackW, attackS, attackD;
	
	//Animation crap
	private Animation walkLeft, walkRight, walkUp, walkDown, animation;
	private Animation[] walking;
	
	/**
	 * Constructs the Player
	 * @param handler the game's Handler
	 * @param x The spawn x coord of the Player
	 * @param y The spawn y coord of the Player
	 * @param id Id Enum
	 * @param filepath File containing the Player's SpriteSheet
	 */
	public Player(Handler handler, float x, float y, ID id, int width, int height, String filepath) {
		super(handler, x, y, width, height, 1, id);
		this.maxHealth = 100;
		this.name = "Default";
		stats = new Stats(this, maxHealth, 1);
		stats.setDmgMod(0.05f);
		damaged = false;
		npc = false;
		inventory = new Inventory(this);
		walking = new Animation[5];
		bounds.x = (int)(x + width/6);
		bounds.y = (int)(y + 3*height/4);
		bounds.width = 20;
		bounds.height = height/4 - 1;
		direction = 1;
		invSlots = 20;
		
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
		
		// Attacks
		this.attackA = new Attack(600, 20, animation, 25, 25, "Default");
		this.attackW = new Attack(800, 25, animation, 25, 25, "Uh..");
		this.attackD = new Attack(600, 15, animation, 35, 35, "Thrust");
		this.attackS = new Attack(1000, 35, animation, 20, 20, "Charge");
	}

	/**
	 * Ticks the Player - checking KeyInput, animation, movement, and GameCamera
	 */
	public void tick() {
		getInput();
		move();
		animation.tick();
		y = Utils.clamp((int)y, 0, handler.getWorld().getMap().getHeight() * Tile.TILE_HEIGHT - height);
		x = Utils.clamp((int)x, 0, handler.getWorld().getMap().getWidth() * Tile.TILE_WIDTH - width);
		handler.getGameCamera().centerOnEntity(this);
		if(isDamaged()){
			if(damageTime > 0){
				damageTime--;
			}
			if(damageTime <= 0){
				setDamaged(false);
				damageTime = 20;
			}
		}
		stats.tick();
	}

	/**
	 * Checks player Input via Keyboard and adjusts movement and animation
	 */
	private void getInput(){
		// Movement
		movement(handler.getKeyInput().up, handler.getKeyInput().down, handler.getKeyInput().left, handler.getKeyInput().right);
		
		// Special Commands
		if(handler.getKeyInput().E){
			timerTwo += System.currentTimeMillis() - otherTime;
			otherTime = System.currentTimeMillis();
			if(timerTwo > 100){
				stats.heal(5);
				timerTwo = 0;
			}
		}
		else if(handler.getKeyInput().Q){
			timer += System.currentTimeMillis() - lastTime;
			lastTime = System.currentTimeMillis();
			if(timer > 100){
				stats.damage(2);
				timer = 0;
			}
		}
		else if(handler.getKeyInput().R){
			timerTwo += System.currentTimeMillis() - otherTime;
			otherTime = System.currentTimeMillis();
			if(timerTwo > 100){
				stats.addExperience(100);
				timerTwo = 0;
			}
		}
		
		/*
		// Pause
		boolean pause = false;
		if(handler.getKeyInput().space){
			
			long tempTimer = 0;
			long tempLast = 0;
			
			pause = true;
			spaceTimer += System.currentTimeMillis() - spaceLast;
			spaceLast = System.currentTimeMillis();
			if(spaceTimer > 100){
				spaceTimer = 0;
				spaceLast = System.currentTimeMillis();
				while(pause){
					System.out.println("PAUSED");
					handler.getKeyInput().tick();
					if(handler.getKeyInput().space){
						spaceTimer += System.currentTimeMillis() - spaceLast;
						spaceLast = System.currentTimeMillis();
						if(spaceTimer > 100){
							pause = false;
							spaceTimer = 0;
						}
					}
				}
			}
		}
		*/
		
		//Attacking
		attacking(handler.getKeyInput().A, attackA);
		attacking(handler.getKeyInput().W, attackW);
		attacking(handler.getKeyInput().S, attackS);
		attacking(handler.getKeyInput().D, attackD);
		
		//Inventory
		if(handler.getKeyInput().I){
			invTimer += System.currentTimeMillis() - invLast;
			invLast = System.currentTimeMillis();
			if(invTimer > 150){
				if(!handler.getWorld().getHUD().isInv()){
					handler.getWorld().getHUD().setInv(true);
				}else{
					handler.getWorld().getHUD().setInv(false);
				}
				invTimer = 0;
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
		}else{
			//g.drawImage(sprite, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), null);
		}
		stats.render(g);
		//g.setColor(Color.RED);
		//g.drawRect(attackBounds.x - (int)handler.getGameCamera().getxOffset(), attackBounds.y - (int)handler.getGameCamera().getyOffset(), attackBounds.width, attackBounds.height);
	}
	
	
	/**
	 * If an arrow key is pressed, changes Player velocity
	 * @param b if any arrow key is pressed
	 */
	private void movement(boolean up, boolean down, boolean left, boolean right){
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
	 * Does the attacking stuff
	 * @param b if the attack's key is pressed
	 * @param attack which attack
	 */
	private void attacking(boolean b, Attack attack){
		attackTimer += System.currentTimeMillis() - attackTime;
		attackTime = System.currentTimeMillis();
		if(b){
			if(attackTimer > attack.getSpeed()){
				if(!attack(attack)){
					stats.getList().add(new BouncyText(handler, "Miss!", Color.WHITE, (int)x, (int)y));
				}
				attackTimer = 0;
			}
		}
	}
	
	public int getInvSlots(){
		return invSlots;
	}

	/**
	 * Increases the Player's inventory slots by the given amount
	 * @param amount Amount to increase the inventory by
	 */
	public void increaseInvSlots(int amount){
		invSlots += amount;
	}
	
	/**
	 * Sets the timer variable to 0
	 */
	public void resetTimer(){
		timer = 0;
	}
	
	public Inventory getInventory(){
		return inventory;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String text){
		name = text;
	}
}
