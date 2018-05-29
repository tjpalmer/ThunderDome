package virassan.gfx.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import virassan.entities.Entity;
import virassan.main.Handler;
import virassan.utils.Utils;

public class EventText {
	private float lifeSpan = 100000;
	private boolean live;
	private Handler handler;
	private String text;
	private Color color;
	private Entity entity;
	private int opacity;
	private float x, y;
	private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
	

	public EventText(Entity entity, Handler handler, String text, float x, float y) {
		this.handler = handler;
		this.entity = entity;
		live = true;
		this.text = text;
		this.x = x - ((text.length()/2)*5);
		this.y = y - 10;
		opacity = 255;
		this.color = new Color(255, 240, 0, opacity);
	}

	public void render(Graphics g){
		if(live){
			g.setFont(font);
			color = new Color(255, 240, 0, opacity);
			g.setColor(color);
			g.drawString(text, (int)x - (int)handler.getGameCamera().getxOffset(), (int)y - (int)handler.getGameCamera().getyOffset());
		}
	}
	
	public void tick(double delta){
		if(lifeSpan > 0){
			//TODO: Just testing the delta w/ lifeSpan
			lifeSpan -= (.001F/delta);
			opacity = Utils.clamp(opacity - 4, 0, 255);
		}else{
			live = false;
		}
		x = ((int)entity.getX() - ((text.length()/2) * 5));
		y = (int)entity.getY() - 10;
	}
	
	public boolean isLive(){
		return live;
	}
	
	public String toString(){
		return text;
	}
}
