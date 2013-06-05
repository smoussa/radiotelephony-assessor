package comp2012.controller.parser.assessment;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import comp2012.controller.parser.XMLTagAbstr;

public class Candidate extends XMLTagAbstr {

	private static final String FORENAME = "forename";
	private static final String SURNAME = "surname";
	private static final String COMPANY = "company";
	private static final String AIRPORT = "airport";

	private String id, forename, surname, company, airport;

	public Candidate(String id) {
		this.id = id;
		company = "BAA";
		airport = "LHR";
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
			String text = buffer.toString().trim();
			if (FORENAME.equals(qName)) {
				forename = text;
			} else if (SURNAME.equals(qName)) {
				surname = text;
			} else if (COMPANY.equals(qName)) {
				company = text;
			} else if (AIRPORT.equals(qName)) {
				airport = text;
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getForename() {
		return forename;
	}

	public String getSurname() {
		return surname;
	}

	public String getCompany() {
		return company;
	}

	public String getAirport() {
		return airport;
	}

	@Override
	public String toString() {
		return "\t\tforename=" + forename + ", surname=" + surname
				+ ", company=" + company + ", airport=" + airport + ", id="
				+ id;
	}

}
