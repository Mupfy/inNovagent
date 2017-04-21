package innova.inNovagent.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Point;

public class MapPainter extends JPanel {
	private int xMin;
	private int yMin;
	private int noOfSquares;

	private NodeMap map;

	private static final int SIZE = 600;

	public MapPainter() {
		xMin = -5;
		yMin = -5;
		noOfSquares = 10;

		createMap();

		setLayout(null);
		setPreferredSize(new Dimension(SIZE, SIZE));
	}

	private void createMap() {
		map = new NodeMap();
		Node node1 = map.createOrGet(new Point(0, 0));
		node1.setStone(true);
		Node node2 = map.createOrGet(new Point(-2, -5));
		node2.setTrap(true);
		Node node3 = map.createOrGet(new Point(3, 3));
		node3.setHoneyAmount(5);
		Node node4 = map.createOrGet(new Point(3, 1));
		node4.setHoneyAmount(30);
		map.createOrGet(new Point(4, 5));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(Color.BLACK);
		int squareSize = getWidth() / noOfSquares;
		for (int i = 1; i < noOfSquares; ++i) {
			g.drawLine(0, i * squareSize, getWidth(), i * squareSize);
			g.drawLine(i * squareSize, 0, i * squareSize, getHeight());
		}

		if (map != null) {
			for (int x = xMin; x < xMin + noOfSquares; ++x) {
				for (int y = yMin; y < yMin + noOfSquares; ++y) {
					Node node = map.getNode(x, y);
					if (node != null) {
						paintNode(node, x, y, squareSize, g);
					}
				}
			}
		}
	}

	private void paintNode(Node node, int x, int y, int squareSize, Graphics g) {
		if (node.isTrap()) {
			g.setColor(Color.RED);
		} else if (node.isStone()) {
			g.setColor(Color.BLACK);
		} else {
			g.setColor(Color.GREEN);
		}
		g.fillRect((x - xMin) * squareSize + 1, (y - yMin) * squareSize + 1, squareSize - 1, squareSize - 1);
		
//		int smell = node.getSmell();
//		int stench = node.getStench();
		int honeyAmount = node.getHoneyAmount();
		if (honeyAmount != 0) {
			g.setColor(Color.YELLOW);
			int edge = squareSize / 4;
			g.fillRect((x - xMin) * squareSize + edge, (y - yMin) * squareSize + edge, squareSize - 2 * edge, squareSize - 2 * edge);
			
			JLabel label = new JLabel(String.valueOf(honeyAmount));
			label.setSize(label.getPreferredSize());
			add(label);
			int labelX = (x - xMin) * squareSize + squareSize / 2 - label.getSize().width / 2;
			int labelY = (y - yMin) * squareSize + squareSize / 2 - label.getSize().height / 2;
			label.setLocation(labelX, labelY);
		}
	}
}
