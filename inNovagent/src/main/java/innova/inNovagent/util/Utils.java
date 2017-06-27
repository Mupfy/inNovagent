package innova.inNovagent.util;

import org.apache.log4j.Logger;

import innova.inNovagent.core.logic.Pathfinding.Direction;

public final class Utils {
	
	private Utils(){}
	
	// TODO unnötig?
	/**
	 * Returns the object if it's not null, otherwise it throws a
	 * NullPointerException.
	 * 
	 * @param pivot
	 *            the object
	 * @param message
	 *            the error-message for the NullPointerException
	 * @return the object, if it's not null, otherwise nothing
	 */
	public static <T> T notNull(T pivot, String message) {
		if (pivot != null) {
			return pivot;
		}
		throw new NullPointerException(message);
	}

	/**
	 * Returns the object if it's not null, otherwise it throws a
	 * NullPointerException.
	 * 
	 * @param pivot
	 *            the object
	 * @return the object, if it's not null, otherwise nothing
	 */
	public static <T> T notNull(T pivot) {
		if(pivot != null){
			return pivot;
		}
		throw new NullPointerException("Variable should not be null");
	}
	
	/**
	 * Consistently logs the messages with the logger.
	 * 
	 * @param logger
	 *            the logger which logs the message
	 * @param agentName
	 *            the name of the agent
	 * @param message
	 *            the message to be logged
	 */
	public static void consistentAgentLog(Logger logger, String agentName, Object message){
		StringBuilder b = new StringBuilder();
		b.append("----START").append(System.lineSeparator())
		.append("#").append(agentName).append(": " + message).append(System.lineSeparator());
		b.append("----END");
		logger.debug(b.toString());
	}

	// TODO warum in utils? wird nur an einer stelle gebraucht
	/**
	 * Calculates and returns the orthogonal next direction on a grid you have
	 * to follow to get to the point.
	 * 
	 * @param source
	 *            the position from where you start
	 * @param p
	 *            the goal
	 * @return the direction you have to follow
	 */
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
			return Direction.UNKNOWN;
		}
	}
	
	// TODO warum in utils? wird nur an einer stelle gebraucht
	/**
	 * Calculates a new point from a direction and a point.
	 * 
	 * @param d
	 *            the direction where you want to move
	 * @param source
	 *            the point from where you want to move
	 * @return the new point
	 */
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
