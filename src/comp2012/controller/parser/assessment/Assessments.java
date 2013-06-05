package comp2012.controller.parser.assessment;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import comp2012.controller.parser.Parser;
import comp2012.controller.parser.XMLTagAbstr;

public class Assessments extends XMLTagAbstr implements Parser {

	private final static String ASSESSMENT = "assessmentTaken";


	protected StringBuffer buffer = null;

	List<Assessment> parsedObjects = new ArrayList<Assessment>();

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (tempTag != null) {
			tempTag.startElement(uri, localName, qName, atts);
		} else if (ASSESSMENT.equals(qName)) {
			tempTag = new Assessment(atts.getValue(ID));
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (tempTag != null) {
			tempTag.endElement(uri, localName, qName);
			if (ASSESSMENT.equals(qName)) {
				parsedObjects.add((Assessment) tempTag);
				tempTag = null;
				tempTag = null;
			}
		}
	}

	public List<Assessment> loadFile(String file) {
		return parsedObjects;
	}

}
