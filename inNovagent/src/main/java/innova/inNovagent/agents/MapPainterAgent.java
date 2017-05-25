package innova.inNovagent.agents;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import innova.inNovagent.ui.MainFrame;
import innova.inNovagent.ui.MapPainter;
import jade.lang.acl.ACLMessage;


public class MapPainterAgent extends SyncMapAgent {
	private JFrame frame;
	
	public MapPainterAgent() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new MainFrame();
				frame.add(new MapPainter(MapPainterAgent.this));
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
	
	@Override
	protected void setup() {
		super.setup();
		System.out.println(getLocalName() + ": Juhu, ich lebe!!!! xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
	}
	
	@Override
	protected void receiveDispatchedMessage(ACLMessage msg, JSONObject rootNode) {
		super.receiveDispatchedMessage(msg, rootNode);
		frame.repaint();
	}
}

//public class WorldPainterAgent extends MapSyncAgent {
//	private JFrame m_Frame;
//	private WorldMapComponent m_MapView;
//
//	public WorldPainterAgent() {
//		m_Frame = new JFrame("MapView");
//		m_Frame.setSize(720, 480);
//		m_Frame.setLayout(new BorderLayout());
//		m_MapView = new WorldMapComponent(m_Map);
//		m_Frame.add(m_MapView);
//		m_Frame.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				send_kill(getAID());
//			}
//		});
//		m_Frame.setVisible(true);
//	}
//
//	@Override
//	public void notifyObserver(Object arg) {
//		super.notifyObserver(arg);
//		System.out.println("Observer aufgerufen");
//		System.out.println("map size: " + m_Map.getAllNodes().size());
//		m_MapView.updateView();
//		m_Frame.repaint();
//		if (arg != null && arg.equals(JTAG_MAP_CHANGED_TAG)) {
//			m_MapView.updateView();
//			m_Frame.repaint();
//		}
//	}
//
//	@Override
//	public void takeDown() {
//		super.takeDown();
//		m_Frame.dispose();
//	}
//}
