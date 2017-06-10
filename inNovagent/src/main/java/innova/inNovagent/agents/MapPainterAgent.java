package innova.inNovagent.agents;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import innova.inNovagent.core.logic.TrapScanner;
import innova.inNovagent.ui.MapPainter;
import innova.inNovagent.ui.NodeInformationPanel;
import jade.lang.acl.ACLMessage;


public class MapPainterAgent extends SyncMapAgent {
	private JFrame frame;
	private TrapScanner scanner;
	
	public MapPainterAgent() {
		this.scanner = new TrapScanner();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new JFrame("Agenten");
				frame.setLayout(new BorderLayout());
				NodeInformationPanel nodeInformationPanel = new NodeInformationPanel();
				frame.add(nodeInformationPanel, BorderLayout.SOUTH);
				frame.add(new MapPainter(MapPainterAgent.this, nodeInformationPanel), BorderLayout.NORTH);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.pack();
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
		this.scanner.evaluate(getMap());
		frame.repaint();
	}
	
	@Override
	protected void takeDown() {
		frame.dispose();
		super.takeDown();
	}
}