package comp2012.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import comp2012.controller.marker.Feedback;
import comp2012.controller.parser.assessment.Assessment;
import comp2012.controller.parser.assessment.Recording;
import comp2012.model.db.DBAccess;

public class ReportPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private SpringLayout layout;
	private JPanel panel;
	private String candidateID;

	public ReportPanel(final ReportFrame frame, Dimension dim,
			String[] details) {
		super();

		// define layout
		setPreferredSize(dim);
		layout = new SpringLayout();
		setLayout(layout);
		setBackground(Styles.PANEL_COLOUR);
		panel = this;

		// candidate details titles
		JLabel candDetailsLabel = new JLabel("Candidate details");
		candDetailsLabel.setFont(Styles.LARGE_LABEL_FONT);
		JLabel idLabel = new JLabel("ID");
		JLabel fNameLabel = new JLabel("First name");
		JLabel sNameLabel = new JLabel("Surname");
		JLabel companyLabel = new JLabel("Company");
		JLabel airportLabel = new JLabel("Airport");

		// add styles
		idLabel.setFont(Styles.COMP_FONT);
		idLabel.setForeground(Styles.GREY_COLOUR);
		fNameLabel.setFont(Styles.COMP_FONT);
		fNameLabel.setForeground(Styles.GREY_COLOUR);
		sNameLabel.setFont(Styles.COMP_FONT);
		sNameLabel.setForeground(Styles.GREY_COLOUR);
		companyLabel.setFont(Styles.COMP_FONT);
		companyLabel.setForeground(Styles.GREY_COLOUR);
		airportLabel.setFont(Styles.COMP_FONT);
		airportLabel.setForeground(Styles.GREY_COLOUR);

		// load candidate details
		JLabel[] canLabels = getCandidateDetails(details);

		// assessments details
		JLabel assTakenLabel = new JLabel("Assessments taken");
		assTakenLabel.setFont(Styles.LARGE_LABEL_FONT);

		layout.putConstraint(SpringLayout.NORTH, candDetailsLabel, 30,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, candDetailsLabel, 30,
				SpringLayout.WEST, this);
		// id
		layout.putConstraint(SpringLayout.NORTH, idLabel, 15,
				SpringLayout.SOUTH, candDetailsLabel);
		layout.putConstraint(SpringLayout.WEST, idLabel, 15,
				SpringLayout.WEST, candDetailsLabel);

		layout.putConstraint(SpringLayout.NORTH, canLabels[0], 15,
				SpringLayout.SOUTH, idLabel);
		layout.putConstraint(SpringLayout.WEST, canLabels[0], 0,
				SpringLayout.WEST, idLabel);
		// first name
		layout.putConstraint(SpringLayout.NORTH, fNameLabel, 0,
				SpringLayout.NORTH, idLabel);
		layout.putConstraint(SpringLayout.WEST, fNameLabel, 50,
				SpringLayout.EAST, canLabels[0]);
		layout.putConstraint(SpringLayout.NORTH, canLabels[1], 15,
				SpringLayout.SOUTH, idLabel);
		layout.putConstraint(SpringLayout.WEST, canLabels[1], 0,
				SpringLayout.WEST, fNameLabel);
		// surname
		layout.putConstraint(SpringLayout.NORTH, sNameLabel, 0,
				SpringLayout.NORTH, idLabel);
		layout.putConstraint(SpringLayout.WEST, sNameLabel, 50,
				SpringLayout.EAST, canLabels[1]);
		layout.putConstraint(SpringLayout.NORTH, canLabels[2], 15,
				SpringLayout.SOUTH, idLabel);
		layout.putConstraint(SpringLayout.WEST, canLabels[2], 0,
				SpringLayout.WEST, sNameLabel);
		// company
		layout.putConstraint(SpringLayout.NORTH, companyLabel, 0,
				SpringLayout.NORTH, idLabel);
		layout.putConstraint(SpringLayout.WEST, companyLabel, 50,
				SpringLayout.EAST, canLabels[2]);
		layout.putConstraint(SpringLayout.NORTH, canLabels[3], 15,
				SpringLayout.SOUTH, idLabel);
		layout.putConstraint(SpringLayout.WEST, canLabels[3], 0,
				SpringLayout.WEST, companyLabel);
		// airport
		layout.putConstraint(SpringLayout.NORTH, airportLabel, 0,
				SpringLayout.NORTH, idLabel);
		layout.putConstraint(SpringLayout.WEST, airportLabel, 50,
				SpringLayout.EAST, canLabels[3]);
		layout.putConstraint(SpringLayout.NORTH, canLabels[4], 15,
				SpringLayout.SOUTH, idLabel);
		layout.putConstraint(SpringLayout.WEST, canLabels[4], 0,
				SpringLayout.WEST, airportLabel);

		// assessments
		layout.putConstraint(SpringLayout.NORTH, assTakenLabel, 160,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, assTakenLabel, 0,
				SpringLayout.WEST, candDetailsLabel);

		// get candidate assessments
		getCandidateAssessments();

		// add candidate details data labels
		for (int i = 0; i < canLabels.length; i++) {
			add(canLabels[i]);
		}		
		// add candidate details static labels
		add(candDetailsLabel);
		add(idLabel);
		add(fNameLabel);
		add(sNameLabel);
		add(companyLabel);
		add(airportLabel);

		add(assTakenLabel);

	}

	private JLabel[] getCandidateDetails(String[] details) {

		// get ID
		candidateID = details[0];

		// labels for the values
		JLabel[] labels = new JLabel[details.length];
		for (int i = 0; i < details.length; i++) {
			labels[i] = new JLabel(details[i]);
			labels[i].setFont(Styles.MEDIUM_LABEL_FONT);
		}

		return labels;
	}

	private void getCandidateAssessments() {

		List<Assessment> assessments = 
				DBAccess.getSingleton().getAssessments(candidateID);

		// download report button
		JButton reportButton = new JButton("Download XML report");
		reportButton.setFont(Styles.COMP_FONT);
		
		reportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
				int result = fileChooser.showSaveDialog(panel);
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
						List<String> singleID = new ArrayList<String>();
						singleID.add(candidateID);
						String xml = DBAccess.getXML(singleID);
						writer.write(xml);
						writer.close();
						JOptionPane.showMessageDialog(panel,
								"Results successfully written to file.",
								"Completed successfully",
								JOptionPane.PLAIN_MESSAGE);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		layout.putConstraint(SpringLayout.NORTH, reportButton, 40,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, reportButton, -80,
				SpringLayout.EAST, panel);
		panel.add(reportButton);
		
		int a = 0;
		for (Assessment assessment : assessments) {

			// add the assessment id label
			JLabel assessIDLabel = new JLabel("Assessment " + assessment.getId());
			assessIDLabel.setFont(Styles.MEDIUM_LABEL_FONT);
			layout.putConstraint(SpringLayout.NORTH, assessIDLabel, 200,
					SpringLayout.NORTH, panel);
			layout.putConstraint(SpringLayout.WEST, assessIDLabel, 40,
					SpringLayout.WEST, panel);
			panel.add(assessIDLabel);

			double avgScore = (int) (assessment.getAverageScore() * 10) / 10.0;
			JLabel avgMarkLabel = new JLabel();
			avgMarkLabel.setFont(Styles.MEDIUM_LABEL_FONT);
			String text = "";
			if (avgScore > 90) {
				avgMarkLabel.setForeground(Styles.GREEN_COLOUR);
				text = "[ PASS ]    Average score: " + avgScore;
			} else {
				avgMarkLabel.setForeground(Styles.RED_COLOUR);
				text = "[ FAIL ]    Average score: " + avgScore;
			}
			avgMarkLabel.setText(text);
			layout.putConstraint(SpringLayout.NORTH, avgMarkLabel, 200,
					SpringLayout.NORTH, panel);
			layout.putConstraint(SpringLayout.WEST, avgMarkLabel, 40,
					SpringLayout.EAST, assessIDLabel);
			panel.add(avgMarkLabel);

			int r = a;
			List<Recording> recordings = assessment.getRecordings();
			for (Recording recording : recordings) {

				// add the activity id label
				JLabel activityIDLabel = new JLabel("Activity "
						+ recording.getActivityId());
				activityIDLabel.setFont(Styles.MEDIUM_LABEL_FONT);
				activityIDLabel.setForeground(Styles.BLUE_COLOUR);
				layout.putConstraint(SpringLayout.NORTH, activityIDLabel, r + 15,
						SpringLayout.SOUTH, assessIDLabel);
				layout.putConstraint(SpringLayout.WEST, activityIDLabel, 0,
						SpringLayout.WEST, assessIDLabel);
				panel.add(activityIDLabel);

				// get recording id
				JLabel recordingIDLabel = new JLabel("Recording ID "
						+ recording.getId());
				recordingIDLabel.setFont(Styles.MEDIUM_LABEL_FONT);
				recordingIDLabel.setForeground(Styles.BLUE_COLOUR);
				layout.putConstraint(SpringLayout.NORTH, recordingIDLabel, 0,
						SpringLayout.NORTH, activityIDLabel);
				layout.putConstraint(SpringLayout.WEST, recordingIDLabel, 30,
						SpringLayout.EAST, activityIDLabel);
				panel.add(recordingIDLabel);

				// get expected response
				JLabel expectedLabel = new JLabel("Expected response");
				expectedLabel.setFont(Styles.COMP_FONT);
				expectedLabel.setForeground(Styles.GREY_COLOUR);
				layout.putConstraint(SpringLayout.NORTH, expectedLabel, 10,
						SpringLayout.SOUTH, activityIDLabel);
				layout.putConstraint(SpringLayout.WEST, expectedLabel, 0,
						SpringLayout.WEST, activityIDLabel);
				panel.add(expectedLabel);

				JLabel expectedValueLabel = new JLabel(
						"<html><div " +
								"style='background-color:#FFFFFF; padding:10px;'>" +
								recording.getExpectedResponse()
								+ "</div></html>");
				expectedValueLabel.setFont(Styles.COMP_FONT);
				expectedValueLabel.setPreferredSize(new Dimension(675, 75));
				layout.putConstraint(SpringLayout.NORTH, expectedValueLabel, 5,
						SpringLayout.SOUTH, expectedLabel);
				layout.putConstraint(SpringLayout.WEST, expectedValueLabel, 0,
						SpringLayout.WEST, expectedLabel);
				panel.add(expectedValueLabel);

				// get feedback/actual response
				JLabel feedbackLabel = new JLabel("Actual response");
				feedbackLabel.setFont(Styles.COMP_FONT);
				feedbackLabel.setForeground(Styles.GREY_COLOUR);
				layout.putConstraint(SpringLayout.NORTH, feedbackLabel, 10,
						SpringLayout.SOUTH, expectedValueLabel);
				layout.putConstraint(SpringLayout.WEST, feedbackLabel, 0,
						SpringLayout.WEST, activityIDLabel);
				panel.add(feedbackLabel);

				Feedback feedback = recording.getFeedback();
				JLabel feedbackValueLabel = new JLabel(
						"<html><div " +
								"style='background-color:#FFFFFF; padding:10px;'>" +
								feedback.getFeedback()
								+ "</div></html>");
				feedbackValueLabel.setFont(Styles.COMP_FONT);
				feedbackValueLabel.setPreferredSize(new Dimension(675, 75));
				layout.putConstraint(SpringLayout.NORTH, feedbackValueLabel, 5,
						SpringLayout.SOUTH, feedbackLabel);
				layout.putConstraint(SpringLayout.WEST, feedbackValueLabel, 0,
						SpringLayout.WEST, feedbackLabel);
				panel.add(feedbackValueLabel);

				double score = (int) (feedback.getScore() * 10) / 10.0;
				JLabel markLabel = new JLabel();
				markLabel.setFont(Styles.COMP_FONT);
				String markText = "";
				if (score > 90) {
					markLabel.setForeground(Styles.GREEN_COLOUR);
					markText = "[ PASS ]    Mark: " + score;
				} else {
					markLabel.setForeground(Styles.RED_COLOUR);
					markText = "[ FAIL ]    Mark: " + score;
				}
				markLabel.setText(markText);
				layout.putConstraint(SpringLayout.NORTH, markLabel, 10,
						SpringLayout.SOUTH, feedbackValueLabel);
				layout.putConstraint(SpringLayout.EAST, markLabel, 0,
						SpringLayout.EAST, feedbackValueLabel);
				panel.add(markLabel);

				r += 300; // distance between recordings
				a += r;
			}

		}

		panel.validate();
		panel.repaint();
	}

}
