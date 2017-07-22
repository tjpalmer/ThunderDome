package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.main.Display;
import virassan.main.Handler;

public class MenuLevelUp {

	private Handler handler;
	private MouseInput mouseInput;
	private KeyInput keyInput;
	
	private int traitAmounts[];
	private Rectangle traitButtons[][];
	
	public MenuLevelUp(Handler handler) {
		this.handler = handler;
		mouseInput = handler.getMouseInput();
		keyInput = handler.getKeyInput();
		traitButtons = new Rectangle[5][2];
		int x = (Display.WIDTH/2) + 250 - 150 - 40;
		int y = (Display.WIDTH/2) - 75 - 276;
		for(int i = 0; i < traitButtons.length; i++){
			traitButtons[i][0] = new Rectangle(x, y, 25, 25);
			traitButtons[i][1] = new Rectangle(x+80, y, 25, 25);
			y += 30;
		}
	}

	public void render(Graphics g){
		//TODO: finish level up Render
		g.setColor(Color.GRAY);
		g.setFont(new Font("Verdana", Font.PLAIN, 18));
		int h = (Display.HEIGHT/2) - 85;
		int w = (Display.WIDTH/2) - 150;
		g.fillRect(w, h, 400, 170);
		g.setColor(Color.WHITE);
		String traits[] = new String[]{"Strength", "Resilience", "Dexterity", "Intelligence", "Charisma"};
		int x = w + 15;
		int y = h;
		for(int i = 0; i < traits.length; i++){
			g.fillRect(w+400 - 150, y+10, 25, 25);
			y += 30;
			g.drawString(traits[i], x, y);
		}
		g.setColor(Color.BLACK);
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		for(int i = traitAmounts.length-1; i >= 0; i--){
			g.drawString("" + traitAmounts[i], x + 400 - 152 - (g.getFontMetrics().stringWidth("" + traitAmounts[i])/2), y-2);
			y-= 30;
		}
		
		g.setColor(Color.BLUE);
		for(int i = 0; i < traitButtons.length; i++){
			g.drawRect(traitButtons[i][0].x, traitButtons[i][0].y, traitButtons[i][0].width, traitButtons[i][0].height);
			g.drawRect(traitButtons[i][1].x, traitButtons[i][1].y, traitButtons[i][1].width, traitButtons[i][1].height);
		}
	}
	
	public void tick(){
		traitAmounts = new int[]{handler.getPlayer().getTraits().getStrength(), handler.getPlayer().getTraits().getResilience(), handler.getPlayer().getTraits().getDexterity(), handler.getPlayer().getTraits().getIntelligence(), handler.getPlayer().getTraits().getCharisma()};
		if(!mouseInput.getLeftClicks().isEmpty()){
			leftClick();
		}
		if(!mouseInput.getRightClicks().isEmpty()){
			rightClick();
		}
		HUDManager.MENUTIMER += System.currentTimeMillis() - HUDManager.MENULAST;
		HUDManager.MENULAST = System.currentTimeMillis();
		if(HUDManager.MENUTIMER > HUDManager.MENUWAIT){
			if(keyInput.T){
				handler.setState(States.World);
				handler.getEntityManager().setPaused(false);
				HUDManager.MENUTIMER = 0;
			}
		}
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(clicks.element() != null){
			outer : {
				while(clicks.element() != null){
					Point head = clicks.poll().getObject();
					double x = head.getX();
					double y = head.getY();
					for(int i = 0; i < traitButtons.length; i++){
						if(new Rectangle((int)x, (int)y, 1, 1).intersects(traitButtons[i][0])){
							traitAmounts[i] -= 1;
							break outer;
						}else if(new Rectangle((int)x, (int)y, 1, 1).intersects(traitButtons[i][1])){
							traitAmounts[i] += 1;
							break outer;
						}
					}
				}
			}
		}
	}
	
	public void rightClick(){
		LinkedQueue clicks = mouseInput.getRightClicks();
		if(handler.getEntityManager().getPaused()){

		}
	}
	
}
