package virassan.states;

import java.awt.Graphics;

import virassan.main.Handler;

public abstract class State {
	
	private static State currentState = null;
	public StateManager sm;
	
	public State(StateManager sm){
		this.sm = sm;
		init();
	}
	
	public static void setState(State state){
		currentState = state;
	}
	
	public static State getState(){
		return currentState;
	}
	
	protected Handler handler;
	
	public State(Handler handler){
		this.handler = handler;
	}
	
	public abstract void init();
	public abstract void tick();
	public abstract void render(Graphics g);

}
