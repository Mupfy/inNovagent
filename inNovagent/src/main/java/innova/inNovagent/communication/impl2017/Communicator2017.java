package innova.inNovagent.communication.impl2017;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import innova.inNovagent.communication.AntWorldCommunicator;
import innova.inNovagent.util.Constants;
import innova.inNovagent.util.Utils;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

/**
 * Sends messages to antWorld2017.
 */
public class Communicator2017 implements AntWorldCommunicator {
	
	private static final Logger LOGGER  = Logger.getLogger(Communicator2017.class);
	
	private Agent agent;
	private AID antworld2017Agent;
	private ACLMessage lastMessage;
	private String agentColor;
	
	public Communicator2017(Agent agent){
		this.agent = agent;
		this.agentColor = "ANT_COLOR_BLUE";
	}

	@Override
	public void moveUp() {
		action("ANT_ACTION_GOUP");
	}

	@Override
	public void moveDown() {
		action("ANT_ACTION_GODOWN");
	}

	@Override
	public void moveLeft() {
		action("ANT_ACTION_GOLEFT");
	}

	@Override
	public void moveRight() {
		action("ANT_ACTION_GORIGHT");
	}

	@Override
	public void pickUp() {
		action("ANT_ACTION_PICK");
	}

	@Override
	public void drop() {
		action("ANT_ACTION_DROP");
	}

	@Override
	public void login() {
		this.antworld2017Agent = findAntWorld();
		JSONObject birthRequest = new JSONObject();
		birthRequest
		.put("type", "ANT_ACTION_BIRTH")
		.put("color", this.agentColor );
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(antworld2017Agent);
		msg.setContent(birthRequest.toString());
		msg.setLanguage(Constants.JSON_TAG);
		agent.send(msg);
	}
	
	private void action(String command){
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(antworld2017Agent);
		msg.setInReplyTo(lastMessage.getReplyWith());
		msg.setLanguage(Constants.JSON_TAG);
		
		JSONObject content = new JSONObject().put("type", command);
		content.put("color", this.agentColor);
		msg.setContent( content.toString());
		this.agent.send(msg);
		Utils.consistentAgentLog(LOGGER, agent.getLocalName(), "sending ACL-Message with: " + content.toString(4) + " GENERATED ACL message " + msg);
	}
	
	private AID findAntWorld(){
		ServiceDescription filter = new ServiceDescription();
		filter.setType("antworld2017");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(filter);
		
		try {			
			DFAgentDescription[] results = DFService.search(agent, dfd);
			if(results.length < 1){
				LOGGER.error("Antworld not found");
				return null;
			}else{
				return results[0].getName();
			}
		} catch (FIPAException e) {
			LOGGER.error(e);
			return null;
		}
	}

	@Override
	public void setLastMessage(ACLMessage msg) {
		this.lastMessage = msg;
	}

	@Override
	public void setAgentColor(String colorOfAgent) {
		this.agentColor = colorOfAgent;
	}
}
