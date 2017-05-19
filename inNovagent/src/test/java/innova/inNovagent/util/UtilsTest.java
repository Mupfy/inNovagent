package innova.inNovagent.util;

import org.junit.Assert;
import org.junit.Test;

import innova.inNovagent.core.logic.Pathfinding.Direction;

public class UtilsTest {
	
	@Test
	public void testDirectionToPoint(){
		Point p = new Point(0,0);
		Direction d = Direction.UP;
		Assert.assertEquals(new Point(0,1), Utils.DirectionToPoint(d, p));
		
		d = Direction.DOWN;
		Assert.assertEquals(new Point(0,-1), Utils.DirectionToPoint(d, p));
		
		d = Direction.LEFT;
		Assert.assertEquals(new Point(-1,0), Utils.DirectionToPoint(d, p));
		
		d = Direction.RIGHT;
		Assert.assertEquals(new Point(1,0), Utils.DirectionToPoint(d, p));
	}
}
