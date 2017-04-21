package innova.inNovagent.gui;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	public MainFrame() {
		super("VisualisationFrame");
		
		add(new MapPainter());

		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}
