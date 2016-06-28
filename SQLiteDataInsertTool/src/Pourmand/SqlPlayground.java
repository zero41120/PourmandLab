package Pourmand;

import java.sql.*;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SqlPlayground {

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

		// Get file from command line argument.
		File myFile = new File(args[0]);
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
					+ "' ('Time' DOUBLE, 'pA' DOUBLE, 'mV' DOUBLE)");
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
					+ "' (Time, pA, mV) VALUES ";
			int counter = 0, progress = 0;
			
			// Read lines from file and insert
			while ((line = bufferedReader.readLine()) != null) {
				line = bufferedReader.readLine();
				StringTokenizer sTk = new StringTokenizer(line);
				dataFromLine = "(";
				dataFromLine += sTk.nextToken("\t") + ",";
				dataFromLine += sTk.nextToken("\t") + ",";
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
