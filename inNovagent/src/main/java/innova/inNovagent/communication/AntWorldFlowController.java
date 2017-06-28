package innova.inNovagent.communication;

import org.json.JSONObject;

import jade.lang.acl.ACLMessage;


/**
 * Abstraction class for reaction to antworld messages.
 *
 */
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
	interface OnFailedPickUp{
		public void callback();
	}
	
	public void setOnDeathCallback(OnDeathCallback callback);
	public void setOnSuccessfulMovement(OnMovementCallback callback);
	public void setOnFailedMovement(OnFailedMovementCallback callback);
	public void setOnDropCallback(Runnable callback);
	public void setOnPickCallback(Runnable callback);
	public void setOnFailedPickUp(Runnable callback);
	
	public void setMessageTranslator(AntWorldMessageTranslator translator);
	
	/**
	 * Processes the given antworld message and calls the corresponding callbacks.
	 * @param rootNode The antoworld message.
	 */
	public void consumeMessage(ACLMessage original, JSONObject rootNode);
}
