package edu.pourmand.soe.ucsc.BioGrapher;

import java.io.File;
import java.util.List;

class DataProvider {

	public static DataProvider dP = new DataProvider();
	private static DataAnalyzer dA = new DataAnalyzer();

	private List<DataListCollection> mainList = null;
	private List<File> workingFiles = null;
	private List<Double> workingConcentration = null;
	private String currentType = null;
	private Integer pathCounter = 0;
	private Boolean isVariableValid = false;
	private Double yIntersect;
	private Double slope;
	private Double type1Voltage = null;

	
	public Double getFileSize(Integer whichType){
		Double counter = 0.0;
		if (getDataCollection() != null) {
			for (DataListCollection dataList : mainList) {
				if (dataList.getType().equals(whichType)) {
					counter += dataList.getListAuto().size();
				}
			}
		}
		return counter;
	}

	/**
	 * This is the method which returns the remaining size of the workinFiles
	 * 
	 * @return Size of remaining workingFiles
	 */
	protected Integer getRemainingFileSize() {
		if (workingFiles == null) {
			return 0;
		} else if (workingFiles.size() == pathCounter) {
			return 0;
		} else {
			return workingFiles.size() - pathCounter;
		}
	}

	/**
	 * This is the method which returns the file on the workingFile list. This
	 * method will increment the pathCounter and return the correct next file.
	 * 
	 * @return File Next file on the workingFile list.
	 */
	protected File getNextWorkingFile() {
		if (getWorkingFiles().size() == pathCounter) {
			return null;
		}
		File temp = getWorkingFiles().get(pathCounter);
		incPathCounter();
		return temp;
	}

	/**
	 * This is the method which reset the data provider.
	 * 
	 * @param isKeepingData
	 *            If true, will assign null to the list of object.
	 */
	protected void resetProviderAndKeepData(boolean isKeepingData) {
		workingFiles = null;
		currentType = null;
		workingConcentration = null;
		pathCounter = 0;
		isVariableValid = false;
		if (!isKeepingData) {
			mainList = null;
		}
	}

	/**
	 * This is the method which extract variable from refFile and assigns the
	 * variable into the default data provider. TODO This method sets
	 * isVariableValid to true if variables are loaded.
	 * 
	 * @param refFile
	 *            File which is containing variable.
	 */
	protected void extractVariable(File refFile) {
		dP.isVariableValid = false;
		// Creates a temporary provider to receive data from analyzer
		DataProvider tP = dA.extractVariableFromFile(this.currentType, refFile);

		if (tP != null) {
			dP.isVariableValid = true;

			// Copy current type and counter to dP.
			if (tP.getCurrentType() != null)
				dP.setCurrentType(tP.getCurrentType());
			if (tP.getPathCounter() != 0)
				dP.setPathCounter(tP.getPathCounter());

			// Append DataList from tP to dP.
			if (tP.getDataCollection() != null) {
				if (dP.getDataCollection() == null) {
					dP.setMainList(tP.getDataCollection());
				} else {
					dP.getDataCollection().addAll(tP.getDataCollection());
				}
			}

			if (tP.getWorkingConcentration() != null) {
				dP.setWorkingConcentration(tP.getWorkingConcentration());
			}

			if (tP.workingFiles != null) {
				dP.workingFiles = tP.workingFiles;
			}
		}
	}

	/**
	 * This is the method which generate a report of data in dP.
	 * 
	 * @return String of the report.
	 */
	public String getReport() {
		String message = "DataProvider report:\t";
		message += this.getDataCollection() == null ? "N/A\n" : "Loaded files:" + getDataCollection().size();
		if (getDataCollection() != null) {
			message += "(Concentration : File Title)\n";
			int counter = 1;
			for (DataListCollection dataList : mainList) {
				message += dataList.getConcentration() + " : " + dataList.getFileTitle();
				message += counter++ % 5 == 0 ? "\n" : " | ";
			}

		}

		return message;
	}

	/*
	 * Methods below are getters and setters.
	 */
	protected void setCurrentType(File refFile) {
		DataAnalyzer myAnalyzer = new DataAnalyzer();
		this.currentType = myAnalyzer.getDataType(refFile);
	}

	protected void setCurrentTypeAsNull() {
		this.currentType = null;
	}

	protected boolean isValidVariable() {
		return isVariableValid;
	}

	protected boolean isValidType() {
		return this.currentType == null ? false : true;
	}

	protected void incPathCounter() {
		this.pathCounter++;
	}

	protected String getCurrentType() {
		return currentType;
	}

	protected void setCurrentType(String currentType) {
		this.currentType = currentType;
	}

	protected Integer getPathCounter() {
		return pathCounter;
	}

	protected void setPathCounter(Integer pathCounter) {
		this.pathCounter = pathCounter;
	}

	protected void setValidVariable(boolean validVariable) {
		this.isVariableValid = validVariable;
	}

	public List<File> getWorkingFiles() {
		return workingFiles;
	}

	public void setWorkingFiles(List<File> workingFiles) {
		this.workingFiles = workingFiles;
	}

	protected List<DataListCollection> getDataCollection() {
		return mainList;
	}

	protected void setMainList(List<DataListCollection> mainList) {
		this.mainList = mainList;
	}

	public List<Double> getWorkingConcentration() {
		return workingConcentration;
	}

	public void setWorkingConcentration(List<Double> workingConcentration) {
		this.workingConcentration = workingConcentration;
	}

	public Double getYIntersect() {
		return yIntersect;
	}

	public void setYIntersect(Double yIntersect) {
		this.yIntersect = yIntersect;
	}

	public Double getSlope() {
		return slope;
	}

	public void setSlope(Double slope) {
		this.slope = slope;
	}

	public Double getType1Voltage() {
		return type1Voltage;
	}

	public void setType1Concetration(Double type1Concetration) {
		this.type1Voltage = type1Concetration;
	}

}
