package comp2012.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import comp2012.model.db.DBAccess;
import comp2012.model.db.EqualityFilter;
import comp2012.model.db.Filter;
import comp2012.model.db.RelationalFilter;

public class AssFilterPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox selectedComboBox;
	private boolean marksChosen = false;

	public AssFilterPanel(final AssFilterFrame frame, Dimension dim) {
		super();

		// define layout
		setPreferredSize(dim);
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		setBackground(Styles.PANEL_COLOUR);

		// create labels
		JLabel chooseLabel = new JLabel("Choose a filter");
		chooseLabel.setFont(Styles.MEDIUM_LABEL_FONT);

		/*
		 * create drop down boxes
		 */
		// list of categories
		String categories[] = { "Company", "Airport", "Mark" };
		final JComboBox categoryComboBox = new JComboBox(
				categories);
		categoryComboBox.setFont(Styles.COMP_FONT);
		categoryComboBox.setSelectedIndex(0);

		// list for companies (e.g. BAA)
		final JComboBox companiesComboBox = new JComboBox(
				getCompaniesList());
		companiesComboBox.setFont(Styles.COMP_FONT);
		companiesComboBox.setSelectedIndex(0);
		selectedComboBox = companiesComboBox;

		// list for airports (e.g. LHR)
		final JComboBox airportsComboBox = new JComboBox(
				getAirportsList());
		airportsComboBox.setFont(Styles.COMP_FONT);
		airportsComboBox.setSelectedIndex(0);
		airportsComboBox.setVisible(false);

		// list for marks
		String[] ranges = { "Less than", "More than" };
		final JComboBox rangeComboBox = new JComboBox(ranges);
		rangeComboBox.setFont(Styles.COMP_FONT);
		rangeComboBox.setSelectedIndex(0);
		rangeComboBox.setVisible(false);

		Integer[] marks = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
		final JComboBox marksComboBox = new JComboBox(marks);
		marksComboBox.setFont(Styles.COMP_FONT);
		marksComboBox.setSelectedIndex(0);
		marksComboBox.setVisible(false);

		categoryComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = ((JComboBox) e.getSource()).getSelectedIndex();
				switch (index) {
				// companies
				case 0:
					selectedComboBox.setVisible(false);
					marksComboBox.setVisible(false);
					selectedComboBox = companiesComboBox;
					selectedComboBox.setVisible(true);
					marksChosen = false;
					break;
				case 1:
					selectedComboBox.setVisible(false);
					marksComboBox.setVisible(false);
					selectedComboBox = airportsComboBox;
					selectedComboBox.setVisible(true);
					marksChosen = false;
					break;
				case 2:
					selectedComboBox.setVisible(false);
					selectedComboBox = rangeComboBox;
					selectedComboBox.setVisible(true);
					marksComboBox.setVisible(true);
					marksChosen = true;
					break;
				}
			}
		});

		/*
		 * create buttons
		 */
		JButton addBtn = new JButton("Add");
		JButton cancelBtn = new JButton("Cancel");
		addBtn.setFont(Styles.COMP_FONT);
		cancelBtn.setFont(Styles.COMP_FONT);

		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				String catName = (String) categoryComboBox.getSelectedItem();
				String valueName = (String) selectedComboBox.getSelectedItem();
				String label = catName + ": " + valueName;
				if (marksChosen) {
					String mark = marksComboBox.getSelectedItem().toString();
					label = label.concat(" " + mark);
				}
				Filter f;
				if (!marksChosen) {
					f = new EqualityFilter("candidate", catName.toLowerCase(),
							valueName);
				} else {
					String operator = valueName.equals("More than") ? ">" : "<";
					f = new RelationalFilter("assessment", "averageScore", operator,
							marksComboBox.getSelectedItem().toString());
				}
				DBAccess.addFilter(f);
				AssessmentsPanel.addFilterTag(label, f);
				frame.dispose();
			}
		});

		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});

		// align panel and its components
		layout.putConstraint(SpringLayout.NORTH, chooseLabel, 30,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, chooseLabel, 30,
				SpringLayout.WEST, this);
		// align combo boxes
		layout.putConstraint(SpringLayout.NORTH, categoryComboBox, 20,
				SpringLayout.SOUTH, chooseLabel);
		layout.putConstraint(SpringLayout.WEST, categoryComboBox, 0,
				SpringLayout.WEST, chooseLabel);

		layout.putConstraint(SpringLayout.NORTH, companiesComboBox, 0,
				SpringLayout.NORTH, categoryComboBox);
		layout.putConstraint(SpringLayout.WEST, companiesComboBox, 15,
				SpringLayout.EAST, categoryComboBox);
		layout.putConstraint(SpringLayout.NORTH, airportsComboBox, 0,
				SpringLayout.NORTH, categoryComboBox);
		layout.putConstraint(SpringLayout.WEST, airportsComboBox, 15,
				SpringLayout.EAST, categoryComboBox);
		layout.putConstraint(SpringLayout.NORTH, rangeComboBox, 0,
				SpringLayout.NORTH, categoryComboBox);
		layout.putConstraint(SpringLayout.WEST, rangeComboBox, 15,
				SpringLayout.EAST, categoryComboBox);
		layout.putConstraint(SpringLayout.NORTH, marksComboBox, 0,
				SpringLayout.NORTH, categoryComboBox);
		layout.putConstraint(SpringLayout.WEST, marksComboBox, 15,
				SpringLayout.EAST, rangeComboBox);
		// add and cancel buttons
		layout.putConstraint(SpringLayout.EAST, addBtn, -10, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.SOUTH, addBtn, -10,
				SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.EAST, cancelBtn, -10,
				SpringLayout.WEST, addBtn);
		layout.putConstraint(SpringLayout.SOUTH, cancelBtn, 0,
				SpringLayout.SOUTH, addBtn);

		add(chooseLabel);
		add(categoryComboBox);
		add(companiesComboBox);
		add(airportsComboBox);
		add(rangeComboBox);
		add(marksComboBox);
		add(addBtn);
		add(cancelBtn);

	}

	private String[] getCompaniesList() {
		try {
			return DBAccess.getCompaniesList();
		} catch (SQLException e) {
			e.printStackTrace();
			return new String[] { "None loaded" };
		}
	}

	private String[] getAirportsList() {
		try {
			return DBAccess.getAirportsList();
		} catch (SQLException e) {
			e.printStackTrace();
			return new String[] { "None loaded" };
		}
	}

}
