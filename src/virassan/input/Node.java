package virassan.input;

import java.awt.Point;

public class Node {
	
	private Point obj;
	private Node next;
	
	public Node(Point obj) {
		this.obj = obj;
	}

	public void setNext(Node n){
		this.next = n;
	}

	public Point getObject(){
		return obj;
	}

	public Node getNext(){
		return next;
	}
}
