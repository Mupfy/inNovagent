package innova.inNovagent.agents;

import java.awt.Transparency;
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

//TODO: Kein error-handling verhanden, falls z.B. ein pick kommt und dieser bereits aufgehoben wurde.
public class Innovagent extends SyncMapAgent {
	
	private interface AgentState{
		public Point getNextTarget();
		public void reachedNode(Node n);
		public boolean skipMovement();
	}
	
	private class StateScouting implements AgentState{
		
		private boolean skip;
		
		public Point getNextTarget(){
			List<Node> foodNodes = pathfinding.getNearest(Node::hasHoney);
			if(!foodNodes.isEmpty()){
				Utils.consistentAgentLog(LOGGER, agentName, "food found. Go to nearest one");
				int index = (int)(Math.random() * foodNodes.size());
				return foodNodes.get(index).getPosition(); // TODO: Choose to visit which node smarter.
			}
			
			List<Node> unknownNodes = pathfinding.getNearest( node -> !node.isVisited());
			if(!unknownNodes.isEmpty()){
				Utils.consistentAgentLog(LOGGER, agentName, " no food found. Go to nearest unknown");
				int index = (int)(Math.random() * unknownNodes.size());
				return unknownNodes.get(index).getPosition();
			}
			
			Utils.consistentAgentLog(LOGGER, agentName, " no food and unknwon found. Go to start");
			return START_POSITION;
		}
		
		public void reachedNode(Node n){
			if( !carryingFood && n.hasHoney() ){
				skip = true;
				COMMUNICATOR.pickUp();
				return;
			}
			skip = false;
			System.out.println("NOT SKIPPING MVNT");
		}
		
		public boolean skipMovement(){
			return skip;
		}
	}
	
	private class StateCarrying implements AgentState{
		
		private boolean skip;
		
		public Point getNextTarget(){
			return START_POSITION;
		}
		
		public void reachedNode(Node n){
			System.out.println("carrying: " + carryingFood);
			System.out.println("pos: " + n);
			if(carryingFood && START_POSITION.equals(n.getPosition())){
				System.out.println("dropping");
				COMMUNICATOR.drop();
				skip = true;
				return;
			}
			
			skip = false;
		}
		
		public boolean skipMovement(){
			return skip;
		}
	}
	
	private class StateDead implements AgentState{
		public Point getNextTarget(){
			return START_POSITION;
		}
		
		public void reachedNode(Node n){
			
		}
		
		public boolean skipMovement(){
			return true;
		}
	}
		
	private Logger LOGGER = Logger.getLogger(Innovagent.class);

	private static final AntWorldMessageTranslator TRANSLATOR = AntWorldTranslatorFactory.create();
	private final AntWorldCommunicator COMMUNICATOR = AntWorldCommunicatorFactory.create(this);
	private final AntWorldFlowController flowController = AntWorldFlowControllerFactory.create();
	
	private static final Point START_POSITION = new Point(0,0);
	private Point targetPosition;
	private Point lastPosition;
	private Point position;
	private String agentName;
	private Pathfinding pathfinding;
	private AgentState currentState;
	private boolean carryingFood;
	
	
	public Innovagent() {
		
		flowController.setOnDeathCallback( () -> 
		{
			Node data = this.nodeMap.createOrGet(position).setTrap(true);
			shareAntWorldUpdate(Arrays.asList(data));
			currentState = new StateDead(); // TODO in konstanten verschieben
		});
		flowController.setOnSuccessfulMovement(this::movementSuccessful);
		flowController.setOnFailedMovement( this::movementFailed);
		flowController.setOnDropCallback( () -> {
			carryingFood = false;
			System.out.println("drop");
			currentState = new StateScouting(); //TODO konstante
			tryMoveing();
		});
		flowController.setOnPickCallback( () -> {
			System.out.println("pickup");
			carryingFood = true;
			Node node = nodeMap.getNode(position);
			node.setHoneyAmount(node.getHoneyAmount() -1 );
			shareAntWorldUpdate( Arrays.asList(node));
			currentState = new StateCarrying(); //TODO konstante
			tryMoveing();
		});
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
		this.currentState = new StateScouting();
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
	
	private Node basicFailedMovement(){
		Node node = getMap().createOrGet(position);
		node.setStone(true);
		node.setVisited(true);
		this.position = lastPosition;
		shareAntWorldUpdate(Arrays.asList(node));
		return node;
	}
	
	private Node basicSuccessfulMovemnt(NodeInformationTO data){
		this.lastPosition = position;
		Node node = getMap().createOrGet(position);
		if(!(node.isVisited() && node.getHoneyAmount() == data.getHoney() )){ // Is this a new node or did the honey amount change.
			node.setVisited(true);
			applyDataToNode(node, data);
			expandNode(node);
			Collection<Node> nodes = new ArrayList<>(node.getNeighbours());
			nodes.add(node);
			shareAntWorldUpdate(nodes);
		}
		return node;
	}
	
	private void moveToDirection(Direction d){
		System.out.println("move Direction: " + d);
		switch(d){
			case UP: COMMUNICATOR.moveUp(); break;
			case DOWN: COMMUNICATOR.moveDown(); break;
			case LEFT: COMMUNICATOR.moveLeft(); break;
			case RIGHT: COMMUNICATOR.moveRight(); break;
		}
	}
	
	private void tryMoveing(){
		pathfinding.recalculateMap(nodeMap, position);
		this.targetPosition = currentState.getNextTarget();
		Utils.consistentAgentLog(LOGGER, agentName, " targeting " + targetPosition);
		Direction d = pathfinding.getNextStep(targetPosition);
		this.position = Utils.DirectionToPoint(d, position);
		moveToDirection(d);
	}
	
	
	private void movementSuccessful(NodeInformationTO data) {
		Node node = basicSuccessfulMovemnt(data);
		Utils.consistentAgentLog(LOGGER, this.agentName, "Agent succesful reached: " + this.lastPosition);
		currentState.reachedNode(node);
		if(currentState.skipMovement()){
			return;
		}
		tryMoveing();
	}
	
	private void movementFailed() {
		basicFailedMovement();
		Utils.consistentAgentLog(LOGGER, this.agentName, "failed to move to "+this.position+" [STONE|BORDER]");
		tryMoveing();
	}
	
	private void applyDataToNode(Node node, NodeInformationTO data){
		node.setHoneyAmount(data.getHoney());
		node.setStench(data.getStench());
		node.setTrap(data.isTrap());
		node.setStone(data.isTrap());
		node.setSmell(data.getSmell());
	}
	
}
