package comp2012.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {

	private final static DateFormat timeFormatter = new SimpleDateFormat(
			"HH:mm");
	private final static DateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static String formatTime(Date date) {
		return timeFormatter.format(date);
	}

	public static String formatDate(Date date) {
		return dateFormatter.format(date);
	}
}
