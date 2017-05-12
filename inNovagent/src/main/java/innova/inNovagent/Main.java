package innova.inNovagent;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.json.JSONObject;

import innova.inNovagent.agents.Innovagent;
import innova.inNovagent.agents.SynchronizedAgent;
import innova.inNovagent.ui.ControlCenter;
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
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(new ControlCenter());
		frame.setSize(200, 750);
		frame.setVisible(true);
	}
}
