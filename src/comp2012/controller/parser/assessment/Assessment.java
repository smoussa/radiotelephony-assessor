package comp2012.controller.parser.assessment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import comp2012.controller.parser.XMLTagAbstr;

public class Assessment extends XMLTagAbstr {

	private static final DateFormat DATE_PARSER = new SimpleDateFormat(
			"yyyy-MM-dd-HH:mm");

	private static final String DATETAKEN = "dateTaken";
	private static final String CANDIDATE = "candidate";
	private static final String RECORDING = "recording";

	private String id;
	private Date dateTaken;
	private Candidate candidate;
	private Double averageScore;
	private double total;
	private int numRecordings;
	private List<Recording> recordings;

	public Assessment(String id) {
		this.id = id;
		candidate = null;
		recordings = new ArrayList<Recording>();
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (tempTag != null) {
			tempTag.startElement(uri, localName, qName, atts);
		} else if (RECORDING.equals(qName)) {
			tempTag = new Recording(atts.getValue(ID),
					atts.getValue(ACTIVITYID));
		} else if (CANDIDATE.equals(qName)) {
			tempTag = new Candidate(atts.getValue(ID));
		} else if (DATETAKEN.equals(qName)) {
			buffer = new StringBuffer();
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (tempTag != null) {
			tempTag.endElement(uri, localName, qName);
			if (RECORDING.equals(qName)) {
				Recording r = (Recording) tempTag;
				total += r.getFeedback().getScore();
				numRecordings++;
				recordings.add(r);
				tempTag = null;
			} else if (CANDIDATE.equals(qName)) {
				candidate = (Candidate) tempTag;
				tempTag = null;
			}
		} else if (DATETAKEN.equals(qName)) {
			try {
				if (buffer != null) {
					String date = buffer.toString().trim();
					buffer = null;
					dateTaken = DATE_PARSER.parse(date);
				}
			} catch (ParseException e) {
			}
		}
	}

	public String getId() {
		return id;
	}

	public Date getDateTaken() {
		return dateTaken;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public List<Recording> getRecordings() {
		return recordings;
	}

	@Override
	public String toString() {
		return "\tid=" + id + ", dateTaken=" + dateTaken + "\ncandidate="
				+ candidate + "\nrecordings=" + recordings;
	}

	public double getAverageScore() {
		if (averageScore == null) {
			return total / numRecordings;
		}
		return averageScore;
	}
	
	public void setAverageScore(double averageScore){
		this.averageScore = averageScore;
	}

}
