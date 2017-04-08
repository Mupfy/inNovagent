package innova.inNovagent.core;

import java.util.HashSet;
import java.util.Set;

import innova.inNovagent.util.Point;
import innova.inNovagent.util.Utils;

/**
 * Representation of a single field in the AntWorld.
 *
 */
public class Node {
	private Point position;

	private Set<Node> neighbours;
	

	private boolean isStone;
	private boolean isTrap;
	private int honeyAmout;
	private int smell;
	private int stank;

	public Node(Point position){
		this.position = Utils.notNull(position);
		this.neighbours = new HashSet<>();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Node other = (Node) obj;
		return other.position.equals(position);
	}
	
	@Override
	public int hashCode() {
		return this.position.hashCode();
	}
	
	public boolean hasHoney() {
		return this.honeyAmout != 0;
	}

	public int getHoneyAmount() {
		return this.honeyAmout;
	}

	public boolean hasStank() {
		return this.stank != 0;
	}

	public int getStank() {
		return this.stank;
	}

	public boolean isTrap() {
		return this.isTrap;
	}
	
	public boolean isAccessible(){
		return !this.isStone && !this.isTrap;
	}

	public void addNeighbour(Node earl){
		this.neighbours.add(earl);
	}
	
	public Set<Node> getNeighbours() {
		return neighbours;
	}
	
	public Point getPosition() {
		return position;
	}
	
}
