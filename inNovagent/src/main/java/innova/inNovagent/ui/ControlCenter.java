package innova.inNovagent.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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

	private JTextField ipInputField;
	private JSpinner agentNumber;
	private JPanel agentOverviewContainer;

	AgentController mapPainterController;

	private JButton paintMapBttn;
	private JButton resetMapBttn;
	private JButton launchBttn;
	private JButton killAllBttn;

	private List<AgentController> workingAgents;

	public ControlCenter() {
		this.agentOverviewContainer = new JPanel(new GridLayout(0, 1));
		workingAgents = new ArrayList<>();
		constructUI();
	}

	private void constructUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel userInputPanel = new JPanel();
		userInputPanel.setLayout(new BoxLayout(userInputPanel, BoxLayout.Y_AXIS));

		JPanel ipPanel = new JPanel();
		JLabel ipLabel = new JLabel("IP:");
		ipPanel.add(ipLabel);
		this.ipInputField = new JTextField("localhost", 10);
		ipInputField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent pE) {
				ipInputField.selectAll();
			}
		});
		ipPanel.add(ipInputField);
		JButton applyIpBttn = new JButton("Connect");
		applyIpBttn.addActionListener(e -> connectToAntWorld());
		ipPanel.add(applyIpBttn);
		userInputPanel.add(ipPanel);

		JPanel mapPainterPanel = new JPanel();
		paintMapBttn = new JButton("Paint Map");
		paintMapBttn.addActionListener(e -> createMapPainterAgent());
		paintMapBttn.setEnabled(false);
		mapPainterPanel.add(paintMapBttn);
		resetMapBttn = new JButton("Reset Map");
		resetMapBttn.addActionListener(e -> resetMapPainter());
		resetMapBttn.setEnabled(false);
		mapPainterPanel.add(resetMapBttn);
		userInputPanel.add(mapPainterPanel);

		JPanel launchPanel = new JPanel();
		launchBttn = new JButton("Launch Agent(s)");
		launchBttn.addActionListener(e -> {
			if ((int) agentNumber.getValue() > 0) {
				killAllBttn.setEnabled(true);
				for (int i = 0; i < (int) agentNumber.getValue(); ++i) {
					agentOverviewContainer.add(createAgentControl(FunStuff.createNameForAgent()));
				}
				agentNumber.setValue(1);
				agentOverviewContainer.revalidate();
			}
		});
		launchBttn.setEnabled(false);
		launchPanel.add(launchBttn);
		agentNumber = new JSpinner();
		agentNumber.setValue(1);
		Component mySpinnerEditor = agentNumber.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
		jftf.setColumns(2);
		launchPanel.add(agentNumber);
		killAllBttn = new JButton("Kill all");
		killAllBttn.setEnabled(false);
		killAllBttn.addActionListener(e -> {
			for (AgentController agentController : workingAgents) {
				try {
					agentController.kill();
				} catch (StaleProxyException e1) {
					LOGGER.error(e1);
				}
			}
			workingAgents = new ArrayList<>();
			killAllBttn.setEnabled(false);
			agentOverviewContainer.removeAll();
			agentOverviewContainer.repaint();
		});
		launchPanel.add(killAllBttn);
		userInputPanel.add(launchPanel);
		userInputPanel.setMaximumSize(userInputPanel.getPreferredSize());

		add(userInputPanel);

		Border border = BorderFactory.createTitledBorder("Agents");
		agentOverviewContainer.setBorder(border);
		agentOverviewContainer.setPreferredSize(new Dimension(getPreferredSize().width, 600));
		add(agentOverviewContainer);
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
		workingAgents.add(target);
		killBttn.addActionListener(e -> {
			try {
				workingAgents.remove(target);
				if (workingAgents.size() == 0) {
					killAllBttn.setEnabled(false);
				}
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
		paintMapBttn.setEnabled(true);
		launchBttn.setEnabled(true);
	}

	private void createMapPainterAgent() {
		paintMapBttn.setEnabled(false);
		resetMapBttn.setEnabled(true);
		mapPainterController = AgentLauncher.instance().createAgent(MapPainterAgent.class.getName(),
				MapPainterAgent.class);
		try {
			mapPainterController.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	private void resetMapPainter() {
		resetMapBttn.setEnabled(false);
		paintMapBttn.setEnabled(true);
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
