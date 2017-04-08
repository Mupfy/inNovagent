package innova.inNovagent.util;

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
}
