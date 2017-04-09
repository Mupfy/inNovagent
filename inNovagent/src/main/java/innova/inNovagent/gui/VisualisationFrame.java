package innova.inNovagent.gui;

import javax.swing.JButton;
import javax.swing.JFrame;

public class VisualisationFrame extends JFrame {
	public VisualisationFrame() {
		super("VisualisationFrame");
		
		add(new VisualisationPanel());
		// Because Buttons are a must-have
		add(new JButton("Do something"));
		pack();
		setResizable(false);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new VisualisationFrame();
	}
}
