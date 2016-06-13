package virassan.main;

/**
 * Launches the Thing! 
 * @author Virassan
 *
 */
public class Launcher {

	public static void main(String[] args)
	{
		Game game = new Game("TEST GAME!", Display.WIDTH, Display.HEIGHT);
		game.start();
		
	}
}
