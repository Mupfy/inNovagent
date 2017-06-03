package innova.inNovagent.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

import innova.inNovagent.agents.Innovagent;
import innova.inNovagent.agents.MapPainterAgent;
import innova.inNovagent.core.AgentLauncher;
import innova.inNovagent.util.FunStuff;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class ControlCenter extends JPanel {

	private static final Logger LOGGER = Logger.getLogger(ControlCenter.class);
	private static int agentCounter = 1;

	private JTextField ipInputField;
	private JPanel agentOverviewContainer;

	AgentController mapPainterController;
	
	private JButton mapPainterBttn;
	private JButton mapResetBttn;
	private JButton launchBttn;

	public ControlCenter() {
		this.ipInputField = new JTextField("localhost");
		this.agentOverviewContainer = new JPanel(new GridLayout(0, 1));
		constructUI();
	}

	private void constructUI() {
		setLayout(new GridBagLayout());

		// IP overview
		GridBagConstraints c = new GridBagConstraints();
		JLabel ipLabel = new JLabel("IP:");
		JButton applyIpBttn = new JButton("Connect");
		applyIpBttn.addActionListener(e -> connectToAntWorld());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		add(ipLabel, c);
		++c.gridx;
		c.weightx = 1.0;
		add(ipInputField, c);
		c.weightx = 0.0;
		++c.gridx;
		add(applyIpBttn, c);

		// MapPainter
		mapPainterBttn = new JButton("Paint Map");
		mapPainterBttn.addActionListener(e -> createMapPainterAgent());
		c.gridx = 0;
		++c.gridy;
		mapPainterBttn.setEnabled(false);
		add(mapPainterBttn, c);
		

		mapResetBttn = new JButton("Reset Map");
		mapResetBttn.addActionListener(e -> resetMapPainter());
		++c.gridx;
		mapResetBttn.setEnabled(false);
		add(mapResetBttn, c);

		// Launch Button
		launchBttn = new JButton("Launch Agent");
		launchBttn.addActionListener(e -> {
			this.agentOverviewContainer.add(createAgentControl(FunStuff.createNameForAgent()));
			this.agentOverviewContainer.revalidate();
		});
		++c.gridy;
		c.gridx = 0;
		c.weightx = 1.0;
		c.gridwidth = 3;
		launchBttn.setEnabled(false);
		add(launchBttn, c);

		// Agent overview
		Border border = BorderFactory.createTitledBorder("Agents");
		this.agentOverviewContainer.setBorder(border);
		c.fill = GridBagConstraints.BOTH;
		++c.gridy;
		c.gridx = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 3;

		add(this.agentOverviewContainer, c);
	}

	private JPanel createAgentControl(String agentName) {
		final JPanel panel = new JPanel();
		final JLabel label = new JLabel(agentName);
		final JButton killBttn = new JButton("X");

		panel.add(label);
		panel.add(killBttn);

		final AgentController target = AgentLauncher.instance().createAgent(agentName, Innovagent.class);
		try {
			target.start();
		} catch (Exception e) {
			LOGGER.error(e);
			JPanel errorPanel = new JPanel();
			errorPanel.add(new JLabel("ERROR"));
			return errorPanel;
		}
		killBttn.addActionListener(e -> {
			try {
				target.kill();
				this.agentOverviewContainer.remove(panel);
				this.agentOverviewContainer.revalidate();
			} catch (StaleProxyException e1) {
				LOGGER.error(e1);
			}
		});
		return panel;
	}
	
	private void connectToAntWorld() {
		// not perfect, but prevents an accidental click before clicking "connect"
		AgentLauncher.instance().setIPAdress(this.ipInputField.getText());
		mapPainterBttn.setEnabled(true);
		launchBttn.setEnabled(true);
	}

	private void createMapPainterAgent() {
		mapPainterBttn.setEnabled(false);
		mapResetBttn.setEnabled(true);
		mapPainterController = AgentLauncher.instance().createAgent(MapPainterAgent.class.getName(),
				MapPainterAgent.class);
		try {
			mapPainterController.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
	
	private void resetMapPainter() {
		mapPainterBttn.setEnabled(true);
		if (mapPainterController != null) {
			try {
				mapPainterController.kill();
				mapPainterController = null;
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}
}
