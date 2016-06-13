package virassan.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Loads Images to be read and done something with
 * @author Virassan
 *
 */
public class ImageLoader {
	
	/**
	 * Loads the BufferedImage and reads it
	 * @param filepath file of the BufferedImage
	 * @return the BufferedImage
	 */
	public static BufferedImage loadImage(String filepath){
		try {
			return ImageIO.read(ImageLoader.class.getResource(filepath)); //returns the buffered image
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	
}
