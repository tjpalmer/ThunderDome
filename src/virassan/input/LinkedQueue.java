package virassan.input;

import java.util.Collection;
import java.util.Iterator;

public class LinkedQueue{

	private Node head;
	
	public LinkedQueue() {
		// TODO Auto-generated constructor stub
	}

	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear() {
		head = null;
	}

	public boolean contains(Node o) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEmpty() {
		if(size() == 0){
			return true;
		}
		return false;
	}

	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(Node o) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeAll(Collection c) {
		// TODO
		return false;
	}

	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	public int size() {
		int count = 0;
		if(head != null){
			Node temp = head;
			while(temp != null){
				count++;
				temp = head.getNext();
			}
		}
		return count;
	}

	public Node[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node[] toArray(Node[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean add(Node e) {
		e.setNext(head);
		head = e;
		return true;
	}

	public Node element() {
		return head;
	}

	public boolean offer(Node e) {
		// TODO Auto-generated method stub
		return false;
	}

	public Node peek() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node poll() {
		return remove();
	}

	public Node remove() {
		Node old = head;
		if(head.getNext() != null){
			head = head.getNext();
		}else{
			head = null;
		}
		return old;
	}
}