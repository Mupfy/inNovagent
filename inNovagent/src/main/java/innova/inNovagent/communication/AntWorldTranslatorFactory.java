package innova.inNovagent.communication;

import innova.inNovagent.communication.impl2017.MessageTranslator2017;

public class AntWorldTranslatorFactory {
	public static AntWorldMessageTranslator create() {
		return new MessageTranslator2017();
	}
}
