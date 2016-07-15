package nanopipettes;

import java.io.File;
import java.util.ArrayList;

abstract public class DataProvider {
	ArrayList<TNData> dataSet;
	ArrayList<TNPeakData> peakSet;
	static Double experimentVoltage;
	
	/**
	 * This method scans the data and store the time, current, 
	 * and the voltage in the field dataSet.
	 * 
	 * @param startTime Time start to scan. 
	 * @param endTime End time, zero for scan to EOF.
	 * @param existing table name or a file path.
	 * @throws Exception 
	 * 			Actual implementation may complain the input name.
	 */
	abstract public void scanData(Double startTime, Double endTime, String name) throws Exception;
	
	/**
	 * This method scans the data and store the time, current, 
	 * and the voltage in the field dataSet.
	 * 
	 * @param startTime Time start to scan. 
	 * @param endTime End time, zero for scan to EOF.
	 * @param Input text file.
	 * @throws Exception 
	 * 			Actual implementation may complain file existence and formatting.
	 */
	abstract public void scanData(Double startTime, Double endTime, File inputText) throws Exception;
	
	/**
	 * This method gets the entire data set.
	 * @return entire data set
	 */
	abstract public ArrayList<TNData> getData();
	
	/**
	 * This method adds a single data at the end of the data set.
	 * @param toAdd TNData
	 */
	abstract public void appendData(TNData toAdd);
	
	/**
	 * This method adds a set of data at the end of the data set.
	 * @param toAdd List of TNData
	 */
	abstract public void appendData(ArrayList<TNData> toAdd);
	
	/**
	 * This method adds a single data at the end of the data set.
	 * @param time Time when the data is read.
	 * @param current Current when the data is read.
	 * @param voltage Voltage when the data is read.
	 */
	abstract public void appendData(Double time, Double current, Double voltage);
	
	/**
	 * This method gets the data set in the provided range.
	 * @return a set of data in the provided range. null is no data are in the provided range.
	 */
	abstract public ArrayList<TNData> getData(Double rangeStart, Double rangeEnd);
	
	
	/**
	 * This method scans the data set and store the peak start time,
	 * peak end time, and the average value between.
	 */
	abstract public void scanPeaks();
	
	/**
	 * This method returns the entire of peak set.
	 * @return entire peak set
	 */
	abstract public ArrayList<TNPeakData> getPeakSet();
	
	/**
	 * This method adds a peak into the peak data set.
	 * @param toAdd TNPeakData with start time, end time, and average.
	 */
	abstract public void addPeak(TNPeakData toAdd);
	
	/**
	 * This method adds a set of peaks into the peak data set.
	 * @param toAdd TNPeakData with start time, end time, and average.
	 */
	abstract public void addPeak(ArrayList<TNPeakData> toAdd);

	/**
	 * This method adds a peak into the peak data set.
	 * @param startTime Time for rising peak
	 * @param endTime Time for falling peak
	 * @param average Average value in between
	 */
	abstract public void addPeak(Double startTime, Double endTime, Double average);
	
	/**	
	 * This method gets a section of peaks from the peak set.
	 * @param rangeStart	DataProvider should check the set and find the nearest peak from the start time.
	 * @param rangeEnd	DataProvider should check the set and find the nearest peak from the end time.
	 * @return a section of peaks in the provided rang. null if no peak is the range.  
	 */
	abstract public ArrayList<TNPeakData> getPeakSet(Double rangeStart, Double rangeEnd);
	
}
