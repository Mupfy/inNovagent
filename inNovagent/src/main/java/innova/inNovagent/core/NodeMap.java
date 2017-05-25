package innova.inNovagent.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import innova.inNovagent.util.Point;

public class NodeMap {
	private Map<Point, Node> field;
	
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;

	public NodeMap() {
		field = new HashMap<>();
		
		minX = 0;
		maxX = 0;
		minY = 0;
		maxY = 0;
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
		
		minX = point.getX() < minX ? point.getX() : minX;
		maxX = point.getX() > maxX ? point.getX() : maxX;
		minY = point.getY() < minY ? point.getY() : minY;
		maxY = point.getY() > maxY ? point.getY() : maxY;
		
		return res;
	}
	
	public Map<Point, Node> getField() {
		return field;
	}

	public Node getNode(int x, int y) {
		return this.field.get(new Point(x, y));
	}
	
	public Node getNode(Point point){
		return getNode(point.getX(), point.getY());
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}

	public int getWidth() {
		return maxX - minX + 1;
	}

	public int getHeight() {
		return maxY - minY + 1;
	}

}
