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

import comp2012.controller.parser.assessment.Assessment;
import comp2012.controller.parser.assessment.Assessments;
import comp2012.model.db.DBAccess;
import comp2012.view.AssessmentsPanel;

public class AssessmentParser extends DefaultHandler implements Parser {

	private final static String ACTIVITY = "activities";
	private final static String ASSESSMENT = "assessment";

	private Schema schema;

	private Assessments tempTag = null;

	protected StringBuffer buffer = null;

	private DBAccess database;

	private List<Assessment> parsedObjects;

	public AssessmentParser() {
		new Thread() {
			public void run() {
				try {
					database = DBAccess.getSingleton();
					Object[][] objects = database.loadAll();
					AssessmentsPanel.setRecords(objects);
				} catch (SQLException e) {
					e.printStackTrace();
				}
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

	public void startDocument() {
		tempTag = null;
		parsedObjects = new ArrayList<Assessment>();
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (tempTag != null) {
			tempTag.startElement(uri, localName, qName, atts);
		} else if (ACTIVITY.equals(qName)) {
			throw new UnexpectedTagException(localName);
		} else if (ASSESSMENT.equals(qName)) {
			tempTag = new Assessments();
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

	public List<Assessment> loadFile(String file) throws ParsingException,
			SQLException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		spf.setNamespaceAware(true);
		spf.setSchema(schema);
		try {
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(file, this);
		} catch (Exception e) {
			throw new ParsingException(e);
		}
		for (Assessment a : parsedObjects) {
			database.addAssessment(a);
		}
		Object[][] objects = database.loadAll();
		AssessmentsPanel.setRecords(objects);
		return parsedObjects;
	}

}
