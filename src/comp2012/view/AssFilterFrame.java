package comp2012.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class AssFilterFrame extends JFrame {

	private static final long serialVersionUID = -2561610208362401680L;

	public AssFilterFrame(String title) {
		super(title);

		// get look and feel of OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Failed to load system LookAndFeel.");
		}

		// get dimensions
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int frameWidth = 400;
		int frameHeight = 200;

		// add panel
		AssFilterPanel panel = new AssFilterPanel(this, new Dimension(frameWidth,
				frameHeight));
		setContentPane(panel);

		// align window
		setBounds((int) (dim.getWidth() - frameWidth) / 2,
				(int) (dim.getHeight() - frameHeight) / 2, frameWidth,
				frameHeight);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);

	}
	
}
