package virassan.gfx;

import java.awt.image.BufferedImage;

/**
 * Animates stuff!
 * @author Virassan
 *
 */
public class Animation {
	
	public long sharedLastTime;
	private boolean timeShared = false;
	private boolean animeLoop = false;
	private int speed, index;
	private BufferedImage[] frames;
	private long lastTime, timer;
	private boolean stopped;
	private int totalFrames;
	// private int idleFrame;
	
	/**
	 * Constructs the Animation
	 * @param speed how often to change the frame in milliseconds //TODO: I think it's milliseconds?
	 * @param frames BufferedImage Array of frames to be used in the animation.
	 */
	public Animation(int speed, BufferedImage[] frames){
		this.speed = speed;
		this.frames = frames;
		// idleFrame = 0;
		timer = 0;
		lastTime = System.currentTimeMillis();
		stopped = false;
		totalFrames = frames.length;
		index = totalFrames -1;
	}
	
	public Animation(int speed, int idleFrame, BufferedImage[] frames){
		this.speed = speed;
		this.frames = frames;
		// this.idleFrame = idleFrame;
		timer = 0;
		lastTime = System.currentTimeMillis();
		stopped = false;
		totalFrames = frames.length;
		index = totalFrames - 1;
		
	}
	
	/**
	 * Set if sharing timer with all animations or not //TODO: In attempt to not get weird animation when switching directions
	 * @param timeShared if true, adds to the shared timer, if false it doesn't
	 */
	public void setTimeShared(boolean timeShared){
		this.timeShared = timeShared;
		if(timeShared){
			sharedLastTime = lastTime;
		}
	}
	
	/**
	 * If the animation is to loop or not
	 * @param animeLoop true if the animation contiues to loop, false if it doesn't
	 */
	public void setAnimationLoop(boolean animeLoop){
		this.animeLoop = animeLoop;
	}
	
	/**
	 * Ticks the animation - figures out which frame it's on depending on the animation speed and if set to keep looping or not
	 */
	public void tick(){
		if(!stopped){
			timer += System.currentTimeMillis() - lastTime;
			lastTime = System.currentTimeMillis();
			if(timeShared){
				timer += System.currentTimeMillis() - sharedLastTime;
			}
			if(timer > speed){
				index--;
				timer = 0;
				if(animeLoop){
					if(index <= 0){
						reset();
					}
				}else if(!animeLoop){
					if(index <= 0){
						index = 0;
						stopped = true;
					}
				}
				if(index < 0){
					index = 0;
				}else if(index > totalFrames -1){
					index = totalFrames -1;
				}
			}
		}
	}
	
	/**
	 * Begins the Animation
	 */
	public void start(){
		if(!stopped){
			return;
		}else if (stopped){
			reset();
			this.stopped = false;
		}
		if(frames.length == 0){
			return;
		}
	}
	
	/**
	 * Resets the animation
	 */
	public void reset(){
		if(!stopped){
			this.index = totalFrames -1;
		}
	}
	
	/**
	 * Returns the current animation Frame
	 * @return the current animation frame
	 */
	public BufferedImage getCurrentFrame(){
		return frames[index];
	}
	
	/**
	 * Sets the current animation frame
	 * @param index the frame's index within the animation array
	 */
	public void setCurrentFrame(int index){
		this.index = index;
	}
	
	
	
}