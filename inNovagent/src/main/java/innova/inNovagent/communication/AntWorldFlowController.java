package innova.inNovagent.communication;

import org.json.JSONObject;

import innova.inNovagent.util.Point;

public interface AntWorldFlowController {
	interface OnDeathCallback {
		public void callback();
	}
	interface OnMovementCallback {
		public void onSuccessfulMovement(NodeInformationTO data);
	}
	interface OnFailedMovementCallback {
		public void onFailedMovement();
	}
	interface PositionAccess {
		public Point getLastPosition();
	}
	
	public void setOnDeathCallback(OnDeathCallback callback);
	public void setOnSuccessfulMovement(OnMovementCallback callback);
	public void setOnFailedMovement(OnFailedMovementCallback callback);
	public void setOnDropCallback(Runnable callback);
	public void setOnPickCallback(Runnable callback);
	
	public void setMessageTranslator(AntWorldMessageTranslator translator);
	
	public void consumeMessage(JSONObject rootNode);
}
