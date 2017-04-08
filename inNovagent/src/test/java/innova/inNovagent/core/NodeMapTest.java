package innova.inNovagent.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import innova.inNovagent.util.Point;

public class NodeMapTest {
	
	private NodeMap classUnderTest;
	
	@Test
	public void testNeighbourSetup() {
		classUnderTest = new NodeMap();
		classUnderTest.createOrGet(new Point(0, 0));
		classUnderTest.createOrGet(new Point(0, 1));
		classUnderTest.createOrGet(new Point(0, 2));
		assertFalse(classUnderTest.getField().isEmpty());
		Node former = null;
		for (Node node : classUnderTest.getField().values()) {
			assertFalse(node.getNeighbours().isEmpty());
			if(former != null){
				assertTrue(former.getNeighbours().contains(node) && node.getNeighbours().contains(former));
			}
			former = node;
		}
	}
}
