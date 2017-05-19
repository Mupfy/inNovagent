package innova.inNovagent.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
import innova.inNovagent.core.logic.DijkstraPathfinding;
import innova.inNovagent.core.logic.Pathfinding;
import innova.inNovagent.core.logic.Pathfinding.Direction;
import innova.inNovagent.util.Constants;
import innova.inNovagent.util.Point;
import innova.inNovagent.util.Utils;
import jade.core.CaseInsensitiveString;
import jade.lang.acl.ACLMessage;

//TODO: Agent stürzt ab  alle felder besucht sind. Weil Direction zu Position eine Exception schmeißt denn pos == 0,0 
public class Innovagent extends SyncMapAgent {
	
	private Logger LOGGER = Logger.getLogger(Innovagent.class);

	private static final AntWorldMessageTranslator TRANSLATOR = AntWorldTranslatorFactory.create();
	private final AntWorldCommunicator COMMUNICATOR = AntWorldCommunicatorFactory.create(this);
	private final AntWorldFlowController flowController = AntWorldFlowControllerFactory.create();
	
	private Point targetPosition;
	private Point lastPosition;
	private Point position;
	private String agentName;
	private Pathfinding pathfinding;
	
	public Innovagent() {
		
		flowController.setOnDeathCallback( () -> 
		{
			Node data = this.nodeMap.createOrGet(position).setTrap(true);
			shareAntWorldUpdate(Arrays.asList(data));
		});
		flowController.setOnSuccessfulMovement(this::movementSuccessful);
		flowController.setOnFailedMovement( this::movementFailed);
		flowController.setMessageTranslator(TRANSLATOR);
		this.pathfinding = new DijkstraPathfinding();
		this.pathfinding.setNodeFilter( node -> !node.isStone() && !node.isTrap());
		
	}
	
	@Override
	protected void setup() {
		super.setup();
		this.agentName  = getLocalName();
		this.targetPosition = new Point(0,0);
		this.lastPosition = new Point(0,0);
		this.position = new Point(0,0);
	}
	
	public void onSync() {
		super.onSync();
		COMMUNICATOR.login();
	}
	
	@Override
	protected void onMapSynchronized() {
		
	}
	
	@Override
	protected void receiveDispatchedMessage(ACLMessage msg, JSONObject rootNode) {
		Utils.consistentAgentLog(LOGGER, agentName, "received message with: " + rootNode.toString(4) );
		if(rootNode.has(Constants.INTERNAL_MESSAGE_TYPE) ){
			super.receiveDispatchedMessage(msg, rootNode);
		}else{
			COMMUNICATOR.setLastMessage(msg);
			flowController.consumeMessage(rootNode);
			String state = rootNode.getString("state");
			System.out.println(state);
			if(!state.equals("ALIVE")){
				System.out.println("raus");
				
				return;
			}
			
			System.out.println(this.agentName + " pos: " + this.targetPosition);
		}
	}
	
	/**
	 * Expands the given node.
	 * All possible neighbours of the node will be created if not already and set to unvisited.
	 * @param source
	 */
	private void expandNode(Node source){
		Point point = source.getPosition();
		List<Point> points = Arrays.asList(
				new Point(point.getX(), point.getY() + 1),  //Create north node
				new Point(point.getX(), point.getY() - 1),  //Create south node
				new Point(point.getX() + 1, point.getY()),  //Create east node
				new Point(point.getX() - 1, point.getY())); //Create west node
		
		for(Point p : points){
			Node neighbour = getMap().getNode(p);
			if(neighbour == null){
				neighbour = getMap().createOrGet(p);
				neighbour.setVisited(false);
			}
		}
	}
	
	private Point calculateTarget(){
		this.pathfinding.recalculateMap(nodeMap, this.position);
		List<Node> nodes = this.pathfinding.getNearestUnvisited();
		int i = (int)(Math.random() * nodes.size()); //TODO: Kein random mehr, absprache mit anderen Agenten,

		//Point res = nodes.isEmpty() ? new Point(0,0) : nodes.get(i).getPosition(); // TODO: remove Return to start
		Point res = nodes.isEmpty() ? new Point(0,0) : nodes.get(0).getPosition(); //Wahrscheinlich noch fehler in getNearestUnknwon.
		Utils.consistentAgentLog(LOGGER, agentName, "Tries to move to " + res);
		return res;
	}
	
	private void movementSuccessful(NodeInformationTO data) {
		this.lastPosition = position;
		Node node = getMap().createOrGet(position); //TODO:  sollte nicht mehr richtig sein. Target kann mehrere Felder weg sein. Diese Methode wird aber für jeden erfolgreichen schritt aufgerufen
		
		if(!(node.isVisited() && node.getHoneyAmount() == data.getHoney() )){
			node.setVisited(true);
			applyDataToNode(node, data);
			expandNode(node);
			Collection<Node> nodes = new ArrayList<>(node.getNeighbours());
			nodes.add(node);
			shareAntWorldUpdate(nodes);
		}
		
		
		this.targetPosition = calculateTarget();
	
		Direction d = pathfinding.getNextStep(targetPosition);
		this.position = Utils.DirectionToPoint(d, position);
		System.out.println("target: " + this.targetPosition);
		System.out.println("position: " + this.position);
		switch(d){
			case UP: COMMUNICATOR.moveUp(); break;
			case DOWN: COMMUNICATOR.moveDown(); break;
			case LEFT: COMMUNICATOR.moveLeft(); break;
			case RIGHT: COMMUNICATOR.moveRight(); break;
		}
		
		
		Utils.consistentAgentLog(LOGGER, this.agentName, "Agent succesful reached: " + this.lastPosition + ", new target: " + this.targetPosition);
	}
	
	private void movementFailed() {
		Utils.consistentAgentLog(LOGGER, this.agentName, "failed to move to "+this.position+" [STONE|BORDER]");
		Node node = getMap().createOrGet(position);
		node.setStone(true);
		node.setVisited(true);
		
		this.position = lastPosition;
		this.targetPosition = calculateTarget();
		Direction d = pathfinding.getNextStep(targetPosition);
		this.position = Utils.DirectionToPoint(d, position);
		System.out.println("target: " + this.targetPosition);
		System.out.println("position: " + this.position);
		switch(d){
			case UP: COMMUNICATOR.moveUp(); break;
			case DOWN: COMMUNICATOR.moveDown(); break;
			case LEFT: COMMUNICATOR.moveLeft(); break;
			case RIGHT: COMMUNICATOR.moveRight(); break;
		}
		
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
