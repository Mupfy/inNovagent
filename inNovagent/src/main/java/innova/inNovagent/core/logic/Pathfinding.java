package innova.inNovagent.core.logic;

import java.util.List;
import java.util.function.Predicate;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Point;

/**
 * Can create a single source shortest path algorithm.
 * Some Node types can be filtered by {@link #setNodeFilter(Predicate)}.
 * The object holds a state of a current map configuration and
 *  should be recalculated after changes with {@link #recalculateMap(NodeMap, Point)}.
 */
public interface Pathfinding {

	public enum Direction {UP, DOWN, LEFT, RIGHT, UNKNOWN}
	
	/**
	 * Filter-function that prohibit nodes that evaluate to true.
	 * @param filter
	 */
	public void setNodeFilter(Predicate<Node> filter);
	
	/**
	 * Resets the state of the object and updates the path with all new informations.
	 * @param map
	 * @param source
	 */
	public void recalculateMap(NodeMap map, Point source);
	
	/**
	 * Returns the next orthogonal point that leads to the given target.
	 * @param target
	 * @return 
	 */
	public Direction getNextStep(Point target);
	
	/**
	 * Returns a list of the nodes with the shortest path which fulfill the given filter.
	 * @param include
	 * @return
	 */
	public List<Node> getNearest(Predicate<Node> include);
	
}
