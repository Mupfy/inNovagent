package innova.inNovagent.core;

import org.apache.log4j.Logger;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public final class AgentLuncher {
	
	private static final String[] EMPTY_ARRAY = new String[0];
	private static final Logger LOGGER = Logger.getLogger(AgentLuncher.class);
	private static final AgentLuncher INSTANCE = new AgentLuncher();
	private static final Runtime JADE_RUNTIME = Runtime.instance();
	private Profile currentProfile;
	private AgentContainer targetJadeContainer;
	
	public static AgentLuncher instance(){
		return AgentLuncher.INSTANCE;
	}
	
	private AgentLuncher(){		
	}
	
	public AgentLuncher setIPAdress(String host){
		if(this.currentProfile != null){
			LOGGER.info("Created a new Profile over an existing one");
		}
		this.currentProfile = new ProfileImpl(host, -1, null, false);
		this.targetJadeContainer = JADE_RUNTIME.createAgentContainer(this.currentProfile);
		return this;
	}
	
	public AgentController createAgent(String agentName, Class<?> clazz){
		if(this.targetJadeContainer == null){
			LOGGER.error("No target container present");
			return null;
		}
		try {
			return this.targetJadeContainer.createNewAgent(agentName, clazz.getName(), EMPTY_ARRAY);
		} catch (StaleProxyException e) {
			LOGGER.error(e);
			return null;
		}
	}
	
	
}
