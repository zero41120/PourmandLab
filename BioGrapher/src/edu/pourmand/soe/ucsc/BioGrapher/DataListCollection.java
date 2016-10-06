package edu.pourmand.soe.ucsc.BioGrapher;

import java.util.List;

public class DataListCollection {
	private String fileTitle;
	private String filePath;
	private Double concentration;
	private Integer type;
	private List<? extends AutoDataType> listAutoType;

	public String getFileTitle() {
		return fileTitle;
	}

	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}
	
	public void setListAuto(List<? extends AutoDataType> toSave, Integer whichType){
		type = whichType;
		listAutoType = toSave;
	}

	public List<? extends AutoDataType> getListAuto() {
		return listAutoType;
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getConcentration() {
		return concentration;
	}

	public void setConcentration(Double concentration) {
		this.concentration = concentration;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}

abstract class AutoDataType { }

class DataType_1 extends AutoDataType {
	private double voltage;
	private double currnet;

	protected DataType_1(String vol, String cur) {
		super();
		this.voltage = Double.parseDouble(vol);
		this.currnet = Double.parseDouble(cur);
		;
	}

	protected DataType_1(Double vol, Double cur) {
		super();
		this.voltage = vol;
		this.currnet = cur;
	}

	protected double getVoltage() {
		return voltage;
	}

	protected void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	protected double getCurrnet() {
		return currnet;
	}

	protected void setCurrnet(double currnet) {
		this.currnet = currnet;
	}
}

class DataType_2 extends AutoDataType {

	private double time;
	private double averageVol;

	protected DataType_2(double time, double trace_1_mV, double trace_1_pA, double trace_2_mV, double trace_2_pA,
			double trace_3_mV, double trace_3_pA) {
		this.time = time;
		this.averageVol = (trace_1_mV + trace_2_mV + trace_3_mV) / 3;
	}

	protected DataType_2(String time, String trace_1_mV, String trace_1_pA, String trace_2_mV, String trace_2_pA,
			String trace_3_mV, String trace_3_pA) {
		this.time = Double.parseDouble(time);
		Double d = Double.parseDouble(trace_1_mV);
		d += Double.parseDouble(trace_2_mV);
		d += Double.parseDouble(trace_3_mV);
		this.averageVol = (d) / 3;
	}

	protected double getTime() {
		return time;
	}

	protected void setTime(double time) {
		this.time = time;
	}

	protected double getAverageVol() {
		return averageVol;
	}

	protected void setAverageVol(double averageVol) {
		this.averageVol = averageVol;
	}

}

class DataType_3 extends AutoDataType {
	private double time;
	private double voltage;
	private double current;

	protected DataType_3(double time, double current, double voltage) {
		this.time = time;
		this.current = current;
		this.voltage = voltage;
	}

	protected DataType_3(String time, String current, String voltage) {
		this.time = Double.parseDouble(time);
		this.current = Double.parseDouble(current);
		this.voltage = Double.parseDouble(voltage);
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	public double getCurrent() {
		return current;
	}

	public void setCurrent(double current) {
		this.current = current;
	}

}
