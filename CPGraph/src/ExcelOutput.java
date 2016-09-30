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
		Date date = new Date();
		String dateTrim = date.toString().replaceAll("\\s", "_");
		dateTrim = dateTrim.toString().replaceAll(":", "-");

		FileOutputStream fileOut = new FileOutputStream(rootDir + "/" + dateTrim + ".xlsx");

		Integer rowCounter = 0;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("result");
		int counter = 0, nameIndex = 0;
		for (CPMark mark : marks) {
			XSSFRow row = sheet.createRow(rowCounter++);
			setRow(row, mark, names.get(nameIndex));
			counter++;
			if (counter % 8 == 0) {
				createChart(sheet, row.getRowNum(), names.get(nameIndex++));
			}
		}

		System.out.println("End marks");
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();

	}

	void setRow(XSSFRow row, CPMark mark, String name) {
		row.createCell(0).setCellValue(name);
		row.createCell(1).setCellValue(mark.getHeadTime());
		row.createCell(2).setCellValue(mark.getHeadCurr());
		row.createCell(3).setCellValue(mark.getTailTime());
		row.createCell(4).setCellValue(mark.getTailCurr());
		row.createCell(5).setCellValue(mark.getNearPoti());
		createFormatedCell(row);
	}

	void createFormatedCell(XSSFRow row) {
		int rowNum = row.getRowNum() + 1;
		String difFormula = "C" + rowNum + "-E" + rowNum;
		XSSFCell cell = row.createCell(6);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula(difFormula);
	}

	void createChart(XSSFSheet sheet, int endRowNum, String name) {
		Drawing drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 7, endRowNum-7, 15, endRowNum);
		
		Chart chart = drawing.createChart(anchor);
		ChartLegend legend = chart.getOrCreateLegend();
		legend.setPosition(LegendPosition.TOP_RIGHT);

		LineChartData data = chart.getChartDataFactory().createLineChartData();
		
		// Use a category axis for the bottom axis.
		ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
		ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
		leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

		ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet,
				new CellRangeAddress(endRowNum-7, endRowNum, 5, 5));
		ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet,
				new CellRangeAddress(endRowNum-7, endRowNum, 6, 6));
		
		LineChartSeries dataSeries = data.addSeries(xs, ys);
		dataSeries.setTitle(name);		
		
		chart.plot(data, bottomAxis, leftAxis);
	}
}
