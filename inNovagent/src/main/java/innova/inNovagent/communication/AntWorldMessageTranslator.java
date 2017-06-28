package innova.inNovagent.communication;

import org.json.JSONObject;

/**
 * Translates the JSON Information from Antworld in an internal data structure.
 *
 */
public interface AntWorldMessageTranslator {
	NodeInformationTO translate(JSONObject orginalMessage);
}
