package innova.inNovagent.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Point;

public class MapPainter extends JPanel {
	private int xMin;
	private int yMin;
	private int noOfSquaresX;
	private int noOfSquaresY;

	private NodeMap map;

	public MapPainter() {
		xMin = -5;
		yMin = -5;
		noOfSquaresX = 12;
		noOfSquaresY = 10;

		createMap();

		setPreferredSize(new Dimension(noOfSquaresX * 60, noOfSquaresY * 60));
	}

	// TODO just for testing
	private void createMap() {
		map = new NodeMap();
		Node node1 = map.createOrGet(new Point(0, 0));
		node1.setVisited(true);
		node1.setStone(true);
		Node node2 = map.createOrGet(new Point(-2, -5));
		node2.setVisited(true);
		node2.setTrap(true);
		Node node3 = map.createOrGet(new Point(3, 3));
		node3.setVisited(true);
		node3.setHoneyAmount(5);
		Node node4 = map.createOrGet(new Point(3, 1));
		node4.setVisited(true);
		node4.setHoneyAmount(30);
		Node node5 = map.createOrGet(new Point(1, 1));
		node5.setVisited(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(new Color(130, 150, 150));

		int squareWidth = getWidth() / noOfSquaresX;
		int squareHeight = getHeight() / noOfSquaresY;
		paintSquares(g, squareWidth, squareHeight);
		paintGrid(g, squareWidth, squareHeight);
	}

	private void paintSquares(Graphics g, int squareWidth, int squareHeight) {
		if (map != null) { // TODO kann weg, ist nur für test-main da (mit
							// createMap())
			for (int x = xMin; x < xMin + noOfSquaresX; ++x) {
				for (int y = yMin; y < yMin + noOfSquaresY; ++y) {
					Node node = map.getNode(x, y);
					if (node != null) {
						paintNode(node, x, y, squareWidth, squareHeight, g);
					}
				}
			}
		}
	}

	private void paintNode(Node node, int x, int y, int squareWidth, int squareHeight, Graphics g) {
		if (node.isTrap()) {
			g.setColor(Color.RED);
		} else if (node.isStone()) {
			g.setColor(Color.BLACK);
		} else if (node.isVisited()) {
			g.setColor(Color.GREEN);
		} else {
			g.setColor(Color.WHITE);
		}
		g.fillRect((x - xMin) * squareWidth, (y - yMin) * squareHeight, squareWidth, squareHeight);
		paintHoney(node, x, y, squareWidth, squareHeight, g);
	}

	private void paintHoney(Node node, int x, int y, int squareWidth, int squareHeight, Graphics g) {
		int honeyAmount = node.getHoneyAmount();
		if (honeyAmount != 0) {
			g.setColor(Color.YELLOW);
			int xEdge = squareWidth / 4;
			int yEdge = squareHeight / 4;
			g.fillRect((x - xMin) * squareWidth + xEdge, (y - yMin) * squareHeight + yEdge, squareWidth - 2 * xEdge,
					squareHeight - 2 * yEdge);

			String text = String.valueOf(honeyAmount);
			int labelX = (int) ((x - xMin) * squareWidth + squareWidth / 2 - g.getFontMetrics().stringWidth(text) / 2);
			int labelY = (int) ((y - yMin) * squareHeight + squareHeight / 2 + g.getFontMetrics().getHeight() / 4);
			g.setColor(Color.BLACK);
			g.drawString(text, labelX, labelY);
		}
	}

	private void paintGrid(Graphics g, int squareWidth, int squareHeight) {
		g.setColor(Color.BLACK);
		for (int i = 1; i < noOfSquaresX; ++i) {
			g.drawLine(i * squareWidth, 0, i * squareWidth, getHeight());
		}
		for (int i = 1; i < noOfSquaresY; ++i) {
			g.drawLine(0, i * squareHeight, getWidth(), i * squareHeight);
		}
	}
}
