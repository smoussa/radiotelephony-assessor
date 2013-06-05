package comp2012.controller.parser;

public abstract class XMLTagAbstr implements XMLTag {

	protected StringBuffer buffer = null;
	protected XMLTag tempTag = null;

	public void characters(char[] ch, int start, int length) {
		if (tempTag == null) {
			if (buffer != null) {
				buffer.append(ch, start, length);
			}
		} else {
			tempTag.characters(ch, start, length);
		}
	}

}
