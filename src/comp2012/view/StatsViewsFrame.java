package comp2012.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class StatsViewsFrame extends JFrame {

	private static final long serialVersionUID = -2561610208362401680L;

	public StatsViewsFrame(String title) {
		super(title);

		// get look and feel of OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Failed to load system LookAndFeel.");
		}

		// get dimensions
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int frameWidth = 700;
		int frameHeight = 500;

		// add panel
		StatsViewsPanel panel = new StatsViewsPanel(this, new Dimension(frameWidth,
				frameHeight));
		setContentPane(panel);

		// align window
		setBounds((int) (dim.getWidth() - frameWidth) / 2,
				(int) (dim.getHeight() - frameHeight) / 2, frameWidth,
				frameHeight);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		pack();

	}

}
