package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import org.json.simple.JSONObject;

import virassan.main.Display;
import virassan.main.Handler;
import virassan.utils.DatabaseConnect;
import virassan.utils.MapRead;

public class LoadMap {

	private final String[] loadMessages = new String[]{"Loading Map File", "Loading Map Data", "Loading Textures", "Spawning NPCs", "Spawning Creatures", "Starting World"};
	
	private String curLoadMes;
	private Handler handler;
	private int curLoadCount;
	private JSONObject json;
	private boolean isLoadSave;
	
	public LoadMap(Handler handler) {
		this.handler = handler;
		curLoadCount = 0;
		isLoadSave = false;
		curLoadMes = loadMessages[0];
	}

	public void setNewSpawn(int x, int y){
		handler.getPlayer().setX((float)x);
		handler.getPlayer().setY((float)y);
	}
	
	
	public void render(Graphics g){
		g.setColor(Handler.LAVENDER);
		g.fillRect(0,0, handler.getWidth(), handler.getHeight());
		g.setColor(Color.BLACK);
		g.setFont(new Font("Verdana", Font.BOLD, 18));
		g.drawString(curLoadMes, 300, 350);
	}
	
	public void tick(double delta){
		String mapID = handler.getMapID();
		String filepath = "";
		filepath = "res\\worlds\\maps\\" + DatabaseConnect.getMapFilename(mapID);
		
		if(curLoadCount == 0){
			json = MapRead.loadFile(filepath);
			curLoadCount++;
		}else if(curLoadCount == 1){
			MapRead.loadBasic(handler, json, filepath);
			curLoadCount++;
		}else if(curLoadCount == 2){
			MapRead.loadStatics(handler, json);
			curLoadCount++;
		}else if(curLoadCount == 3){
			MapRead.loadNPCs(handler, json);
			curLoadCount++;
		}else if(curLoadCount == 4){
			MapRead.loadEnemies(handler, json);
			curLoadCount++;
		}else if(curLoadCount == 5){
			curLoadCount = 0;
			if(isLoadSave){
				handler.setState(States.LaunchLoad);
			}else{
				handler.setState(States.GameState);
			}
		}
		curLoadMes = loadMessages[curLoadCount];
		
	}
	
	public void setIsLoadSave(boolean b){
		isLoadSave = b;
	}
	
}
