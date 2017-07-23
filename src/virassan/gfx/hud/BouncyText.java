package virassan.gfx.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

import virassan.main.Handler;

public class BouncyText {

	private float currentUp;
	private float lifeTime = 1;
	private float maxUp = 2.5f;
	
	private Handler handler;
	
	private int x, y;
	private String text;
	private Color color;
	
	private boolean left, right, jump, live;
	
	public BouncyText(Handler handler, String text, Color color, int x, int y) {
		this.text = text;
		this.handler = handler;
		this.x = x;
		this.y = y;
		this.color = color;
		live = true;
		Random gen = new Random();
		int ran = gen.nextInt(2);
		if(ran == 0){
			right = true;
			left = false;
		}
		else if(ran == 1){
			right = false;
			left = true;
		}
	}
	
	
	public void tick(double delta){
		if(lifeTime > 0){
			lifeTime -= 0.01f*delta;
		}
		if(lifeTime <= 0){
			live = false;
		}
		if(jump){
			if(currentUp != maxUp){
				currentUp += 0.2f;
			}
			if(currentUp >= maxUp){
				jump = false;
			}
			if(right){
				x += currentUp * new Random().nextFloat() * maxUp * 2;
			}
			if(left){
				x -= currentUp * new Random().nextFloat() * maxUp * 2;
			}
		}else{
			if(currentUp != 0){
				currentUp -= 0.1f;
				y += currentUp;
			}
			if(currentUp <= 0){
				maxUp -= 0.2f;
				jump = true;
			}
		}
		
	}
	
	public void render(Graphics g){
		if(live){
			g.setFont(new Font(Font.SERIF, Font.PLAIN, 12));
			g.setColor(color);
			g.drawString(text, x - (int)handler.getGameCamera().getxOffset(), y - (int)handler.getGameCamera().getyOffset());
		}
	}

	public boolean getLive(){
		return live;
	}
	
}
