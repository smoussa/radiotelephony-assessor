package comp2012.controller.parser.activity;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import comp2012.controller.parser.Parser;
import comp2012.controller.parser.XMLTagAbstr;

public class Activities extends XMLTagAbstr implements Parser {

	private final static String ACTIVITY = "activity";

	private List<Activity> parsedObjects = new ArrayList<Activity>();

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (tempTag != null) {
			tempTag.startElement(uri, localName, qName, atts);
		} else if (ACTIVITY.equals(qName)) {
			tempTag = new Activity(atts.getValue(ID));
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (tempTag != null) {
			tempTag.endElement(uri, localName, qName);
			if (ACTIVITY.equals(qName)) {
				parsedObjects.add((Activity) tempTag);
				tempTag = null;
			}
		}
	}

	public List<Activity> loadFile(String file) {
		return parsedObjects;
	}

}
