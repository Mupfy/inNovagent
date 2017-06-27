package innova.inNovagent.ui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import innova.inNovagent.core.Node;

/**
 * A panel that shows detailed information about single Nodes.
 */
public class NodeInformationPanel extends JPanel {
	private JLabel position;
	private JLabel honey;
	private JLabel smell;
	private JLabel stench;
	private JLabel stone;
	private JLabel trap;
	private JLabel visited;
	private JLabel dangerous;

	public NodeInformationPanel() {
		setLayout(new GridLayout(2, 4));
		position = new JLabel("Position: ");
		add(position);
		honey = new JLabel("HoneyAmount: ");
		add(honey);
		smell = new JLabel("Smell: ");
		add(smell);
		stench = new JLabel("Stench: ");
		add(stench);
		stone = new JLabel("isStone:");
		add(stone);
		trap = new JLabel("isTrap:");
		add(trap);
		visited = new JLabel("isVisited: ");
		add(visited);
		dangerous = new JLabel("isDangerous:");
		add(dangerous);
	}

	/**
	 * Updates the informations to the ones of the given node.
	 * 
	 * @param node
	 *            the node which informations should be displayed
	 */
	public void updateNodeInformation(Node node) {
		if (node != null) {
			position.setText("Position: " + node.getPosition().getX() + ", " + node.getPosition().getY());
			honey.setText("HoneyAmount: " + node.getHoneyAmount());
			smell.setText("Smell: " + node.getSmell());
			stench.setText("Stench: " + node.getStench());
			stone.setText("Stone: " + node.isStone());
			trap.setText("Trap: " + node.isStone());
			visited.setText("Visited: " + node.isVisited());
			dangerous.setText("Dangerous: " + node.isDangerous());
		}
	}
}
