package innova.inNovagent.agents;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import innova.inNovagent.ui.MapPainter;
import jade.lang.acl.ACLMessage;


public class MapPainterAgent extends SyncMapAgent {
	private JFrame frame;
	
	public MapPainterAgent() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new JFrame("Agenten");
				frame.add(new MapPainter(MapPainterAgent.this));
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
	
	@Override
	protected void receiveDispatchedMessage(ACLMessage msg, JSONObject rootNode) {
		super.receiveDispatchedMessage(msg, rootNode);
		frame.repaint();
	}
}