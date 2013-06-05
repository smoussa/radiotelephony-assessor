package comp2012.controller.parser;

public class UnexpectedTagException extends RuntimeException {

	private static final long serialVersionUID = -6661233043519153409L;
	private String localName;

	public UnexpectedTagException(String localName) {
		this.localName = localName;
	}

	public String getLocalName() {
		return localName;
	}
}
