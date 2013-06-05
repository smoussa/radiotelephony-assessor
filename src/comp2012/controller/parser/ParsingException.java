package comp2012.controller.parser;

@SuppressWarnings("serial")
public class ParsingException extends Exception {
	private Exception cause;

	public ParsingException(Exception e) {
		cause = e;
	}

	public Exception getCause() {
		return cause;
	}
}
