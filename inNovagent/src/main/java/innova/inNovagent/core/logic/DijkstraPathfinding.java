package innova.inNovagent.core.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;

import javax.swing.plaf.synth.SynthSpinnerUI;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Point;
import innova.inNovagent.util.Utils;

public class DijkstraPathfinding implements Pathfinding{
	
	
	
	private class Data{
		int distance = Integer.MAX_VALUE;
		Data ancestor = null;
		Node father = null;
		
		public Data setFather(Node n){
			this.father = n;
			return this;
		}
		
		public Data makeSource(){
			this.distance = 0;
			this.ancestor = this;
			return this;
		}
	}
	
	public DijkstraPathfinding(){
		this.nodeToData = new HashMap<>();
		this.unvisitedNodes = new PriorityQueue<>( (d1,d2) -> d1.distance - d2.distance);
		this.allNodes = new HashSet<>();
	}
	
	private Map<Node,Data> nodeToData;
	private PriorityQueue<Data> unvisitedNodes;
	private NodeMap currentMap;
	private Point currentSource;
	private Predicate<Node> nodeFilter;
	private Set<Data> allNodes;
	
	private boolean include(Node n){
		return this.nodeFilter == null ? true : this.nodeFilter.test(n);
	}
	
	private void initData(NodeMap map){
		nodeToData.clear();
		unvisitedNodes.clear();
		allNodes.clear();
		map.getField().values().stream()
		.filter(this::include)
		.forEach(node -> {
			Data d = new Data().setFather(node);
			nodeToData.put(node, d);
			unvisitedNodes.add(d);
			allNodes.add(d); 
		});
	}
	
	private void createSingleSourceShortestPath(NodeMap map, Point position){
		initData(map);
		Data source = nodeToData.get(map.getNode(position)).makeSource();
		this.unvisitedNodes.remove(source); //Update position
		this.unvisitedNodes.add(source); 
		
		while(!unvisitedNodes.isEmpty()){
			Data current = unvisitedNodes.poll();
			updateNeighbours(current);
		}
	}
	
	private void updateNeighbours(Data current){
		Set<Node> neighbours = current.father.getNeighbours();
		neighbours.stream()
		.map(nodeToData::get)
		.filter(unvisitedNodes::contains)
		.forEach( data -> {
			if(current.distance == Integer.MAX_VALUE){ // MaxValue + 1 would lead to overflow
				return;
			}
			int newDistance = current.distance + length(current, data);
			if(newDistance < data.distance){
				data.distance = newDistance;
				data.ancestor = current;
				unvisitedNodes.remove(data); //Update with new distance.
				unvisitedNodes.add(data);
			}
		});
	}
	
	private int length(Data one, Data two){
		return 1; //Distance of neighbours is one in the antworld-scenario;
		//But could add something like if two.father.isUnvisited * 2;
	}

	@Override
	public Direction getNextStep(Point target) {
		Data current = nodeToData.get(this.currentMap.getNode(target));
		Data source = nodeToData.get(this.currentMap.getNode(this.currentSource));
		
		while(current.ancestor != source){
			current = current.ancestor;
		}
		return Utils.PointToDirection(source.father.getPosition(), current.father.getPosition());
	}

	@Override
	public List<Node> getNearestUnvisited() {
		PriorityQueue<Data> queue = new PriorityQueue<>( (d1,d2) -> {
			
			if(d1.father.isVisited() && !d2.father.isVisited()){
				return 1;
			}
			
			if(d2.father.isVisited() && !d1.father.isVisited()){
				return -1;
			}
			return d1.distance - d2.distance;
		});
		queue.addAll(allNodes);
		
		List<Node> res = new ArrayList<>();
		Data current = queue.poll();
		while(current != null && !current.father.isVisited()){
			res.add(current.father);
			current = queue.poll();
		}
		return res;
		
	}

	@Override
	public void setNodeFilter(Predicate<Node> filter) {
		this.nodeFilter = filter;
	}

	@Override
	public void recalculateMap(NodeMap map, Point source) {
		this.currentMap = Utils.notNull(map);
		this.currentSource = Utils.notNull(source);
		createSingleSourceShortestPath(currentMap, currentSource);
	}
}
