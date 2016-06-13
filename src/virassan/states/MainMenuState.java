package virassan.states;

import java.awt.Graphics;

import virassan.main.Handler;

public class MainMenuState extends State{

	
	public MainMenuState(StateManager sm, Handler handler) {
		super(sm);
	}

	@Override
	public void tick() {
		if(handler.getKeyInput().up){
			handler.getStateManager().setState(StateManager.MAINGAME);;
		}else
			return;
	}

	@Override
	public void render(Graphics g) {
		handler.render(g);
		
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
}
