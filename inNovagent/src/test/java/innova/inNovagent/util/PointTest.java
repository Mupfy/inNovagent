package innova.inNovagent.util;

import org.junit.Assert;
import org.junit.Test;

public class PointTest {
	
	private Point classUnderTest;
	
	@Test
	public void testHashEquals(){
		classUnderTest = new Point(8,8);
		Point p = new Point(8,8);
		
		Assert.assertEquals(p, classUnderTest);
		
	}
	
	@Test
	public void testHashNotEquals(){
		classUnderTest = new Point(0,0);
		Point p = new Point(8,8);
		
		
		Assert.assertNotEquals(p, classUnderTest);
	}
}
