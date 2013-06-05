package comp2012.tests.unit;

import java.net.MalformedURLException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import comp2012.controller.parser.ActivityParser;
import comp2012.controller.parser.AssessmentParser;
import comp2012.controller.parser.Parser;
import comp2012.controller.parser.ParsingException;

import static org.junit.Assert.*;

public class ParserTest {
	
	private Parser activity;
	private Parser assessment;
	
	@Before
	public void setUp()
	{
		try {
			activity = new ActivityParser();
		} catch (SQLException e) {
			fail();
		}
		assessment = new AssessmentParser();
	}
	
	// Parser must throw on broken file so GUI can handle logic
	@Test(expected=ParsingException.class)
	public void testParserThrowsOnBrokenFile() throws ParsingException
	{
		try {
			activity.loadFile("this string is soooo not a file!!!\n \r");
		} catch (SQLException e) {
			fail();
		}
	}
	
	@Test(expected=ParsingException.class)
	public void testLoadIncorrectXML1() throws ParsingException
	{
		try {
			assessment.loadFile("../docs/activities.xml");
		}
		catch (SQLException e)
		{
			fail();
		}
	}
	
	@Test(expected=ParsingException.class)
	public void testLoadIncorrectXML2() throws ParsingException
	{
		try {
			activity.loadFile("../docs/assessment.xml");
		}
		catch (SQLException e)
		{
			fail();
		}
	}

}
