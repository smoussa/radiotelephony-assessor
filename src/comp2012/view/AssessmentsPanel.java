package comp2012.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import comp2012.controller.marker.Marker;
import comp2012.controller.parser.assessment.Assessment;
import comp2012.controller.parser.assessment.Candidate;
import comp2012.model.db.DBAccess;
import comp2012.model.db.Filter;
import comp2012.util.Utilities;

public class AssessmentsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static DefaultTableModel tableModel;
	private static Object[] columnNames = { "Candidate ID", "Assessment ID",
		"Date", "Time", "Forename", "Surname", "Company", "Airport", "Mark" };

	private static SpringLayout layout;
	private static JButton addFilterBtn;
	private static Component currentElement;
	private static JPanel panel;

	private String[] canDetails;

	public AssessmentsPanel() {
		super();

		// define layout
		layout = new SpringLayout();
		setLayout(layout);
		setBackground(Styles.PANEL_COLOUR);

		// create labels
		JLabel titleLabel = new JLabel("(RCAS) Radiotelephony Competency"
				+ " Assessment System");
		JLabel filtersLabel = new JLabel("Filters");
		JLabel markedAssLabel = new JLabel("Marked assessments");

		// create buttons
		addFilterBtn = new JButton("Add filter");
		addFilterBtn.setFocusable(false);
		currentElement = addFilterBtn;

		final JButton viewReportBtn = new JButton("View full report");
		viewReportBtn.setVisible(false);

		// set styles
		titleLabel.setFont(Styles.LARGE_LABEL_FONT);
		filtersLabel.setFont(Styles.LARGE_LABEL_FONT);
		markedAssLabel.setFont(Styles.LARGE_LABEL_FONT);
		addFilterBtn.setFont(Styles.COMP_FONT);
		viewReportBtn.setFont(Styles.COMP_FONT);

		addFilterBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AssFilterFrame("Add a filter");
			}
		});

		viewReportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ReportFrame("Report", canDetails);
			}
		});

		// create table of candidates
		final JTable table = new JTable();
		JScrollPane tableScrollPane = new JScrollPane(table);
		table.getTableHeader().setReorderingAllowed(false);
		tableModel = new DefaultTableModel(columnNames, 0) {
			public boolean isCellEditable(int row, int column) {
				if (column == 8) {
					return true;
				}
				return false;
			}

			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 8) {
					return Double.class;
				}
				return String.class;
			}
		};
		tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				if (row != -1) {
					DBAccess.updateMark(tableModel.getValueAt(row, 1),
							tableModel.getValueAt(row, 8));
				}
			}
		});
		canDetails = new String[5];

		table.setModel(tableModel);
		table.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
			private Color PASS_COLOUR = new Color(77, 255, 118);
			private Color FAIL_COLOUR = new Color(252, 53, 53);

			@Override
			public Component getTableCellRendererComponent(//
					JTable table,//
					Object value,//
					boolean isSelected,//
					boolean hasFocus,//
					int row,//
					int column) {
				Component c = super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
				try {
					double v = Double.parseDouble(value.toString());
					if (v < Marker.getPassMark()) {
						c.setBackground(FAIL_COLOUR);
					} else {
						c.setBackground(PASS_COLOUR);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return c;
			}
		});
		table.setFillsViewportHeight(true);

		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						viewReportBtn.setVisible(true);
						int selectedRow = table.convertRowIndexToModel(table
								.getSelectedRow());
						if (selectedRow != -1) {
							// Candidate ID, First name, Surname, Company,
							// Airport
							canDetails[0] = (String) tableModel.getValueAt(
									selectedRow, 0);
							canDetails[1] = (String) tableModel.getValueAt(
									selectedRow, 4);
							canDetails[2] = (String) tableModel.getValueAt(
									selectedRow, 5);
							canDetails[3] = (String) tableModel.getValueAt(
									selectedRow, 6);
							canDetails[4] = (String) tableModel.getValueAt(
									selectedRow, 7);
						}
					}
				});

		// align panel and its components
		layout.putConstraint(SpringLayout.NORTH, titleLabel, 25,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, titleLabel, 0,
				SpringLayout.HORIZONTAL_CENTER, this);

		layout.putConstraint(SpringLayout.NORTH, filtersLabel, 50,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, filtersLabel, 30,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, addFilterBtn, 10,
				SpringLayout.SOUTH, filtersLabel);
		layout.putConstraint(SpringLayout.WEST, addFilterBtn, 30,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, markedAssLabel, 60,
				SpringLayout.NORTH, addFilterBtn);
		layout.putConstraint(SpringLayout.WEST, markedAssLabel, 30,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.WEST, tableScrollPane, 30,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, tableScrollPane, 10,
				SpringLayout.SOUTH, markedAssLabel);
		layout.putConstraint(SpringLayout.EAST, tableScrollPane, -30,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, tableScrollPane, -30,
				SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.SOUTH, viewReportBtn, -10,
				SpringLayout.NORTH, tableScrollPane);
		layout.putConstraint(SpringLayout.EAST, viewReportBtn, 0,
				SpringLayout.EAST, tableScrollPane);

		add(titleLabel);
		add(filtersLabel);
		add(markedAssLabel);
		add(addFilterBtn);
		add(tableScrollPane);
		add(viewReportBtn);

		panel = this;

	}

	public static void addFilterTag(String filterName, final Filter filter) {

		String label = "[x] " + filterName;
		final JButton newFilterBtn = new JButton(label);
		newFilterBtn.setFont(Styles.COMP_FONT);

		newFilterBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DBAccess.removeFilter(filter);
				panel.remove(newFilterBtn);
				panel.validate();
				panel.repaint();
			}
		});

		layout.putConstraint(SpringLayout.NORTH, newFilterBtn, 0,
				SpringLayout.NORTH, addFilterBtn);
		layout.putConstraint(SpringLayout.WEST, newFilterBtn, 15,
				SpringLayout.EAST, currentElement);

		currentElement = newFilterBtn;
		panel.add(newFilterBtn);
		panel.validate();
		panel.repaint();

	}

	/**
	 * The assessment class representing the record.
	 */
	public static void addRecord(Assessment a) {
		/*
		 * Should be added in the order "Candidate ID", "Assessment ID", "Date",
		 * "Time", "Forename", "Surname", "Company", "Airport", "Mark"
		 */
		ArrayList<Object> list = new ArrayList<Object>(9);
		Candidate c = a.getCandidate();
		list.add(c.getId());
		list.add(a.getId());
		Date d = a.getDateTaken();
		String time = Utilities.formatTime(d);
		String date = Utilities.formatDate(d);
		list.add(date);
		list.add(time);
		list.add(c.getForename());
		list.add(c.getSurname());
		list.add(c.getCompany());
		list.add(c.getAirport());
		list.add(a.getAverageScore());
		tableModel.addRow(list.toArray());
	}

	/**
	 * 
	 * @param recordObjects
	 *            order: "Candidate ID", "Assessment ID", "Activity ID", "Date",
	 *            "Time", "Forename", "Surname", "Company", "Airport", "Mark"
	 */
	public static void setRecords(Object[][] recordObjects) {
		tableModel.setDataVector(recordObjects, columnNames);
	}

	/**
	 * 
	 * @param recordObjects
	 *            order: "Candidate ID", "Assessment ID", "Date", "Time",
	 *            "Forename", "Surname", "Company", "Airport", "Mark"
	 */
	public static void addRecord(Object[] recordObjects) {
		tableModel.addRow(recordObjects);
	}
}
