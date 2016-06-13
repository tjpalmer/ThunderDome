package virassan.world.maps;

import virassan.gfx.Assets;
import virassan.main.Handler;

/**
 * Creates the Tile objects to be loaded
 * @author Virassan
 *
 */
public class Tiles {
	
	public static Tile grass, water;
	
	public Tiles(Handler handler) {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Initializes Tiles //TODO: where do I wanna call this method? Prob in map?
	 */
	public static void init(){
		grass = new Tile(Assets.grass, 0, false);
		water = new Tile(Assets.water, 1, true);
	}

}
