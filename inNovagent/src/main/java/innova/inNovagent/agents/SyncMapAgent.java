package innova.inNovagent.agents;

import org.json.JSONObject;

import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Constants;
import jade.lang.acl.ACLMessage;

public class SyncMapAgent extends SynchronizedAgent{
	
	public static final String MAP_UPDATED_TYPE = "map_updated";
	
	private static final String MAP_INFORMATION_UPDATED = "MAP_INFORMATION_UPDATED";
	
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
			notifyObserver(MAP_UPDATED_TYPE, "dmjfdkgf");
		}
	}

}
