package nanopipettes;

public class TNPeakData {
	private double startTime;
	private double endTime;
	private double average;
		
	protected TNPeakData(double startTime, double endTime, double average) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.average = average;
	}
	public double getStartTime() {
		return startTime;
	}
	public double getEndTime() {
		return endTime;
	}
	public double getAverage() {
		return average;
	}
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}
	public void setAverage(double average) {
		this.average = average;
	}
}
