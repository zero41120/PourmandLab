package Pourmand;

import java.sql.*;

public class SqlReader {

	final private static String DB_NAME = "Pourmand.sqlite";
	final private static String DB_SYSTEM = "org.sqlite.JDBC";
	final private static boolean debugMode = false;

	static Connection db_connect = null;
	static Statement stmt = null;
	static String db_table_name = null;
	public static void main(String[] args) {

		// Get database connection.
		try {
			Class.forName(DB_SYSTEM);
			db_connect = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
			stmt = db_connect.createStatement();
			db_connect.setAutoCommit(false);
			db_table_name = args[0];
			if (!isTableExist(db_connect, db_table_name)) {
				throw new RuntimeException("Table does not exist.");
			}
		} catch (ArrayIndexOutOfBoundsException aE) {
			System.err.println("Table name is not provided.");
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Connection failed");
			System.exit(0);
		}

		try {
			ResultSet myResultSet = stmt.executeQuery("SELECT * FROM '" + db_table_name + "' WHERE mV > 10 or mV < -10");
			Data sum = new Data(0, 0, 0), posExpected = new Data(0, 0, 0), negExpected = new Data(0, 0, 0);
			Data last = new Data(0, 0, 0), current = new Data(0, 0, 0);
			int counter = 1, posCounter = 1, negCounter = 1;
			while (myResultSet.next()) {
				last.setData(current);
				current.setData(myResultSet.getDouble(1), myResultSet.getDouble(2), myResultSet.getDouble(3));
				if (debugMode) {
					System.out.println("Current :" + current.toSting(true) + "| Sum: " + sum);
				}

				if (Math.abs(current.mV - last.mV) > 100 && !last.isEmpty()) {
					Data average = sum.calculateAverage(current.time, counter);
					if (average.mV > 0) {
						posCounter++;
						posExpected.incrementData(average);
					} else {
						negCounter++;
						negExpected.incrementData(average);
					}
					counter = 1;
					sum.setData(0, 0, 0);

					if (debugMode) {
						System.out.println("Voltage chagne: ");
						System.out.println("Average :" + average);
						System.out.println("   Last :" + last);
						System.out.println("Current :" + current);
					}
				} else {
					sum.incrementData(current);
					counter++;
				}
			}
			System.out.println();
			System.out.println("Expcected values for table " + db_table_name + ":");
			System.out.println(posExpected.calculateAverage(0.0, posCounter));
			System.out.println(negExpected.calculateAverage(0.0, negCounter));
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	static boolean isTableExist(Connection connection, String tableName) throws SQLException {
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		while (rs.next()) {
			if (tableName.equals(rs.getString(3))) {
				return true;
			}
		}
		return false;
	}

}

class Data {
	double time;
	double pA;
	double mV;

	Data(double time, double pA, double mV) {
		this.time = time;
		this.pA = pA;
		this.mV = mV;
	}

	@Override
	public String toString() {
		return pA + "\t" + mV;
	}

	public String toSting(boolean printTime) {
		return time + "\t" + pA + "\t" + mV;
	}

	public Data calculateAverage(Double timeMark, int counter) {
		return new Data(timeMark, pA / counter, mV / counter);
	}

	public void setData(double time, double pA, double mV) {
		this.time = time;
		this.pA = pA;
		this.mV = mV;
	}

	public void setData(Data toCopy) {
		this.time = toCopy.time;
		this.pA = toCopy.pA;
		this.mV = toCopy.mV;
	}

	public boolean isEmpty() {
		return this.time == 0;
	}

	public void incrementData(Data toIncrement) {
		this.pA += toIncrement.pA;
		this.mV += toIncrement.mV;
	}
}
