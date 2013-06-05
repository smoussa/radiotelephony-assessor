package comp2012.model.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp2012.controller.marker.Feedback;
import comp2012.controller.marker.Marker;
import comp2012.controller.parser.activity.Activity;
import comp2012.controller.parser.assessment.Assessment;
import comp2012.controller.parser.assessment.Candidate;
import comp2012.controller.parser.assessment.Recording;
import comp2012.util.Utilities;
import comp2012.view.AssessmentsPanel;

public class DBAccess {

	private static DBAccess singleton;
	private static List<Filter> filters = new ArrayList<Filter>();

	public DBAccess() throws SQLException {
		createTables();
	}

	/**
	 * Should be run on start up to initialise sqlite JDBC and to create all the
	 * required tables in the database.
	 * 
	 * @throws SQLException
	 */
	public void createTables() throws SQLException {
		// load the sqlite-JDBC driver using the current class loader
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
		}
		Connection connection = null;
		try {
			connection = getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS candidate ("
					+ "id TEXT PRIMARY KEY," //
					+ "forename TEXT," //
					+ "surname TEXT," //
					+ "company TEXT," //
					+ "airport TEXT)");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS assessment ("
					+ "id TEXT PRIMARY KEY," //
					+ "date NUMERIC," //
					+ "candidateId TEXT," //
					+ "averageScore REAL," //
					+ "FOREIGN KEY(candidateId) REFERENCES candidate(id))");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS activity ("
					+ "id TEXT PRIMARY KEY," //
					+ "question TEXT," //
					+ "answer TEXT)");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS recording ("
					+ "id TEXT PRIMARY KEY," //
					+ "assessId TEXT," //
					+ "transcript TEXT," //
					+ "feedback TEXT," //
					+ "activityId TEXT," //
					+ "FOREIGN KEY(activityId) REFERENCES activity(id))");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS results ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT," //
					+ "recordingId TEXT," //
					+ "score REAL," //
					+ "FOREIGN KEY(recordingId) REFERENCES recording(id))");
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Get average mark for a specific company or airport, for a specific month
	 * of a specific year.
	 * 
	 * @param type
	 *            - should be "company" or "airport"
	 * @param selection
	 *            - should be the company or airport
	 */
	public static double getMarksCompany(String type, String selection,
			int year, int month) throws SQLException {
		Connection connection = null;
		List<Double> marks = new ArrayList<Double>();
		try {
			connection = getSingleton().getConnection();
			Statement statement = connection.createStatement();
			Calendar start = new GregorianCalendar(year, month, 1, 0, 0);
			int day = 30;
			switch (month) {
			case 2:// Feb
				day = 28;
				break;
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				day = 31;
			}
			Calendar end = new GregorianCalendar(year, month, day, 23, 59);
			ResultSet rs = statement
					.executeQuery("SELECT assessment.averageScore "
							+ "FROM assessment, candidate "
							+ "WHERE assessment.candidateId=candidate.id AND candidate."
							+ type.toLowerCase() + "=\"" + selection
							+ "\" AND date>=" + start.getTime().getTime()
							+ " AND date<=" + end.getTime().getTime());
			while (rs.next()) {
				marks.add(Double.parseDouble(rs.getString(1)));
			}

		} finally {
			getSingleton().closeConnection(connection);
		}

		return getAverage(marks);
	}

	/**
	 * Get average mark for a specific company or airport, for a specific year.
	 * 
	 * @param type
	 *            - should be "company" or "airport"
	 * @param selection
	 *            - should be the company or airport
	 */
	public static double getMarksCompany(String type, String selection, int year)
			throws SQLException {
		Connection connection = null;
		List<Double> marks = new ArrayList<Double>();
		try {
			connection = getSingleton().getConnection();
			Statement statement = connection.createStatement();
			Calendar start = new GregorianCalendar(year, 0, 1, 0, 0);
			Calendar end = new GregorianCalendar(year, 11, 31, 23, 59);
			ResultSet rs = statement
					.executeQuery("SELECT assessment.averageScore "
							+ "FROM assessment, candidate "
							+ "WHERE assessment.candidateId=candidate.id AND candidate."
							+ type.toLowerCase() + "=\"" + selection
							+ "\" AND date>=" + start.getTime().getTime()
							+ " AND date<=" + end.getTime().getTime());
			while (rs.next()) {
				marks.add(Double.parseDouble(rs.getString(1)));
			}

		} finally {
			getSingleton().closeConnection(connection);
		}

		return getAverage(marks);
	}

