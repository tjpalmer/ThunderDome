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
	
	public void tick(double delta){
		if(isSave){
			boolean saveFile = new File("res\\saves\\testsave2.json").isFile();
			if(saveFile){
				keyInput.tick(delta);
				if(keyInput.enter){
					//TODO: create a New Game
					isSave = false;
					
				}else if(keyInput.space){
					String filepath = "";
					File[] files = Utils.fileFinder("res\\saves\\", ".json");
					filepath = files[files.length - 1].toString();
					Handler.LAUNCHLOAD.setFilepath(filepath);
					isSave = false;
					handler.setState(States.LaunchLoad);
				}
			}else{
				isSave = false;
			}
		}
	}

}
