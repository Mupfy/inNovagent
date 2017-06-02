package innova.inNovagent.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import innova.inNovagent.agents.SyncMapAgent;
import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Point;

//TODO: Man kann einen zweiten Map-Painter starten und das führt zum Absturz. Bzw man kann den Map-Painter ncit reseten und alle neuen Agenten bekommen falsche Informationen bei einer neuen Karte
public class MapPainter extends JPanel {
	private SyncMapAgent mapPainterAgent;

	private static Color BACKGROUND_COLOR = new Color(130, 150, 150);
	private static Color HOME_COLOR = Color.BLUE;
	private static Color TRAP_COLOR = Color.RED;
	private static Color STONE_COLOR = Color.BLACK;
	private static Color VISITED_COLOR = Color.GREEN;
	private static Color UNVISITED_COLOR = Color.WHITE;
	private static Color HONEY_COLOR = Color.YELLOW;
	private static Color DANGEROUS_COLOR = Color.ORANGE;
	private static Color TEXT_GRID_COLOR = Color.BLACK;

	public MapPainter(SyncMapAgent mapPainterAgent) {
		this.mapPainterAgent = mapPainterAgent;

		setPreferredSize(new Dimension(800, 800));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(BACKGROUND_COLOR);

		NodeMap map = mapPainterAgent.getMap();
		int squareWidth = getWidth() / map.getWidth();
		int squareHeight = getHeight() / map.getHeight();
		paintSquares(g, map, squareWidth, squareHeight);
		paintGrid(g, map, squareWidth, squareHeight);
	}

	private void paintSquares(Graphics g, NodeMap map, int squareWidth, int squareHeight) {
		for (int x = map.getMinX(); x < map.getMinX() + map.getWidth(); ++x) {
			for (int y = map.getMinY(); y < map.getMinY() + map.getHeight(); ++y) {
				Node node = map.getNode(x, y);
				if (node != null) {
					paintNode(node, x, y, map, squareWidth, squareHeight, g);
				}
			}
		}
	}

	private void paintNode(Node node, int x, int y, NodeMap map, int squareWidth, int squareHeight, Graphics g) {
		if (node.getPosition().equals(new Point(0, 0))) {
			g.setColor(HOME_COLOR);
		} else if (node.isTrap()) {
			g.setColor(TRAP_COLOR);
		} else if (node.isStone()) {
			g.setColor(STONE_COLOR);
		} else if(node.isDangerous()){
			g.setColor(DANGEROUS_COLOR);
		} else if (node.isVisited()) {
			g.setColor(VISITED_COLOR);
		}else{
			g.setColor(UNVISITED_COLOR);
		}
		g.fillRect((x - map.getMinX()) * squareWidth, getHeight() - (y - map.getMinY()) * squareHeight - squareHeight, squareWidth,
				squareHeight);
		paintHoney(node, x, y, map, squareWidth, squareHeight, g);
	}

	private void paintHoney(Node node, int x, int y, NodeMap map, int squareWidth, int squareHeight, Graphics g) {
		int honeyAmount = node.getHoneyAmount();
		if (honeyAmount != 0) {
			g.setColor(HONEY_COLOR);
			int xEdge = squareWidth / 4;
			int yEdge = squareHeight / 4;
			g.fillRect((x - map.getMinX()) * squareWidth + xEdge,
					getHeight() - (y - map.getMinY()) * squareHeight - squareHeight + yEdge, squareWidth - 2 * xEdge,
					squareHeight - 2 * yEdge);

			String text = String.valueOf(honeyAmount);
			int labelX = (int) ((x - map.getMinX()) * squareWidth + squareWidth / 2
					- g.getFontMetrics().stringWidth(text) / 2);
			int labelY = (int) (getHeight() - (y - map.getMinY()) * squareHeight - squareHeight + squareHeight / 2
					+ g.getFontMetrics().getHeight() / 4);
			g.setColor(TEXT_GRID_COLOR);
			g.drawString(text, labelX, labelY);
		}
	}

	private void paintGrid(Graphics g, NodeMap map, int squareWidth, int squareHeight) {
		g.setColor(TEXT_GRID_COLOR);
		for (int i = 1; i < map.getWidth(); ++i) {
			g.drawLine(i * squareWidth, 0, i * squareWidth, getHeight());
		}
		for (int i = 1; i < map.getHeight(); ++i) {
			g.drawLine(0, getHeight() - i * squareHeight, getWidth(), getHeight() - i * squareHeight);
		}
	}
}
