package nanopipettes;

public class TNData {
	private Double time;
	private Double current;
	private Double voltage;

	protected TNData(Double current, Double voltage) {
		this.time = null;
		this.current = current;
		this.voltage = voltage;
	}

	protected TNData(Double time, Double current, Double voltage) {
		this.time = time;
		this.current = current;
		this.voltage = voltage;
	}

	protected TNData(Double time, String current, String voltage) {
		this.time = time;
		this.current = Double.parseDouble(current);
		this.voltage = Double.parseDouble(voltage);
	}

	Boolean isAbstractData() {
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

	@Override
	public String toString() {
		return "Time:" + time + "\tpA:" + current + "\tmV:" + voltage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((current == null) ? 0 : current.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((voltage == null) ? 0 : voltage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TNData other = (TNData) obj;
		if (current == null) {
			if (other.current != null)
				return false;
		} else if (!current.equals(other.current))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (voltage == null) {
			if (other.voltage != null)
				return false;
		} else if (!voltage.equals(other.voltage))
			return false;
		return true;
	}


	public String getSqlValue(Boolean terminate) {
		String sql = "(" + time + ", " + current + ", " + voltage + ")";
		return terminate? sql + ";" : sql;
	}

}
