package virassan.gfx.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import virassan.main.Handler;
import virassan.utils.Utils;

public class SkillText {
	
	private float lifeSpan = 1;
	private boolean live;
	private Handler handler;
	private String text;
	private Color color;
	private int opacity;
	private int x, y;
	private final Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

	public SkillText(Handler handler, String text, Color color, int x, int y) {
		this.handler = handler;
		this.text = text;
		this.x = x;
		this.y = y;
		opacity = 255;
		live = true;
		this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
	}
	
	public void render(Graphics g){
		if(live){
			g.setFont(font);
			this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
			g.setColor(color);
			g.drawString(text, x - (int)handler.getGameCamera().getxOffset(), y - (int)handler.getGameCamera().getyOffset());
		}
	}
	
	public void tick(){
		if(lifeSpan > 0){
			lifeSpan -= 0.01F;
			opacity = Utils.clamp(opacity - 5, 0, 255);
		}else{
			live = false;
		}
	}
	
	public boolean isLive(){
		return live;
	}
	
	public String toString(){
		return text;
	}
}
