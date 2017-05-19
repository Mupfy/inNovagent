package innova.inNovagent.core.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.core.logic.Pathfinding.Direction;
import innova.inNovagent.util.Point;

public class DijkstraPathfindingTest {
	
	private DijkstraPathfinding classUnderTest = new DijkstraPathfinding();
	
	private Point point(int x, int y){
		return new Point(x,y);
	}
	
	private NodeMap create3x3Map(){
		NodeMap m = new NodeMap();
		m.createOrGet(point(0,0));
		m.createOrGet(point(0,1));
		m.createOrGet(point(0,2));
		
		m.createOrGet(point(1,0));
		m.createOrGet(point(1,1));
		m.createOrGet(point(1,2));
		
		m.createOrGet(point(2,0));
		m.createOrGet(point(2,1));
		m.createOrGet(point(2,2));
		
		// X X X
		// X X X
 		// X X X
 		
		return m;
	}
	
	@Test
	public void testGetNextStep(){
		NodeMap map = create3x3Map();
		Point source = new Point(1, 1);
		Point left = new Point(0, 1);
		Point right = new Point(2, 1);
		Point up = new Point(1, 2);
		Point down = new Point(1, 0);
		
		classUnderTest.recalculateMap(map, source);
		
		assertEquals(Direction.DOWN, classUnderTest.getNextStep(down));
		assertEquals(Direction.LEFT, classUnderTest.getNextStep(left));
		assertEquals(Direction.RIGHT, classUnderTest.getNextStep(right));
		assertEquals(Direction.UP, classUnderTest.getNextStep(up));
		
		source = new Point(0,0);
		classUnderTest.recalculateMap(map, source);
		
		assertEquals(Direction.UP, classUnderTest.getNextStep(point(0, 2)));
	}
	
	@Test
	public void testGetNearestUnknown(){
		NodeMap map = create3x3Map();
		for(Node n : map.getField().values() ){
			n.setVisited(true);
		}
		Point source = new Point(1,1);
		map.getNode(point(0,0) ).setVisited(false);
		classUnderTest.recalculateMap(map, source);
		
		assertEquals(point(0,0), classUnderTest.getNearestUnvisited().get(0).getPosition());
		
		map.getNode(point(0,0) ).setVisited(false);
		map.getNode(point(1,0) ).setVisited(false);
		map.getNode(point(2,0) ).setVisited(false);
		
		assertEquals(3, classUnderTest.getNearestUnvisited().size());
	}
}
