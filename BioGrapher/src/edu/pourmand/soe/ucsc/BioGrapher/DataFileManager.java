package edu.pourmand.soe.ucsc.BioGrapher;

import static java.lang.System.out;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.msg;

public class DataFileManager {

	/**
	 * This is the method that opens the file using the filePathString argument.
	 * This method will return null if it fails to open the file.
	 * 
	 * @param filePathString
	 *            String that contains the path of the file.
	 * @return The File that has been opened by the method or null if the method
	 *         fails to open the file.
	 */
	protected File openFile(String filePathString) throws Exception {
		File myFile = new File(filePathString);
		return myFile.exists() ? myFile : null;
	}

	/**
	 * This is the method that checks the BioTemp.bgt, which has the paths this
	 * program opened in the previous operation.
	 * 
	 * @return true if the BioTemp.bgt exist.
	 */
	protected boolean checkPathFile() {
		/*
		File dir = new File("/Users/SeanLin/Documents/Eclipse/BIoGrapher/");
		File files[] = dir.listFiles();
		for(File f: files){
			out.println(f.getName() + " is " +(f.isFile()? "File":"Folder"));
		}
		*/

		out.println(msg.getString("<Notice>OldPathFind"));
		File oldPath = new File("./BioTemp.bgt");
		boolean flag = oldPath.getAbsoluteFile().exists();
		return flag;
	}

	/**
	 * This is the method that deletes the BioTemp.bgt, which has the paths this
	 * program opened in the previous operation.
	 */
	protected void deletePathFile() throws Exception {
		Path p = Paths.get("./BioTemp.bgt");
		if (Files.deleteIfExists(p)) {
			System.out.println(msg.getString("<Notice>OldPathDelete"));
		}
	}

	/**
	 * This is the method that creates the BioTemp.bgt file with a signature of
	 * "-BGSignature-". This method calls deletePathFile() to prevent
	 * duplication of the file.
	 */
	private void createPathFile() throws Exception {
		FileWriter writeFile = new FileWriter("./BioTemp.bgt");
		PrintWriter writerPrint = new PrintWriter(writeFile);
		writerPrint.println("-BGSignature-");
		writerPrint.flush();
		System.out.println(msg.getString("<Notice>OldPathCreate"));
		writerPrint.close();

	}

	/**
	 * This is the method that appends a path to the BioTemp.bgt file. If the
	 * path file does not exist, this method will call createPathFile().
	 * 
	 * @param refPath
	 *            The file to get path from.
	 */
	protected void savePath(DataProvider tP) throws Exception {
		if (!checkPathFile()) {
			createPathFile();
		}
		FileWriter writeFile = new FileWriter("./BioTemp.bgt", true);
		PrintWriter writerPrint = new PrintWriter(writeFile);
		System.out.print(msg.getString("<Notice>OldPathSave"));
		for (DataListCollection dC : tP.getDataCollection()) {
			writerPrint.println(dC.getFilePath() +":"+ dC.getConcentration());
			System.out.println(dC.getFilePath() +":"+ dC.getConcentration());

		}
		writerPrint.flush();
		/*if (refPath.getPath() == "./BioTemp.bgt") {
			deletePathFile();
			System.out.println("Delete duplicate");
		}*/
		writerPrint.close();
	}

	/**
	 * This is the method which gets the BioTemp.bgt file
	 * 
	 * @return The BioTemp.bgt file if exists, null otherwise.
	 */
	protected File getFilePath() throws Exception{
		File myFile = openFile("./BioTemp.bgt");
		return myFile;
	}

}
