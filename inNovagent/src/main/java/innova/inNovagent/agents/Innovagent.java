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

//TODO FIND SMELL OVER UNKNWON.
/**
 * The ant which moves on the field.
 */
@SuppressWarnings("serial")
public class Innovagent extends SyncMapAgent {
	
	
	
	/**
	 * Internal capsulated state of the agent which can be implemented by
	 * different behaviors.
	 *
	 */
	private interface AgentState {
		
		/**
		 * Gives the agent the next target he will try to reach with each round.
		 */
		public Point getNextTarget();

		/**
		 * Shows the state that the agent reached a node.
		 * @param n
		 */
		public void reachedNode(Node n);
		
		/**
		 * Tells the agent to skip movement this round
		 * so it can do other actions.
		 * @return
		 */
		public boolean skipMovement();
	}
	
	/**
	 * Scouts the map for honey.
	 * 
	 */
	private class StateScouting implements AgentState {
		private boolean skip;

		/**
		 * Calculates the next Target. Returns the nearest Node with Honey, if
		 * there is one. Otherwise it returns the nearest safe Node and - if no
		 * safe Node exists - the nearest unvisited (and dangerous) Node. If
		 * there are no more unvisited Nodes which are no traps or stones, it
		 * returns the startpoint.
		 */
		public Point getNextTarget() {
			Innovagent.this.pathfinding.setNodeFilter(node -> !node.isStone() && !node.isTrap() && !node.isDangerous());
			pathfinding.recalculateMap(getMap(), position);

			List<Node> foodNodes = pathfinding.getNearest(Node::hasHoney);
			if (!foodNodes.isEmpty()) {
				Utils.consistentAgentLog(LOGGER, agentName, "food found. Go to nearest one");
				int index = (int) (Math.random() * foodNodes.size());
				return foodNodes.get(index).getPosition();
			}

			List<Node> unknownNodes = pathfinding.getNearest(node -> !node.isVisited());
			if (!unknownNodes.isEmpty()) {
				Utils.consistentAgentLog(LOGGER, agentName, " no food found. Go to nearest unknown");
				int index = (int) (Math.random() * unknownNodes.size());
				return unknownNodes.get(index).getPosition();
			}

			Innovagent.this.pathfinding.setNodeFilter(node -> !node.isStone() && !node.isTrap());
			pathfinding.recalculateMap(getMap(), position);
			List<Node> dangerousFields = pathfinding.getNearest(node -> !node.isVisited());
			if (!dangerousFields.isEmpty()) {
				Utils.consistentAgentLog(LOGGER, agentName, " no safe fields found. Go to nearest unsafe");
				int index = (int) (Math.random() * dangerousFields.size());
				return dangerousFields.get(index).getPosition();
			}

			Utils.consistentAgentLog(LOGGER, agentName, " no food and unknwon found. Go to start");
			return START_POSITION;
		}

		
		public void reachedNode(Node n) {
			if (!carryingFood && n.hasHoney()) {
				skip = true;
				COMMUNICATOR.pickUp();
				return;
			}
			skip = false;

		}

		public boolean skipMovement() {
			return skip;
		}
	}
	
	/**
	 * Will bring honey home
	 *
	 */
	private class StateCarrying implements AgentState {

		private boolean skip;

		public Point getNextTarget() {
			return START_POSITION;
		}

		public void reachedNode(Node n) {

			if (carryingFood && START_POSITION.equals(n.getPosition())) {

				COMMUNICATOR.drop();
				skip = true;
				return;
			}

			skip = false;
		}

		public boolean skipMovement() {
			return skip;
		}
	}
	
	/**
	 * Now the real journey begins.
	 * Because you are dead. 
	 */
	private class StateDead implements AgentState {
		public Point getNextTarget() {
			return START_POSITION;
		}

		public void reachedNode(Node n) {

		}

		public boolean skipMovement() {
			return true;
		}
	}

	private Logger LOGGER = Logger.getLogger(Innovagent.class);

	private static final AntWorldMessageTranslator TRANSLATOR = AntWorldTranslatorFactory.create();
	private final AntWorldCommunicator COMMUNICATOR = AntWorldCommunicatorFactory.create(this);
	private final AntWorldFlowController flowController = AntWorldFlowControllerFactory.create();

	private static final Point START_POSITION = new Point(0, 0);
	private Point targetPosition;
	private Point lastPosition;
	private Point position;
	private String agentName;
	private Pathfinding pathfinding;
	private AgentState currentState;
	private boolean carryingFood;
	private TrapScanner scanner;

	private final StateDead STATE_DEAD = new StateDead();
	private final StateScouting STATE_SCOUTING = new StateScouting();
	private final StateCarrying STATE_CARRYING = new StateCarrying();

