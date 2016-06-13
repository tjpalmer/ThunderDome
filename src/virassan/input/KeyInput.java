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
	public boolean up, down, left, right, Q, E, W, A, S, D, R, I;
	public boolean ctrl, alt, shift, space, enter;
	
	public KeyInput(){
		keys = new boolean[256];
		isTyping = true;
		text = "";
	}
	
	/**
	 * Ticks whether these keys are pressed/typed/etc
	 */
	public void tick(){
		up = keys[KeyEvent.VK_UP];
		down = keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_RIGHT];
		Q = keys[KeyEvent.VK_Q];
		E = keys[KeyEvent.VK_E];
		W = keys[KeyEvent.VK_W];
		A = keys[KeyEvent.VK_A];
		S = keys[KeyEvent.VK_S];
		D = keys[KeyEvent.VK_D];
		R = keys[KeyEvent.VK_R];
		I = keys[KeyEvent.VK_I];
		
		ctrl = keys[KeyEvent.VK_CONTROL];
		alt = keys[KeyEvent.VK_ALT];
		shift = keys[KeyEvent.VK_SHIFT];
		space = keys[KeyEvent.VK_SPACE];
		enter = keys[KeyEvent.VK_ENTER];
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
		// TODO Auto-generated method stub
		if(isTyping){
			char keyCharacter = e.getKeyChar();
			text += keyCharacter;
			// System.out.println("Key Typed: " + e);
			// System.out.println("Text: " + text);
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
