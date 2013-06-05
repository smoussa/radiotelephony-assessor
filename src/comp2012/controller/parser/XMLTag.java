package comp2012.controller.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface XMLTag {

	static final String ID = "id";
	static final String ACTIVITYID = "activityID";
	
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException;

	public void characters(char[] ch, int start, int length);

	public void endElement(String uri, String localName, String qName)
			throws SAXException;
}
