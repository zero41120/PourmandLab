package edu.pourmand.soe.ucsc.BioGrapher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.pourmand.soe.ucsc.BioGrapher.DataFileManager;
import edu.pourmand.soe.ucsc.BioGrapher.DataProvider;
import edu.pourmand.soe.ucsc.BioGrapher.DataType_1;

public class DataAnalyzer {

	/**
	 * This is the method which scans signature from refFile.
	 * 
	 * @param refFile
	 *            File which is containing variable.
	 * @return String of the type. Type1, Type2, or PathType.
	 */
	protected String getDataType(File refFile) {

		try (FileInputStream readFile = new FileInputStream(refFile);
				InputStreamReader readIn = new InputStreamReader(readFile, "UTF8")) {
			BufferedReader ReadBuffer = new BufferedReader(readIn);
			String line = "";
			while ((line = ReadBuffer.readLine()) != null) {
				if (this.checkType_1(line)) {
					return "Type1";
				} else if (this.checkType_2(line)) {
					return "Type2";
				} else if (this.checkType_3(line)) {
					return "Type3";
				} else if (this.checkPathFile(line)) {
					return "PathType";
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/**
	 * This is the method which scans variable from refFile. dataType argument
	 * should instruct this method to scans for variables defined in DataType_X
	 * class.
	 * 
	 * @param dataType
	 *            Type instruction.
	 * @param refFile
	 *            File which is containing variable.
	 * @return DataProvider object. An temporary DataProvider should received
	 *         this object.
	 */
	protected DataProvider extractVariableFromFile(String dataType, File refFile) {
		DataProvider tempProvider = new DataProvider();
		List<DataListCollection> myDataListCollections = new ArrayList<>();
		DataListCollection dataList = new DataListCollection();
		dataList.setFileTitle(refFile.getName());

		switch (dataType) {
		case "Type1":
			DataType_1 pivot1 = searchDataType_1_Pivot(refFile);
			dataList.setListAuto(createDataType_1_Array(refFile, pivot1), 1);
			break;
		case "Type2":
			dataList.setListAuto(createDataType_2_Array(refFile), 2);
			break;
		case "Type3":
			dataList.setListAuto(createDataType_3_Array(refFile), 3);
			break;
		case "PathType":
			FileConcetrationList myFC = this.getFilesFromPathFile(refFile);
			tempProvider.setWorkingFiles(myFC.getMyFiles());
			tempProvider.setWorkingConcentration(myFC.getMyConcentration());
			return tempProvider;
		default:
			// File is not a valid type, ask user to re-enter file.
			break;
		}
		dataList.setFilePath(refFile.getAbsolutePath());
		myDataListCollections.add(dataList);
		tempProvider.setMainList(myDataListCollections);
		return tempProvider;
	}

	/**
	 * --PathFile-- This is the method which scans for PathFile signature.
	 * 
	 * @param sampleString
	 *            A string extract from file.
	 * @return true if sampleString contains the signature.
	 */
	private boolean checkPathFile(String sampleString) {
		return sampleString.equals("-BGSignature-");
	}

	/**
	 * --PathFile-- This is the method which scans paths in the PathFile and
	 * creates a list of all paths extract from the PathFile.
	 * 
	 * @param pathFile
	 *            PathFile that contains the paths from previous operation.
	 * @return A list of string object of all paths.
	 */
	private FileConcetrationList getFilesFromPathFile(File pathFile) {

		DataFileManager myManager = new DataFileManager();
		FileConcetrationList myFC = new FileConcetrationList();
		List<Double> myConcentration = new ArrayList<>();
		List<File> myFiles = new ArrayList<>();

		try (FileInputStream readFile = new FileInputStream(pathFile);
				InputStreamReader readIn = new InputStreamReader(readFile, "UTF8")) {
			BufferedReader ReadBuffer = new BufferedReader(readIn);
			String line = "";
			while ((line = ReadBuffer.readLine()) != null) {
				StringTokenizer stk = new StringTokenizer(line);
				File myFile = myManager.openFile(stk.nextToken(":"));
				if (myFile != null) {
					myFiles.add(myFile);
					try {
						myConcentration.add(Double.parseDouble(stk.nextToken()));
					} catch (Exception e) {
						myConcentration.add(new Double(0));
					}
				}
			}
			myFC.setMyFile(myFiles);
			myFC.setMyConcentration(myConcentration);
		} catch (Exception e) {
			System.out.println(e);
		}

		return myFC;
	}

	/**
	 * --DataType_1-- This is the method which scans for DataType_1 signature.
	 * 
	 * @param sampleString
	 *            A string extract from file.
	 * @return true if sampleString contains the signature.
	 */
	private boolean checkType_1(String sampleString) {
		return sampleString.equals("Linear Sweep Voltammetry");
	}

	/**
	 * --DataType_1-- This is the method which scans for variable defined in
	 * DataType_1 class and creates a list of DataType_1 object. This method
	 * uses the pivot object to get the correct reading of the data. The all
	 * current variable that our program uses needs to subtract the current of
	 * the data at 0 voltage. This method calls the createDataType_1_Object() to
	 * create each individual object in the list.
	 * 
	 * @param refFile
	 *            The file that contains the variable.
	 * @param pivot
	 *            The pivot object that contains the 0 voltage.
	 * @return A list of DataType_1 object.
	 */
	private List<DataType_1> createDataType_1_Array(File refFile, DataType_1 pivot) {

		List<DataType_1> myDataArray = new ArrayList<>();

		try (FileInputStream readFile = new FileInputStream(refFile);
				InputStreamReader readIn = new InputStreamReader(readFile, "UTF8")) {
			BufferedReader ReadBuffer = new BufferedReader(readIn);
			String line = "";
			while ((line = ReadBuffer.readLine()) != null) {
				if (line.equals("Potential/V, Current/A")) {
					line = ReadBuffer.readLine(); // There is an empty line.
					while ((line = ReadBuffer.readLine()) != null) {
						myDataArray.add(createDataType_1_Object(line, pivot));
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return myDataArray;
	}

	/**
	 * --DataType_1-- This is the method which scans the 0 voltage line in the
	 * file and returns the object that contains the 0 voltage: current data.
	 * 
	 * @param refFile
	 *            The file that contains the variable.
	 * @return DataType_1 object that has the 0 voltage data.
	 */
	private DataType_1 searchDataType_1_Pivot(File refFile) {

		DataType_1 pivot = new DataType_1(0.0, 0.0);

		try (FileInputStream readFile = new FileInputStream(refFile);
				InputStreamReader readIn = new InputStreamReader(readFile, "UTF8")) {
			BufferedReader ReadBuffer = new BufferedReader(readIn);
			String line = "";
			while ((line = ReadBuffer.readLine()) != null) {
				if (line.length() > 6) {
					if (line.substring(0, 7).equals("-0.000,")) {
						StringTokenizer stk = new StringTokenizer(line);
						pivot = new DataType_1(stk.nextToken(", "), stk.nextToken(", "));
						break;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return pivot;
	}

	/**
	 * --DataType_1-- This is the method which scans the voltage: current from
	 * the line argument and creates a DataType_1 object with correct current.
	 * 
	 * @param line
	 *            String which contains voltage: current data.
	 * @param pivot
	 *            DataType_1 Object which has the 0 voltage: current data.
	 * @return A DataType_1 object which the caller should have a list of
	 *         DataType_1 object.
	 */
	private DataType_1 createDataType_1_Object(String line, DataType_1 pivot) {
		StringTokenizer stk = new StringTokenizer(line);
		DataType_1 temp = new DataType_1(stk.nextToken(", "), stk.nextToken(", "));
		temp.setCurrnet(temp.getCurrnet() - pivot.getCurrnet());
		temp.setCurrnet(temp.getCurrnet() * 1000000000);
		return temp;
	}

	/**
	 * --DataType_2-- This is the method which scans for DataType_2 signature.
	 * 
	 * @param sampleString
	 *            A string extract from file.
	 * @return true if sampleString contains the signature.
	 */
	private boolean checkType_2(String sampleString) {
		return sampleString.equals(
				"\"Signals=\"	\"Vm_primar\"	\"Im_sec\"	\"Vm_primar\"	\"Im_sec\"	\"Vm_primar\"	\"Im_sec\"");

	}

	/**
	 * --DataType_2-- This is the method which scans for variable defined in
	 * DataType_2 class and creates a list of DataType_2 object. This This
	 * method calls the createDataType_2_Object() to create each individual
	 * object in the list.
	 * 
	 * @param refFile
	 *            The file that contains the variable.
	 * @return A list of DataType_2 object.
	 */
	private List<DataType_2> createDataType_2_Array(File refFile) {

		List<DataType_2> myDataArray = new ArrayList<>();

		try (FileInputStream readFile = new FileInputStream(refFile);
				InputStreamReader readIn = new InputStreamReader(readFile, "UTF8")) {
			BufferedReader ReadBuffer = new BufferedReader(readIn);
			String line = "";
			while ((line = ReadBuffer.readLine()) != null) {
				if (line.equals(
						"\"Time (s)\"	\"Trace #1 (mV)\"	\"Trace #1 (pA)\"	\"Trace #2 (mV)\"	\"Trace #2 (pA)\"	\"Trace #3 (mV)\"	\"Trace #3 (pA)\"")) {
					while ((line = ReadBuffer.readLine()) != null) {
						myDataArray.add(createDataType_2_Object(line));
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return myDataArray;
	}

	/**
	 * --DataType_2-- This is the method which scans the time and traces from
	 * the line argument and creates a DataType_2 object.
	 * 
	 * @param line
	 *            String which contains data of interest.
	 * @param pivot
	 *            DataType_2 Object which has data of interest.
	 * @return A DataType_2 object which the caller should have a list of
	 *         DataType_2 object.
	 */
	private DataType_2 createDataType_2_Object(String line) {
		StringTokenizer stk = new StringTokenizer(line);
		// All data will be parsed, but only the voltage will be stored in the
		// object
		DataType_2 temp = new DataType_2(stk.nextToken("\t"), stk.nextToken("\t"), stk.nextToken("\t"),
				stk.nextToken("\t"), stk.nextToken("\t"), stk.nextToken("\t"), stk.nextToken("\t"));
		return temp;
	}

	private boolean checkType_3(String sampleString) {
		return sampleString.equals("\"Signals=\"	\"Im_primar\"	\"Vm_sec\"");
	}

	private List<DataType_3> createDataType_3_Array(File refFile) {

		List<DataType_3> myDataArray = new ArrayList<>();

		try (FileInputStream readFile = new FileInputStream(refFile);
				InputStreamReader readIn = new InputStreamReader(readFile, "UTF8")) {
			BufferedReader ReadBuffer = new BufferedReader(readIn);
			String line = "";
			while ((line = ReadBuffer.readLine()) != null) {
				if (line.equals("\"Signals=\"	\"Im_primar\"	\"Vm_sec\"")) {
					line = ReadBuffer.readLine();
					while ((line = ReadBuffer.readLine()) != null) {
						myDataArray.add(createDataType_3_Object(line));
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return myDataArray;
	}

	private DataType_3 createDataType_3_Object(String line) {
		StringTokenizer stk = new StringTokenizer(line);
		DataType_3 temp = new DataType_3(stk.nextToken("\t"), stk.nextToken("\t"), stk.nextToken("\t"));
		return temp;
	}

}

class FileConcetrationList {
	private List<File> myFile = null;
	private List<Double> myConcentration = null;

	public List<File> getMyFiles() {
		return myFile;
	}

	public void setMyFile(List<File> myFile) {
		this.myFile = myFile;
	}

	public List<Double> getMyConcentration() {
		return myConcentration;
	}

	public void setMyConcentration(List<Double> myConcentration) {
		this.myConcentration = myConcentration;
	}

}
