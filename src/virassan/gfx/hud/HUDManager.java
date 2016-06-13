package virassan.gfx.hud;

import java.awt.Color;
import java.awt.Graphics;

import virassan.main.Display;
import virassan.main.Game;
import virassan.main.Handler;

/**
 * Heads Up Display Manager //TODO: HUD is supposed to manage display things that the Player CANNOT change
 * @author Virassan
 *
 */
public class HUDManager {
	
	private float waitTime = 300;
	private long lastTime;
	private long timer = 0;
	private Handler handler;
	private boolean isInv;
	
	public HUDManager(Handler handler){
		this.handler = handler;
		lastTime = System.currentTimeMillis();
		isInv = false;
	}

	
	public void tick(){
	}
	
	public void render(Graphics g){
		//Red Removed Health bar
		//TODO make it deplete faaster the closer it is to the HealthScale
		if(!handler.getPlayer().isDamaged()){
			timer += System.currentTimeMillis() - lastTime;
			if(handler.getPlayer().getStats().getDmgScale() > handler.getPlayer().getStats().getHealthScale()){
				if(timer > waitTime){
					handler.getPlayer().getStats().setDmgScale(handler.getPlayer().getStats().getDmgScale() - 0.01f);
					waitTime -= waitTime*.5f;
					if(waitTime <= 0){
						waitTime = 300;
					}
					lastTime = System.currentTimeMillis();
				}	
			}
		}else{
			waitTime = 300;
		}
		g.setColor(Color.RED);
		g.fillRect(10, 10, (int)(150 * handler.getPlayer().getStats().getDmgScale()), 25);
		
		//Health Bar
		g.setColor(Color.GREEN);
		g.fillRect(10, 10, (int)(150 * handler.getPlayer().getStats().getHealthScale()), 25);
		
		//Black Outline
		g.setColor(Color.BLACK);
		g.drawRect(9, 9, 25, 26);
		g.drawRect(34, 9, 25, 26);
		g.drawRect(59, 9, 25, 26);
		g.drawRect(84, 9, 25, 26);
		g.drawRect(109, 9, 25, 26);
		g.drawRect(134, 9, 25, 26);
		
		g.setColor(Color.WHITE);
		g.drawString(handler.getPlayer().getX() + " " + handler.getPlayer().getY(), 5, 50);
		g.drawString(handler.getPlayer().getName(), 5, 65);
		g.drawString("Level: " + handler.getPlayer().getLevel(), 5, 80);
		g.drawString("Ticks: " + Game.TICK, Display.WIDTH - 60, 20);
		
		//Commands
		g.setColor(Color.WHITE);
		g.drawString("Q : Damage the Player", 10, Display.HEIGHT - 64);
		g.drawString("E : Heal the Player", 10, Display.HEIGHT - 50);
		g.drawString("A : Attack an Enemy", 10, Display.HEIGHT - 36);
		g.drawString("R : Gain Exp", 10, Display.HEIGHT - 22);
		g.drawString("Arrow Keys : Movement", 10, Display.HEIGHT - 8);
	
		//Inventory
		if(isInv){
			handler.getPlayer().getInventory().render(g);
		}
	
	}
	
	public Handler getHandler(){
		return handler;
	}
	
	public boolean isInv(){
		return isInv;
	}
	
	public void setInv(boolean b){
		isInv = b;
	}
}
