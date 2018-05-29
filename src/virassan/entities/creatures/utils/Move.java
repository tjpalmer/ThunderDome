package virassan.entities.creatures.utils;

import java.awt.Rectangle;

import virassan.entities.Entity;
import virassan.entities.creatures.Creature;
import virassan.entities.creatures.enemies.Enemy;
import virassan.items.Drop;
import virassan.main.Display;
import virassan.main.Handler;
import virassan.utils.Utils;
import virassan.utils.Vector2F;
import virassan.world.maps.Tile;

public class Move {

	public static boolean isHit(Vector2F v1){
		int s = 3;
		Rectangle target = new Rectangle((int)(Handler.GAMESTATE.getPlayer().getCenter().dX) - s, 
				(int)(Handler.GAMESTATE.getPlayer().getCenter().dY) - s, s*2, s*2);
		if(v1.intersects(target)){
			return true;
		}
		return false;
	}
	
	public static int getHomingDirection(Vector2F v1){
		int dir = -1;
		dir = v1.angleToDirection(v1.getAngle(Handler.GAMESTATE.getPlayer().getCenter()));
		return dir;
	}
	
	public static boolean move(Object o, int speed, int direction, float delta){
		if(o instanceof Entity){
			return move((Entity)o, (float)speed, direction, delta);
		}
		else{
			if(o instanceof Drop){
				Drop d = (Drop)o;
				direction = getHomingDirection(d.getCenter());
				if(direction >= 0){
					Vector2F v1 = calcVelocity(delta, speed, direction, d);
					d.setPos(new Vector2F(Utils.clamp(d.getPos().dX + v1.dX, 0, Handler.GAMESTATE.getPlayer().getCenter().dX - (d.getItem().getWidth()/2)), 
							Utils.clamp(d.getPos().dY + v1.dY, 0, Handler.GAMESTATE.getPlayer().getCenter().dY - (d.getItem().getHeight()/2))));
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean move(Entity e, float speed, int direction, float delta){
		boolean bx = false;
		boolean by = false;
		if(direction >= 0 && direction <= 7){
			Vector2F v1 = calcVelocity(delta, speed, direction, e);
			if(!e.isDead() && !e.checkEntityCollisions(v1.dX, v1.dY)){
				if(v1.dX != 0){
					bx = moveX(e, v1);
				}
				if(v1.dY != 0){
					by = moveY(e, v1);
				}
			}
		}
		if(bx || by){
			return true;
		}else{
			return false;
		}
	}
	
	private static boolean moveX(Entity e, Vector2F v1){
		boolean b = false;
		float newX = e.getX() + v1.dX;
		if(v1.dX > 0){
			int tempX = (int)(newX + e.getBounds().x + e.getBounds().width)/Tile.TILE_WIDTH;
			if(!collisionWithTile(tempX, (int)(e.getY() + e.getBounds().y)/ Tile.TILE_HEIGHT) &&
					!collisionWithTile(tempX, (int)(e.getY() + e.getBounds().y + e.getBounds().height)/ Tile.TILE_HEIGHT)){
				if(((Creature)e).isNPC()){
					if(Utils.clamp(newX, ((Creature)e).getWalkBounds().x, ((Creature)e).getWalkBounds().x + ((Creature)e).getWalkBounds().width) == newX){
						b = true;
						newX = Utils.clamp(newX, 0, Handler.GAMESTATE.getMap().getWidth()*64);
					}else{
						newX -= v1.dX;
					}
					if(newX >= Handler.GAMESTATE.getMap().getWidth()*64 - e.getWidth()){
						newX -= v1.dX;
					}
				}else{
					b = true;
				}
			}else{
				newX -= 1;
			}
		}
		else if(v1.dX < 0){
			int tempX = (int)(newX + e.getBounds().x)/Tile.TILE_WIDTH;
			if(!collisionWithTile(tempX, (int)(e.getY() + e.getBounds().y)/ Tile.TILE_HEIGHT) &&
					!collisionWithTile(tempX, (int)(e.getY() + e.getBounds().y + e.getBounds().height)/ Tile.TILE_HEIGHT)){
				if(((Creature)e).isNPC()){
					if(Utils.clamp(newX, ((Creature)e).getWalkBounds().x, ((Creature)e).getWalkBounds().x + ((Creature)e).getWalkBounds().width) == newX){
						b = true;
						newX = Utils.clamp(newX, 0, Handler.GAMESTATE.getMap().getWidth()*64);
					}else{
						newX -= v1.dX;
					}
					if(newX >= Handler.GAMESTATE.getMap().getWidth()*64 - e.getWidth()){
						newX -= v1.dX;
					}
				}else{
					b = true;
				}
			}else{
				newX += Tile.TILE_WIDTH;
			}
		}
		newX = Utils.clamp(newX, 0, Handler.GAMESTATE.getMap().getWidth()*64 - e.getWidth());
		e.setX(newX);
		return b;
	}
	
	private static boolean moveY(Entity e, Vector2F v1){
		boolean b = false;
		float newY = e.getY() + v1.dY;
		if(v1.dY > 0){
			int tempY = (int)(newY + e.getBounds().y + e.getBounds().height)/Tile.TILE_HEIGHT;
			if(!collisionWithTile(tempY, (int)(e.getX() + e.getBounds().x)/ Tile.TILE_WIDTH) &&
					!collisionWithTile(tempY, (int)(e.getX() + e.getBounds().x + e.getBounds().width)/ Tile.TILE_WIDTH)){
				if(((Creature)e).isNPC()){
					if(Utils.clamp(newY, ((Creature)e).getWalkBounds().y, ((Creature)e).getWalkBounds().y + ((Creature)e).getWalkBounds().height) == newY){
						b = true;
						newY = Utils.clamp(newY, 0, Handler.GAMESTATE.getMap().getHeight()*64);
					}else{
						newY -= v1.dY;
					}
				}else{
					b = true;
				}
			}else{
				newY -= 1;
			}
		}
		else if(v1.dY < 0){
			int tempY = (int)(newY + e.getBounds().y)/Tile.TILE_HEIGHT;
			if(!collisionWithTile(tempY, (int)(e.getX() + e.getBounds().x)/ Tile.TILE_WIDTH) &&
					!collisionWithTile(tempY, (int)(e.getX() + e.getBounds().x + e.getBounds().width)/ Tile.TILE_WIDTH)){
				if(((Creature)e).isNPC()){
					if(Utils.clamp(newY, ((Creature)e).getWalkBounds().y, ((Creature)e).getWalkBounds().y + ((Creature)e).getWalkBounds().height) == newY){
						b = true;
						newY = Utils.clamp(newY, 0, Handler.GAMESTATE.getMap().getHeight()*64);
					}else{
						newY -= v1.dY;
					}
				}else{
					b = true;
				}
			}else{
				newY += Tile.TILE_HEIGHT;
			}
		}
		e.setY(newY);
		return b;
	}
	
	private static Vector2F calcVelocity(float delta, float speed, int direction, Object e){
		Vector2F vector = new Vector2F();
		int xMod;
		int yMod;
		switch(direction){
		case 0: xMod = 1; yMod = 0; break; //right
		case 1: xMod = 1; yMod = -1; break; //up-right
		case 2: xMod = 0; yMod = -1; break; //up
		case 3: xMod = -1; yMod = -1; break; //up-left
		case 4: xMod = -1; yMod = 0; break; //left
		case 5: xMod = -1; yMod = 1; break; //down-left
		case 6: xMod = 0; yMod = 1; break; //down
		case 7: xMod = 1; yMod = 1; break; //down-right
		default: xMod = 0; yMod = 0; break; //NONE
		}
		if(e instanceof Enemy){
			Enemy a = (Enemy)e;
			int playerDist = (int)Math.round(Math.sqrt((double)(Math.pow(a.getCenter().dX - a.getHandler().getPlayer().getCenter().dX, 2) + 
					Math.pow(a.getCenter().dY - a.getHandler().getPlayer().getCenter().dY, 2))));
			if(playerDist <= ((Enemy)e).getAggroDist() || ((Enemy)e).getStats().getAggro()){
				if(playerDist < speed){
					speed = playerDist;
				}
			}
		}else if(e instanceof Drop){
			Drop d = (Drop)e;
			int playerDist = (int)Math.round(Math.sqrt((double)(Math.pow(d.getCenter().dX - Handler.GAMESTATE.getPlayer().getCenter().dX, 2) + 
					Math.pow(d.getCenter().dY - Handler.GAMESTATE.getPlayer().getCenter().dY, 2))));
			if(playerDist < speed){
				speed = playerDist;
			}
		}
		
		float xMin = 0;
		float yMin = 0;
		float xMax = speed;
		float yMax = speed;
		if(xMod == -1){
			xMin = -speed;
			xMax = 0;
		}
		if(yMod == -1){
			yMin = -speed;
			yMax = 0;
		}
		float velx = 0;
		float vely = 0;
		//TODO: changed back to speed / delta 
		velx = Utils.clamp((speed / delta)*xMod, xMin, xMax);
		vely = Utils.clamp((speed / delta)*yMod, yMin, yMax);
		if(xMod != 0 && yMod != 0){ //TODO: observed to not work correctly
			float temp = (float)Math.sqrt(Math.pow(Math.abs(speed), 2)/2);
			xMin = 0;
			yMin = 0;
			xMax = temp;
			yMax = temp;
			if(xMod == -1){
				xMin = -temp;
				xMax = 0;
			}
			if(yMod == -1){
				yMin = -temp;
				yMax = 0;
			}
			velx = Utils.clamp((temp/delta)*xMod, xMin, xMax);
			vely = Utils.clamp((temp/delta)*yMod, yMin, yMax);
		}
		vector.dX = (float)Math.ceil(velx);
		vector.dY = (float)Math.ceil(vely);
		return vector;
	}
		
	
	private static boolean collisionWithTile(int x, int y){
		return Handler.GAMESTATE.getMap().getTile(x, y).isSolid();
	}
	
	private float collisionDamage(int x, int y){
		return Handler.GAMESTATE.getMap().getTile(x, y).getCollisionDamage();
	}
	
}
