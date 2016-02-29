package edu.pourmand.soe.ucsc.BioGrapher;

import java.util.List;

public class DataList{
	private String fileTitle;
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
	private double trace_1_mV;
	private double trace_1_pA;
	private double trace_2_mV;
	private double trace_2_pA;
	private double trace_3_mV;
	private double trace_3_pA;

	protected DataType_2(double time, double trace_1_mV, double trace_1_pA, double trace_2_mV, double trace_2_pA,
			double trace_3_mV, double trace_3_pA) {
		this.time = time;
		this.trace_1_mV = trace_1_mV;
		this.trace_1_pA = trace_1_pA;
		this.trace_2_mV = trace_2_mV;
		this.trace_2_pA = trace_2_pA;
		this.trace_3_mV = trace_3_mV;
		this.trace_3_pA = trace_3_pA;
	}

	protected DataType_2(String time, String trace_1_mV, String trace_1_pA, String trace_2_mV, String trace_2_pA,
			String trace_3_mV, String trace_3_pA) {
		this.time = Double.parseDouble(time);
		this.trace_1_mV = Double.parseDouble(trace_1_mV);
		this.trace_1_pA = Double.parseDouble(trace_1_pA);
		this.trace_2_mV = Double.parseDouble(trace_2_mV);
		this.trace_2_pA = Double.parseDouble(trace_2_pA);
		this.trace_3_mV = Double.parseDouble(trace_3_mV);
		this.trace_3_pA = Double.parseDouble(trace_3_pA);
	}

	protected double getTime() {
		return time;
	}

	protected void setTime(double time) {
		this.time = time;
	}

	protected double getTrace_1_mV() {
		return trace_1_mV;
	}

	protected void setTrace_1_mV(double trace_1_mV) {
		this.trace_1_mV = trace_1_mV;
	}

	protected double getTrace_1_pA() {
		return trace_1_pA;
	}

	protected void setTrace_1_pA(double trace_1_pA) {
		this.trace_1_pA = trace_1_pA;
	}

	protected double getTrace_2_mV() {
		return trace_2_mV;
	}

	protected void setTrace_2_mV(double trace_2_mV) {
		this.trace_2_mV = trace_2_mV;
	}

	protected double getTrace_2_pA() {
		return trace_2_pA;
	}

	protected void setTrace_2_pA(double trace_2_pA) {
		this.trace_2_pA = trace_2_pA;
	}

	protected double getTrace_3_mV() {
		return trace_3_mV;
	}

	protected void setTrace_3_mV(double trace_3_mV) {
		this.trace_3_mV = trace_3_mV;
	}

	protected double getTrace_3_pA() {
		return trace_3_pA;
	}

	protected void setTrace_3_pA(double trace_3_pA) {
		this.trace_3_pA = trace_3_pA;
	}

}

