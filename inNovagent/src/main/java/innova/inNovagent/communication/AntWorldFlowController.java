package innova.inNovagent.communication;

import org.json.JSONObject;

import innova.inNovagent.util.Point;

public interface AntWorldFlowController {
	interface OnDeathCallback {
		public void callback(int x, int y);
	}
	interface OnMovementCallback {
		public void onSuccessfulMovement(NodeInformationTO data);
	}
	interface OnFailedMovementCallback {
		public void onFailedMovement(Point target);
	}
	interface PositionAccess {
		public Point getLastPosition();
	}
	
	public void setOnDeathCallback(OnDeathCallback callback);
	public void setOnSuccessfulMovement(OnMovementCallback callback);
	public void setOnFailedMovement(OnFailedMovementCallback callback);
	public void setPositionGetter(PositionAccess access);
	
	public void setMessageTranslator(AntWorldMessageTranslator translator);
	
	public void consumeMessage(JSONObject rootNode);
}
