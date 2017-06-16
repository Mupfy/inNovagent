package innova.inNovagent.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
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
	private JSpinner agentNumber;
	private JPanel agentOverviewContainer;

	AgentController mapPainterController;
	
	private JButton mapPainterBttn;
	private JButton mapResetBttn;
	private JButton launchBttn;

	public ControlCenter() {
		this.ipInputField = new JTextField("localhost", 10);
		this.agentOverviewContainer = new JPanel(new GridLayout(0, 1));
		constructUI();
	}

	private void constructUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel userInputPanel = new JPanel();
		userInputPanel.setLayout(new BoxLayout(userInputPanel, BoxLayout.Y_AXIS));
		
		JPanel ipPanel = new JPanel();
		JLabel ipLabel = new JLabel("IP:");
		ipPanel.add(ipLabel);
		ipPanel.add(ipInputField);
		JButton applyIpBttn = new JButton("Connect");
		applyIpBttn.addActionListener(e -> connectToAntWorld());
		ipPanel.add(applyIpBttn);
		userInputPanel.add(ipPanel);
		
		JPanel mapPainterPanel = new JPanel();
		mapPainterBttn = new JButton("Paint Map");
		mapPainterBttn.addActionListener(e -> createMapPainterAgent());
		mapPainterBttn.setEnabled(false);
		mapPainterPanel.add(mapPainterBttn);
		mapResetBttn = new JButton("Reset Map");
		mapResetBttn.addActionListener(e -> resetMapPainter());
		mapResetBttn.setEnabled(false);
		mapPainterPanel.add(mapResetBttn);
		userInputPanel.add(mapPainterPanel);
		
		
		JPanel launchPanel = new JPanel();
		launchBttn = new JButton("Launch Agent(s)");
		launchBttn.addActionListener(e -> {
			for (int i = 0; i < (int) agentNumber.getValue(); ++i) {
				this.agentOverviewContainer.add(createAgentControl(FunStuff.createNameForAgent()));
			}
			this.agentOverviewContainer.revalidate();
		});
		launchBttn.setEnabled(false);
		launchPanel.add(launchBttn);
		agentNumber = new JSpinner();
		agentNumber.setValue(1);
		Component mySpinnerEditor = agentNumber.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
		jftf.setColumns(2);
		launchPanel.add(agentNumber);
		userInputPanel.add(launchPanel);
		
		add(userInputPanel);
		
		Border border = BorderFactory.createTitledBorder("Agents");
		this.agentOverviewContainer.setBorder(border);
		System.out.println(getPreferredSize());
		agentOverviewContainer.setPreferredSize(new Dimension(getPreferredSize().width, 1000));
		add(this.agentOverviewContainer);
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
				this.agentOverviewContainer.repaint();
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
