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
	private int honeyAmount;
	private int smell;
	private int stench;

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
		return this.honeyAmount != 0;
	}

	public int getHoneyAmount() {
		return this.honeyAmount;
	}

	public boolean hasStench() {
		return this.stench != 0;
	}

	public int getStench() {
		return this.stench;
	}

	public boolean isTrap() {
		return this.isTrap;
	}
	
	public boolean isStone() {
		return this.isStone;
	}
	
	public void setHoneyAmount(int honeyAmount) {
		this.honeyAmount = honeyAmount;
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

	public int getSmell() {
		return smell;
	}

	public void setSmell(int smell) {
		this.smell = smell;
	}

	public void setStone(boolean isStone) {
		this.isStone = isStone;
	}

	public void setTrap(boolean isTrap) {
		this.isTrap = isTrap;
	}
	
}
