package virassan.input;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

public class MouseInput implements MouseListener, MouseMotionListener{

	private LinkedQueue leftClicks, rightClicks;
	private LinkedQueue doubleClicks;
	private Point draggedPos, startDrag, endDrag;
	
	private long doubleTimer, doubleLast;
	
	private boolean dragged;
	private boolean leftPressed, rightPressed, doubleClick, rightClick, leftClick;
	private boolean leftReleased, rightReleased;
	private int mouseX, mouseY;
	
	public MouseInput(){
		leftClicks = new LinkedQueue();
		rightClicks = new LinkedQueue(); 
		doubleClicks = new LinkedQueue();
	}
	
	public void tick(double delta){
		doubleTimer += (System.currentTimeMillis() - doubleLast) * delta;
		doubleLast = System.currentTimeMillis() * (long)delta;
	}
	
	public void render(Graphics g){
	}

	//Implemented methods
	@Override
	public void mouseDragged(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)){
			draggedPos = e.getPoint();
			dragged = true;
		}else{
			dragged = false;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){ //left button
			leftPressed = true;
			if(!doubleClicks.isEmpty()){
				double distance = doubleClicks.element().getObject().distance((e.getPoint()));
				if(doubleTimer < 300 && distance < 10){
					doubleClick = true;
					doubleClicks.clear();
				}else{
					doubleClick = false;
					doubleClicks.clear();
					doubleClicks.add(new Node(e.getPoint()));
				}
			}else{
				doubleClicks.add(new Node(e.getPoint()));
			}
			if(draggedPos == null){
				endDrag = null;
				startDrag = e.getPoint();
			}
			leftClicks.add(new Node(e.getPoint()));
			doubleTimer = 0;
		}else if(e.getButton() == MouseEvent.BUTTON3){ //right button
			rightPressed = true;
			rightClicks.add(new Node(e.getPoint()));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){ //left button
			leftPressed = false;
			if(startDrag != e.getPoint()){
				endDrag = e.getPoint();
				draggedPos = null;
			}
		}else if(e.getButton() == MouseEvent.BUTTON3){ //right button
			rightPressed = false;
		}
		dragged = false;
	}
	
	// Getters
	public boolean isDragged(){
		return dragged;
	}
	
	public Point getDragged(){
		return draggedPos;
	}
	
	public Point getStartDrag(){
		return startDrag;
	}
	
	public Point getEndDrag(){
		return endDrag;
	}
	
	public Rectangle getMouseBounds(){
		return new Rectangle(mouseX, mouseY, 1, 1);
	}

	public boolean getDoubleClick(){
		return doubleClick;
	}
	
	public boolean isLeftClicked(){
		return leftClick;
	}
	
	public boolean isRightClicked(){
		return rightClick;
	}
	
	public boolean isLeftPressed(){
		return leftPressed;
	}
	
	public boolean isRightPressed(){
		return rightPressed;
	}
	
	public boolean isRightReleased(){
		return rightReleased;
	}
	
	public boolean isLeftReleased(){
		return leftReleased;
	}
	
	public int getMouseX(){
		return mouseX;
	}
	
	public int getMouseY(){
		return mouseY;
	}
	
	public LinkedQueue getLeftClicks(){
		return leftClicks;
	}
	
	public LinkedQueue getRightClicks(){
		return rightClicks;
	}
}

