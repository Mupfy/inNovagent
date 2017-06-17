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
import innova.inNovagent.core.logic.TrapScanner;
import innova.inNovagent.util.Constants;
import innova.inNovagent.util.Point;
import innova.inNovagent.util.Utils;
import jade.lang.acl.ACLMessage;

//TODO: Kein error-handling verhanden, falls z.B. ein pick kommt und dieser bereits aufgehoben wurde.
/**
 * The ant which moves on the field.
 */
public class Innovagent extends SyncMapAgent {
	
	private interface AgentState{
		public Point getNextTarget();
		public void reachedNode(Node n);
		public boolean skipMovement();
	}
	
	private class StateScouting implements AgentState{
		
		private boolean skip;
		
		public Point getNextTarget(){
			Innovagent.this.pathfinding.setNodeFilter( node -> !node.isStone() && !node.isTrap() && !node.isDangerous());
			pathfinding.recalculateMap(getMap(), position);
			
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
			
			Innovagent.this.pathfinding.setNodeFilter( node -> !node.isStone() && !node.isTrap());
			pathfinding.recalculateMap(getMap(), position);
			List<Node> dangerousFields = pathfinding.getNearest( node -> !node.isVisited());
			if(!dangerousFields.isEmpty()){
				Utils.consistentAgentLog(LOGGER, agentName, " no safe fields found. Go to nearest unsafe");
				int index = (int)(Math.random() * dangerousFields.size());
				return dangerousFields.get(index).getPosition();
			}
			
			
			Utils.consistentAgentLog(LOGGER, agentName, " no food and unknwon found. Go to start");
			return START_POSITION;
		}
		
		public void reachedNode(Node n){
			if( !carryingFood && n.hasHoney() ){
				// TODO fehler, bei dem der agent manchmal stehenbleibt? was passiert, wenn er nichts aufheben kann? kommt dann trotzdem ein pickupCallback?
				// oder liefert skipMovement() dann immer true zurück?
				skip = true;
				COMMUNICATOR.pickUp();
				return;
			}
			skip = false;
			
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
			
			
			if(carryingFood && START_POSITION.equals(n.getPosition())){
				
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
	private TrapScanner scanner;

	// TODO singleton?
	private final StateDead 	STATE_DEAD 		= new StateDead();
	private final StateScouting STATE_SCOUTING 	= new StateScouting();
	private final StateCarrying STATE_CARRYING 	= new StateCarrying();
	
	public Innovagent() {
		
		flowController.setOnDeathCallback( () -> 
		{
			Node data = this.nodeMap.createOrGet(position).setTrap(true);
			shareAntWorldUpdate(Arrays.asList(data));
			currentState = STATE_DEAD;
		});
		flowController.setOnSuccessfulMovement(this::movementSuccessful);
		flowController.setOnFailedMovement( this::movementFailed);
		flowController.setOnDropCallback( () -> {
			carryingFood = false;
			
			currentState = STATE_SCOUTING;
			tryMoving();
		});
		flowController.setOnPickCallback( () -> {
			
			carryingFood = true;
			Node node = nodeMap.getNode(position);
			node.setHoneyAmount(node.getHoneyAmount() -1 );
			shareAntWorldUpdate( Arrays.asList(node));
			currentState = STATE_CARRYING;
			tryMoving();
		});
		flowController.setMessageTranslator(TRANSLATOR);
		this.pathfinding = new DijkstraPathfinding();
		this.pathfinding.setNodeFilter( node -> !node.isStone() && !node.isTrap() && !node.isDangerous());
		this.scanner = new TrapScanner();
		
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
				neighbour.setSafe( !source.hasStench());
			}
		}
	}
	
	private Node basicFailedMovement(){
		Node node = getMap().createOrGet(position);
		node.setStone(true);
		node.setVisited(true);
		this.position = lastPosition;
		
		this.scanner.evaluate(getMap());
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
			
			this.scanner.evaluate(getMap());
			shareAntWorldUpdate(nodes);
		}
		return node;
	}
	
	private void moveToDirection(Direction d){
		
		switch(d){
			case UP: COMMUNICATOR.moveUp(); break;
			case DOWN: COMMUNICATOR.moveDown(); break;
			case LEFT: COMMUNICATOR.moveLeft(); break;
			case RIGHT: COMMUNICATOR.moveRight(); break;
			default: throw new RuntimeException("Bewegt sicht nicht, sollte es aber!");
		}
	}
	
	private void tryMoving(){
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
		tryMoving();
	}
	
	private void movementFailed() {
		basicFailedMovement();
		Utils.consistentAgentLog(LOGGER, this.agentName, "failed to move to "+this.position+" [STONE|BORDER]");
		tryMoving();
	}
	
	private void applyDataToNode(Node node, NodeInformationTO data){
		node.setHoneyAmount(data.getHoney());
		node.setStench(data.getStench());
		node.setTrap(data.isTrap());
		node.setStone(data.isTrap());
		node.setSmell(data.getSmell());
	}
	
}
