package nanopipettes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLDatabase extends DataProvider {
	final private static String DB_NAME = "Pourmand.sqlite";
	final private static String DB_SYSTEM = "org.sqlite.JDBC";
	static Double experimentVoltage;

	Connection db_connect = null;
	Statement stmt = null;
	String db_working_table = null;
	ArrayList<TNData> dataSet = new ArrayList<>();
	ArrayList<TNPeakData> peakSet = new ArrayList<>();

	/**
	 * SQLDatabase Constructor: Reads or creates Pourmand.sqlite database.
	 * 
	 * @throws SQLException
	 *             Connection may fail.
	 * @throws ClassNotFoundException
	 *             Library may not be included.
	 */
	public SQLDatabase() throws SQLException, ClassNotFoundException {
		Class.forName(DB_SYSTEM);
		this.db_connect = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
		this.stmt = db_connect.createStatement();
		this.db_connect.setAutoCommit(false);
	}

	@Override
	public void scanData(Double startTime, Double endTime, String name) throws Exception {
		if (this.isTableExist(db_connect, name)) {
			// If table exists, scan the table.
			db_working_table = name.substring(0, name.lastIndexOf('.'));
			String sqlString = "SELECT * FROM " + db_working_table;
			sqlString += (startTime <= 0.0 ? " WHERE time >= 0" : " WHERE time >= " + startTime);
			sqlString += (endTime <= 0.0 ? "" : " AND time <= " + endTime);
			ResultSet myResultSet = stmt.executeQuery(sqlString);
			while (myResultSet.next()) {
				dataSet.add(new TNData(myResultSet.getDouble(1), myResultSet.getDouble(2), myResultSet.getDouble(3)));
			}
		} else {
			// User probably provided a file path
			File toScan = new File(name);
			this.scanData(startTime, endTime, toScan);
		}
	}

	@Override
	public void scanData(Double startTime, Double endTime, File inputText) throws Exception {
		/* FILE CHECKING */
		FileManager.checkExistance(inputText);
		FileManager.checkHeader(inputText);

		try (FileInputStream stream = new FileInputStream(inputText);
				InputStreamReader reader = new InputStreamReader(stream, "UTF8");
				BufferedReader bufferedReader = new BufferedReader(reader)) {
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				//TNData[] x = FileFormatHelper.parseData(line);
			}

			String name = inputText.getName().replaceAll("\\s+", "_");
			name = name.substring(0, name.lastIndexOf('.'));
			for (int i = 1; i <= FileFormatHelper.traceCount; i++) {
				String name_trace = name + "_T" + i;
				String sqlString = "CREATE TABLE '" + name_trace + "' ('Time' DOUBLE, 'pA' DOUBLE, 'mV' DOUBLE)";
				stmt.executeUpdate(sqlString);
			}	
			db_connect.commit();
		} catch (IOException e) {
			// IO Streams
		}

	}

	@Override
	public ArrayList<TNData> getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void appendData(TNData toAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void appendData(ArrayList<TNData> toAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void appendData(Double time, Double current, Double voltage) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<TNData> getData(Double rangeStart, Double rangeEnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void scanPeaks() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<TNPeakData> getPeakSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPeak(TNPeakData toAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPeak(ArrayList<TNPeakData> toAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPeak(Double startTime, Double endTime, Double average) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<TNPeakData> getPeakSet(Double rangeStart, Double rangeEnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {
		String message = "";
		for (TNData data : dataSet) {
			message += data.toString() + "\n";
		}

		return message;
	}

	private boolean isTableExist(Connection connection, String tableName) {
		try {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				if (tableName.equals(rs.getString(3))) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			return false;
		}
	}

}
