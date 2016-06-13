package virassan.states;

import java.awt.Color;
import java.awt.Graphics;

import virassan.entities.creatures.Player;
import virassan.gfx.hud.HUDManager;
import virassan.main.Display;
import virassan.main.Game;
import virassan.main.Handler;
import virassan.world.World;

/**
 * Creates a GameState
 * @author Virassan
 *
 */
public class GameState extends State{

	
	private Game game;
	private Handler handler;
	public static World testWorld;
	private Player player;
	private HUDManager hud;
	
	public GameState(StateManager sm, Handler handler, Game game){
		super(sm);
		testWorld = new World(handler);
		
		//TODO: Should init World?
		handler.setWorld(testWorld);
		hud = new HUDManager(handler);
		
		//TODO: What's this nonsense below? Pretty sure I create the Player in the World class
		//player = new Player(handler, 0, 0, ID.Player, "/textures/jelly/blob_spritesheet_testing.png");
		//handler.addObject(new Player(handler, 0, 0, ID.Player, "/textures/jelly/blob_spritesheet_testing.png"));
		
	}
	
	public void tick() {
		sm.tick();
		//testWorld.tick();
		//player.tick();
		//handler.tick();
		
	}

	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
		//testWorld.render(g);
		//Game.hud.render(g);
		sm.render(g);
		//player.render(g);
		//handler.render(g);
		
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	
}
