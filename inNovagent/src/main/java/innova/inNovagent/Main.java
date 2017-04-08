package innova.inNovagent;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

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
		public void receiveDispatchedMessage(ACLMessage msg) {
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

		AgentController controller1 = container.createNewAgent("name1", TestAgent.class.getName(), new String[0]);

		AgentController controller2 = container.createNewAgent("name2", TestAgent.class.getName(), new String[0]);
		AgentController controller3 = container.createNewAgent("name3", TestAgent.class.getName(), new String[0]);

		controller1.start();
		try {
			Thread.sleep(500);
		} catch (Exception e) {

		}
		controller2.start();
		controller3.start();
	}
}
