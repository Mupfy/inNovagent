package innova.inNovagent.core.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;

public class TrapScanner {
	public void evaluate(NodeMap map){
		markDangerous(map);
	}
	
	private void markDangerous(NodeMap map){
		Collection<Node> nodes = map.getField().values();
		for(Node n : nodes){
			if(hasNeighbourStench(n) && !n.isVisited()){
				n.setDangerous(true);
			}
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
}
