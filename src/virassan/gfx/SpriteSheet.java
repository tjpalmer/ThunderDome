package virassan.gfx;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Creates a SpriteSheet object that can be parsed into smaller pieces
 * @author Virassan
 *
 */
public class SpriteSheet {

	private BufferedImage sheet;
	
	public SpriteSheet(BufferedImage sheet){
		this.sheet = sheet;
		
	}
	
	/**
	 * Returns the subImage/desired Sprite from the Sprite Sheet
	 * @param x x coord of spritesheet
	 * @param y y coord of spritesheet
	 * @param width width of sprite image
	 * @param height height of sprite image
	 * @return - the subimage as BufferedImage
	 */
	public BufferedImage sprite(int x, int y, int width, int height){
		return sheet.getSubimage(x,y,width,height);
	}

}
