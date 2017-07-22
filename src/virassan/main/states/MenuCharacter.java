package virassan.main.states;

import java.awt.Graphics;

import virassan.entities.creatures.player.Player;
import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.MouseInput;
import virassan.main.Handler;

public class MenuCharacter {

	private Handler handler;
	private Player player;
	private MouseInput mouseInput;
	private KeyInput keyInput;
	
	
	public MenuCharacter(Handler handler) {
		this.handler = handler;
		mouseInput = handler.getMouseInput();
		keyInput = handler.getKeyInput();
		
	}

	
	public void render(Graphics g){
		
	}
	
	
	public void tick(){
		player = handler.getPlayer();
		drag();
		if(!mouseInput.getLeftClicks().isEmpty()){
			leftClick();
		}
		if(!mouseInput.getRightClicks().isEmpty()){
			rightClick();
		}
		hover();
		HUDManager.MENUTIMER += System.currentTimeMillis() - HUDManager.MENULAST;
		HUDManager.MENULAST = System.currentTimeMillis();
	}
	
	public void leftClick(){
		
	}
	
	public void rightClick(){
		
	}
	
	public void drag(){
		
	}
	
	public void hover(){
		
	}
}
