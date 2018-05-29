package virassan.utils;

import java.awt.Rectangle;

import virassan.main.Display;
import virassan.main.Game;

/**
 * Creates a Vector float 
 * @author Virassan
 *
 */
public class Vector2F {

	public float dX, dY;
	public static float worldXpos, worldYpos;
	
	public Vector2F(){
		this.dX = 0.0f;
		this.dY = 0.0f;
	}
	
	public Vector2F(float xpos, float ypos) {
		this.dX = xpos;
		this.dY = ypos;	
	}
	
	public Vector2F(Vector2F v1){
		this.dX = v1.dX;
		this.dY = v1.dY;
	}
	
	/**
	 * Zeroes the Position
	 */
	public void zero(){
		dX = 0.0F;
		dY = 0.0F;
	}
	
	public float length(){
		return (float)Math.sqrt(dX * dX + dY * dY);
	}
	
	/**
	 * Used to get the direction of the Vector2F
	 */
	public Vector2F normalize(){
		float x = 0;
		float y = 0;
		if(length() != 0.0){
			x = dX / length();
			y = dY / length();
		}
		//TODO: not normalizing yet because weird stuff yo
		return new Vector2F(dX, dY);
	}
	
	/**
	 * Compares this Vector2F with the Vector2F passed
	 * @param vector The Vector2F to compare with this one.
	 * @return Returns true if they are the same, false if not.
	 */
	public boolean equals(Vector2F v1){
		return (this.dX == v1.dX && this.dY == v1.dY);
	}
	
	/**
	 * Copies the Position of the Vector2F passed to this Vector2F
	 * @param vector The Vector2F to be copied
	 */
	public void copyToThis(Vector2F v1){
		this.dX = v1.dX;
		this.dY = v1.dY;
	}
	
	public Vector2F scale(float scaleFactor){
		return new Vector2F(this.dX*scaleFactor, this.dY*scaleFactor);
	}
	
	/**
	 * Adds the Position of this Vector2F to the Vector2F passed.
	 * @param vector
	 * @return
	 */
	public Vector2F add(Vector2F v1){
		return new Vector2F(this.dX + v1.dX, this.dY + v1.dY);
	}

	public Vector2F subtract(Vector2F v1){
		return new Vector2F(this.dX - v1.dX, this.dY - v1.dY);
	}
	
	
	//WORLD POSITION STUFF
	public static void setWorldPositions(float worldx, float worldy){
		worldXpos = worldx;
		worldYpos = worldy;
	}
	
	public static double getDistanceOnScreen(Vector2F vec1, Vector2F vec2){
		float v1 = vec1.dX - vec2.dX;
		float v2 = vec1.dY - vec2.dY;
		return Math.sqrt(v1 * v1 + v2 * v2);
	}
	
	public Vector2F getWorldLocation(){
		return new Vector2F(dX - worldXpos, dY - worldYpos);
	}
	
	public double getDistanceBetweenWorldVectors(Vector2F vec){
		float dx = Math.abs(getWorldLocation().dX - vec.getWorldLocation().dX);
		float dy = Math.abs(getWorldLocation().dY - vec.getWorldLocation().dY);
		return Math.abs(dx * dx - dy * dy);
	}
	
	public boolean intersects(Rectangle rect){
		if(new Rectangle((int)Math.round(dX), (int)Math.round(dY), 1, 1).intersects(rect)){
			return true;
		}
		return false;
	}
	
	/**
	 * Calculates the Dot Product between this vector and the one given.
	 * @param v1
	 * @return If the result is 0, then the two vectors are perpendicular. If the result is > 0, then the angle between the two vectors is greater than 90degrees. If the result is < 0, then the angle between the two vectors is less than 90degrees.
	 */
	public float getDotProduct(Vector2F v1){
		return (float)(this.dX*v1.dX + this.dY*v1.dY);
	}
	
	public double getAngle(Vector2F v1){
		//System.out.println("norm1: " + this + ", v1: " + v1);
		//System.out.println("True norm1 y: " + (-this.dY + Display.HEIGHT) + ", True v1 y: " + (-v1.dY + Display.HEIGHT));
		double angle = (double)Math.atan2((-v1.dY + Game.handler.getHeight()) - (-this.dY + Game.handler.getHeight()), v1.dX - this.dX) * (180/Math.PI);
		if(angle < 0){
			//System.out.println("Angle b4 mod: " + angle);
			angle += 360;
			//System.out.println("Angle after mod: " + angle);
		}
		angle = Math.round((angle + 15)%360);
		return angle;
	}
	
	public int getDirection(){
		return angleToDirection(getAngle(new Vector2F(1, 0)));
	}
	
	public int angleToDirection(double angle){
		int dir = 0;
		// dot product of this vector and other vector - divided by the magnitude of this vector * magnitude of other vector - all of this inside arccos (inverse cos)
		//System.out.println("getDirection - angle: " + angle);
		if(angle > 0 && angle < 30){
			dir = 0;
		}
		else if(angle >= 30 && angle <= 90){
			dir = 1;
		}
		else if(angle > 90 && angle < 120){
			dir = 2;
		}
		else if(angle >= 120 && angle <= 180){
			dir = 3;
		}
		else if(angle > 180 && angle < 210){
			dir = 4;
		}
		else if(angle >= 210 && angle <= 270){
			dir = 5;
		}
		else if(angle > 270 && angle < 300){
			dir = 6;
		}
		else if(angle >= 300 && angle <= 360){
			dir = 7;
		}
		return dir;
	}
	
	public String toString(){
		return "Vector dX: " + dX + ", dY: " + dY;
	}
}
