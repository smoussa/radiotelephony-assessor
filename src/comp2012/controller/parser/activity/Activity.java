package comp2012.controller.parser.activity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import comp2012.controller.parser.XMLTagAbstr;

public class Activity extends XMLTagAbstr {

	private static final String QUESTION = "questionText";
	private static final String ANSWER = "expectedResponse";

	private String id;

	private String question, answer;

	public Activity(String id) {
		this.id = id;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		buffer = new StringBuffer();
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (buffer != null) {
			String text = buffer.toString().trim();
			if(QUESTION.equals(qName)){
				question = text;
			}else if(ANSWER.equals(qName)){
				answer = text;
			}
		}
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}
	
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Activity [id=" + id + ", question=" + question + ", answer="
				+ answer + "]";
	}

}
