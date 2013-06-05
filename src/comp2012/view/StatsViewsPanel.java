package comp2012.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import comp2012.model.db.DBAccess;

public class StatsViewsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private String[] timeRangeList;

	private static SpringLayout layout;

	public StatsViewsPanel(final StatsViewsFrame frame, Dimension dim) {
		super();

		// define layout
		setPreferredSize(dim);
		layout = new SpringLayout();
		setLayout(layout);
		setBackground(Styles.PANEL_COLOUR);

		// create labels
		JLabel timeLabel = new JLabel("Time period");

		final JLabel label1 = new JLabel("From");
		final JLabel label2 = new JLabel("To");

		JLabel categoryLabel = new JLabel("Categories");
		JLabel categoryHelpLabel = new JLabel("Hold down CTRL or SHIFT"
				+ " to select multiple items.");

		// radio buttons
		final JRadioButton yearsButton = new JRadioButton("Years");
		yearsButton.setSelected(true);
		JRadioButton monthsButton = new JRadioButton("Months");
		ButtonGroup group = new ButtonGroup();
		group.add(yearsButton);
		group.add(monthsButton);

		// set styles
		timeLabel.setFont(Styles.LARGE_LABEL_FONT);
		yearsButton.setFont(Styles.MEDIUM_LABEL_FONT);
		monthsButton.setFont(Styles.MEDIUM_LABEL_FONT);

		label1.setFont(Styles.MEDIUM_LABEL_FONT);
		label2.setFont(Styles.MEDIUM_LABEL_FONT);

		categoryLabel.setFont(Styles.LARGE_LABEL_FONT);

		/*
		 * create drop down boxes
		 */

		// list of years and months
		final String[] years = { "2010", "2011", "2012", "2013" };
		timeRangeList = years;

		// first dropwdown
		final JComboBox combo1 = new JComboBox(timeRangeList);
		combo1.setFont(Styles.COMP_FONT);
		combo1.setSelectedIndex(0);

		// second dropdown
		final JComboBox combo2 = new JComboBox(timeRangeList);
		combo2.setFont(Styles.COMP_FONT);
		combo2.setSelectedIndex(0);

		// category dropdown
		String[] categories = { "Companies", "Airports" };
		final JComboBox catCombo = new JComboBox(categories);
		catCombo.setFont(Styles.COMP_FONT);
		catCombo.setSelectedIndex(0);

		yearsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				label1.setText("From");
				label2.setVisible(true);
				combo2.setVisible(true);
			}
		});

		monthsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				label1.setText("During");
				label2.setVisible(false);
				combo2.setVisible(false);
			}
		});

		/*
		 * List of categories
		 */

		final DefaultListModel catListModel = new DefaultListModel();
		final JList categoryList = new JList(catListModel);
		categoryList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane catScrollPane = new JScrollPane(categoryList);
		categoryList.setFont(Styles.MEDIUM_LABEL_FONT);
		String[] catList;
		if (catCombo.getSelectedIndex() == 0) {
			catList = getCompaniesList();
		} else {
			catList = getAirportsList();
		}
		for (String string : catList) {
			catListModel.addElement(string);
		}
		catCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox cb = (JComboBox) e.getSource();
				String[] catList;
				if (cb.getSelectedIndex() == 0) {
					catList = getCompaniesList();
				} else {
					catList = getAirportsList();
				}
				catListModel.clear();
				for (String string : catList) {
					catListModel.addElement(string);
				}
			}
		});

		/*
		 * create buttons
		 */
		JButton saveBtn = new JButton("Save");
		JButton cancelBtn = new JButton("Cancel");
		saveBtn.setFont(Styles.COMP_FONT);
		cancelBtn.setFont(Styles.COMP_FONT);

		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (categoryList.getSelectedIndices().length > 0) {
					// change view
					if (yearsButton.isSelected()) {
						String year1 = years[combo1.getSelectedIndex()];
						String year2 = years[combo2.getSelectedIndex()];
						String type = catCombo.getSelectedItem().toString();
						Object[] objects = categoryList.getSelectedValues();
						List<String> selection = new ArrayList<String>();
						for(Object o : objects){
							selection.add(o.toString());
						}
						StatisticsPanel.setGraph(year1, year2, type, selection);
					} else {
						String year = years[combo1.getSelectedIndex()];
						String type = catCombo.getSelectedItem().toString();

						Object[] objects = categoryList.getSelectedValues();
						List<String> selection = new ArrayList<String>();
						for(Object o : objects){
							selection.add(o.toString());
						}
						StatisticsPanel.setGraph(year, type, selection);

					}
					frame.dispose();
				} else {
					String category = catCombo.getSelectedItem().toString().toLowerCase();
					JOptionPane
							.showMessageDialog(StatsViewsPanel.this, "No "
									+ category
									+ " selected. Please select at least one.",
									"No " + category + " selected",
									JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});

		// align panel and its components
		layout.putConstraint(SpringLayout.NORTH, timeLabel, 30,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, timeLabel, 30,
				SpringLayout.WEST, this);

		// align radio buttons
		layout.putConstraint(SpringLayout.NORTH, yearsButton, 15,
				SpringLayout.SOUTH, timeLabel);
		layout.putConstraint(SpringLayout.WEST, yearsButton, 0,
				SpringLayout.WEST, timeLabel);
		layout.putConstraint(SpringLayout.NORTH, monthsButton, 0,
				SpringLayout.NORTH, yearsButton);
		layout.putConstraint(SpringLayout.WEST, monthsButton, 15,
				SpringLayout.EAST, yearsButton);

		// align labels and drop downs
		layout.putConstraint(SpringLayout.NORTH, label1, 15,
				SpringLayout.SOUTH, yearsButton);
		layout.putConstraint(SpringLayout.WEST, label1, 0, SpringLayout.WEST,
				yearsButton);
		layout.putConstraint(SpringLayout.NORTH, combo1, 0, SpringLayout.NORTH,
				label1);
		layout.putConstraint(SpringLayout.WEST, combo1, 15, SpringLayout.EAST,
				label1);
		layout.putConstraint(SpringLayout.NORTH, label2, 0, SpringLayout.NORTH,
				label1);
		layout.putConstraint(SpringLayout.WEST, label2, 30, SpringLayout.EAST,
				combo1);
		layout.putConstraint(SpringLayout.NORTH, combo2, 0, SpringLayout.NORTH,
				label2);
		layout.putConstraint(SpringLayout.WEST, combo2, 15, SpringLayout.EAST,
				label2);

		// align categories
		layout.putConstraint(SpringLayout.NORTH, categoryLabel, 80,
				SpringLayout.SOUTH, yearsButton);
		layout.putConstraint(SpringLayout.WEST, categoryLabel, 0,
				SpringLayout.WEST, yearsButton);

		layout.putConstraint(SpringLayout.WEST, catScrollPane, 30,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, catScrollPane, 10,
				SpringLayout.SOUTH, categoryLabel);
		layout.putConstraint(SpringLayout.EAST, catScrollPane, -30,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, catScrollPane, -50,
				SpringLayout.SOUTH, this);

		layout.putConstraint(SpringLayout.SOUTH, categoryHelpLabel, -10,
				SpringLayout.NORTH, catScrollPane);
		layout.putConstraint(SpringLayout.EAST, categoryHelpLabel, 0,
				SpringLayout.EAST, catScrollPane);

		layout.putConstraint(SpringLayout.SOUTH, catCombo, -10,
				SpringLayout.NORTH, catScrollPane);
		layout.putConstraint(SpringLayout.WEST, catCombo, 15,
				SpringLayout.EAST, categoryLabel);

		// align save and cancel buttons
		layout.putConstraint(SpringLayout.EAST, saveBtn, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, saveBtn, -10,
				SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.EAST, cancelBtn, -10,
				SpringLayout.WEST, saveBtn);
		layout.putConstraint(SpringLayout.SOUTH, cancelBtn, 0,
				SpringLayout.SOUTH, saveBtn);

		add(timeLabel);

		add(yearsButton);
		add(monthsButton);

		add(label1);
		add(combo1);
		add(label2);
		add(combo2);

		add(categoryLabel);
		add(catScrollPane);
		add(categoryHelpLabel);
		add(catCombo);

		add(saveBtn);
		add(cancelBtn);

	}

	private String[] getCompaniesList() {
		String[] arr = new String[] { "No companies" };
		try {
			arr = DBAccess.getCompaniesList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return arr;
	}

	private String[] getAirportsList() {
		String[] arr = new String[] { "No airports" };
		try {
			arr = DBAccess.getAirportsList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return arr;
	}

}
