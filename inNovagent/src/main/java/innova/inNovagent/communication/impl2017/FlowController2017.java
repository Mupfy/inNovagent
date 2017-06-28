package innova.inNovagent.communication.impl2017;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import innova.inNovagent.communication.AntWorldFlowController;
import innova.inNovagent.communication.AntWorldMessageTranslator;
import innova.inNovagent.communication.NodeInformationTO;
import innova.inNovagent.util.Utils;
import jade.lang.acl.ACLMessage;

/**
 * Controls what happens after an action was performed (When an agent died /
 * moved successfully / ...).
 */
public class FlowController2017 implements AntWorldFlowController {

	private static final Logger LOGGER = Logger.getLogger(FlowController2017.class);

	private static final String PICK_ACTION = "ANT_ACTION_PICK";
	private static final String DROP_ACTION = "ANT_ACTION_DROP";

	private OnDeathCallback deathCallback;
	private OnMovementCallback movementCallback;
	private OnFailedMovementCallback failedMovementCallback;
	private AntWorldMessageTranslator messageTranslator;
	private Runnable onPick;
	private Runnable onDrop;
	private Runnable onFailedPick;

	@Override
	public void setOnDeathCallback(OnDeathCallback callback) {
		this.deathCallback = Utils.notNull(callback);
	}

	@Override
	public void setOnSuccessfulMovement(OnMovementCallback callback) {
		this.movementCallback = Utils.notNull(callback);
	}

	@Override
	public void setOnFailedMovement(OnFailedMovementCallback callback) {
		this.failedMovementCallback = Utils.notNull(callback);
	}

	@Override
	public void setMessageTranslator(AntWorldMessageTranslator translator) {
		this.messageTranslator = Utils.notNull(translator);
	}

	@Override
	public void consumeMessage(ACLMessage original, JSONObject rootNode) {
		LOGGER.debug("Starting to consume Message");
		NodeInformationTO data = messageTranslator.translate(rootNode);

		if (PICK_ACTION.equals(rootNode.getString("action"))) {
			if(ACLMessage.REFUSE == original.getPerformative() ){
				this.onFailedPick.run();
				return;
			}else{				
				this.onPick.run();
				return;
			}
		}

		if (DROP_ACTION.equals(rootNode.getString("action"))) {
			this.onDrop.run();
			return;
		}

		if (data.isTrap()) { // Current order of if-statements is important. If
								// you die you don't have moved.
			this.deathCallback.callback();
			LOGGER.debug("Ending consuming with: death");
			return;
		}
		
		if(ACLMessage.REFUSE == original.getPerformative() ){	
			this.failedMovementCallback.onFailedMovement();
			LOGGER.debug("Ending consuming with: failedMvnt");
			return;
		}

		
		this.movementCallback.onSuccessfulMovement(data);
		LOGGER.debug("Ending consuming with: success");
	}

	@Override
	public void setOnDropCallback(Runnable callback) {
		this.onDrop = Utils.notNull(callback);
	}

	@Override
	public void setOnPickCallback(Runnable callback) {
		this.onPick = Utils.notNull(callback);
	}

	@Override
	public void setOnFailedPickUp(Runnable callback) {
		this.onFailedPick = callback;
	}

}
