package comp2012.view;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public MainPanel(Dimension dim) {
		super();

		setPreferredSize(dim);
		setLayout(new GridLayout());
		setBackground(Styles.BLACK_COLOUR);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(Styles.COMP_FONT);

		JPanel assessmentsPanel = new AssessmentsPanel();
		tabbedPane.addTab("Assessments", assessmentsPanel);
		JPanel statsPanel = new StatisticsPanel();
		tabbedPane.addTab("Statistics", statsPanel);

		add(tabbedPane);

	}
	
}
