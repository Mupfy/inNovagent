package innova.inNovagent.agents;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import innova.inNovagent.util.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

/**
 * The base class of all inNovaGent-Agents. The {@link SynchronizedAgent}
 * will automatically seek other {@link SynchronizedAgent} in Jade and will
 * include them in his intern known agent list. 
 * It will also handle messages.
 */
@SuppressWarnings("serial")
public abstract class SynchronizedAgent extends Agent {

	private static final String SERVICE_NAME = "ANTWORLD_2017_INNOVAGENT_SERVICE";
	private static final Logger LOGGER = Logger.getLogger(SynchronizedAgent.class);
	private static final String SYNCRONIZED_AGENT_MESSAGE_TAG = "SYNCRONIZED_AGENT_MESSAGE_TAG";

	private static final String NEW_AGENT_TAG = "NEW_AGENT_TAG";
	private static final String REMOVE_AGENT_TAG = "REMOVE_AGENT_TAG";
	private static final String KNOWN_AGENTS_LIST_TAG = "KNOWN_AGENTS_LIST_TAG";
	private static final String REGISTER_AGENT_TAG = "REGISTER_AGENT_TAG";
	
	private static final String MESSAGE_TYPE = "MESSAGE_TYPE";
	
	private static final String AGENT_ID = "AGENT_ID";

	private Set<AID> knowAgents;

	private boolean service;

	public SynchronizedAgent() {
		this.knowAgents = new HashSet<>();
	}

	public Set<AID> getKnownAgents() {
		return this.knowAgents;
	}

	@Override
	protected void setup() {
		super.setup();
		
		initMessageConsumer();
		DFAgentDescription[] serviceDescription = findService();

		if (serviceDescription.length == 0) {
			initService();
			onSync();
		}else{
			JSONObject content = new JSONObject().put(MESSAGE_TYPE, REGISTER_AGENT_TAG).put(AGENT_ID, getName());
			sendInternalMessage(content, SYNCRONIZED_AGENT_MESSAGE_TAG, serviceDescription[0].getName());
		}
		
	}

	@Override
	protected void takeDown() {
		if (service) {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			try {
				DFService.deregister(this, dfd);
			} catch (FIPAException e) {
				LOGGER.error("Unable to deregister service", e);
			}
		}
		super.takeDown();
	}
	
	/**
	 * Called as soon this agent knows all its big brothers and little sisters.
	 */
	protected abstract void onSync();
	
	/**
	 * This method will be called if a message occurs and can't be handled by {@link SynchronizedAgent}
	 * @param msg
	 * @param content
	 */
	protected abstract void receiveDispatchedMessage(ACLMessage msg, JSONObject content);

	private void initMessageConsumer() {
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					consumeMessage(msg);
				} else {
					block();
				}
			}
		});
	}
	
	private boolean isMessageForMe(JSONObject contentNode){
		return contentNode.has(Constants.INTERNAL_MESSAGE_TYPE)
				&& contentNode.getString(Constants.INTERNAL_MESSAGE_TYPE).equals(SYNCRONIZED_AGENT_MESSAGE_TAG);
	}

	private void consumeMessage(ACLMessage msg) {
		if (Constants.JSON_TAG.equals(msg.getLanguage())) {
			JSONObject contentNode = new JSONObject(msg.getContent());
			if (isMessageForMe(contentNode)) {
				parseInformation(msg, contentNode);
			} else {
				receiveDispatchedMessage(msg, contentNode);
			}
		} else {
			LOGGER.error("Got unknown msg lang with " + msg.getLanguage() + "with msg: " + msg);
		}
		
	}

	private void parseInformation(ACLMessage orginalMessage, JSONObject aclMessageContent) {
		JSONObject content = aclMessageContent.getJSONObject(Constants.MESSAGE_CONTENT);
		if(!content.has(MESSAGE_TYPE) ){
			LOGGER.error("Unknows message type");
			return;
		}
		String type = content.getString(MESSAGE_TYPE);
		
		if(NEW_AGENT_TAG.equals(type) ){
			this.knowAgents.add(new AID(content.getString(AGENT_ID),true) );
			
		}else if(REMOVE_AGENT_TAG.equals(type) ){
			this.knowAgents.remove(new AID(content.getString(AGENT_ID),true) );
			
		}else if(REGISTER_AGENT_TAG.equals(type)){
			sendKnownAgents(orginalMessage);
			distributeNewAgent(content);
			this.knowAgents.add(orginalMessage.getSender());
			
		}else if(KNOWN_AGENTS_LIST_TAG.equals(type)){
			JSONArray agents = content.getJSONArray("agents");
			agents.forEach( id -> this.knowAgents.add(new AID(id.toString(), true)));
			onSync();
		}
		
	}
	
	private void sendKnownAgents(ACLMessage msg){
		ACLMessage reply = msg.createReply();
		JSONObject answer = new JSONObject();
		JSONArray agents = new JSONArray();
		this.knowAgents.forEach( agent -> agents.put(agent.getName()));
		agents.put(getName());
		
		answer.put(Constants.INTERNAL_MESSAGE_TYPE, SYNCRONIZED_AGENT_MESSAGE_TAG);
		answer.put(Constants.MESSAGE_CONTENT, new JSONObject().put(MESSAGE_TYPE, KNOWN_AGENTS_LIST_TAG).put("agents", agents));
		
		LOGGER.info("SENDING MESSAGE: " + answer.toString(4) );
		reply.setContent(answer.toString());
		send(reply);
	}
	
	private void distributeNewAgent(JSONObject obj){
		String newAgent = obj.getString(AGENT_ID);
		JSONObject content = new JSONObject().put(AGENT_ID, newAgent).put(MESSAGE_TYPE, NEW_AGENT_TAG);
		sendInternalMessage(content, SYNCRONIZED_AGENT_MESSAGE_TAG, knowAgents);
	}

	private void initService() {
		ServiceDescription desc = new ServiceDescription();
		desc.setType(SERVICE_NAME);
		desc.setName(SERVICE_NAME);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(desc);
		try {
			DFService.register(this, dfd);
			this.service = true;
		} catch (FIPAException e) {
			LOGGER.error("Could not register service", e);
			throw new RuntimeException(e);
		}
	}

	private DFAgentDescription[] findService() {
		ServiceDescription filter = new ServiceDescription();
		filter.setType(SERVICE_NAME);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(filter);
		try {
			DFAgentDescription[] results = DFService.search(this, dfd);
			return results;
		} catch (FIPAException e) {
			LOGGER.error(e);
			return null;
		}
	}
	
	/**
	 * Wraps the given content in the internal communication overhead.
	 * @param content 
	 * @param messageType
	 * @return Header with content payload.
	 */
	protected JSONObject applyHeader(JSONObject content, String messageType){
		JSONObject obj = new JSONObject();
		obj.put(Constants.INTERNAL_MESSAGE_TYPE, messageType);
		obj.put(Constants.MESSAGE_CONTENT, content);
		return obj;
	}
	
	protected void sendInternalMessage(JSONObject content, String messageType, Collection<AID> receiver){
		JSONObject obj = applyHeader(content, messageType);
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		for (AID r : receiver) {
			msg.addReceiver(r);
		}
		msg.setSender(getAID());
		msg.setLanguage(Constants.JSON_TAG);
		msg.setContent(obj.toString());
		send(msg);
		LOGGER.info("[MESSAGE SEND, content: "+obj.toString(4)+"]"); 
	}
	
	protected void sendInternalMessage(JSONObject content, String messageType, AID... receiver) {
		sendInternalMessage(content, messageType, Arrays.asList(receiver));
	}
}
