package virassan.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import virassan.entities.creatures.npcs.Dialog;
import virassan.entities.creatures.npcs.NPC;
import virassan.entities.creatures.player.Player;
import virassan.gfx.Assets;
import virassan.gfx.hud.HUDManager;
import virassan.input.KeyInput;
import virassan.input.LinkedQueue;
import virassan.input.MouseInput;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.utils.Utils;

public class NPCDialog {

	private Handler handler;
	private MouseInput mouseInput;
	private KeyInput keyInput;
	private Player player;
	
	private boolean isResponseMenu;
	private NPC currentNPC;
	private final ArrayList<Rectangle> responseMenu = new ArrayList<>(Arrays.asList(new Rectangle(10, 120, 200, 57), 
			new Rectangle(10, 177, 200, 57), new Rectangle(10, 234, 200, 57), new Rectangle(10, 291, 200, 57)));
	private final Rectangle responseBox = new Rectangle(10, 120, 200, 228);
	private int respCurSelect;
	private ArrayList<Dialog> responses;
	private Dialog nextDialog;
	private String nextNPC;
	private Random gen;
	
	public NPCDialog(Handler handler) {
		this.handler = handler;
		mouseInput = handler.getMouseInput();
		keyInput = handler.getKeyInput();
		gen = new Random(37472);
		
	}

	public void render(Graphics g){
		Handler.GAMESTATE.render(g);
		int x = (int)((handler.getGameCamera().getWidth() - Assets.dialogBox.getWidth()) / 2);
		int y = (int)handler.getGameCamera().getHeight() - 200;
		g.drawImage(Assets.dialogBox, x, y, null);
		g.setColor(Color.PINK);
		g.setFont(new Font("Gentium Basic", Font.PLAIN, 28));
		try{
			g.drawString(player.getCurrentNPC().getCurrentDialog().getText(), x + 15, y + 35);
		}catch(NullPointerException e){
			g.drawString("", x + 15, y + 35);
			System.out.println("Error Message: NPCDialog_render current dialog text is null: " + player.getCurrentNPC().getCurrentDialog());
		}
		g.drawImage(Assets.button_a, x + 995, y + 105, null);
		if(isResponseMenu){
			g.setColor(Color.PINK);
			g.fillRect(responseBox.x, responseBox.y, responseBox.width, responseBox.height);
			g.setFont(new Font("Gentium Basic", Font.PLAIN, 18));
			int mult = 57;
			int textHeight = 15;
			for(int i = 0; i < responses.size(); i++){
				if(i == respCurSelect){
					g.setColor(Color.BLACK);
					g.fillRect(responseMenu.get(i).x, responseMenu.get(i).y, responseMenu.get(i).width, responseMenu.get(i).height);
				}
				g.setColor(Color.WHITE);
				g.drawString(responses.get(i).getText(), 10, 177 + (mult * i) - textHeight);
			}
			
		}
	}
	
	public void tick(double delta){
		player = handler.getPlayer();
		if(!mouseInput.getLeftClicks().isEmpty()){
			leftClick();
		}
		if(!mouseInput.getRightClicks().isEmpty()){
			rightClick();
		}
		hover();
		HUDManager.MENUTIMER += (System.currentTimeMillis() - HUDManager.MENULAST);
		HUDManager.MENULAST = System.currentTimeMillis();
		currentNPC = player.getCurrentNPC();
		if(!isResponseMenu){
			if(HUDManager.MENUTIMER > HUDManager.MENUWAIT){
				if(keyInput.A){
					if(nextDialog != null){ //if there is a nextDialog, switch to that
						
						/*
						if(!player.getCurrentNPC().getNPCID().equals(nextNPC)){
							for(Entity e : handler.getEntityManager().getEntities()){
								if(e instanceof NPC){
									System.out.println("Update Message: NPCDialog_tick switching currentNPC: " + nextNPC);
									player.setCurrentNPC((NPC)e);
									currentNPC = player.getCurrentNPC();
								}
							}
						}
						*/
						
						setCurrentDialog();
					}else if(nextDialog == null){ //if there is no nextDialog, close out of interaction with the NPC
						closeNPCDialog();
					}
					HUDManager.MENUTIMER = 0;
				}
			}
		}else{
			// isResponseMenu is TRUE
			
		}
	}
	
