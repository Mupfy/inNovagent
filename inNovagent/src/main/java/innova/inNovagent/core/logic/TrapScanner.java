package innova.inNovagent.core.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;

public class TrapScanner {
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
			if(current.hasStench() ){
				return true;
			}
		}
		return false;
	}
	
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
						n.setDangerous(true); //TODO:  is a trap sill dangerous?
						
						touchedNodes.addAll(n.getNeighbours());
					}
				}
			}else{
				//check only fields that have smell. Stones/Traps/Unvisited have all smell 0 because we never visited them
				if(!current.isStone() && !current.isTrap() && current.isVisited() && current.getStench() - knownTraps(current) == 0){ //All unvisited neighbours must be safe
					for(Node n: current.getNeighbours() ){
						if(n.isDangerous() && !n.isTrap()){ 
							n.setDangerous(false); //The Node is still unvisited and could be marked as dangerous again. Could make Node have a field safe
							n.setSafe(true);
							if(!touchedNodes.contains(n)){ // TODO eric hasst sich (zu recht)
								touchedNodes.add(n);
							}
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
	
	private int getAccumulatedStench(Node source){
		int res = 0;
		for(Node n : source.getNeighbours() ){
			res += n.getStench();
		}
		return res;
	}
	
	protected void placeholder_name(Node source){
		Set<Node> touchedNodes = new HashSet<>();
		Stack<Node> evaluationStack = new Stack<>();
		evaluationStack.push(source);
		Node current = null;
		while(!evaluationStack.isEmpty() ){
			current = evaluationStack.peek();
			touchedNodes.add(current);
			if(isEvaluationReady(current) ){
				evaluationStack.pop();
				evaluateFromSource(current);
			}else{
				current.getNeighbours().stream()
				.filter( n -> !touchedNodes.contains(n) )
				.forEach( n -> {
					evaluationStack.push(null);
					touchedNodes.add(n);
				});
			}
		}
	}
	
	protected boolean isEvaluationReady(Node n){
		return n.isVisited() || n.isTrap() || isStenchNeutralizing(n);
	}
	
	private boolean isAlternativePossible(NodeMap original, Set<Node> alternative){
		for(Node alt : alternative){
			Node n = original.getNode(alt.getPosition());
			if(n.getStench() != alt.getStench()){
				return false;
			}
		}
		return true;
	}
	
	private void createAlternative(NodeMap original, Node source){
		Set<Node> unknowns = source.getNeighbours().stream().filter( n -> !n.isVisited()).collect(Collectors.toSet());
		for(Node n : unknowns){
			NodeMap alternative = new NodeMap();
			
		}
	}
	private Set<Node> getDifference(Set<Node> all, Node without){
		return all.stream().filter( n -> n != without).collect(Collectors.toSet());
	}
}
