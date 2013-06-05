package comp2012.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class ReportFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public ReportFrame(String title, String[] details) {
		super(title);

		// get look and feel of OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Failed to load system LookAndFeel.");
		}

		// get dimensions
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int frameWidth = 800;
		int frameHeight = 600;

		// add panel
		ReportPanel panel = new ReportPanel(this,
				new Dimension(800, 4000), details);
		JScrollPane scrollPane = new JScrollPane(panel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setContentPane(scrollPane);
		
		// align window
		setBounds((int) (dim.getWidth() - frameWidth) / 2,
				(int) (dim.getHeight() - frameHeight) / 2, frameWidth,
				frameHeight);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);

	}
	
}
