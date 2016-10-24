import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelOutput {

	void createWorkbook(ArrayList<String> names, ArrayList<CPMark> marks, String rootDir) throws IOException {
		// Set up output file name.
		Date date = new Date();
		String dateTrim = date.toString().replaceAll("\\s", "_");
		dateTrim = dateTrim.toString().replaceAll(":", "-");
		FileOutputStream fileOut = new FileOutputStream(rootDir + "/" + dateTrim + ".xlsx");

		// Create variables
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("result");
		Integer counter = 0, nameIndex = 0, rowCounter = 0, cellOffset = 0;
		
		// Create a row for each mark
		for (CPMark mark : marks) {
			XSSFRow row = sheet.createRow(rowCounter++);
			cellOffset = setRow(row, mark, names.get(nameIndex));
			if (++counter % 8 == 0) {
				System.out.println("Row Done: " + names.get(nameIndex));
				createChart(sheet, row.getRowNum(), names.get(nameIndex++), cellOffset);
				rowCounter += 7;
			}
		}

		// Clean up
		System.out.println("End marks");
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();

	}

	int setRow(XSSFRow row, CPMark mark, String name) {
		int readingNumber = mark.getExperimentCount(), offset = 0, count = 0;
		while (readingNumber-- > 0) {
			row.createCell(offset).setCellValue(name);
			row.createCell(offset + 1).setCellValue(mark.getHeadTime());
			row.createCell(offset + 2).setCellValue(mark.getHeadCurr(count));
			row.createCell(offset + 3).setCellValue(mark.getTailTime());
			row.createCell(offset + 4).setCellValue(mark.getTailCurr(count++));
			row.createCell(offset + 5).setCellValue(mark.getNearPoti());
			createFormatedCell(row, offset);
			offset += 15;
		}
		return offset;
	}

	void createFormatedCell(XSSFRow row, int offset) {
		int rowNum = row.getRowNum() + 1;
		Character first = (char) (offset + 2 + 'A');
		Character second = (char) (offset + 4 + 'A');

		String difFormula = first + "" + rowNum + "-" + second + "" + rowNum;
		XSSFCell cell = row.createCell(offset + 6);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula(difFormula);
	}

	void createChart(XSSFSheet sheet, int endRowNum, String name, int cellOffset) {
		int readingCount = cellOffset % 15;
		while (cellOffset > 0) {
			cellOffset -= 15;
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, cellOffset+7, endRowNum - 7, cellOffset+15,
					endRowNum + 7);
			Chart chart = drawing.createChart(anchor);
			ChartLegend legend = chart.getOrCreateLegend();
			legend.setPosition(LegendPosition.BOTTOM);
			LineChartData data = chart.getChartDataFactory().createLineChartData();

			// Use a category axis for the bottom axis.
			ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
			ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
			leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

			ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet,
					new CellRangeAddress(endRowNum - 7, endRowNum, cellOffset +5, cellOffset +5));
			ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet,
					new CellRangeAddress(endRowNum - 7, endRowNum, cellOffset +6, cellOffset +6));

			LineChartSeries dataSeries = data.addSeries(xs, ys);
			dataSeries.setTitle(name + " NP" + readingCount);
			chart.plot(data, bottomAxis, leftAxis);
		}
	}
}