	public Innovagent() {

		flowController.setOnDeathCallback(() -> {
			Node data = this.nodeMap.createOrGet(position).setTrap(true);
			shareAntWorldUpdate(Arrays.asList(data));
			currentState = STATE_DEAD;
		});
		flowController.setOnSuccessfulMovement(this::movementSuccessful);
		flowController.setOnFailedMovement(this::movementFailed);
		flowController.setOnDropCallback(() -> {
			carryingFood = false;

			currentState = STATE_SCOUTING;
			tryMoving();
		});
		flowController.setOnPickCallback(() -> {

			carryingFood = true;
			Node node = nodeMap.getNode(position);
			node.setHoneyAmount(node.getHoneyAmount() - 1);
			shareAntWorldUpdate(Arrays.asList(node));
			currentState = STATE_CARRYING;
			tryMoving();
		});
		flowController.setOnFailedPickUp( () -> {
			Node node = nodeMap.getNode(position);
			node.setHoneyAmount(0);
			shareAntWorldUpdate(Arrays.asList(node));
			currentState = STATE_SCOUTING;
			tryMoving();
		});
		flowController.setMessageTranslator(TRANSLATOR);
		this.pathfinding = new DijkstraPathfinding();
		this.pathfinding.setNodeFilter(node -> !node.isStone() && !node.isTrap() && !node.isDangerous());
		this.scanner = new TrapScanner();

	}

	@Override
	protected void setup() {
		super.setup();
		this.agentName = getLocalName();
		this.targetPosition = new Point(0, 0);
		this.lastPosition = new Point(0, 0);
		this.position = new Point(0, 0);
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
		Utils.consistentAgentLog(LOGGER, agentName, "received message with: " + rootNode.toString(4));
		if (rootNode.has(Constants.INTERNAL_MESSAGE_TYPE)) {
			if(rootNode.getString(Constants.INTERNAL_MESSAGE_TYPE).equals(Constants.CHANGE_COLOR)){
				String color = rootNode.getJSONObject(Constants.MESSAGE_CONTENT).getString(Constants.COLOR);
				this.COMMUNICATOR.setAgentColor(color);
			}else{				
				super.receiveDispatchedMessage(msg, rootNode);
			}
		} else {
			COMMUNICATOR.setLastMessage(msg);
			flowController.consumeMessage(msg, rootNode);
		}
	}

	/**
	 * Expands the given node. All possible neighbours of the node will be
	 * created and set to unvisited if they don't exist already.
	 * 
	 * @param source
	 */
	private void expandNode(Node source) {
		Point point = source.getPosition();
		List<Point> points = Arrays.asList(new Point(point.getX(), point.getY() + 1), // Create north node
				new Point(point.getX(), point.getY() - 1), // Create south node
				new Point(point.getX() + 1, point.getY()), // Create east node
				new Point(point.getX() - 1, point.getY())); // Create west node

		for (Point p : points) {
			Node neighbour = getMap().getNode(p);
			if (neighbour == null) {
				neighbour = getMap().createOrGet(p);
				neighbour.setVisited(false);
				neighbour.setSafe(!source.hasStench());
			}
		}
	}

	private Node basicFailedMovement() {
		Node node = getMap().createOrGet(position);
		node.setStone(true);
		node.setVisited(true);
		this.position = lastPosition;

		this.scanner.evaluate(getMap());
		shareAntWorldUpdate(Arrays.asList(node));
		return node;
	}

	private Node basicSuccessfulMovemnt(NodeInformationTO data) {
		this.lastPosition = position;
		Node node = getMap().createOrGet(position);
		if (!(node.isVisited() && node.getHoneyAmount() == data.getHoney())) { // Is this a new node or
																				// did the honey amount change.
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

	private void moveToDirection(Direction d) {
		switch (d) {
		case UP:
			COMMUNICATOR.moveUp();
			break;
		case DOWN:
			COMMUNICATOR.moveDown();
			break;
		case LEFT:
			COMMUNICATOR.moveLeft();
			break;
		case RIGHT:
			COMMUNICATOR.moveRight();
			break;
		default:
			break;
		}
	}

	private void tryMoving() {
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
		if (currentState.skipMovement()) {
			return;
		}
		tryMoving();
	}

	private void movementFailed() {
		basicFailedMovement();
		Utils.consistentAgentLog(LOGGER, this.agentName, "failed to move to " + this.position + " [STONE|BORDER]");
		tryMoving();
	}

	private void applyDataToNode(Node node, NodeInformationTO data) {
		node.setHoneyAmount(data.getHoney());
		node.setStench(data.getStench());
		node.setTrap(data.isTrap());
		node.setStone(data.isTrap());
		node.setSmell(data.getSmell());
	}
}
