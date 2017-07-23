package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import virassan.entities.creatures.player.Player;
import virassan.entities.creatures.utils.SkillTracker;
import virassan.gfx.Assets;
import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.MouseInput;
import virassan.main.Handler;

public class MenuSkills {

	private Handler handler;
	private Player player;
	private KeyInput keyInput;
	private MouseInput mouseInput;
	
	
	public MenuSkills(Handler handler) {
		this.handler = handler;
		keyInput = handler.getKeyInput();
		mouseInput = handler.getMouseInput();
	}

	
	public void render(Graphics g){
		//TODO: actually render skillbook stuff
		player = handler.getPlayer();
		g.drawImage(Assets.skillMenu, 0, 0, null);
		float y = 138;
		float x = 768;
		for(SkillTracker skill : player.getSkillBar()){
			if(skill != null){
				g.drawImage(skill.getSkillType().getIcon(), (int)x, (int)y, null);
				g.drawImage(Assets.skill_border,(int)x, (int)y, null);
				x += 74F;
			}
		}
		g.setFont(new Font("Verdana", Font.BOLD, 32));
		String inventory = "Inventory";
		String questlog = "Quest Log";
		String skillbook = "Skillbook";
		g.setColor(Color.WHITE);
		g.drawString(inventory, 125, 50);
		g.setColor(Handler.BLUE_VIOLET);
		g.drawString(skillbook, 640 - (g.getFontMetrics().stringWidth(skillbook) / 2), 50);
		g.setColor(Color.WHITE);
		g.drawString(questlog, 950, 50);
	}
	
	
	public void tick(double delta){
		player = handler.getPlayer();
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
		if(HUDManager.MENUTIMER > HUDManager.MENUWAIT){
			if(keyInput.K || keyInput.I || keyInput.L || keyInput.esc){
				HUDManager.MENUTIMER = 0;
				if(keyInput.K){
					handler.setState(States.World);
					handler.getEntityManager().setPaused(false);
				}else if(keyInput.I){
					handler.setState(States.MenuInventory);
				}else if(keyInput.esc){
					handler.setState(States.MenuSettings);
				}else if(keyInput.L){
					handler.setState(States.MenuQuest);
				}
			}
		}
	}
	
	public void hover(){
		
	}
	
	public void drag(){
		
	}
	
	public void leftClick(){
		
	}
	
	public void rightClick(){
		
	}
}
