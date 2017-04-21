package innova.inNovagent.communication;

import org.json.JSONObject;

public interface AntWorldMessageTranslator {
	NodeInformationTO translate(JSONObject orginalMessage);
}
