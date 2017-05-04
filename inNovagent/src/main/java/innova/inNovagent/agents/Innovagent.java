package innova.inNovagent.agents;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import innova.inNovagent.communication.AntWorldCommunicator;
import innova.inNovagent.communication.AntWorldCommunicatorFactory;
import innova.inNovagent.communication.AntWorldFlowController;
import innova.inNovagent.communication.AntWorldFlowControllerFactory;
import innova.inNovagent.communication.AntWorldMessageTranslator;
import innova.inNovagent.communication.AntWorldTranslatorFactory;
import innova.inNovagent.communication.NodeInformationTO;
import innova.inNovagent.core.Node;
import innova.inNovagent.util.Constants;
import innova.inNovagent.util.Point;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class Innovagent extends SyncMapAgent {
	
	private Logger LOGGER = Logger.getLogger(Innovagent.class);

	private static final AntWorldMessageTranslator TRANSLATOR = AntWorldTranslatorFactory.create();
	private final AntWorldCommunicator COMMUNICATOR = AntWorldCommunicatorFactory.create(this);
	private final AntWorldFlowController flowController = AntWorldFlowControllerFactory.create();
	
	private Point position;
	private Point lastPosition;
	private Point target;
	private String agentName;
	
	public Innovagent() {
		/*
		flowController.setOnDeathCallback( (x,y) -> System.out.print("DIED AT " + new Point(x,y) ));
		flowController.setOnSuccessfulMovement(this::movementSuccessful);
		flowController.setOnFailedMovement( this::movementFailed);
		flowController.setPositionGetter( () -> this.lastPosition);
		flowController.setMessageTranslator(TRANSLATOR);
		*/
	}
	
	@Override
	protected void setup() {
		super.setup();
		
	}
	
	public void onSync() {
		super.onSync();
		System.out.println("mhhhhhh");
		COMMUNICATOR.login();
	}
	
	@Override
	protected void onMapSynchronized() {
		
	}
	
	@Override
	protected void receiveDispatchedMessage(ACLMessage msg, JSONObject rootNode) {
		LOGGER.info(this.agentName + " received message with: " + rootNode.toString(4) );
		if(rootNode.has(Constants.INTERNAL_MESSAGE_TYPE) ){
			super.receiveDispatchedMessage(msg, rootNode);
		}else{
			//TODO Klärung der Bedingung, ob es eine AntWorld Message ist.
			//flowController.consumeMessage(rootNode);
			COMMUNICATOR.setLastMessage(msg);
			int d = (int)(Math.random() * 4);
			System.out.println("Direction: " + d);
			if(d == 1){
				COMMUNICATOR.moveDown();
			}else if(d == 2){
				COMMUNICATOR.moveUp();
			}else if(d == 3){
				COMMUNICATOR.moveLeft();
			}else{
				COMMUNICATOR.moveRight();
			}
			System.out.println("=====");
		}
	}
	
	private void movementSuccessful(NodeInformationTO data) {
		Node node = getMap().createOrGet(position);
		
		if(node.isVisited() && node.getHoneyAmount() == data.getHoney() ){
			return;
		}
		
		node.setVisited(true);
		applyDataToNode(node, data);
		shareAntWorldUpdate(Arrays.asList(node));
	}
	
	private void movementFailed(Point target) {
		Node node = getMap().createOrGet(target);
		node.setStone(true);
		node.setVisited(true);
		shareAntWorldUpdate(Arrays.asList(node));
	}
	
	private void applyDataToNode(Node node, NodeInformationTO data){
		node.setHoneyAmount(data.getHoney());
		node.setStench(data.getStench());
		node.setTrap(data.isTrap());
		node.setStone(data.isTrap());
		node.setSmell(data.getSmell());
	}
	
}
