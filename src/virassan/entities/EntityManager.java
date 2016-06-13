package virassan.entities;

import java.awt.Graphics;

import java.util.ArrayList;
import java.util.Comparator;
import virassan.entities.creatures.Player;
import virassan.main.Display;
import virassan.main.Handler;

public class EntityManager {

	private Handler handler;
	private Player player;
	private ArrayList<Entity> entities;
	
	private Comparator<Entity> renderSorter = new Comparator<Entity>(){
		@Override
		public int compare(Entity a, Entity b) {
			if(a.getY() + a.getHeight() < b.getY() + b.getHeight())
				return -1;
			return 1;
		}
		
	};
	
	public EntityManager(Handler handler){
		this.handler = handler;
		entities = new ArrayList<Entity>();
	}

	public void tick(){
		for(int i = 0; i < entities.size(); i++){
			if(!entities.get(i).isDead()){
				entities.get(i).tick();
			}else{
				entities.remove(entities.get(i));
				i--;
			}
		}
		entities.sort(renderSorter);
	}
	
	public void render(Graphics g){
		int xStart = (int)handler.getGameCamera().getxOffset() - 60;
		int xEnd = (int)(Display.WIDTH + handler.getGameCamera().getxOffset()) + 60;
		int yStart = (int)handler.getGameCamera().getyOffset() - 60;
		int yEnd = (int)(handler.getGameCamera().getyOffset() + Display.HEIGHT) + 60;
		for(Entity e : entities){
			if(e.getX() >= xStart-e.getWidth() && e.getX() <= xEnd && e.getY() >= yStart-e.getHeight() && e.getY() <= yEnd){
				e.render(g);
			}
		}
	}

	public void addEntity(Entity e){
		entities.add(e);
	}
	
	
	//Getters and Setters
	
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		addEntity(player);
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}
	
}
