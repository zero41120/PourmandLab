package Pourmand;

import java.sql.*;
import java.util.StringTokenizer;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SqlFiveTrace {

	final private static String db_name = "Pourmand.sqlite";
	final private static String db_system = "org.sqlite.JDBC";
	static Connection db_connect = null;
	static Statement stmt = null;

	/**
	 * This tool takes a command line argument of a file format with
	 * <data> \t <data> \t <data>, then insert the data into a SQLite databse.
	 * @param args[0] is the file
	 * 
	 * Usage java -jar Lab.jar "<filename>"
	 */
	public static void main(String[] args) {

		File myFile = null;
		// Get file from command line argument.
		try {
			myFile = new File(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("No file provided");
			System.exit(0);
		}
		String tableName = args[0].replaceAll("\\s+", "");

		// Check if file exists.
		if (!myFile.exists()) {
			System.err.println("file not exists");
			System.exit(0);
		}

		// Get database connection.
		try {
			Class.forName(db_system);
			db_connect = DriverManager.getConnection("jdbc:sqlite:" + db_name);
			stmt = db_connect.createStatement();
			db_connect.setAutoCommit(false);
		} catch (Exception e) {
			System.err.println("Connection failed");
			e.printStackTrace();
			System.exit(0);
		}

		// Create table using input file's name.
		try {
			stmt.executeUpdate("CREATE TABLE '" + tableName
					+ "' ('Time' DOUBLE, 'pA1' DOUBLE, 'mV1' DOUBLE,'pA2' DOUBLE, 'mV2' DOUBLE,'pA3' DOUBLE, 'mV3' DOUBLE,'pA4' DOUBLE, 'mV4' DOUBLE,'pA5' DOUBLE, 'mV5' DOUBLE)");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.err
					.println("Recommand action: \nTo avoid data duplication in the same table, please rename the file.");
			System.exit(0);
		}

		// Read file and create insertion statement.
		try (FileInputStream stream = new FileInputStream(myFile);
				InputStreamReader reader = new InputStreamReader(stream, "UTF8");
				BufferedReader bufferedReader = new BufferedReader(reader)) {
			
			// Create required variable
			String line = "";
			String dataFromLine = "";
			String sql = "INSERT INTO '" + tableName
					+ "' (Time, pA1, mV1, pA2, mV2, pA3, mV3, pA4, mV4, pA5, mV5) VALUES ";
			int counter = 0, progress = 0;
			
			// Read lines from file and insert
			while ((line = bufferedReader.readLine()) != null) {
				line = bufferedReader.readLine();
				StringTokenizer sTk = new StringTokenizer(line);
				dataFromLine = "(";
				for (int i = 0; i < 10; i++) {
					dataFromLine += sTk.nextToken("\t") + ",";	
				}
				dataFromLine += sTk.nextToken("\t") + ");";
				stmt.executeUpdate(sql + dataFromLine);
				progress = printProgress(progress, counter++);
			}
			
			// commit and close all connections
			stmt.close();
			db_connect.commit();
			db_connect.close();
			System.out.println("\nInsertion completed");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Program exit...");
	}
	
	static int printProgress(int progress, int counter){
		if (counter % 25000 == 0) {
			progress += 5;
			String progString = "[";
			for (int i = 0; i < progress; i += 5) {
				progString += "::";
			}
			for (int i = 0; i < 100 - progress; i += 5) {
				progString += "--";
			}
			progString += "] " + progress + "%";
			System.out.print("\r" + progString);
		}
		return progress;
	}
}
