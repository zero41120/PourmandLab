package edu.pourmand.soe.ucsc.BioGrapher;

import java.io.File;
import java.util.List;


class DataProvider {

	public static DataProvider dP = new DataProvider();
	private static DataAnalyzer dA = new DataAnalyzer();
	private List<DataList> mainList = null;
	

	private List<File> workingFiles = null;
	private String currentType = null;
	private Integer pathCounter = 0;
	private boolean isVariableValid = false;

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
		pathCounter = 0;
		isVariableValid = false;
		if (!isKeepingData) {
			mainList = null;
		}
	}

	/**
	 * This is the method which extract variable from refFile and assigns the
	 * variable into the default data provider.
	 * TODO
	 * This method sets isVariableValid to true if variables are loaded. 
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
			if (tP.getMainList() != null) {
				if (dP.getMainList() == null) {
					dP.setMainList(tP.getMainList());
				} else {
					dP.getMainList().addAll(tP.getMainList());
				}
			}

			/* 
			// Append type 2 data from tP to dP.
			if (tP.getType_2() != null) {
				if (dP.getType_2() == null) {
					dP.setType_2(tP.getType_2());
				} else {
					dP.getType_2().addAll(tP.getType_2());
				}
			}
*/
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
		StringBuffer reportMessage = new StringBuffer("DataProvider report:\n");
		String message = this.getMainList() == null ? "N/A\n"
				: "Provider- Size:" + getMainList().size();
		reportMessage.append(message);
		
		return reportMessage.toString();
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
/*
	protected List<DataType_1> getType_1() {
		return type_1;
	}

	protected void setType_1(List<DataType_1> type_1) {
		this.type_1 = type_1;
	}

	protected List<DataType_2> getType_2() {
		return type_2;
	}

	protected void setType_2(List<DataType_2> type_2) {
		this.type_2 = type_2;
	}
*/
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
	
	protected List<DataList> getMainList() {
		return mainList;
	}
	
	protected void setMainList(List<DataList> mainList) {
		this.mainList = mainList;
	}

}