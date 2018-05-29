package virassan.entities.creatures.npcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import virassan.entities.creatures.Creature;
import virassan.gfx.ImageLoader;
import virassan.main.Handler;

public class NPC extends Creature{
	private BufferedImage image;
	private int interactDist;
	private boolean isInteract;
	private ArrayList<Dialog> defaultDialog;
	private ArrayList<Dialog> dialogs;
	private Dialog currentDialog;
	private String npcID;
	private String mapID;
	private String npcType;

	public NPC(Handler handler, String npcID, String mapID, float x, float y, int width, int height, String name, String filepath, String npcType) {
		super(handler, name, x, y, width, height, 1);
		// TODO Add Portrait
		this.mapID = mapID;
		this.npcID = npcID;
		this.npcType = npcType;
		defaultDialog = new ArrayList<Dialog>();
		defaultDialog.add(new Dialog("0001", "Hello, Stranger! My name is " + name + ". Thanks for stopping by!"));
		image = ImageLoader.loadImage("/textures/entities/" + filepath);
		
		//image = ImageLoader.loadImage("/textures/entities/npc_test.png");
		
		interactDist = (int)(Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
		bounds.x += 22;
		bounds.width -= 46;
		bounds.y += 54;
		bounds.height -= 60;
		isInteract = false;
		currentDialog = defaultDialog.get(0);
	}
	

	@Override
	public void resetTimer() {
		// No Timers here to reset
	}
	
	public void setCurrentDialog(Dialog dialog){
		currentDialog = dialog;
	}
	
	@Override
	public void tick(double delta) {
		//TODO: Add animation and possible movement to NPC when Not Paused
	}

	@Override
	public void render(Graphics g) {
		float xrel = vector.normalize().dX ;//* handler.getGameCamera().getWidth();
		float yrel = vector.normalize().dY ;//* handler.getGameCamera().getHeight();
		
		g.setFont(new Font("Verdana", Font.PLAIN, 12));
		g.drawImage(image, (int)(xrel - handler.getGameCamera().getxOffset()), (int)(yrel - handler.getGameCamera().getyOffset()), null);
		g.setColor(Color.WHITE);
		g.drawString(name, (int)(xrel - handler.getGameCamera().getxOffset() + 2), (int)(yrel - handler.getGameCamera().getyOffset() - 5));
		int temp = (int)(Math.sqrt(Math.pow(xrel - handler.getPlayer().getX(), 2) + Math.pow(yrel - handler.getPlayer().getY(), 2)));
		if(temp <= interactDist){
			g.drawString("F", (int)(xrel - handler.getGameCamera().getxOffset() + (width / 2) - 5), (int)(yrel - handler.getGameCamera().getyOffset() + height + 10));
		}
	}
	
	//GETTERS AND SETTERS
	
	public String getNPCType(){
		return npcType;
	}
	
	public String getMapID(){
		return mapID;
	}
	
	public String getNPCID(){
		return npcID;
	}
	
	public Dialog getCurrentDialog(){
		return currentDialog;
	}
	
	public String getCurrentDialogText(){
		return currentDialog.getText();
	}
	
	public void setDialog(ArrayList<Dialog> dialogs){
		this.dialogs = dialogs;
		ArrayList<Dialog> defaultDia = new ArrayList<>();
		for(Dialog d : dialogs){
			if(!d.getReqs().isEmpty()){
				for(String s : d.getReqs().keySet()){
					if(s != null){
						if(s.equals("is_met")){
							if(d.getReqs().get(s).get(1).equals("0")){
								defaultDia.add(d);
							}
						}
					}
				}
			}
		}
		defaultDialog = defaultDia;
	}
	
	public ArrayList<Dialog> getDialog(){
		return dialogs;
	}
	
	public ArrayList<Dialog> getDefaultDialog(){
		return defaultDialog;
	}
	
	public boolean getInteract(){
		return isInteract;
	}
	
	public void setInteract(boolean b){
		isInteract = b;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getInteractDist() {
		return interactDist;
	}

	public void setInteractDist(int interactDist) {
		this.interactDist = interactDist;
	}

	@Override
	public void unPause() {}

	@Override
	public Class findClass() {
		return getClass().getSuperclass();
	}

	
}
