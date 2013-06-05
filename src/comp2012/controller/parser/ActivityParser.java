package comp2012.controller.parser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import comp2012.controller.parser.activity.Activities;
import comp2012.controller.parser.activity.Activity;
import comp2012.model.db.DBAccess;

public class ActivityParser extends DefaultHandler implements Parser {

	private final static String ACTIVITY = "activities";
	private final static String ASSESSMENT = "assessment";

	private Schema schema;

	private Activities tempTag = null;

	protected StringBuffer buffer = null;

	private DBAccess database;

	public ActivityParser() throws SQLException {
		new Thread() {
			public void run() {
				database = DBAccess.getSingleton();
			}
		}.start();
		try {
			SchemaFactory sf = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = sf.newSchema(new Source[] { new StreamSource(
					"SEGSchema.xsd") });
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private List<Activity> parsedObjects;

	public void startDocument() {
		parsedObjects = new ArrayList<Activity>();
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (tempTag != null) {
			tempTag.startElement(uri, localName, qName, atts);
		} else if (ACTIVITY.equals(qName)) {
			tempTag = new Activities();
		} else if (ASSESSMENT.equals(qName)) {
			throw new UnexpectedTagException(localName);
		}
	}

	public void characters(char[] ch, int start, int length) {
		if (tempTag != null) {
			tempTag.characters(ch, start, length);
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (tempTag != null) {
			tempTag.endElement(uri, localName, qName);
			if (ACTIVITY.equals(qName) || ASSESSMENT.equals(qName)) {
				parsedObjects = tempTag.loadFile("");
				tempTag = null;
			}
		}
	}

	public List<Activity> loadFile(String file) throws SQLException,
			ParsingException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		spf.setNamespaceAware(true);
		spf.setSchema(schema);
		SAXParser saxParser;
		try {
			saxParser = spf.newSAXParser();
			saxParser.parse(file, this);
		} catch (Exception e) {
			throw new ParsingException(e);
		}
		for (Activity a : parsedObjects) {
			database.addActivity(a);
		}
		return parsedObjects;
	}

}
