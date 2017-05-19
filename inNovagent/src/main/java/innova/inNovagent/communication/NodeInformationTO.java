package innova.inNovagent.communication;

import innova.inNovagent.core.Node;

/**
 * Simple Data-Holder to store information about Antworld.
 * If you look for a real Node implementation look at {@link Node}
 * @author Tyrone
 *
 */
public class NodeInformationTO {

	private int x;
	private int y;
	private int honey;
	private int smell;
	private int stench;
	private boolean stone;
	private boolean trap;
	private boolean visited;
	
	/**
	 * This x is from antworlds-coordinate system.
	 * Don't use it for internal stuff. 
	 * @return
	 */
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * This y is from antworlds-coordinate system.
	 * Don't use it for internal stuff. 
	 * @return
	 */
	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getHoney() {
		return honey;
	}

	public void setHoney(int honey) {
		this.honey = honey;
	}

	public int getSmell() {
		return smell;
	}

	public void setSmell(int smell) {
		this.smell = smell;
	}

	public int getStench() {
		return stench;
	}

	public void setStench(int stench) {
		this.stench = stench;
	}

	public boolean isStone() {
		return stone;
	}

	public void setStone(boolean stone) {
		this.stone = stone;
	}

	public boolean isTrap() {
		return trap;
	}

	public void setTrap(boolean trap) {
		this.trap = trap;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	

}
