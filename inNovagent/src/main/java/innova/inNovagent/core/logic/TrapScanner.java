package innova.inNovagent.core.logic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;

/**
 * Logic for evaluating each node for traps, danger or even safety.
 * 
 */
public class TrapScanner {
	
	/**
	 * Evaluates the given map.
	 * @param map
	 */
	public void evaluate(NodeMap map){
		markDangerous(map);
	}
	
	private void markDangerous(NodeMap map){
		Collection<Node> nodes = map.getField().values();
		for(Node n : nodes){
			if(hasNeighbourStench(n) && !n.isVisited() && !n.isStone() && !n.isSafe()){
				n.setDangerous(true);
			}
			if(!n.isTrap() && (n.isVisited() || n.isStone())){
				n.setDangerous(false);
			}
			evaluateFromSource(n);
		}
	}
	
	private boolean hasNeighbourStench(Node n){
		for(Node current : n.getNeighbours()){
			if(current.hasStench()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Evaluates a single node and all surrounding nodes that have changed. 
	 * @param source
	 */
	public void evaluateFromSource(Node source){
		Queue<Node> touchedNodes = new LinkedList<>();
		touchedNodes.add(source);
		Node current = null;
		while(!touchedNodes.isEmpty()){
			current = touchedNodes.poll();
			if(isStenchNeutralizing(current)){
				for(Node n : current.getNeighbours()){
					if(!n.isVisited() && !n.isSafe()){
						n.setTrap(true);
						n.setVisited(true);
						n.setDangerous(true);
						
						touchedNodes.addAll(n.getNeighbours());
					}
				}
			}else{
				//check only fields that have smell. Stones/Traps/Unvisited have all smell 0 because we never visited them
				if(!current.isStone() && !current.isTrap() && current.isVisited() && current.getStench() - knownTraps(current) == 0){ //All unvisited neighbours must be safe
					for(Node n: current.getNeighbours() ){
						if(n.isDangerous() && !n.isTrap()){ 
							n.setDangerous(false);
							n.setSafe(true);
							touchedNodes.add(n);
						}
					}
				}
			}
			if(current.isDangerous() && hasNeighbourWithoutStench(current)){
				current.setDangerous(false);
				current.setSafe(true);
				touchedNodes.addAll(current.getNeighbours());
			}
		}
	}
	
	private boolean hasNeighbourWithoutStench(Node current){
		return current.getNeighbours().stream()
				.filter(Node::isVisited)
				.filter(n -> !n.isTrap())
				.filter(n -> !n.isStone())
				.filter(n -> !n.hasStench())
				.findAny().isPresent();
	}
	
	private int knownTraps(Node source){
		return (int)source.getNeighbours().stream().filter(Node::isTrap).count();
	}
	/**
	 * Indicates that all unknown neighbours must be traps
	 * @param source
	 * @return
	 */
	protected boolean isStenchNeutralizing(Node source){ //protected for JUnit Test
		int stench = source.getStench();
		int unknown = 0;
		for(Node n : source.getNeighbours()){
			if(!n.isVisited() && !n.isSafe()){
				++unknown;
			}
			if(n.isTrap() ){
				--stench;
			}
		}
		return (unknown - stench) == 0;
	}
}
