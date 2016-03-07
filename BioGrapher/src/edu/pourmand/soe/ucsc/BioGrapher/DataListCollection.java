package edu.pourmand.soe.ucsc.BioGrapher;

import java.util.List;

public class DataListCollection{
	private String fileTitle;
	private String filePath;
	private Double concentration;
	private List<DataType_1> listType_1;
	private List<DataType_2> listType_2;

	
	public String getFileTitle() {
		return fileTitle;
	}
	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}
	public List<DataType_1> getListType_1() {
		return listType_1;
	}
	public void setListType_1(List<DataType_1> listType_1) {
		this.listType_1 = listType_1;
	}
	public List<DataType_2> getListType_2() {
		return listType_2;
	}
	public void setListType_2(List<DataType_2> listType_2) {
		this.listType_2 = listType_2;
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

class DataType_1 {
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


class DataType_2 {

	private double time;
	private double averageVol;

	protected DataType_2(double time, double trace_1_mV, double trace_1_pA, double trace_2_mV, double trace_2_pA,
			double trace_3_mV, double trace_3_pA) {
		this.time = time;
		this.averageVol = (trace_1_mV + trace_2_mV + trace_3_mV)/3;
	}

	protected DataType_2(String time, String trace_1_mV, String trace_1_pA, String trace_2_mV, String trace_2_pA,
			String trace_3_mV, String trace_3_pA) {
		this.time = Double.parseDouble(time);
		Double d = Double.parseDouble(trace_1_mV);
		d += Double.parseDouble(trace_2_mV);
		d += Double.parseDouble(trace_3_mV);
		this.averageVol = (d)/3;
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
