package innova.inNovagent.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import innova.inNovagent.util.Point;

public class NodeMap {
	private Map<Point, Node> field;

	public NodeMap() {
		field = new HashMap<>();
	}
	
	public Node createOrGet(Point point) {

		Node res = field.get(point);
		if (res != null) {
			return res;
		}

		res = new Node(new Point(point));

		List<Point> points = Arrays.asList(
				new Point(point.getX(), point.getY() + 1),  //Create north node
				new Point(point.getX(), point.getY() - 1),  //Create south node
				new Point(point.getX() + 1, point.getY()),  //Create east node
				new Point(point.getX() - 1, point.getY())); //Create west node
		
		for(Point p : points){
			Node neighbour = field.get(p);
			if(neighbour != null){
				res.addNeighbour(neighbour);
				neighbour.addNeighbour(res);
			}
		}
		
		field.put(res.getPosition(), res);
		
		return res;
	}
	
	/**
	 * Returns a Node at the given position or null if not present.
	 * @param point
	 * @return A node or {@code null}
	 */
	public Node getNode(Point point){
		return this.field.get(point);
	}
	
	public Map<Point, Node> getField() {
		return field;
	}

}
