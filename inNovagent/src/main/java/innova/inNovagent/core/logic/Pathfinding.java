package innova.inNovagent.core.logic;

import java.util.List;
import java.util.function.Predicate;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Point;

public interface Pathfinding {
	
	public enum Direction {UP, DOWN, LEFT, RIGHT, UNKNOWN}
	
	public void setNodeFilter(Predicate<Node> filter);
	public void recalculateMap(NodeMap map, Point source);
	public Direction getNextStep(Point target);
	public List<Node> getNearestUnvisited();
	public List<Node> getNearest(Predicate<Node> include);
	
}
