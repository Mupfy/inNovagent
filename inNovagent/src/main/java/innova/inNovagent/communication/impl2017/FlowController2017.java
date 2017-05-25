package innova.inNovagent.communication.impl2017;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import innova.inNovagent.communication.AntWorldFlowController;
import innova.inNovagent.communication.AntWorldMessageTranslator;
import innova.inNovagent.communication.NodeInformationTO;
import innova.inNovagent.util.Utils;

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
	private PositionAccess positionAccess;
	private Runnable onPick;
	private Runnable onDrop;

	/**
	 * These are the x and y of antworld and not of the internal used
	 * coordinates
	 */
	private int x = -1, y = -1;

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
	public void consumeMessage(JSONObject rootNode) {
		LOGGER.debug("Starting to consume Message");
		NodeInformationTO data = messageTranslator.translate(rootNode);

		if (PICK_ACTION.equals(rootNode.getString("action"))) {
			this.onPick.run();
			return;
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

		if (this.x == data.getX() && this.y == data.getY() || data.isStone()) { // TODO Abfrage erweitern verändern, wenn durch
																				// zufall die initialwerte und der spawnpunkt
																				// auf dem gleichem fleck liegen
			this.failedMovementCallback.onFailedMovement();
			LOGGER.debug("Ending consuming with: failedMvnt");
			return;
		}

		LOGGER.debug("MVNT successful: old x " + this.x + " | new x " + data.getX() + " : old y " + this.y + " | new y "
				+ data.getY());
		this.x = data.getX();
		this.y = data.getY();
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

}
