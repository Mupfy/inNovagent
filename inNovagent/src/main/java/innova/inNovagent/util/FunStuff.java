package innova.inNovagent.util;

import java.util.HashMap;
import java.util.Map;

/**
 * The inner child class.
 *
 */
public class FunStuff {
	
	private static final String[] AGENTNAMES = {
			"Innovagent_Antman",
			"Innovagent_Antlord",
			"Innovagent_Antastisch",
			"Innovagent_Antnastsia",
			"Innovagent_AntGueltig",
			"Innovagent_Antlager",
			"Innovagent_Antgent47",
			"Innovagent_Antgent007",
			"Innovagent_Antschlossen"
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
