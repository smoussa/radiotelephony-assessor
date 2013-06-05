package comp2012.controller.marker;

public class Feedback {
	
	public static final float PASS_MARK = 90.0f;

	private float score;
	private String feedback;

	public Feedback(float score) {
		this.score = score;
	}

	public void averageScore(Feedback feedback) {
		this.score += feedback.score;
		this.score /= 2;
	}

	public void averageScore(float mark) {
		this.score += mark;
		this.score /= 2;
	}
	
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public Float getScore() {
		return score;
	}
	
	public String getFeedback() {
		return feedback;
	}
	
	@Override
	public String toString() {
		return "Score: " + score;
	}
}
