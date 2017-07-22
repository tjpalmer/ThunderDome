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
	
	/**
	 * Zeroes the Position
	 */
	public void zero(){
		xPos = 0.0F;
		yPos = 0.0F;
	}
	
	/**
	 * Used to get the direction of the Vector2F
	 */
	public void normalize(){
		double length = Math.sqrt(xPos * xPos + yPos * yPos);
		if(length != 0.0){
			float a = 1.0f / (float)length;
			xPos = xPos * a;
			yPos = yPos * a;
		}
	}
	
	/**
	 * Compares this Vector2F with the Vector2F passed
	 * @param vector The Vector2F to compare with this one.
	 * @return Returns true if they are the same, false if not.
	 */
	public boolean equals(Vector2F vector){
		return (this.xPos == vector.xPos && this.yPos == vector.yPos);
	}
	
	/**
	 * Copies the Position of the Vector2F passed to this Vector2F
	 * @param vector The Vector2F to be copied
	 * @return Returns this Vector2F
	 */
	public Vector2F copyToThis(Vector2F vector){
		this.xPos = vector.xPos;
		this.yPos = vector.yPos;
		return this;
	}
	
	/**
	 * Used for creating a new Vector2F using this vector's Position
	 * @return Returns a new Vector2F identical to this one.
	 */
	public Vector2F copy(){
		return new Vector2F(xPos, yPos);
	}
	
	/**
	 * Adds the Position of this Vector2F to the Vector2F passed.
	 * @param vector
	 * @return
	 */
	public Vector2F add(Vector2F vector){
		xPos = xPos + vector.xPos;
		yPos = yPos + vector.yPos;
		return new Vector2F(xPos, yPos);
	}
	
	
	//WORLD POSITION STUFF
	public static void setWorldPositions(float worldx, float worldy){
		worldXpos = worldx;
		worldYpos = worldy;
	}
	
	public static double getDistanceOnScreen(Vector2F vec1, Vector2F vec2){
		float v1 = vec1.xPos - vec2.xPos;
		float v2 = vec1.yPos - vec2.yPos;
		return Math.sqrt(v1 * v1 + v2 * v2);
	}
	
	public Vector2F getWorldLocation(){
		return new Vector2F(xPos - worldXpos, yPos - worldYpos);
	}
	
	public double getDistanceBetweenWorldVectors(Vector2F vec){
		float dx = Math.abs(getWorldLocation().xPos - vec.getWorldLocation().xPos);
		float dy = Math.abs(getWorldLocation().yPos - vec.getWorldLocation().yPos);
		return Math.abs(dx * dx - dy * dy);
	}
	
}
