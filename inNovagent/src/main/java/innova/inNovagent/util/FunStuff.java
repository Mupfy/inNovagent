package innova.inNovagent.util;

import java.util.HashMap;
import java.util.Map;


public class FunStuff {
	
	private static final String[] AGENTNAMES = {
			"Antman",
			"Antlord",
			"Antastisch",
			"Antnastsia",
			"AntGueltig",
			"Antlager",
			"Antgent47",
			"Antgent007",
			"Antschlossen"
	};
	private static final Map<String, Integer> GIVEN_AGENT_NAMES = new HashMap<>();
	public static String createNameForAgent(){
		
		int i = (int)(Math.random() * AGENTNAMES.length);
		String name = AGENTNAMES[i];
		Integer value = GIVEN_AGENT_NAMES.get(name);
		if(value != null && value != 0){
			GIVEN_AGENT_NAMES.put(name, value + 1);
			return name + "#" + (value + 1);
		}else{
			GIVEN_AGENT_NAMES.put(name, 1);
			return name;
		}
	}
}
