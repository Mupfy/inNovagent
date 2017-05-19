package innova.inNovagent.util;

import org.apache.log4j.Logger;

import innova.inNovagent.core.logic.Pathfinding.Direction;

public final class Utils {
	
	private Utils(){}
	
	public static <T> T notNull(T pivot, String message){
		if(pivot != null){
			return pivot;
		}
		throw new NullPointerException(message);
	}
	
	public static <T> T notNull(T pivot){
		if(pivot != null){
			return pivot;
		}
		throw new NullPointerException("Variable should not be null");
	}
	
	public static void consistentAgentLog(Logger log, String agentName, Object message){
		StringBuilder b = new StringBuilder();
		b.append("----START").append(System.lineSeparator())
		.append("#").append(agentName).append(": " + message).append(System.lineSeparator());
		b.append("----END");
		log.debug(b.toString());
	}
	
	public static Direction PointToDirection(Point source, Point p){
		int x = p.getX() - source.getX() ;
		int y = p.getY() - source.getY();
		
		if(x > 0){
			return Direction.RIGHT;
		}else if( x < 0){
			return Direction.LEFT;
		}else if( y > 0){
			return Direction.UP;
		}else if( y < 0){
			return Direction.DOWN;
		}else{
			throw new RuntimeException("Should not happen: " + new Point(x,y)); //TODO: Could add an enum CURRENT if you are at the position
		}
	}
	
	public static Point DirectionToPoint(Direction d, Point source){
		switch(d){
			case UP: return new Point(source.getX(), source.getY() + 1);
			case DOWN: return new Point(source.getX(), source.getY() - 1);
			case LEFT: return new Point(source.getX() - 1, source.getY());
			case RIGHT: return new Point(source.getX() + 1, source.getY());
			default: return null;
		}
	}
}
