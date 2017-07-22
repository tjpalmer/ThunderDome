package virassan.gfx.hud;

import java.awt.*;
import java.awt.image.BufferedImage;

import virassan.utils.Utils;

public class ItemPickUp {

	private BufferedImage image;
	private float lifeSpan;
	private boolean live;
	private float x, y;
	private float alpha;
	
	public ItemPickUp(BufferedImage image) {
		this.image = image;
		lifeSpan = 1F;
		live = true;
		alpha = 0;
	}

	public void tick(){
		if(lifeSpan > 0){
			image = Utils.transparency(image, (int)alpha);
			alpha = Utils.clamp((float)(alpha + 0.05F), 0F, 255F);
			lifeSpan -= 0.01F;
		}else{
			live = false;
		}
	}
	
	public void render(Graphics g){
		if(live){
			g.drawImage(image, (int)x, (int)y, null);
		}
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public boolean isLive(){
		return live;
	}
	
	public BufferedImage getImage(){
		return image;
	}
}
