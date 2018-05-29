package virassan.gfx;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
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
			BufferedImage original = ImageIO.read(ImageLoader.class.getResource(filepath));
			return original; //returns the buffered image
		}catch(FileNotFoundException f){
			System.out.println("Error Message: ImageLoader_loadImage FILE NOT FOUND: " + filepath);
			f.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error Message: ImageLoader_loadImage IOException filepath: " + filepath);
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	
}
