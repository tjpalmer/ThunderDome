package virassan.states;

import java.awt.Graphics;
import java.util.Stack;

import virassan.main.Game;
import virassan.main.Handler;

public class StateManager {

	public static Stack<State> states;
	public State gameState, mainMenuState;
	public Handler handler;
	public static State currentState;
	public static State MAINMENU;
	public static State MAINGAME;
	
	public StateManager(Handler handler, Game game){
		this.handler = handler;
		states = new Stack<State>();
		MAINMENU = new MainMenuState(this, handler);
		MAINGAME = new GameState(this, handler, game);
		states.push(MAINGAME);
	}
	
	public void setState(State state){
		states.push(state);
		currentState = state;
	}
	
	public void init(){
		states.peek().init();
	}
	
	public void tick(){
		states.peek().tick();
	}
	
	
	public void render(Graphics g){
		states.peek().render(g);
	}
	
	public State getState(){
		return currentState;
	}
}
