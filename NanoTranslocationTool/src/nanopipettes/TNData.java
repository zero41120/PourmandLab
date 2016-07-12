package nanopipettes;

public class TNData {
	private Double time;
	private Double current;
	private Double voltage;
	

	protected TNData (Double current, Double voltage) {
		this.time = null;
		this.current = current;
		this.voltage = voltage;
	}
	
	protected TNData(Double time, Double current, Double voltage) {
		this.time = time;
		this.current = current;
		this.voltage = voltage;
	}
	Boolean isAbstractData(){
		return time.equals(null);
	}
	public double getTime() {
		return time;
	}
	public double getCurrent() {
		return current;
	}
	public double getVoltage() {
		return voltage;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public void setCurrent(double current) {
		this.current = current;
	}
	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}
}
