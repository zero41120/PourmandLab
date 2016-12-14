import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;

import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LegendPosition;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.LineChartSeries;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CapturedGroup {

	private ArrayList<String> capturedGroups;

	private void autoGroup(Matcher m) {
		for (int i = 1; i <= m.groupCount(); i++) {
			capturedGroups.add(m.group(i));
		}
	}

	public CapturedGroup(Matcher m) {
		capturedGroups = new ArrayList<>();
		autoGroup(m);
	}

	public Integer getSize() {
		return capturedGroups.size();
	}

	public String getGroup(Integer which) {
		return capturedGroups.get(which);
	}

	public void resetGroup() {
		capturedGroups = new ArrayList<>();
	}

	public static void createWorkbook(ArrayList<CapturedGroup> capturedRows, FileOutputStream fileOut)
			throws IOException {
		Integer rowCounter = 0;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("result");
		for (CapturedGroup cRow : capturedRows) {
			XSSFRow row = sheet.createRow(rowCounter++);
			setRow(row, cRow);
		}
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();
	}

	protected static void setRow(XSSFRow row, CapturedGroup column) {
		for (int i = 0; i < column.getSize(); i++) {
			try {
				row.createCell(i).setCellValue(Double.parseDouble(column.getGroup(i)));
			} catch (NumberFormatException e) {
				row.createCell(i).setCellValue(column.getGroup(i));
			}
		}
	}

}

class Nano {

	static ArrayList<Double> ColumnAverages;
	static XSSFDataFormat format = null;

	final static int digitShift = 100000;

	public static void createWorkbook(ArrayList<CapturedGroup> capturedRows, FileOutputStream fileOut)
			throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		format = workbook.createDataFormat();
		sheetResult(workbook, capturedRows);
		Integer size = capturedRows.size();
		size /= 1000;
		for (int i = 0; i < size; i++) {
			sheetMarks(i, workbook, capturedRows);
		}
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();
	}

	private static void sheetResult(XSSFWorkbook workbook, ArrayList<CapturedGroup> capturedRows) {
		XSSFSheet sheet = workbook.createSheet("result");
		Integer rowCounter = 0;
		for (CapturedGroup cRow : capturedRows) {
			XSSFRow row = sheet.createRow(rowCounter++);
			CapturedGroup.setRow(row, cRow);
		}
	}

	private static void sheetMarks(Integer setNumber, XSSFWorkbook workbook, ArrayList<CapturedGroup> capturedRows) {
		System.out.println("Sheet Marks" + setNumber);
		XSSFSheet sheet = workbook.createSheet(setNumber.toString());
		Integer rowCounter = 1;
		Integer totalCol = Integer.MAX_VALUE;
		for (int i = setNumber * 1000; i < (setNumber + 1) * 1000; i++) {
			XSSFRow row = sheet.createRow(rowCounter++);
			totalCol = Nano.setRow(row, capturedRows.get(i));
		}
		setRowAverage(sheet.createRow(0), totalCol, rowCounter);
		createChart(sheet, totalCol);
	}

	protected static Integer setRow(XSSFRow row, CapturedGroup column) {
		Integer remain = column.getSize();
		Integer groupCount = 0;
		Integer colIndex = 0;
		while (remain > 0) {
			try {
				Character lastAlpha = (char) (colIndex + 'A');
				XSSFCell cell = row.createCell(colIndex++);
				cell.setCellValue(Double.parseDouble(column.getGroup(groupCount++)));
				cell.getCellStyle().setDataFormat(format.getFormat("0.00000000000"));				
				String formula = lastAlpha + "" + (row.getRowNum()+1) + "-" + lastAlpha + "1";
				cell = row.createCell(colIndex);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				// System.out.println(formula);
				cell.setCellFormula(formula);

				remain--;
			} catch (NumberFormatException e) {
				row.createCell(colIndex++).setCellValue(column.getGroup(groupCount++));
			}
		}
		return colIndex;
	}

	protected static void setRowAverage(XSSFRow row, Integer colSize, Integer rowSize) {
		for (int i = 0; i < (colSize - 1) * 2 + 1; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			Character alpha = (char) (i + 'A');
			System.out.println("AVERAGE(" + alpha + "2:" + alpha + rowSize + ")");
			cell.setCellFormula("AVERAGE(" + alpha + "2:" + alpha + rowSize + ")");
		}
	}

	private static void createChart(XSSFSheet sheet, Integer endColumn) {
		Drawing drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, endColumn, 1, endColumn+15, 30);
		Chart chart = drawing.createChart(anchor);
		ChartLegend legend = chart.getOrCreateLegend();
		legend.setPosition(LegendPosition.BOTTOM);
		LineChartData data = chart.getChartDataFactory().createLineChartData();

		// Use a category axis for the bottom axis.
		ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
		ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
		leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

		ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet,
				new CellRangeAddress(1, 1001, endColumn, endColumn));
		ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, 1001, 0, 0));

		LineChartSeries dataSeries = data.addSeries(xs, ys);
		dataSeries.setTitle("graph");
		chart.plot(data, bottomAxis, leftAxis);
	}
}
