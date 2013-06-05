package comp2012.controller.parser;

import java.sql.SQLException;
import java.util.List;

public interface Parser {

	List<? extends XMLTag> loadFile(String file) throws SQLException,
			ParsingException;

}
