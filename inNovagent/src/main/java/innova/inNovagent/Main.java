package innova.inNovagent;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.json.JSONObject;

import innova.inNovagent.agents.Innovagent;
import innova.inNovagent.agents.SynchronizedAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public final class Main {
	private Main() {
	}

	public static class TestAgent extends SynchronizedAgent {

		@Override
		public void onSync() {
			System.out.println("Guten Tag ich kenne meine Freunde " + getLocalName());
			System.out.println(getKnownAgents());
		}

		@Override
		public void receiveDispatchedMessage(ACLMessage msg, JSONObject rootNode) {
			// TODO Auto-generated method stub

		}

	}

	public static void main(String[] args) throws StaleProxyException {
		try {
			BasicConfigurator.configure(new FileAppender( new PatternLayout(), "tmp.log", false));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Runtime jadeRuntime = Runtime.instance();
		Profile profile = new ProfileImpl("localhost", -1, null, false);
		AgentContainer container = jadeRuntime.createAgentContainer(profile);

		AgentController controller1 = container.createNewAgent("Innova1", Innovagent.class.getName(), new String[0]);
		
		controller1.start();
		try {
			Thread.sleep(500);
		} catch (Exception e) {

		}
	}
}
