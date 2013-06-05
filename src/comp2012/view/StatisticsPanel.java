package comp2012.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import comp2012.model.db.DBAccess;

public class StatisticsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static JLabel graph = null;
	private static JPanel panel;

	public StatisticsPanel() {
		super();

		// define layout
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		setBackground(Styles.PANEL_COLOUR);

		// create labels
		JLabel graphViewLabel = new JLabel("Graph view");

		// create buttons
		JButton changeViewBtn = new JButton("Change view");
		changeViewBtn.setFocusable(false);

		DBAccess db = null;
		try {
			db = new DBAccess();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Calendar cal1 = new GregorianCalendar();
		cal1.set(2012, 04, 06);
		Calendar cal2 = new GregorianCalendar();
		cal2.set(2012, 04, 07);

		// Create Graph
		graph = new JLabel(" ");

		// set styles
		graphViewLabel.setFont(Styles.LARGE_LABEL_FONT);
		changeViewBtn.setFont(Styles.COMP_FONT);

		changeViewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new StatsViewsFrame("Add a filter");
			}
		});

		// align panel1 and its components
		layout.putConstraint(SpringLayout.NORTH, graphViewLabel, 30,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, graphViewLabel, 30,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, changeViewBtn, 15,
				SpringLayout.SOUTH, graphViewLabel);
		layout.putConstraint(SpringLayout.WEST, changeViewBtn, 0,
				SpringLayout.WEST, graphViewLabel);
		layout.putConstraint(SpringLayout.NORTH, graph, 70, SpringLayout.SOUTH,
				graphViewLabel);
		layout.putConstraint(SpringLayout.WEST, graph, 0, SpringLayout.WEST,
				graphViewLabel);

		add(graphViewLabel);
		add(changeViewBtn);
		add(graph);
		panel = this;

	}

	/**
	 * 
	 * @param year
	 * @param selectionType
	 *            - should be "Company" or "Airport"
	 * @param selection
	 *            - which compnies or airports they selected
	 */
	static void setGraph(String year, String selectionType,
			List<String> selection) {
		if (selectionType.equals("Companies")) {
			selectionType = "Company";
		} else if (selectionType.equals("Airports")) {
			selectionType = "Airport";
		}
		String title = selectionType + " comparison for the year " + year;
		double[][] marks = new double[selection.size()][12];
		String[] months = new String[] { "Jan", "Feb", "Mar", "Apr", "May",
				"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		for (int j = 0; j < selection.size(); j++) {
			for (int i = 0; i < months.length; i++) {
				try {
					int y = Integer.parseInt(year);
					marks[j][i] = DBAccess.getMarksCompany(selectionType,
							selection.get(j), y, i);
				} catch (SQLException e) {
				}
			}
		}
		graph.setIcon(new ImageIcon(Graphs.getGraph(
				selection.toArray(new String[selection.size()]), months, marks,
				title)));
	}

	static void setGraph(String year1, String year2, String selectionType,
			List<String> selection) {
		if (selectionType.equals("Companies")) {
			selectionType = "Company";
		} else if (selectionType.equals("Airports")) {
			selectionType = "Airport";
		}
		String title = selectionType + " comparison for the years " + year1
				+ " - " + year2;
		final int y1 = Integer.parseInt(year1);
		final int y2 = Integer.parseInt(year2);
		String[] years = new String[y2 - y1 + 1];
		double[][] marks = new double[selection.size()][y2 - y1 + 1];
		for (int i = 0; i <= y2 - y1; i++) {
			years[i] = Integer.toString(y1 + i);
		}
		for (int j = 0; j < selection.size(); j++) {
			for (int i = 0; i < years.length; i++) {
				try {
					marks[j][i] = DBAccess.getMarksCompany(selectionType,
							selection.get(j), y1 + i);
				} catch (SQLException e) {
				}
			}
		}
		graph.setIcon(new ImageIcon(Graphs.getGraph(
				selection.toArray(new String[selection.size()]), years, marks,
				title)));
	}

}
