package virassan.utils;

import java.awt.Point;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import virassan.entities.creatures.enemies.EnemySpecies;
import virassan.entities.creatures.npcs.Dialog;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.creatures.npcs.Quest;
import virassan.items.Item;
import virassan.main.Handler;
import virassan.world.maps.Tile;

public class DatabaseConnect {
	
	public static String getMapFilename(String mapID){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM maps WHERE map_id=" + "\"" + mapID + "\"";
		String filename = null;
		
		try{
			conn = connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				return rs.getString("filename") + ".json";
			}
				
		}catch(SQLException e){
			System.out.println("Error Message: DatabaseConnect_getMapFilename SQLEXCEPTION.");
			System.out.println(e.getMessage());
		}
		
		return filename;
	}
	
	
	/**
	 * Creates a new NPC Entity from the database and returns it
	 * @param handler pass in the game handler
	 * @param npcID the ID of the npc to be created
	 * @return
	 */
	public static ArrayList<NPC> createNPC(Handler handler, ArrayList<String> npcIDs){
		ArrayList<NPC> npcs = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM npcs WHERE npc_id=" + "\"" + npcIDs.get(0) + "\"";
		for(int i = 1; i < npcIDs.size(); i++){
			sql = sql + " OR npc_id=" +  "\"" + npcIDs.get(i) + "\"";
		}
		
		NPC npc = null;
		
		try{
			conn = connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				
				
				// npc_id, name, map_id, map_coord_x, map_coord_y, img_filename, npc_type
				float x_coord = rs.getInt("map_coord_x") * Tile.TILE_WIDTH;
				float y_coord = rs.getInt("map_coord_y") * Tile.TILE_HEIGHT;
				npc = new NPC(handler, rs.getString("npc_id"), rs.getString("map_id"), x_coord, y_coord, 64, 64, 
						rs.getString("name"), rs.getString("img_filename"), rs.getString("npc_type"));
				System.out.println("Message: DatabaseConnect_createNPCs npc loc: " + (new Point((int)x_coord, (int)y_coord)));
				// create dialogs
				sql = "SELECT * FROM dialog WHERE npc_id=" + "\"" + rs.getString("npc_id") + "\"";
				Statement dialogStmt = conn.createStatement();
				ResultSet dialogSet = dialogStmt.executeQuery(sql);
				ArrayList<Dialog> dialogs = new ArrayList<>();
				while(dialogSet.next()){
					dialogs.add(dialog(dialogSet.getString("dialog_id"), conn));
				}
				npc.setDialog(dialogs);
				npcs.add(npc);
			}
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}finally{
			try{
				stmt.close();
			}catch(SQLException i){
				System.out.println("Error closing connections: stmt");
			}
		}
		if(npc == null){
			System.out.println("Error Message: DatabaseConnect_createNPC NPC IS NULL");
		}
		return npcs;
	}
	
	public static Quest getQuest(Handler handler, String questID){
		Connection conn = connect();
		try{
			return quest(handler, questID, conn);
		}catch(Exception e){
			
		}finally{
			try{
				conn.close();
			}catch(SQLException k){
				System.out.println("Error closing connections: conn");
			}
		}
		return null;
	}
	
	public static Quest quest(Handler handler, String questID, Connection conn){
		String sql = "SELECT * from quests where quest_id=" + "\"" + questID + "\"";
		ResultSet rs = null;
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			// chapter, quest_id, name, npc_start, npc_fin, descrip, reqs, rewards, start_dia, incomp_dia, comp_dia
			while(rs.next()){
				return new Quest(handler, rs.getString("quest_id"), rs.getString("name"), rs.getString("descrip"), 
						getRequirements(rs.getString("reqs")), getRewards(rs.getString("rewards")), 
						new ArrayList<String>(Arrays.asList(rs.getString("npc_start"), rs.getString("npc_fin"))),
						rs.getString("start_dia"), rs.getString("incomp_dia"), rs.getString("comp_dia"));
			}
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		
		
		return null;
	}
	
	public static HashMap<Object, Object> getRequirements(String text){
		HashMap<Object,Object> reqs = new HashMap<>();
		text = text.replace("[","");
		text = text.replace(" ", ""); //removes any accidental spaces
		text = text.replace("],","]"); //removes any commas between array items
		List<String> reqList = Arrays.asList(text.split("]"));
		
		// Iterate through the list now split by each requirement<String:Object,String:Object>
		for(int i = 0; i < reqList.size(); i++){
			//reqHashList separates the StringofType:Object from the amount:int 
			List<String> reqHashList = Arrays.asList(reqList.get(i).split(","));
			// iterates every 2 items because the 2nd item is USUALLY the amount of the Object of the first item
			for(int k = 0; k < reqHashList.size(); k+=2){
				//reqType separates the StringofType from the Object itself (ie separates "EnemySpecies" from "Slime"
				List<String> reqType = Arrays.asList(reqHashList.get(k).split(":"));
				switch(reqType.get(0)){
				case "EnemySpecies": 
					try{
						EnemySpecies enemy = EnemySpecies.valueOf(reqType.get(1).toUpperCase());
						int amount = Integer.parseInt(Arrays.asList(reqHashList.get(k+1).split(":")).get(1));
						reqs.put(enemy, amount);
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
				case "EnemyType":
					try{
						String enemyID = reqType.get(1).toUpperCase();
						int amount = Integer.parseInt(Arrays.asList(reqHashList.get(k+1).split(":")).get(1));
						reqs.put(enemyID, amount);
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
				case "item":
					try{
						Item item = Item.valueOf(reqType.get(1).toUpperCase());
						int amount = Integer.parseInt(Arrays.asList(reqHashList.get(k+1).split(":")).get(1));
						reqs.put(item, amount);
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
				}
			}
		}
		return reqs;
	}
	
	
	public static HashMap<Object, Integer> getRewards(String text){
		HashMap<Object,Integer> rewards = new HashMap<>();
		text = text.replace("[","");
		text = text.replace(" ", "");
		text = text.replace("],", "]");
		
		List<String> rewardsList = Arrays.asList(text.split("]"));
		
		for(int i = 0; i < rewardsList.size(); i++){
			
			List<String> rewardsHashList = Arrays.asList(rewardsList.get(i).split(","));
			
			for(int k = 0; k < rewardsHashList.size(); k++){
			
				List<String> rewardType = Arrays.asList(rewardsHashList.get(k).split(":"));
				
				switch(rewardType.get(0)){
				case "item":
					try{
						Item item = Item.valueOf(rewardType.get(1).toUpperCase());
						int amount = Integer.parseInt(Arrays.asList(rewardsHashList.get(k+1).split(":")).get(1));
						k++;
						rewards.put(item, amount);
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
				default:
					String object = rewardType.get(0);
					int amount = Integer.parseInt(rewardType.get(1));
					rewards.put(object, amount);
					break;
				}
			}
		}
		return rewards;
	}
	
	
	
	public static Dialog getDialog(String dialogID){
		Connection conn = connect();
		try{
			return dialog(dialogID, conn);
		}catch(Exception e){
			
		}finally{
			try{
				conn.close();
			}catch(SQLException k){
				System.out.println("Error closing connections: conn");
			}
		}
		return null;
	}
	
	public static Dialog dialog(String dialogID, Connection conn){
		// Dialog, Response's 1-4, Action1 type, action1 varchar, action1 int1, action1 int2, action1 boolean,
		// action2 type, action2 varchar, action2 int1, action2 int2, action2 boolean, 
		// req1 type, req1 info1, req1 info2, req2 type, req2 info1, req2 info2
		String sql = "Select * from dialog where dialog_id=" + "\"" + dialogID + "\"";
		ResultSet rs = null;
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String d_id = rs.getString("dialog_id");
				String text = rs.getString("dialog");
				String resp1_id = rs.getString("resp1_id");
				ArrayList<String> resps = new ArrayList<String>();
				ArrayList<Dialog> respDialogs =  new ArrayList<>();
				// responses
				if(rs.getString("resp1_id") != null){
					String resp2_id = rs.getString("resp2_id");
					String resp3_id = rs.getString("resp3_id");
					String resp4_id = rs.getString("resp4_id");
					resps.addAll(Arrays.asList(resp1_id, resp2_id, resp3_id, resp4_id));
					for(String r : resps){
						respDialogs.add(dialog(r, conn));
					}
				}
				// actions/events
				HashMap<String, ArrayList<Object>> actions = new HashMap<>();
				actions.put(rs.getString("act1_type"), new ArrayList<Object>(Arrays.asList(rs.getString("act1_vchar"), 
						rs.getInt("act1_int1"), rs.getInt("act1_int2"), rs.getBoolean("act1_bool"))));
				actions.put(rs.getString("act2_type"), new ArrayList<Object>(Arrays.asList(rs.getString("act2_vchar"), 
						rs.getInt("act2_int1"), rs.getInt("act2_int2"), rs.getBoolean("act2_bool"))));
				// requirements
				HashMap<String, ArrayList<Object>> reqs = new HashMap<>();
				reqs.put(rs.getString("req1_type"), new ArrayList<Object>(Arrays.asList(rs.getString("req1_info1"), rs.getString("req1_info2"))));
				reqs.put(rs.getString("req2_type"), new ArrayList<Object>(Arrays.asList(rs.getString("req2_info1"), rs.getString("req2_info2"))));
				return new Dialog(d_id, text, respDialogs, reqs, actions);
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}finally{
			try{
				stmt.close();
			}catch(SQLException i){
				System.out.println("Error closing connections: stmt");
			}
			try{
				rs.close();
			}catch(SQLException m){
				System.out.println("Error closing connections: rs");
			}
		}
		return null;
	}

	private static Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:\\Users\\Virassan\\Documents\\!ThunderDome\\Extra Files and Things\\SQLite Database\\testing.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Update Message: DatabaseConnect_connect Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
        return conn;
    }
		
}
