package innova.inNovagent.core.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Point;

public class TrapScannerTest {
	
	private TrapScanner classUnderTest = new TrapScanner();
	private NodeMap createMap(int width, int height){
		NodeMap res = new NodeMap();
		for(int x = 0; x < width; ++x){
			for(int y = 0;  y < height; ++y){
				res.createOrGet(new Point(x,y));
			}
		}
		return res;
	}
	
	@Test
	public void testIsStenchNeutralizingNoUnknownNoStench(){
		NodeMap map = createMap(3, 3);
		map.getField().values().stream().forEach( n -> n.setVisited(true));
		Node source = map.getNode(1,1);
		
		boolean res = classUnderTest.isStenchNeutralizing(source);
		assertTrue(res);
		
	}
	
	@Test
	public void testIsStenchNeutralizingTwoUnknownTwoStench(){
		NodeMap map = createMap(3, 3);
		map.getNode(1, 1).setVisited(true);
		map.getNode(1, 0).setVisited(true);
		map.getNode(2, 1).setVisited(true);
		Node src = map.getNode(1,1);
		src.setStench(2);
		
		boolean res = classUnderTest.isStenchNeutralizing(src);
		assertTrue(res);
	}
	
	@Test
	public void testIsStenchNeutralizingThreeUnknownTwoStench(){
		NodeMap map = createMap(3, 3);
		map.getNode(1, 1).setVisited(true);
		map.getNode(1, 0).setVisited(true);
		Node src = map.getNode(1,1);
		src.setStench(2);
		
		boolean res = classUnderTest.isStenchNeutralizing(src);
		assertFalse(res);
	}
	
	@Test
	public void testRainDropEffectWithSingleStartingSource(){
		// U U V U U | Y:4
		// U T 2 T U | Y:3
		// V 2 S 2 V | Y:2
		// U T 2 T U | Y:1
		// U U V U U | Y:0
		// u = unvisited | v = visited | t = trap | {number} = stench
		//In this scenario are magical visited nodes that should start a chain-reaction and mark all traps. The magical nodes could be found via luck or another algorithm
		NodeMap map = createMap(5,5);
		map.getNode(2, 0).setVisited(true);
		map.getNode(2, 1).setVisited(true).setStench(2);
		map.getNode(2, 2).setVisited(true);
		map.getNode(2, 3).setVisited(true).setStench(2);
		map.getNode(2, 4).setVisited(true);
		
		map.getNode(0, 2).setVisited(true);
		map.getNode(1, 2).setVisited(true).setStench(2);
		map.getNode(2, 2).setVisited(true);
		map.getNode(3, 2).setVisited(true).setStench(2);
		map.getNode(4, 2).setVisited(true);
		
		classUnderTest.evaluateFromSource(map.getNode(2, 3));
		
		assertTrue(map.getNode(1, 1).isTrap());
		assertTrue(map.getNode(3, 1).isTrap());
		assertTrue(map.getNode(1, 3).isTrap());
		assertTrue(map.getNode(3, 3).isTrap());
	}
}
