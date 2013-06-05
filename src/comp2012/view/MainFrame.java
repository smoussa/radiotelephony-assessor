package comp2012.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import comp2012.controller.parser.ActivityParser;
import comp2012.controller.parser.AssessmentParser;
import comp2012.controller.parser.Parser;
import comp2012.controller.parser.ParsingException;
import comp2012.controller.parser.UnexpectedTagException;
import comp2012.controller.parser.assessment.NoSuchActivityException;
import comp2012.model.db.DBAccess;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private Parser assessmentsParser;
	private Parser activityParser;

	private String lastOpened = "";

	public MainFrame(String name) {
		super(name);

		// get look and feel of OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Failed to load system LookAndFeel.");
		}

		// get dimensions
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int frameWidth = 860;
		int frameHeight = 600;

		// add menu bar
		JMenuBar menuBar = new JMenuBar() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Styles.BLACK_COLOUR);
				g.fillRect(0, 0, getWidth(), getHeight());
			}

		};
		menuBar.setPreferredSize(new Dimension(getWidth(), 42));
		menuBar.setBorder(null);
		setJMenuBar(menuBar);

		// add menu items
		JMenu fileMenu = new JMenu("  File  ");
		JMenu helpMenu = new JMenu("  Help  ");
		JMenuItem loadActivityMenuItem = new JMenuItem("Load Activities");
		JMenuItem loadAssessmentMenuItem = new JMenuItem("Load Assessments");
		JMenuItem exportResultsMenuItem = new JMenuItem("Export Results");

		// add styles
		fileMenu.setFont(Styles.COMP_FONT);
		fileMenu.setForeground(Color.WHITE);
		helpMenu.setFont(Styles.COMP_FONT);
		helpMenu.setForeground(Color.WHITE);
		loadActivityMenuItem.setFont(Styles.COMP_FONT);
		loadAssessmentMenuItem.setFont(Styles.COMP_FONT);
		exportResultsMenuItem.setFont(Styles.COMP_FONT);

		fileMenu.add(loadActivityMenuItem);
		fileMenu.add(loadAssessmentMenuItem);
		fileMenu.add(exportResultsMenuItem);
		fileMenu.setBorderPainted(true);
		Border lineBorder = BorderFactory.createLineBorder(Styles.BLACK_COLOUR,
				2);
		fileMenu.getPopupMenu().setBorder(lineBorder);
		fileMenu.getPopupMenu().setBorderPainted(true);
		menuBar.add(fileMenu);
		// menuBar.add(helpMenu); REMOVED until it does something.
		loadAssessmentMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String file = getFile();
				if (file != null) {
					try {
						int num = assessmentsParser.loadFile(file).size();
						JOptionPane.showMessageDialog(MainFrame.this, num
								+ " assessments loaded successfully",
								"Finished", JOptionPane.PLAIN_MESSAGE);
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(MainFrame.this,
								e.getMessage(), "Database Error",
								JOptionPane.ERROR_MESSAGE);
						//e.printStackTrace();
						return;
					} catch (ParsingException e) {
						JOptionPane.showMessageDialog(MainFrame.this, e
								.getCause().getMessage(), "Database Error",
								JOptionPane.ERROR_MESSAGE);
						//e.printStackTrace();
						return;
					} catch (NoSuchActivityException e) {
						JOptionPane.showMessageDialog(
								MainFrame.this,
								"Activity "
										+ e.getAcivityId()
										+ " could not be found. Please load this activity \nfirst before loading assessments again.",
								"Activity not found", JOptionPane.ERROR_MESSAGE);
					} catch (UnexpectedTagException e) {
						JOptionPane.showMessageDialog(MainFrame.this, "Failed to parse - please verify this is an Assessment file",
								"Invalid XML file",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});

		loadActivityMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String file = getFile();
				if (file != null) {
					try {
						int num = activityParser.loadFile(file).size();
						JOptionPane.showMessageDialog(MainFrame.this, num
								+ " activities loaded successfully",
								"Finished", JOptionPane.PLAIN_MESSAGE);
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(MainFrame.this,
								e.getMessage(), "Database Error",
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return;
					} catch (ParsingException e) {
						JOptionPane.showMessageDialog(MainFrame.this, e
								.getCause().getMessage(), "Database Error",
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return;
					} catch (UnexpectedTagException e) {
						JOptionPane.showMessageDialog(MainFrame.this, "Failed to parse - please verify this is an Activity file",
								"Invalid XML file",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});

		exportResultsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(lastOpened);
				fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
				int result = fileChooser.showSaveDialog(MainFrame.this);
				File file = null;
				if (result == JFileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile();
					String path = file.getAbsolutePath();
					if (!path.endsWith(".xml")) {
						path += ".xml";
					}
					file = new File(path);
					Writer writer;
					try {
						writer = new FileWriter(file);
						String xml = DBAccess.getXML();
						writer.write(xml);
						writer.close();
						JOptionPane.showMessageDialog(MainFrame.this,
								"Results successfully written to file.",
								"Completed successfully",
								JOptionPane.PLAIN_MESSAGE);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		// add panel
		MainPanel mainPanel = new MainPanel(new Dimension(frameWidth,
				frameHeight));
		setContentPane(mainPanel);

		// align window
		setBounds((int) (dim.getWidth() - frameWidth) / 2,
				(int) (dim.getHeight() - frameHeight) / 2, frameWidth,
				frameHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		try {
			activityParser = new ActivityParser();
			assessmentsParser = new AssessmentParser();
		} catch (SQLException e) {
			assessmentsParser = null;
			activityParser = null;
			JOptionPane
					.showMessageDialog(
							MainFrame.this,
							"Error initialising the database, you will not be able to do much as the database won't work: "
									+ e.getMessage(), "Database Error",
							JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	private String getFile() {
		JFileChooser fileChooser = new JFileChooser(lastOpened);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileFilter() {
			final static String XML = "xml";

			@Override
			public String getDescription() {
				return "XML files";
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String path = f.getAbsolutePath();
				int index = path.lastIndexOf('.');
				if (index > 0) {
					path = path.substring(index + 1);
					return path.equals(XML);
				}
				return true;
			}
		});
		int result = fileChooser.showOpenDialog(this);
		File file = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			if (file.exists()) {
				lastOpened = file.getParent();
				return file.getAbsolutePath();
			} else {
				JOptionPane.showMessageDialog(null, "The file does not exist.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return null;
	}

	public static void main(String[] args) {
		new MainFrame("Radio Telephony Assessor");

		// Object[] sampleRow = new Object[10];
		// sampleRow[0] = 1;
		// sampleRow[1] = 23;
		// sampleRow[2] = 2;
		// sampleRow[3] = "2012-12-06";
		// sampleRow[4] = "10:13";
		// sampleRow[5] = "Jim";
		// sampleRow[6] = "Davidson";
		// sampleRow[7] = "BAA";
		// sampleRow[8] = "LHR";
		// sampleRow[9] = 60;

		// call this method (from wherever) to add a record to the table
		// AssessmentsPanel.addRecord(sampleRow);

	}

}
