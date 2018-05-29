package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;

import org.json.simple.JSONObject;

import virassan.entities.creatures.npcs.Quest;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.utils.DatabaseConnect;
import virassan.utils.SaveRead;

public class LaunchLoad {

	private Handler handler;
	private JSONObject json;
	private String filepath;
	private String mapID;
	private int curLoadCount;
	private String curLoadMes;
	
	private final String[] loadMessages = new String[] {"Start Loading Player", "Loading Equips", "Loading Inventory", "Loading Skills",
			"Loading Quests", "Loading Buffs", "Loading SkillBar", "Loading NPC Interaction"};
	
	public LaunchLoad(Handler handler) {
		this.handler = handler;
		curLoadCount = 0;
		curLoadMes = "";
	}

	public void render(Graphics g){
		if(curLoadCount > 2){
			g.setColor(Handler.LAVENDER);
			g.fillRect(0,0, handler.getWidth(), handler.getHeight());
			g.setColor(Color.BLACK);
			g.setFont(new Font("Verdana", Font.BOLD, 18));
			g.drawString(curLoadMes, 300, 350);
		}
	}
	
	public void tick(double delta){
		if(curLoadCount == 0){
			json = SaveRead.loadFile(filepath);
			curLoadCount++;
		}
		
		else if(curLoadCount == 1){
			mapID = SaveRead.loadMapID(json);
			System.out.println("Message: LaunchLoad_tick MAPID IS: " + mapID);
			if(mapID == null){
				System.out.println("Error Message: LaunchLoad_tick MAPID IS NULL");
			}
			handler.setMapID(mapID);
			curLoadCount++;
		}
		
		else if(curLoadCount == 2){
			curLoadCount++;
			System.out.println("Update Message: LaunchLoad_tick Loading Map within LoadMap");
			Handler.LOADMAP.setIsLoadSave(true);
			handler.setState(States.LoadMap);
			curLoadMes = loadMessages[0];
		}
		
		else if(curLoadCount == 3){
			handler.setPlayer(SaveRead.loadBasicPlayer(handler, json));
			Handler.GAMESTATE.setPlayer(handler.getPlayer());
			curLoadCount++;
			curLoadMes = loadMessages[1];
		}
		
		else if(curLoadCount == 4){
			SaveRead.loadEquip(handler.getPlayer(), json);
			curLoadCount++;
			curLoadMes = loadMessages[2];
		}
		
		else if(curLoadCount == 5){
			SaveRead.loadInventory(handler.getPlayer(), json);
			curLoadCount++;
			curLoadMes = loadMessages[3];
		}
		
		else if(curLoadCount == 6){
			SaveRead.loadSkills(handler.getPlayer(), json);
			curLoadCount++;
			curLoadMes = loadMessages[4];
		}
		
		else if(curLoadCount == 7){
			SaveRead.loadQuests(handler.getPlayer(), json);
			curLoadCount++;
			curLoadMes = loadMessages[5];
		}
		
		else if(curLoadCount == 8){
			SaveRead.loadBuffs(handler.getPlayer(), json);
			curLoadCount++;
			curLoadMes = loadMessages[6];
		}
		
		else if(curLoadCount == 9){
			SaveRead.loadSkillBar(handler.getPlayer(), json);
			curLoadCount++;
			curLoadMes = loadMessages[7];
		}
		
		else if(curLoadCount == 10){
			SaveRead.loadNPCs(handler.getPlayer(), json);
			curLoadCount++;
		}
		
		else if(curLoadCount == 11){
			System.out.println("Update Message: LaunchLoad_tick Done loading Save and Map.");
			handler.setState(States.GameState);
		}
	}
	
	public void setFilepath(String filepath){
		this.filepath = filepath;
	}
	
}
