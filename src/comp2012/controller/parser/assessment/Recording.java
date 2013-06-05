package comp2012.controller.parser.assessment;

import java.sql.SQLException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import comp2012.controller.marker.Feedback;
import comp2012.controller.marker.Marker;
import comp2012.controller.parser.XMLTagAbstr;
import comp2012.model.db.DBAccess;

public class Recording extends XMLTagAbstr {

	private String id, activityId;
	private String transcript;
	private String expectedResponse;
	private Feedback feedback;
	private static Marker marker = new Marker();

	public Recording(String id, String activityId) {
		this.id = id;
		this.activityId = activityId;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		buffer = new StringBuffer();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (buffer != null) {
			String transcript = buffer.toString().trim();
			this.transcript = transcript;
			try {
				markRecording();
			} catch (SQLException e) {
				throw new NoSuchActivityException(activityId);
			}
		}
	}

	private void markRecording() throws SQLException {
		expectedResponse = "";
		expectedResponse = DBAccess.getSingleton().getActivity(activityId);
		feedback = marker.markRecording(transcript, expectedResponse);
	}

	@Override
	public String toString() {
		return "\t\tid=" + id + "\n\t\tactivityId=" + activityId
				+ "\n\t\ttranscript=" + transcript;
	}

	public Feedback getFeedback() {
		return feedback;
	}

	public String getId() {
		return id;
	}

	public String getActivityId() {
		return activityId;
	}

	public String getTranscript() {
		return transcript;
	}

	public String getExpectedResponse() {
		return expectedResponse;
	}

	public void setFeedback(Feedback feedback) {
		this.feedback = feedback;
	}

	public void setExpectedResponse(String expectedResponse) {
		this.expectedResponse = expectedResponse;
	}
}
