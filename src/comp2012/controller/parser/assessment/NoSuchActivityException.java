package comp2012.controller.parser.assessment;

public class NoSuchActivityException extends RuntimeException {

	private static final long serialVersionUID = -4680001902550788781L;

	private String activityId;

	public NoSuchActivityException(String activityID) {
		this.activityId = activityID;
	}

	public String getAcivityId() {
		return activityId;
	}
}
