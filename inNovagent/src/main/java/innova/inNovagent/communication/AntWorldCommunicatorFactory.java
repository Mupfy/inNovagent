package innova.inNovagent.communication;

import innova.inNovagent.communication.impl2017.Communicator2017;
import jade.core.Agent;

public class AntWorldCommunicatorFactory {
	public static AntWorldCommunicator create(Agent agent) {
		return new Communicator2017(agent);
	}
}
