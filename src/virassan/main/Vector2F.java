package virassan.main;

/**
 * Creates a Vector float 
 * @author Virassan
 *
 */
public class Vector2F {

	public float xPos, yPos;
	public static float worldXpos, worldYpos;
	
	public Vector2F(){
		this.xPos = 0.0f;
		this.yPos = 0.0f;
	}
	
	public Vector2F(float xpos, float ypos) {
		this.xPos = xpos;
		this.yPos = ypos;	
	}
	
	public static Vector2F zero(){
		return new Vector2F();
	}
	
	public Vector2F getScreenLocation(){
		return new Vector2F(xPos, yPos);
	}
	
	public Vector2F getWorldLocation(){
		return new Vector2F(xPos - worldXpos, yPos - worldYpos);
	}
	
	public void normalize(){
		double length = Math.sqrt(xPos * xPos + yPos * yPos);
		if(length != 0.0){
			float a = 1.0f / (float)length;
			xPos = xPos * a;
			yPos = yPos * a;
		}
	}
	
	public boolean equals(Vector2F vector){
		return (this.xPos == vector.xPos && this.yPos == vector.yPos);
	}
	
	public Vector2F copyToThis(Vector2F vector){
		this.xPos = vector.xPos;
		this.yPos = vector.yPos;
		return this;
	}
	
	public Vector2F copyNew(Vector2F vector){
		float xpos = vector.xPos;
		float ypos = vector.yPos;
		return new Vector2F(xpos, ypos);
	}
	
	public Vector2F add(Vector2F vector){
		xPos = xPos + vector.xPos;
		yPos = yPos + vector.yPos;
		return new Vector2F(xPos, yPos);
	}
	
	public static void setWorldPositions(float worldx, float worldy){
		worldXpos = worldx;
		worldYpos = worldy;
	}
	
	public static double getDistanceOnScreen(Vector2F vec1, Vector2F vec2){
		float v1 = vec1.xPos - vec2.xPos;
		float v2 = vec1.yPos - vec2.yPos;
		return Math.sqrt(v1 * v1 + v2 * v2);
	}
	
	public double getDistanceBetweenWorldVectors(Vector2F vec){
		float dx = Math.abs(getWorldLocation().xPos - vec.getWorldLocation().xPos);
		float dy = Math.abs(getWorldLocation().yPos - vec.getWorldLocation().yPos);
		return Math.abs(dx * dx - dy * dy);
	}
	
}
