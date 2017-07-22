package virassan.input;

import java.util.Collection;
import java.util.Iterator;

public class LinkedQueue{

	private Node head;
	
	public LinkedQueue() {
	}

	public boolean addAll(Collection c) {
		return false;
	}

	public void clear() {
		head = null;
	}

	public boolean contains(Node o) {
		return false;
	}

	public boolean containsAll(Collection c) {
		return false;
	}

	public boolean isEmpty() {
		if(size() == 0){
			return true;
		}
		return false;
	}

	public Iterator iterator() {
		return null;
	}

	public boolean remove(Node o) {
		return false;
	}

	public boolean removeAll(Collection c) {
		return false;
	}

	public boolean retainAll(Collection c) {
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
		Node[] temp = new Node[size()];
		Node cur = head;
		while(cur != null){
			for(int i = 0; i < temp.length; i++){
				temp[i] = cur;
				cur = cur.getNext();
			}
		}
		return temp;
	}

	public Node[] toArray(Node[] a) {
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
		return false;
	}

	public Node peek() {
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