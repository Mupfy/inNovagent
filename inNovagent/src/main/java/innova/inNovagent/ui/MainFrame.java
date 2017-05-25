package innova.inNovagent.ui;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	public MainFrame() {
		super("VisualisationFrame");
		
		add(new MapPainter());

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}
