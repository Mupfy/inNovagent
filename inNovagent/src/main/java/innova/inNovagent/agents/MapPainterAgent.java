package innova.inNovagent.agents;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import innova.inNovagent.core.logic.TrapScanner;
import innova.inNovagent.ui.MapPainter;
import innova.inNovagent.ui.NodeInformationPanel;
import innova.inNovagent.util.Constants;
import jade.lang.acl.ACLMessage;

/**
 * Agent responsible for creating a frame and a {@link MapPainter}.
 * Also contains the information about the map and provides it to the MapPainter.
 * 
 */
@SuppressWarnings("serial")
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
				frame.setJMenuBar(new JMenuBar());
				createColorMenu(frame.getJMenuBar());
			}
		});
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
	
	private JRadioButtonMenuItem createBttn(JMenu menu, String color){
		JRadioButtonMenuItem bttn = new JRadioButtonMenuItem(color.substring("ANT_COLOR_".length()));
		bttn.addActionListener( event -> {
			JSONObject content = new JSONObject();
			content.put(Constants.COLOR, color);
			sendInternalMessage(content, Constants.CHANGE_COLOR, getKnownAgents());
		});
		menu.add(bttn);
		return bttn;
	}
	
	private void createColorMenu(JMenuBar bar){
		JMenu menu = new JMenu("Farbe");
		ButtonGroup group = new ButtonGroup();
		group.add(createBttn(menu,"ANT_COLOR_GREEN"));
		group.add(createBttn(menu,"ANT_COLOR_YELLOW"));
		group.add(createBttn(menu,"ANT_COLOR_RED"));
		group.add(createBttn(menu,"ANT_COLOR_BLUE"));
		
		bar.add(menu);
	}
}