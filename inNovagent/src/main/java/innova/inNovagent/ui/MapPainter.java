package innova.inNovagent.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import innova.inNovagent.agents.SyncMapAgent;
import innova.inNovagent.core.Node;
import innova.inNovagent.core.NodeMap;
import innova.inNovagent.util.Point;

public class MapPainter extends JPanel {
	private SyncMapAgent mapPainterAgent;
	
	private NodeMap map;
	private int squareWidth;
	private int squareHeight;

	private static Color BACKGROUND_COLOR = new Color(130, 150, 150);
	private static Color HOME_COLOR = Color.BLUE;
	private static Color TRAP_COLOR = Color.RED;
	private static Color STONE_COLOR = Color.BLACK;
	private static Color VISITED_COLOR = Color.GREEN;
	private static Color UNVISITED_COLOR = Color.WHITE;
	private static Color HONEY_COLOR = Color.YELLOW;
	private static Color DANGEROUS_COLOR = Color.ORANGE;
	private static Color TEXT_GRID_COLOR = Color.BLACK;

	public MapPainter(SyncMapAgent mapPainterAgent, NodeInformationPanel nodeInformationPanel) {
		this.mapPainterAgent = mapPainterAgent;
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				nodeInformationPanel.updateNodeInformation(getNodeUnderMouse(e));
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
			}
		});

		setPreferredSize(new Dimension(800, 800));
//		setSize(new Dimension(800, 800));
	}
	
	private Node getNodeUnderMouse(MouseEvent e) {
		int x = map.getMinX() + (int) (e.getX() / squareWidth);
		int y = map.getMinY() + (int) ((getHeight() - e.getY()) / squareHeight);
		return map.getNode(x, y);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(BACKGROUND_COLOR);

		map = mapPainterAgent.getMap();
		squareWidth = getWidth() / map.getWidth();
		squareHeight = getHeight() / map.getHeight();
		paintSquares(g);
		paintGrid(g);
	}

	private void paintSquares(Graphics g) {
		for (int x = map.getMinX(); x < map.getMinX() + map.getWidth(); ++x) {
			for (int y = map.getMinY(); y < map.getMinY() + map.getHeight(); ++y) {
				Node node = map.getNode(x, y);
				if (node != null) {
					paintNode(node, x, y, g);
				}
			}
		}
	}

	private void paintNode(Node node, int x, int y, Graphics g) {
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
		paintHoney(node, x, y, g);
	}

	private void paintHoney(Node node, int x, int y, Graphics g) {
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

	private void paintGrid(Graphics g) {
		g.setColor(TEXT_GRID_COLOR);
		for (int i = 1; i < map.getWidth(); ++i) {
			g.drawLine(i * squareWidth, 0, i * squareWidth, getHeight());
		}
		for (int i = 1; i < map.getHeight(); ++i) {
			g.drawLine(0, getHeight() - i * squareHeight, getWidth(), getHeight() - i * squareHeight);
		}
	}
}
