package innova.inNovagent.agents;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import innova.inNovagent.communication.AntWorldMessageTranslator;
import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Constants;
import innova.inNovagent.util.Point;
import jade.lang.acl.ACLMessage;

public class SyncMapAgent extends SynchronizedAgent{
	
	private static final AntWorldMessageTranslator MESSAGE_TRANSLATOR = null; //TODO: 2016 VERSIONES machen
	
	public static final String MAP_UPDATED_TYPE = "map_updated";
	
	private static final String MAP_PAYLOAD = "MAP_PAYLOAD";
	private static final String MAP_INFORMATION_UPDATED = "MAP_INFORMATION_UPDATED";
	private static final String REQUEST_WHOLE_MAP = "REQUEST_MAP";
	private static final String RESPONSE_WHOLE_MAP = "RESPONSE_WHOLE_MAP";
	private static final String POSITION_X = "X_VALUE";
	private static final String POSITION_Y = "Y_VALUE";
	private static final String HONEY_AMOUNT = "HONEY_AMOUNT";
	private static final String SMELL_AMOUNT = "SMELL_AMOUNT";
	private static final String TRAP_MARKER = "TRAP";
	private static final String STONE_MARKER = "STONE";
	private static final String STENCH_AMOUNT = "STENCH_AMOUNT";
	
	private NodeMap nodeMap;
	
	public NodeMap getMap(){
		return this.nodeMap;
	}

	@Override
	public void onSync() {
		sendMapRequest();
	}

	@Override
	protected void receiveDispatchedMessage(ACLMessage msg, JSONObject rootNode) {
		if(!rootNode.has(Constants.INTERNAL_MESSAGE_TYPE)){
			return;
		}
		if(rootNode.getString(Constants.INTERNAL_MESSAGE_TYPE).equals(MAP_INFORMATION_UPDATED)){
			JSONObject mapNode = rootNode.getJSONObject(Constants.MESSAGE_CONTENT);
			JSONArray nodeArray = mapNode.getJSONArray(MAP_PAYLOAD);
			for(Object obj : nodeArray){
				notifyObserver(MAP_INFORMATION_UPDATED, this, parseMapInformation((JSONObject)obj));
			}
		}else if(rootNode.getString(Constants.INTERNAL_MESSAGE_TYPE).equals(REQUEST_WHOLE_MAP) ){
			answerMapRequest(msg);
		}else if(rootNode.getString(Constants.INTERNAL_MESSAGE_TYPE).equals(RESPONSE_WHOLE_MAP) ){
			JSONObject content = rootNode.getJSONObject(Constants.MESSAGE_CONTENT);
			JSONArray array = new JSONArray(content.get(MAP_PAYLOAD));
			for(Object obj : array){
				parseMapInformation((JSONObject)obj);
			}
		}
	}
	
	private void sendMapRequest(){
		sendInternalMessage(new JSONObject(), REQUEST_WHOLE_MAP, getKnownAgents());
	}
	
	private void answerMapRequest(ACLMessage original){
		ACLMessage response = original.createReply();
		response.setContent(applyHeader(new JSONObject().put(MAP_PAYLOAD, mapToJSON()), RESPONSE_WHOLE_MAP).toString());
		response.setLanguage(Constants.JSON_TAG);
		send(response);
	}
	
	private Node parseMapInformation(JSONObject mapNodeObject){
		int x = mapNodeObject.getInt(POSITION_X);
		int y = mapNodeObject.getInt(POSITION_Y);
		
		return this.nodeMap.createOrGet(new Point(x,y) )
		.setHoneyAmount(mapNodeObject.getInt(HONEY_AMOUNT))
		.setSmell(mapNodeObject.getInt(SMELL_AMOUNT))
		.setStone(mapNodeObject.getBoolean(STONE_MARKER))
		.setTrap(mapNodeObject.getBoolean(TRAP_MARKER))
		.setStench(mapNodeObject.getInt(STENCH_AMOUNT));
	}
	
	private JSONObject nodeToJSON(Node data){
		return new JSONObject()
		.put(POSITION_X, data.getPosition().getX())
		.put(POSITION_Y, data.getPosition().getY())
		.put(HONEY_AMOUNT, data.getHoneyAmount())
		.put(SMELL_AMOUNT, data.getSmell())
		.put(STONE_MARKER, data.isStone())
		.put(STENCH_AMOUNT, data.getStench());
	}
	
	private JSONArray mapToJSON(){
		JSONArray array = new JSONArray();
		for(Node data : this.nodeMap.getField().values() ){
			array.put(nodeToJSON(data));
		}
		return array;
	}
	
	protected void shareAntWorldUpdate(Collection<Node> data){
		JSONArray array = new JSONArray();
		for(Node n : data){
			array.put(nodeToJSON(n));
		}
		sendInternalMessage(new JSONObject().put("MAP", array), MAP_INFORMATION_UPDATED, getKnownAgents() );
	}
}
