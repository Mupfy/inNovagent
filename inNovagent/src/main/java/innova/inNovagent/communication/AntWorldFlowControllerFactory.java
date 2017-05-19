package innova.inNovagent.communication;

import innova.inNovagent.communication.impl2017.FlowController2017;

public interface AntWorldFlowControllerFactory {
	public static AntWorldFlowController create() {
		return new FlowController2017();
	} 
}
