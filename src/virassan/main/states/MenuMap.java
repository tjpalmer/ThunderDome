package virassan.main.states;

import java.awt.Graphics;
import java.awt.Rectangle;

import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.main.Handler;

public class MenuMap {
	
	private Handler handler;
	private MouseInput mouseInput;
	private KeyInput keyInput;
	
	private boolean isDragged;

	public MenuMap(Handler handler) {
		this.handler = handler;
		mouseInput = handler.getMouseInput();
		keyInput = handler.getKeyInput();

	}

	public void render(Graphics g){
		
	}
	
	public void tick(double delta){
		drag();
		if(!mouseInput.getLeftClicks().isEmpty()){
			leftClick();
		}
		if(!mouseInput.getRightClicks().isEmpty()){
			rightClick();
		}
		hover();
		HUDManager.MENUTIMER += (System.currentTimeMillis() - HUDManager.MENULAST) * delta;
		HUDManager.MENULAST = System.currentTimeMillis() * (long)delta;
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(!mouseInput.isDragged()){
			if(clicks.element() != null){
				outer: {
					while(clicks.element() != null){
						
					}
				}
			}
		}
	}
	
	public void rightClick(){
		LinkedQueue clicks = mouseInput.getRightClicks();
		if(clicks.element() != null){
			outer: {
				while(clicks.element() != null){
				
				}
			}
		}
	}
	
	public void hover(){
		if(!mouseInput.isDragged()){
			Rectangle curMouse = mouseInput.getMouseBounds();
		}
	}
	
	public void drag(){
		if(mouseInput.isDragged()){
			isDragged = true;
			Rectangle startRect =  new Rectangle(mouseInput.getStartDrag().x, mouseInput.getStartDrag().y, 1, 1);
			outer: {
				
			}
		}else{
			if(isDragged){
				Rectangle startRect, endRect;
				try{
					startRect =  new Rectangle(mouseInput.getStartDrag().x, mouseInput.getStartDrag().y, 1, 1);
				}catch(NullPointerException e){
					startRect = new Rectangle(0, 0, 0, 0);
				}try{
					endRect = new Rectangle(mouseInput.getEndDrag().x, mouseInput.getEndDrag().y, 1, 1);
				}catch(NullPointerException e){
					endRect = new Rectangle(0, 0, 0, 0);
				}
			}
		}
	}
}
