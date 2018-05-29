package virassan.entities.creatures.enemies;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import virassan.entities.creatures.Attack;
import virassan.entities.creatures.Creature;
import virassan.entities.creatures.utils.Move;
import virassan.gfx.Animation;
import virassan.gfx.hud.BouncyText;
import virassan.items.Drop;
import virassan.main.Handler;
import virassan.utils.Vector2F;

public abstract class Enemy extends Creature{

	public static final int SOLDIER_WIDTH = 32, SOLDIER_HEIGHT = 32;
	
	public static final HashMap<String, EnemyType> enemyIDs = new HashMap<String, EnemyType>();
	static{
		enemyIDs.put("S_001", new EnemyType("S_001", 3, 0.75f, 32, 32, "_green.png"));
		enemyIDs.put("S_002", new EnemyType("S_002", 20, 0.045f, 32, 32, "_pink.png"));
		enemyIDs.put("S_003", new EnemyType("S_003", 5, 0.10f, 32, 32, "_blue.png"));
	};
	
	
	protected EnemySpecies species;
	protected EnemyType type;
	protected int exp;
	protected Drop[] drops;
	protected long timer;
	private long lastTime;
	private long attackTimer, lastAttack;
	private int ranDir;
	protected int ranInterval;

	
	//Animation Shit
	protected Animation[] walking;
	protected Animation animation;
	protected Animation walkLeft, walkRight, walkUp, walkDown;
	protected Attack defaultAttack;
	protected int aggroDistance;
	
	protected String enemyID;
	
	public Enemy(Handler handler, String name, String enemyID, float x, float y, int level, EnemySpecies species) {
		super(handler, name, x, y, enemyIDs.get(enemyID).getWidth(), enemyIDs.get(enemyID).getHeight(), level);
		this.enemyID = enemyID;
		type = enemyIDs.get(enemyID);
		this.species = species;
		this.npc = true;
		exp = 75 * level;
		drops = new Drop[10];
		ranDir = new Random().nextInt(7);
		timer = 0;
		lastTime = System.currentTimeMillis();
	}
	
	public boolean attack(){
		attackTimer += System.currentTimeMillis()-lastAttack;
		lastAttack = System.currentTimeMillis();
		boolean isAttack = false;
		attackRange = defaultAttack.getRange();
		if(this.isAggroDistance()){
			if(attackTimer >= defaultAttack.getSpeed()){
				isAttack = super.attack(defaultAttack);
				if(!isAttack){
					stats.getList().add(new BouncyText(handler, "Miss!", Color.WHITE, (int)vector.dX, (int)vector.dY));
				}
				attackTimer = 0;
			}
		}
		return isAttack;
	}
	
	public float getPlayerDist(){
		return (float)Math.sqrt((double)(Math.pow(vector.dX - handler.getPlayer().getX(), 2) + Math.pow(vector.dY - handler.getPlayer().getY(), 2)));
	}
	
	public boolean isAggroDistance(){
		if(getPlayerDist() <= aggroDistance){
			return true;
		}
		return false;
	}
	
	public void move(double delta){
		isMoving = false;
		timer += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		int dir = -1;
		if(this.isAggroDistance() || this.getStats().getAggro()){
			dir = this.getCenter().angleToDirection(this.getCenter().getAngle(handler.getPlayer().getCenter()));
			isMoving = Move.move(this, speed, dir, (float)delta);
			if(isMoving){
				direction = dir;
			}
		}
		if(dir < 0){
			if(timer > ranInterval){
				ranDir = new Random().nextInt(12);
				ranInterval = new Random().nextInt(200)+1400;
				timer = 0;
			}
			if(ranDir < 9){
				isMoving = Move.move(this, speed, ranDir, (float)delta);
				if(isMoving){
					direction = ranDir;
				}
			}
		}
		animation = walking[direction];
		animation.start();
	}
	
	public EnemySpecies getSpecies(){
		return species;
	}
	
	public void droppedItems(){
		for(int i = 0; i < drops.length; i++){
			if(drops[i].isDropped()){
				int xran = new Random().nextInt(32);
				int yran = new Random().nextInt(32);
				drops[i].setPos(new Vector2F(vector.dX + xran, vector.dY + yran));
				System.out.println("Message: Enemy_droppedItems Item Pos: " + drops[i].getPos());
				handler.getItemManager().addItem(drops[i]);
			}
		}
	}
	
	public void setDefaultAttack(Attack attack){
		defaultAttack = attack;
	}
	
	public void gainExp(){
		if(isDead){
			handler.getPlayer().getStats().addExperience(exp);
		}
	}

	@Override
	public void unPause(){
		//resets all Timers
		lastAttack = System.currentTimeMillis();
		lastTime = System.currentTimeMillis();
	}
	
	public int getAggroDist(){
		return aggroDistance;
	}
	
	public boolean EnemyDeath(){
		if(stats.getHealth() <=0){
			isDead = true;
			droppedItems();
			gainExp();
			handler.getPlayer().addKillList(this);
			return true;
		}
		return false;
	}
	
	public String getEnemyID(){
		return enemyID;
	}
	
}