	private static double getAverage(List<Double> list) {
		if (list.size() == 0) {
			return 0;
		}
		double mark = 0;
		for (Double d : list) {
			mark += d;
		}
		return mark / list.size();
	}

	/**
	 * Returns the expected response
	 * 
	 * @throws SQLException
	 */
	public String getActivity(String id) throws SQLException {
		String response = null;
		Connection connection = null;
		try {
			connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement
					.executeQuery("SELECT answer FROM activity WHERE id=\""
							+ id + "\"");
			response = rs.getString("answer");
		} finally {
			closeConnection(connection);
		}
		return response;
	}

	/**
	 * Add an activity to the database
	 * 
	 * @throws SQLException
	 */
	public void addActivity(Activity a) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT OR REPLACE INTO activity VALUES(\"" //
					+ a.getId() + "\",\"" //
					+ a.getQuestion() + "\",\""//
					+ a.getAnswer() + "\")");
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * @throws SQLException
	 * 
	 */
	public void addAssessment(Assessment a) throws SQLException {
		Candidate c = a.getCandidate();
		Connection connection = null;
		try {
			connection = getConnection();
			Statement statement = connection.createStatement();
			statement
					.executeUpdate("INSERT OR REPLACE INTO candidate VALUES(\""//
							+ c.getId() + "\",\""//
							+ c.getForename() + "\",\"" //
							+ c.getSurname() + "\",\""//
							+ c.getCompany() + "\",\""//
							+ c.getAirport() + "\")");
			statement
					.executeUpdate("INSERT OR REPLACE INTO assessment VALUES(\""//
							+ a.getId() + "\","//
							+ getDate(a.getDateTaken()).getTime() + ",\"" //
							+ c.getId() + "\"," + a.getAverageScore() + ")");
			for (Recording r : a.getRecordings()) {
				StringBuilder recordingsStatement = new StringBuilder();
				StringBuilder feedbackStatement = new StringBuilder();
				recordingsStatement
						.append("INSERT OR REPLACE INTO recording VALUES (");
				recordingsStatement.append("\"");
				recordingsStatement.append(r.getId());
				recordingsStatement.append("\",\"");
				recordingsStatement.append(a.getId());
				recordingsStatement.append("\",\"");
				recordingsStatement.append(r.getTranscript());
				recordingsStatement.append("\",\"");
				recordingsStatement.append(r.getFeedback().getFeedback());
				recordingsStatement.append("\",\"");
				recordingsStatement.append(r.getActivityId());
				recordingsStatement.append("\")");
				Feedback f = r.getFeedback();
				feedbackStatement
						.append("INSERT OR REPLACE INTO results (recordingId, score) VALUES (");
				feedbackStatement.append("\"");
				feedbackStatement.append(r.getId());
				feedbackStatement.append("\",");
				feedbackStatement.append(f.getScore());
				feedbackStatement.append(")");
				statement.executeUpdate(recordingsStatement.toString());
				statement.executeUpdate(feedbackStatement.toString());
			}
		} finally {
			closeConnection(connection);
		}
	}

