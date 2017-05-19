package innova.inNovagent.communication.impl2017;

import org.json.JSONObject;

import innova.inNovagent.communication.AntWorldMessageTranslator;
import innova.inNovagent.communication.NodeInformationTO;

public class MessageTranslator2017 implements AntWorldMessageTranslator{
	
	private static final String CELL = "cell";
	private static final String AGENT_STATE = "state";
	private static final String STATE_ALIVE = "ALIVE";
	private static final String POSITION_X = "col";
	private static final String POSITION_Y = "row";
	private static final String FOOD = "food";
	private static final String SMELL = "smell";
	private static final String STENCH = "stench";

	@Override
	public NodeInformationTO translate(JSONObject orginalMessage) {
		NodeInformationTO data = new NodeInformationTO();
		data.setTrap(!orginalMessage.getString(AGENT_STATE).toUpperCase().equals(STATE_ALIVE.toUpperCase()));
		
		JSONObject cellInfo = orginalMessage.getJSONObject(CELL);
		data.setHoney(cellInfo.getInt(FOOD));
		data.setSmell(cellInfo.getInt(SMELL));
		data.setStench(cellInfo.getInt(STENCH));
		data.setX(cellInfo.getInt(POSITION_X));
		data.setY(cellInfo.getInt(POSITION_Y));
		return data;
	}

}
