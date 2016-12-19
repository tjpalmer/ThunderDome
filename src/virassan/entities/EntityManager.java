package virassan.entities;

import java.awt.Graphics;

import java.util.ArrayList;
import java.util.Comparator;

import virassan.entities.creatures.enemies.Enemy;
import virassan.entities.creatures.npcs.Merchant;
import virassan.entities.creatures.player.Player;
import virassan.input.LinkedQueue;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.quests.QuestTracker;

public class EntityManager {

	private Handler handler;
	private Player player;
	private ArrayList<Entity> entities;
	private boolean isPaused;
	
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
		isPaused = false;
	}

	public void tick(){
		for(int i = 0; i < entities.size(); i++){
			if(!entities.get(i).isDead()){
				if(!(entities.get(i) instanceof Player)){
					if(!isPaused){
						entities.get(i).tick();
					}else{
						if(entities.get(i) instanceof Merchant){
							entities.get(i).tick();
						}
					}
				}else{
					entities.get(i).tick();
				}
			}else{
				if(entities.get(i) instanceof Enemy){
					if(handler.getPlayer().getKillList().containsKey(((Enemy)entities.get(i)).getEnemyType())){
						if(handler.getPlayer().getKillList().get(((Enemy)entities.get(i)).getEnemyType()) != null){
							handler.getPlayer().getKillList().replace(((Enemy)entities.get(i)).getEnemyType(), handler.getPlayer().getKillList().get(((Enemy)entities.get(i)).getEnemyType())+1);
						}else{
							handler.getPlayer().getKillList().replace(((Enemy)entities.get(i)).getEnemyType(), 1);
						}
					}else{
						handler.getPlayer().getKillList().put(((Enemy)entities.get(i)).getEnemyType(), 1);
					}
					System.out.println(handler.getPlayer().getKillList());
					for(QuestTracker quest : handler.getPlayer().getQuestLog().getActive()){
						if(quest.getQuest().getHashMap().containsKey(((Enemy)entities.get(i)).getSpecies())){
							quest.addEnemyCount(((Enemy)entities.get(i)).getSpecies());
						}else if(quest.getQuest().getHashMap().containsKey(((Enemy)entities.get(i)).getEnemyType())){
							quest.addEnemyCount(((Enemy)entities.get(i)).getEnemyType());
						}
					}
				}
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
	
	public void setPaused(boolean b){
		if(!b){
			for(Entity i : entities){
				i.unPause();
			}
		}
		handler.getMouseInput().getLeftClicks().clear();
		handler.getMouseInput().getRightClicks().clear();
		isPaused = b;
	}
	
	public boolean getPaused(){
		return isPaused;
	}
}
