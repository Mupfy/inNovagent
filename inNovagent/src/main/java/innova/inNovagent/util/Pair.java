package innova.inNovagent.util;

public class Pair<First, Second> {
	public First first;
	public Second second;
	
	// TODO unnötig?
	private Pair(First f, Second s){
		this.first = f;
		this.second = s;
	}
	
	public static <F,S> Pair<F,S> of(F first, S second){
		return new Pair<F,S>(first,second);
	}
}
