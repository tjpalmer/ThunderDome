package virassan.main.states;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;

import virassan.input.KeyInput;
import virassan.input.MouseInput;
import virassan.main.Handler;
import virassan.utils.Utils;

public class LaunchMenu {

	private Handler handler;
	private MouseInput mouseInput;
	private KeyInput keyInput;
	
	private boolean isSave;
	
	public LaunchMenu(Handler handler) {
		this.handler = handler;
		mouseInput = handler.getMouseInput();
		keyInput = handler.getKeyInput();
		isSave = true;
	}
	
	public void render(Graphics g){
		if(isSave){
			g.setColor(Color.BLACK);
			g.drawString("To Start a New Game press ENTER", 200, 190);
			g.drawString("To Load the Last Save press SPACE", 200, 220);
		}
	}
	
	public void tick(){
		//TODO: figure out how to see if there's any JSON files in the "/saves/" folder
		//Supposed to say "hey if there's a JSON file in the "saves" folder, then ask if they want to Load a Game or Start New
		if(isSave){
			boolean saveFile = new File("c:\\Users\\Virassan\\Documents\\!ThunderDome\\ThunderDome\\ThunderDome\\res\\saves\\testsave2.json").isFile();
			if(saveFile){
				keyInput.tick();
				if(keyInput.enter){
					//TODO: create a New Game
					isSave = false;
					
				}else if(keyInput.space){
					Utils.loadGame(handler, "c:\\Users\\Virassan\\Documents\\!ThunderDome\\ThunderDome\\ThunderDome\\res\\saves\\testsave2.json");
					isSave = false;
					handler.setState(States.World);
				}
			}else{
				isSave = false;
			}
		}
	}

}