	public void leftClick(){
		LinkedQueue clicks = mouseInput.getLeftClicks();
		if(isResponseMenu){
			if(clicks.element() != null){
				outer :{
					while(clicks.element() != null){
						Point head = clicks.poll().getObject();
						double x = head.getX();
						double y = head.getY();
						for(int i = 0; i < responseMenu.size(); i++){
							if(mouseInput.getMouseBounds().intersects(responseMenu.get(i))){
								responses.get(i).run();
								if(nextDialog != null){ //if there is a nextDialog, switch to that
									
									/*
									if(!player.getCurrentNPC().getNPCID().equals(nextNPC)){
										for(Entity e : handler.getEntityManager().getEntities()){
											if(e instanceof NPC){
												System.out.println("Update Message: NPCDialog_leftClick switching currentNPC: " + nextNPC);
												player.setCurrentNPC((NPC)e);
												currentNPC = player.getCurrentNPC();
											}
										}
									}
									*/
									
									isResponseMenu = false;
									setCurrentDialog();
									break outer;
								}else{
									System.out.println("Message: NPCDialog_leftClick nextDialog is: " + nextDialog);
								}
							}else{
								//System.out.println("Message: NPCDialog_leftClick does not intersect: " + mouseInput.getMouseBounds() + "mouse , " + responseMenu.get(i) + "responseMenu rect");
							}
						}
					}
				}
			}
		}else{
			// isResponseMenu is false
		}
	}
	
	public void rightClick(){
		LinkedQueue clicks = mouseInput.getRightClicks();
		if(handler.getEntityManager().getPaused()){

		}
	}
	
	public void hover(){
		if(isResponseMenu){
			for(Rectangle rect : responseMenu){
				if(mouseInput.getMouseBounds().intersects(rect)){
					//System.out.println("Message: NPCDialog_hover intersects: " + mouseInput.getMouseBounds() + "mouse , " + rect + "responseMenu rect");
					respCurSelect = responseMenu.indexOf(rect);
				}
			}
		}
	}
	
	public void NPCInteract(NPC entity){
		player = handler.getPlayer();
		player.setCurrentNPC(entity);
		currentNPC = player.getCurrentNPC();
		int playerDist = (int)(Math.sqrt(Math.pow(player.getX() - entity.getX(), 2)+Math.pow(player.getY() - entity.getY(),2)));
		if(playerDist <= entity.getInteractDist()){
			entity.setInteract(true);
			ArrayList<Dialog> possibleDia = new ArrayList<>();
			Dialog curDia = new Dialog();
			if(player.isNPCMet(entity.getNPCID())){
				
				for(Dialog d : entity.getDialog()){
					if(d.isMetReq()){
						if(!d.getReqs().isEmpty()){
							if(d.areReqsMet()){
								possibleDia.add(d);
							}
						}
					}
				}
				int bounds = (Utils.clamp(possibleDia.size()-1, 0, possibleDia.size()-1));
				int ran = 0;
				if(bounds > 0){
					ran = gen.nextInt(bounds);
				}
				if(possibleDia.isEmpty()){
					closeNPCDialog();
				}else{
					curDia = possibleDia.get(ran);
				}
			}else{
				int bounds = (Utils.clamp(player.getCurrentNPC().getDefaultDialog().size()-1, 0, player.getCurrentNPC().getDefaultDialog().size()-1));
				int ran = 0;
				if(bounds > 0){
					ran = gen.nextInt(bounds);
				}
				if(player.getCurrentNPC().getDefaultDialog().isEmpty()){
					System.out.println("Error Message: NPCDialog_NPCInteract npc defaultDialog array is somehow empty");
				}else{
					curDia = player.getCurrentNPC().getDefaultDialog().get(ran);
				}
				player.setNPCMet(entity.getNPCID());
			}
			setInteractDialog(curDia);
			handler.getEntityManager().setPaused(true);
		}
	}
	
	public void setInteractDialog(Dialog dialog){
		if(dialog.getText().equals("")){
			dialog.run();
			currentNPC.setCurrentDialog(nextDialog);
		}else{
			currentNPC.setCurrentDialog(dialog);
			nextDialog = null;
			currentNPC.getCurrentDialog().run();
		}
		if(dialog.getText() == null){
			if(nextDialog != null){
				setInteractDialog(nextDialog);
			}
		}
		if(!dialog.getResponses().isEmpty()){
			ArrayList<Dialog> resps = new ArrayList<>();
			for(Dialog r : dialog.getResponses()){
				if(r != null){
					resps.add(r);
				}
			}
			responses = resps;
			respCurSelect = 0;
			isResponseMenu = true;
		}
	}
	
	public void setCurrentDialog(){
		setInteractDialog(nextDialog);
	}
	
	public Dialog getDialog(String dialogID){
		for(Dialog d : player.getCurrentNPC().getDialog()){
			if(d.getDIALOG_ID().equals(dialogID)){
				return d;
			}
		}
		System.out.println("Error Message: NPCDialog_getDialog dialog not found for current NPC");
		return null;
	}
	
	public void setNextDialog(String text){
		nextDialog =  new Dialog(text);
	}
	
	public void setNextDialog(Dialog next){
		nextDialog = next;
	}
	
	public Dialog getNextDialog(){
		return nextDialog;
	}
	
	public void setNextNPC(String npcID){
		nextNPC = npcID;
	}
	
	public Dialog getCurrentDialog(){
		return currentNPC.getCurrentDialog();
	}
	
	public void closeNPCDialog(){
		currentNPC.setInteract(false);
		player.setCurrentNPC(null);
		handler.getEntityManager().setPaused(false);
		handler.setState(States.GameState);
	}
	
}
