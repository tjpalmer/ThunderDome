package virassan.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

import virassan.gfx.Assets;
import virassan.gfx.GameCamera;
import virassan.input.KeyInput;
import virassan.input.MouseInput;

/**
 * Creates the Game, complete with gameloop!
 * @author Virassan
 *
 */
public class Game implements Runnable{

	public static final int IMAGE_SIZE = 32;
	public static int TICK;
	private Display display;
	public String title;
	private Thread thread;
	private boolean running = false;
	private BufferStrategy bs;
	private Graphics g;
	
	public static Handler handler;
	
	//Input
	private KeyInput keyManager;
	private MouseInput mouseManager;
	
	/**
	 * Creates a new Game
	 * @param title Title of the Game/Window
	 * @param width window's width
	 * @param height window's height
	 */
	public Game(String title){
		this.title = title;
		keyManager = new KeyInput();
		mouseManager = new MouseInput();
	}
	
	/**
	 * To initialize Display, Handler, KeyListener, GameCamera, and World of the Game
	 */
	public void init(){
		display = new Display(title);
		display.getFrame().addKeyListener(keyManager);
		display.getFrame().addMouseListener(mouseManager);
		display.getFrame().addMouseMotionListener(mouseManager);
		display.getCanvas().addMouseListener(mouseManager);
		display.getCanvas().addMouseMotionListener(mouseManager);
		handler = new Handler(this);
		Assets.init();
	}
	
	/**
	 * updates the game stuff
	 */
	public void tick(double delta){
		keyManager.tick(delta);
		mouseManager.tick(delta);
		handler.tick(delta);
	}
	
	/**
	 * updates the graphics/display stuff
	 */
	public void render(){
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null){
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		//Clear Screen
		g.clearRect(0, 0, handler.getWidth(), handler.getHeight());
		//Draw Here!
		//((Graphics2D)g).scale(((float)Display.HEIGHT * (16.0f / 9.0f)) / 1024.0f, ((float)Display.HEIGHT) / 520.0f);
		g.setColor(Color.white);
		g.fillRect(0, 0, handler.getWidth(), handler.getHeight());
		handler.render(g);
		
		//End Drawing!
		bs.show();
		g.dispose();
	}
	
	/**
	 * Game loop
	 */
	public void run(){
		init();
		int fps = 60;
		double timePerTick = 1000000000 / fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0; 
		
		while(running){
			now = System.nanoTime();
			delta += (now - lastTime)/timePerTick;
			timer += now - lastTime;
			lastTime = now;
			
			if(delta >= 1){
				tick(delta/1000000000);
				ticks++;
				delta --;
			}
			render();
			if(timer >= 1000000000){
				TICK = ticks;
				ticks = 0;
				timer = 0;
			}
		}
		stop();
	}
	
	public void close(){
		display.restoreScreen();
		System.exit(0);
		
	}
	
	// GETTERS AND SETTERS
	public Display getDisplay(){
		return display;
	}
	
	public int getDisplayWidth(){
		return display.getWidth();
	}
	
	public int getDisplayHeight(){
		return display.getHeight();
	}
	
	public KeyInput getKeyInput(){
		return keyManager;
	}
	
	public MouseInput getMouseInput(){
		return mouseManager;
	}
	
	/**
	 * Creates new Thread
	 */
	public synchronized void start(){
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start(); //calls the run() method above
	}
	
	/**
	 * Stops/Ends the Thread
	 */
	public synchronized void stop(){
		if(!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
