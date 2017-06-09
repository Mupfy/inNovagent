package innova.inNovagent.util;

public class Pair<First, Second> {
	
	private Pair(First f, Second s){
		this.first = f;
		this.second = s;
	}
	
	public First first;
	public Second second;
	
	public static <F,S> Pair<F,S> of(F first, S second){
		return new Pair<F,S>(first,second);
	}
}
