package virassan.gfx.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import virassan.entities.Entity;
import virassan.main.Handler;
import virassan.utils.Utils;

public class EventText {
	private float lifeSpan = 1;
	private boolean live;
	private Handler handler;
	private String text;
	private Color color;
	private Entity entity;
	private int opacity;
	private int x, y;
	private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
	

	public EventText(Entity entity, Handler handler, String text, int x, int y) {
		this.handler = handler;
		this.entity = entity;
		live = true;
		this.text = text;
		this.x = x;
		x -= ((text.length()/2)*5);
		this.y = y - 10;
		opacity = 255;
		this.color = new Color(255, 240, 0, opacity);
	}

	public void render(Graphics g){
		if(live){
			g.setFont(font);
			color = new Color(255, 240, 0, opacity);
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
		x = (int)entity.getX() - ((text.length()/2) * 5);
		y = (int)entity.getY();
	}
	
	public boolean isLive(){
		return live;
	}
	
	public String toString(){
		return text;
	}
}
