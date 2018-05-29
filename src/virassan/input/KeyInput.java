package virassan.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Reads the Keyboard Input and analyzes
 * @author Virassan
 *
 */
public class KeyInput implements KeyListener{
	private String text;
	private boolean isTyping;
	
	private boolean keyPressed;
	private boolean[] keys;
	public boolean up, down, left, right, Q, W, E, R, T, Y, U, I, O, P, A, S, D, F, G, H, J, K, L, Z, X, C, V, B, N, M;
	public boolean ctrl, alt, shift, space, enter, esc;
	public boolean one, two, three, four, five, six, seven, eight, nine, zero;
	
	public KeyInput(){
		keys = new boolean[256];
		isTyping = true;
		text = "";
	}
	
	/**
	 * Ticks whether these keys are pressed/typed/etc
	 * @param delta 
	 */
	public void tick(double delta){
		one = keys[KeyEvent.VK_1];
		two = keys[KeyEvent.VK_2];
		three = keys[KeyEvent.VK_3];
		four = keys[KeyEvent.VK_4];
		five = keys[KeyEvent.VK_5];
		six = keys[KeyEvent.VK_6];
		seven = keys[KeyEvent.VK_7];
		eight = keys[KeyEvent.VK_8];
		nine = keys[KeyEvent.VK_9];
		zero = keys[KeyEvent.VK_0];
		
		up = keys[KeyEvent.VK_UP];
		down = keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_RIGHT];
		Q = keys[KeyEvent.VK_Q];
		W = keys[KeyEvent.VK_W];
		E = keys[KeyEvent.VK_E];
		R = keys[KeyEvent.VK_R];
		T = keys[KeyEvent.VK_T];
		Y = keys[KeyEvent.VK_Y];
		U = keys[KeyEvent.VK_U];
		I = keys[KeyEvent.VK_I];
		O = keys[KeyEvent.VK_O];
		P = keys[KeyEvent.VK_P];
		
		A = keys[KeyEvent.VK_A];
		S = keys[KeyEvent.VK_S];
		D = keys[KeyEvent.VK_D];
		F = keys[KeyEvent.VK_F];
		G = keys[KeyEvent.VK_G];
		H = keys[KeyEvent.VK_H];
		J = keys[KeyEvent.VK_J];
		K = keys[KeyEvent.VK_K];
		L = keys[KeyEvent.VK_L];
		
		Z = keys[KeyEvent.VK_Z];
		X = keys[KeyEvent.VK_X];
		C = keys[KeyEvent.VK_C];
		V = keys[KeyEvent.VK_V];
		B = keys[KeyEvent.VK_B];
		N = keys[KeyEvent.VK_N];
		M = keys[KeyEvent.VK_M];
		
		ctrl = keys[KeyEvent.VK_CONTROL];
		alt = keys[KeyEvent.VK_ALT];
		shift = keys[KeyEvent.VK_SHIFT];
		space = keys[KeyEvent.VK_SPACE];
		enter = keys[KeyEvent.VK_ENTER];
		esc = keys[KeyEvent.VK_ESCAPE];
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		keyPressed = keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyPressed = keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(isTyping){
			char keyCharacter = e.getKeyChar();
			text += keyCharacter;
		}
	}

	/**
	 * Returns which key is Pressed
	 * @return the key pressed
	 */
	public boolean getKeyReleased(){
		return keyPressed;
	}
	
	/**
	 * Returns the String that is typed
	 * @return the text as a String that is typed
	 */
	public String getTyped(){
		return text;
	}
	
	public void isTyping(boolean b){
		isTyping = b;
	}
}