	public Object[][] loadAll() throws SQLException {
		Connection connection = null;
		List<Object[]> list = new ArrayList<Object[]>();
		try {
			connection = getConnection();
			Statement statement = connection.createStatement();
			String filterString = "";
			for (Filter f : filters) {
				filterString += " AND " + f.getWhere();
			}
			ResultSet rs = statement.executeQuery(//
					"SELECT "
							+ "candidate.id,assessment.id,assessment.date,"
							+ "candidate.forename,candidate.surname,candidate.company,"
							+ "candidate.airport,assessment.averageScore "
							+ "FROM candidate, assessment "
							+ "WHERE candidate.id = assessment.candidateId"
							+ filterString);
			connection.commit();
			while (rs.next()) {
				Object[] record = new Object[9];
				record[0] = rs.getObject(1);
				record[1] = rs.getObject(2);
				java.util.Date d = new java.util.Date(rs.getDate(3).getTime());
				record[2] = Utilities.formatDate(d);
				record[3] = Utilities.formatTime(d);
				record[4] = rs.getObject(4);
				record[5] = rs.getObject(5);
				record[6] = rs.getObject(6);
				record[7] = rs.getObject(7);
				record[8] = ((int) (rs.getDouble(8) * 10d)) / 10d;
				list.add(record);
			}
		} finally {
			closeConnection(connection);
		}
		return list.toArray(new Object[list.size()][]);
	}

	public static String[] getCompaniesList() throws SQLException {
		Connection connection = null;
		String[] companies = new String[0];
		try {
			connection = getSingleton().getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement
					.executeQuery("SELECT DISTINCT company FROM candidate");
			List<String> compList = new ArrayList<String>();
			while (rs.next()) {
				compList.add(rs.getString(1));
			}
			companies = compList.toArray(companies);
		} finally {
			getSingleton().closeConnection(connection);
		}
		return companies;
	}

	public static String[] getAirportsList() throws SQLException {
		Connection connection = null;
		String[] airports = new String[0];
		try {
			connection = getSingleton().getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement
					.executeQuery("SELECT DISTINCT airport FROM candidate");
			List<String> airportList = new ArrayList<String>();
			while (rs.next()) {
				airportList.add(rs.getString(1));
			}
			airports = airportList.toArray(airports);
		} finally {
			getSingleton().closeConnection(connection);
		}
		return airports;
	}

