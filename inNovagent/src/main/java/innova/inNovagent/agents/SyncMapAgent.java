package innova.inNovagent.agents;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import innova.inNovagent.communication.AntWorldMessageTranslator;
import innova.inNovagent.communication.NodeInformationTO;
import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Constants;
import innova.inNovagent.util.Point;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SyncMapAgent extends SynchronizedAgent{
	
	private static final AntWorldMessageTranslator MESSAGE_TRANSLATOR = null; //TODO: 2016 VERSIONES machen
	
	public static final String MAP_UPDATED_TYPE = "map_updated";
	
	private static final String MAP_INFORMATION_UPDATED = "MAP_INFORMATION_UPDATED";
	private static final String POSITION_X = "X_VALUE";
	private static final String POSITION_Y = "Y_VALUE";
	private static final String HONEY_AMOUNT = "HONEY_AMOUNT";
	private static final String SMELL_AMOUNT = "SMELL_AMOUNT";
	private static final String TRAP_MARKER = "TRAP";
	private static final String STONE_MARKER = "STONE";
	
	private NodeMap nodeMap;
	
	public NodeMap getMap(){
		return this.nodeMap;
	}

	@Override
	public void onSync() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void receiveDispatchedMessage(ACLMessage msg, JSONObject rootNode) {
		if(rootNode.has(Constants.INTERNAL_MESSAGE_TYPE) && rootNode.getString(Constants.INTERNAL_MESSAGE_TYPE).equals(MAP_INFORMATION_UPDATED)){
			JSONArray nodeArray = rootNode.getJSONArray(Constants.MESSAGE_CONTENT);
			for(Object obj : nodeArray){
				notifyObserver(MAP_INFORMATION_UPDATED, this, parseMapInformation((JSONObject)obj));
			}
		}
	}
	
	private Node parseMapInformation(JSONObject mapNodeObject){
		int x = mapNodeObject.getInt(POSITION_X);
		int y = mapNodeObject.getInt(POSITION_Y);
		
		return this.nodeMap.createOrGet(new Point(x,y) )
		.setHoneyAmount(mapNodeObject.getInt(HONEY_AMOUNT))
		.setSmell(mapNodeObject.getInt(SMELL_AMOUNT))
		.setStone(mapNodeObject.getBoolean(STONE_MARKER))
		.setTrap(mapNodeObject.getBoolean(TRAP_MARKER));
	}
	
	private void shareAntWorldUpdate(Node data){
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		for(AID id : getKnownAgents() ){
			msg.addReceiver(id);
		}
		JSONObject obj = new JSONObject()
		.put(POSITION_X, data.getPosition().getX())
		.put(POSITION_Y, data.getPosition().getY())
		.put(HONEY_AMOUNT, data.getHoneyAmount())
		.put(SMELL_AMOUNT, data.getSmell())
		.put(STONE_MARKER, data.isStone())
		send(msg);
	}
	

}
