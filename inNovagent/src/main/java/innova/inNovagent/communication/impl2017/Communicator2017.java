package innova.inNovagent.communication.impl2017;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import innova.inNovagent.communication.AntWorldCommunicator;
import innova.inNovagent.util.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


public class Communicator2017 implements AntWorldCommunicator{
	
	private static final Logger LOGGER  = Logger.getLogger(Communicator2017.class);
	
	private Agent agent;
	private AID antworld2017Agent;
	private ACLMessage lastMessage;
	
	public Communicator2017(Agent agent){
		this.agent = agent;
		
	}

	@Override
	public void moveUp() {
		move("ANT_ACTION_GOUP");
	}

	@Override
	public void moveDown() {
		move("ANT_ACTION_GODOWN");
	}

	@Override
	public void moveLeft() {
		move("ANT_ACTION_GOLEFT");
	}

	@Override
	public void moveRight() {
		move("ANT_ACTION_GORIGHT");
	}

	@Override
	public void pickUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void login() {
		this.antworld2017Agent = findAntWorld();
		JSONObject birthRequest = new JSONObject();
		birthRequest
		.put("type", "ANT_ACTION_BIRTH")
		.put("color", "ANT_COLOR_BLUE");
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(antworld2017Agent);
		msg.setContent(birthRequest.toString());
		msg.setLanguage(Constants.JSON_TAG);
		agent.send(msg);
	}
	
	private void move(String direction){
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(antworld2017Agent);
		msg.setInReplyTo(lastMessage.getReplyWith());
		msg.setLanguage(Constants.JSON_TAG);
		
		JSONObject content = new JSONObject().put("type", direction);
		content.put("color", "ANT_COLOR_BLUE");
		msg.setContent( content.toString());
		this.agent.send(msg);
		LOGGER.info("Sending ACL-Message for " + this.agent + " with " + content.toString(4) + " WHOTE MSG: " + msg);
	}
	
	private AID findAntWorld(){
		ServiceDescription filter = new ServiceDescription();
		filter.setType("antworld2017");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(filter);
		System.out.println(agent);
		try {
			System.out.println(dfd);
			System.out.println("...");
			DFAgentDescription[] results = DFService.search(agent, dfd);
			System.out.println("... 2");
			System.out.println(results);
			return results[0].getName();
		} catch (FIPAException e) {
			LOGGER.error(e);
			return null;
		}
	}

	@Override
	public void setLastMessage(ACLMessage msg) {
		this.lastMessage = msg;
	}
}