	public List<Assessment> getAssessments(String candidateID) {
		Connection connection = null;
		List<Assessment> assessments = new ArrayList<Assessment>();
		try {
			connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement
					.executeQuery("SELECT recording.assessId, recording.feedback, "
							+ "activity.answer, recording.activityId, "
							+ "results.recordingID, results.score, assessment.averageScore "
							+ "FROM assessment, activity, recording, results "//
							+ "WHERE assessment.candidateId = \""
							+ candidateID
							+ "\" "//
							+ "AND assessment.id=recording.assessId "//
							+ "AND recording.activityId=activity.id "//
							+ "AND recording.id=results.recordingId");
			Map<String, Assessment> map = new HashMap<String, Assessment>();
			while (rs.next()) {
				String id = rs.getString(1);
				if (!map.containsKey(id)) {
					map.put(id, new Assessment(id));
				}
				Assessment a = map.get(id);
				String rId = rs.getString(5);
				String aId = rs.getString(4);
				Recording r = new Recording(rId, aId);
				a.getRecordings().add(r);
				r.setExpectedResponse(rs.getString(3));
				Feedback f = new Feedback(rs.getFloat(6));
				r.setFeedback(f);
				f.setFeedback(rs.getString(2));
				a.setAverageScore(rs.getDouble(7));
			}
			assessments.addAll(map.values());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return assessments;
	}

	public static String getXML() {
		Connection connection = null;
		try {
			connection = getSingleton().getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT id FROM candidate");
			List<String> candidateIds = new ArrayList<String>();
			while (rs.next()) {
				candidateIds.add(rs.getString(1));
			}
			return getXML(candidateIds);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			getSingleton().closeConnection(connection);
		}
		return "";

	}

	public static String getXML(List<String> candidateIds) {
		StringBuilder b = new StringBuilder();
		b.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		b.append("<results>\n");
		for (String cid : candidateIds) {
			b.append(getCandidateXML(cid));
		}
		b.append("</results>");
		return b.toString();
	}

	private static String getCandidateXML(String id) {
		StringBuilder b = new StringBuilder();
		Connection connection = null;
		try {
			connection = getSingleton().getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement
					.executeQuery("SELECT * FROM candidate WHERE id = \"" + id
							+ "\"");
			b.append("\t<candidate id=\"" + id + "\">\n");
			b.append("\t\t<forname>" + rs.getString(2) + "</forname>\n");
			b.append("\t\t<surname>" + rs.getString(3) + "</surname>\n");
			b.append("\t\t<company>" + rs.getString(4) + "</company>\n");
			b.append("\t\t<airport>" + rs.getString(5) + "</airport>\n");
			rs = statement
					.executeQuery("SELECT id FROM assessment WHERE candidateId = \""
							+ id + "\"");
			List<String> assessmentIds = new ArrayList<String>();
			while (rs.next()) {
				assessmentIds.add(rs.getString(1));
			}
			b.append("\t\t<assessments>\n");
			for (String assessId : assessmentIds) {
				b.append("\t\t\t<assessmentTaken id=\"" + assessId + "\">\n");
				rs = statement
						.executeQuery("SELECT * FROM assessment WHERE id = \""
								+ assessId + "\"");
				b.append("\t\t\t\t<dateTaken>"
						+ Utilities.formatDate(rs.getDate(2))
						+ "</dateTaken>\n");
				double mark = rs.getDouble(4);
				b.append("\t\t\t\t<mark>" + mark + "</mark>\n");
				b.append("\t\t\t\t<passed>"
						+ (mark >= Marker.getPassMark() ? "true" : "false")
						+ "</passed>\n");
				rs = statement
						.executeQuery("SELECT recording.id, transcript, feedback, activityId, score"//
								+ " FROM recording, results"//
								+ " WHERE recording.id = recordingId"//
								+ " AND assessId = \"" + assessId + "\"");
				b.append("\t\t\t\t<recordings>\n");
				while (rs.next()) {
					b.append("\t\t\t\t\t<recording id=\"" + rs.getString(1)
							+ "\" activityID=\"" + rs.getString(4) + "\">\n");
					b.append("\t\t\t\t\t\t<transcript>" + rs.getString(2)
							+ "</transcript>\n");
					// TODO store feedback to output
					b.append("\t\t\t\t\t\t<feedback>" + rs.getString(3)
							+ "</feedback>\n");
					b.append("\t\t\t\t\t\t<mark>" + rs.getDouble(5)
							+ "</mark>\n");
					b.append("\t\t\t\t\t</recording>\n");
				}
				b.append("\t\t\t\t</recordings>\n");
				b.append("\t\t\t</assessmentTaken>\n");
			}
			b.append("\t\t</assessments>\n");
			b.append("\t</candidate>\n");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			getSingleton().closeConnection(connection);
		}
		return b.toString();
	}

	public static void updateMark(Object assessId, Object newMark) {
		Connection connection = null;
		try {
			connection = getSingleton().getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate("UPDATE assessment SET averageScore = \""
					+ newMark.toString() + "\" WHERE id = \""
					+ assessId.toString() + "\"");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			getSingleton().closeConnection(connection);
		}
	}

	private static Date getDate(java.util.Date date) {
		return new Date(date.getTime());
	}

	private Connection getConnection() throws SQLException {
		Connection connection = DriverManager
				.getConnection("jdbc:sqlite:database.db");
		connection.setAutoCommit(false);
		return connection;
	}

	private void closeConnection(Connection connection) {
		try {
			if (connection != null)
				connection.commit();
			connection.close();
		} catch (SQLException e) {
			// connection close failed.
			System.err.println(e);
		}
	}

	public static DBAccess getSingleton() {
		if (singleton == null) {
			try {
				singleton = new DBAccess();
			} catch (SQLException e) {
			}
		}
		return singleton;
	}

	public static void addFilter(Filter f) {
		filters.add(f);
		try {
			AssessmentsPanel.setRecords(getSingleton().loadAll());
		} catch (SQLException e) {
		}
	}

	public static void removeFilter(Filter f) {
		filters.remove(f);
		try {
			AssessmentsPanel.setRecords(getSingleton().loadAll());
		} catch (SQLException e) {
		}
	}
}
