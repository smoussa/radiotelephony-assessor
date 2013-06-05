package comp2012.controller.marker;

import org.apache.commons.codec.language.DoubleMetaphone;

public class Marker {

	private static final String WRONG_START = "<b style='color:#FFFFFF;" +
			" background-color:#C9101A;'>";
	private static final String WRONG_END = "</b>";
	private static final String MISSING_START = "<b style='color:#FFFFFF;" +
			" background-color:#999999;'>";
	private static final String MISSING_END = "</b>";
	private static final String CORRECT_START = "";
	private static final String CORRECT_END = "";

	private final DoubleMetaphone encoder = new DoubleMetaphone();

	public static void main(String[] args) {
		Marker marker = new Marker();
		Feedback f = marker.markRecording(
				"one two tree six seven five trip all pie",
				"one two three six four five triple pie");
		System.out.println(f.getFeedback());
		System.out.println(f.getScore());
	}

	/**
	 * Mark the given response against the given expectedResponse, and storing
	 * the results in the feedback object that is then returned.
	 * 
	 * @param response
	 * @param expectedResponse
	 * @return
	 */
	public Feedback markRecording(String response, String expectedResponse) {
		Feedback feedback = null;

		if (expectedResponse.length() == 0) {
			feedback = new Feedback(0);
			feedback.setFeedback(WRONG_START + response + WRONG_END);
		} else if (response.length() == 0) {
			feedback = new Feedback(0);
			feedback.setFeedback(MISSING_START + response + MISSING_END);
		} else if (response.equals(expectedResponse)) {
			feedback = new Feedback(100);
			feedback.setFeedback(response);
		} else {
			// Convert sentences into arrays for easier use
			final String[] rArray = response.split("\\s+");
			final String[] eArray = expectedResponse.split("\\s+");

			// Encode sentences
			final String[][] reArray = encodeArray(rArray);
			final String[][] eeArray = encodeArray(eArray);

			// Store array lengths
			final int rLen = rArray.length;
			final int eLen = eArray.length;

			// Work out maximum look ahead for word matching
			final int maxWords = (rLen / 3 > 5) ? rLen / 3 : 5;

			// These arrays will hold the indexes of the corresponding word it
			// was matched to in the other array
			final int[] rIndexes = new int[rLen];
			final int[] eIndexes = new int[eLen];

			// Intialise to -1
			for (int i = 0; i < rLen; i++)
				rIndexes[i] = -1;
			for (int i = 0; i < eLen; i++)
				eIndexes[i] = -1;

			int rPtr = -1;

			// Match words
			for (int ePtr = 0; ePtr < eLen; ePtr++) {
				for (int r = rPtr + 1; r < rPtr + maxWords && r < rLen; r++) {
					if (compare(eeArray[ePtr], reArray[r])) {
						rPtr = r;
						rIndexes[rPtr] = ePtr;
						eIndexes[ePtr] = rPtr;

						break;
					}
				}
			}

			// Attempt to concatenate words and match
			for (rPtr = 0; rPtr < rLen; rPtr++) {
				if (rIndexes[rPtr] == -1) {
					int end = rPtr;
					for (int r = rPtr + 1; r < rLen; r++) {
						if (rIndexes[r] == -1) {
							end = r;
						} else {
							break;
						}
					}

					int sIndex = (rPtr == 0) ? 0 : rIndexes[rPtr - 1];
					if (sIndex == -1) {
						int i = 0;
						while (rPtr - i >= 0 && sIndex == -1) {
							sIndex = (rPtr - i == 0) ? 0 : rIndexes[rPtr - i];
							i++;
						}
					}
					final int eIndex = (end + 1 >= rLen) ? eLen - 1
							: rIndexes[end + 1];

					if (end != rPtr && sIndex != eIndex) {
						String word = "";
						for (int r = rPtr; r <= end; r++) {
							word += rArray[r];
						}
						String[] ew = encodeWord(word);

						for (int e = sIndex; e <= eIndex; e++) {
							if (compare(ew, eeArray[e])) {
								eIndexes[e] = rPtr;
								for (int r = rPtr; r <= end; r++)
									rIndexes[r] = e;
								rPtr = end;
								break;
							}
						}
					}
				}
			}

			// Work out number of valid words
			float vcount = 0;
			float max = (rLen > eLen) ? rLen : eLen;
			for (int i = 0; i < rLen; i++) {
				if (rIndexes[i] != -1) {
					vcount++;
				}
			}

			for (int i = 0; i < rLen; i++) {
				if (rIndexes[i] == -1)
					rArray[i] = WRONG_START + rArray[i] + WRONG_END;
				else
					rArray[i] = CORRECT_START + rArray[i] + CORRECT_END;
			}

			rPtr = 0;
			for (int i = 0; i < eLen; i++) {
				if (eIndexes[i] == -1) {
					rArray[rPtr] += " " + MISSING_START + eArray[i] + MISSING_END + " ";
				} else {
					rPtr = eIndexes[i];
				}
			}

			// Create feedback
			String marked = arrayToString(rArray);
			feedback = new Feedback((vcount / max) * 100);
			feedback.setFeedback(marked);
		}

		return feedback;
	}

	private boolean compare(String[] encoded1, String[] encoded2) {
		return encoded1[0].equals(encoded2[0])
				|| encoded1[0].equals(encoded2[1])
				|| encoded1[1].equals(encoded2[0])
				|| encoded1[1].equals(encoded2[1]);
	}

	private String arrayToString(String[] words) {
		String s = "";

		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > 0) {
				s += words[i] + " ";
			}
		}
		// s+= "\n";

		return s;
	}

	/**
	 * Returns a new array of the Double Metaphone encoded values of the input
	 * array, using the normal encoding and the alternate encoding.
	 * 
	 * @param words
	 * @return
	 */
	private String[][] encodeArray(String[] words) {
		final String[][] encodedWords = new String[words.length][2];

		for (int i = 0; i < words.length; i++) {
			encodedWords[i] = encodeWord(words[i]);
		}

		return encodedWords;
	}

	private String[] encodeWord(String word) {
		return new String[] { encoder.doubleMetaphone(word, false),
				encoder.doubleMetaphone(word, true) };
	}

	public static double getPassMark() {
		return Feedback.PASS_MARK;
	}

}
