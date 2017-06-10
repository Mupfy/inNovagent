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
	private boolean isVisited;
	private boolean isDangerous;
	private boolean isSafe;
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
	
	public boolean isVisited(){
		return this.isVisited;
	}
	
	public Node setVisited(boolean visited){
		this.isVisited = visited;
		return this;
	}	
	
	public boolean isStone() {
		return this.isStone;
	}
	
	public Node setHoneyAmount(int honeyAmount) {
		this.honeyAmount = honeyAmount;
		return this;
	}
	
	public boolean isAccessible(){
		return !this.isStone && !this.isTrap;
	}

	public Node addNeighbour(Node earl){
		this.neighbours.add(earl);
		return this;
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

	public Node setSmell(int smell) {
		this.smell = smell;
		return this;
	}

	public Node setStone(boolean isStone) {
		this.isStone = isStone;
		return this;
	}

	public Node setTrap(boolean isTrap) {
		this.isTrap = isTrap;
		return this;
	}
	
	public Node setStench(int stenchAmount){
		this.stench = stenchAmount;
		return this;
	}
	
	public Node setDangerous(boolean value){
		this.isDangerous = value;
		return this;
	}
	
	public boolean isDangerous(){
		return this.isDangerous;
	}
	
	public boolean isSafe(){
		return this.isSafe;
	}
	
	public Node setSafe(boolean safe){
		this.isSafe = safe;
		return this;
	}
}
