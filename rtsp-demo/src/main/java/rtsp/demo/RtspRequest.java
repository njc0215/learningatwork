package rtsp.demo;

public enum RtspRequest {
	SETUP("SETUP"), PLAY("PLAY"), PAUSE("PAUSE"), TEARDOWN("TEARDOWN"), UNKNOWN("UNKNOWN");

	private String desc;

	RtspRequest(String desc) {
		this.desc = desc;
	}

	public String toString() {
		return desc;
	}
}