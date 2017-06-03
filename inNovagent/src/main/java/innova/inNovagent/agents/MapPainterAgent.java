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
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
	}
	
	@Override
	public void onSync() {
		// TODO does not work yet, if you start a new MapPainterAgent after closing the old one, the new one does not know the map
		super.onSync();
	}
	
	@Override
	protected void receiveDispatchedMessage(ACLMessage msg, JSONObject rootNode) {
		super.receiveDispatchedMessage(msg, rootNode);
		frame.repaint();
	}
	
	@Override
	protected void takeDown() {
		frame.dispose();
		super.takeDown();
	}
}