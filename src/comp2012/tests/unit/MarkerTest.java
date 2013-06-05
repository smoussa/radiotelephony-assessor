package comp2012.tests.unit;

import static org.junit.Assert.assertTrue;
import org.junit.*;

import comp2012.controller.marker.*;

public class MarkerTest {
	
	private Marker m;
	
	@Before
	public void setUp()
	{
		m = new Marker();
	}
	
	@Test
	public void testPerfectScore()
	{
		// Test some basic properties of the marker
		Feedback f = m.markRecording("The quick brown fox jumped over the lazy dog", "The quick brown fox jumped over the lazy dog");
		
		assertTrue("The same response and expected response must yield 100%",f.getScore() == 100);
	}
	
	@Test
	public void testEmptyResponseText()
	{
		// Test some basic properties of the marker
		Feedback f = m.markRecording("", "The quick brown fox jumped over the lazy dog");
		assertTrue("No response should return 0 mark",f.getScore() == 0);
	}
	
	@Test
	public void testMissingWordsLowersScore()
	{
		// Test that missing (important) words strictly lowers score
		Feedback f = m.markRecording("Leader fower Alpha North between Link wun tree and Juliet, leaving Alphas six and seven available",
				"Leader fower Alpha North between Link wun tree and Juliet approved, leaving Alphas six and seven available");
		assertTrue("Omitting (important) words should strictly reduce score",f.getScore() < 100);
	}
	
	@Test
	public void testMixingWordsLowersScore()
	{
		// Test that missing (important) words strictly lowers score
		Feedback f = m.markRecording("Leader fower Alpha North between Link wun tree and approved Juliet, leaving Alphas six and seven available",
				"Leader fower Alpha North between Link wun tree and Juliet approved, leaving Alphas six and seven available");
		assertTrue("Mixing words should strictly reduce score",f.getScore() < 100);
	}

	@Test
	public void testEquivalentPronunciationSameScore()
	{
		Feedback f = m.markRecording("Leader four Alfa North between Link one tree and Juliet approved, leaving Alfas six and seven available",
				"Leader fower Alpha North between Link wun tree and Juliet approved, leaving Alphas six and seven available");
		assertTrue("Marker should account for equivalent pronunciations",f.getScore() == 100);
	}
	
	@Test
	public void testEquivalentPronunciationSameScoreHard()
	{
		Feedback f = m.markRecording("After the outbound American trip all seven from Romeo, proceed to stand report taxiway vacated. Leader fower",
				"After the outbound American Triple seven from Romeo, proceed to stand report taxiway vacated. Leader fower");
		assertTrue("Marker should account for equivalent pronunciations even when words are split (difficult)",f.getScore() == 100);
	}
	
}
