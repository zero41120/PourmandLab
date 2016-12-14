import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class CPMark extends CPExcel {
	Double headTime = null;
	Double tailTime = null;
	ArrayList<Double> headCurr = null;
	ArrayList<Double> tailCurr = null;
	Double nearPoti = null;
	//static String[] interestHead = { "17.03", "27.2", "37.01", "47.35", "57.34", "67.25", "77.15", "87.06" };
	//static String[] interestTail = { "18.68", "28.41", "38.23", "48.66", "58.73", "68.47", "78.63", "88.45" };
	static ArrayList<String> interestHead = null;
	static ArrayList<String> interestTail = null;
	
	public CPMark() {
		headCurr = new ArrayList<>();
		tailCurr = new ArrayList<>();
	}
	
	static public void findTarget(File file) throws Exception{
		ArrayList<String> heads = new ArrayList<>();
		ArrayList<String> tails = new ArrayList<>();
		BufferedReader readBuffer = FileManager.getReader(file);
		String line = "";
		boolean toggle = true;
		while ((line = readBuffer.readLine()) != null) {
			Pattern p = Pattern.compile("([0-9]+(\\.?[0-9]*)?)");
			Matcher m = p.matcher(line);
			System.out.print(line);
			while (m.find()) {
				System.out.println(" match");
				if (toggle) heads.add(m.group(1));
				else tails.add(m.group(1));
				toggle = toggle? false:true;
			}
		}
		
		if (heads.size() != tails.size()){
			throw new RuntimeException("Target file does not provide pairs of start and end");
		} else {
			interestHead = heads;
			interestTail = tails;
			System.out.println(interestHead);
			System.out.println(interestTail);
		}
	}
	
	public boolean ready() throws IllegalArgumentException, IllegalAccessException {
		for (Field f : getClass().getDeclaredFields()) {
			if (f.get(this) == null)
				return false;
		}
		return true;
	}

	/**
	 * This method set the point of interest. When the matcher's first
	 * group(time) is the interest point, add correct data to correct field.
	 */
	static public void checkInterest(CPMark mark, Matcher matcher) {
		int rawPoti = 0;
		int auto = interestHead.contains(matcher.group(1))? 2 : 
			interestTail.contains(matcher.group(1))? 1 : 0;
		
		if (auto > 0) {
			mark.setAutoTime(Double.parseDouble(matcher.group(1)), auto);
			StringTokenizer stk = new StringTokenizer(matcher.group(2), "\t");
			try {
				while (true) {
					mark.setAutoCurr(Double.parseDouble(stk.nextToken()), auto);
					rawPoti = (int) Double.parseDouble(stk.nextToken());
				}
			} catch (NoSuchElementException e) {
				// Purposely let tokenizer break by exception to end reading.
				int rounded = (((rawPoti + 99) / 100) * 100) - 200;
				mark.setNearPoti(rounded + 0.0);
			}
		}
	}

	public Double getHeadTime() {
		return headTime;
	}

	public Double getTailTime() {
		return tailTime;
	}

	public Integer getExperimentCount() {
		return headCurr.size();
	}

	public Double getHeadCurr(Integer which) {
		return headCurr.get(which);
	}

	public Double getTailCurr(Integer which) {
		return tailCurr.get(which);
	}

	public Double getNearPoti() {
		return nearPoti;
	}

	protected void setAutoTime(Double time, int which) {
		switch (which) {
		case 1:
			tailTime = time;
			break;
		case 2:
			headTime = time;
			break;
		default:
			break;
		}
	}

	protected void setAutoCurr(Double Curr, int which) {
		switch (which) {
		case 1:
			tailCurr.add(Curr);
			break;
		case 2:
			headCurr.add(Curr);
			break;
		default:
			break;
		}
	}

	protected void setNearPoti(Double nearPoti) {
		this.nearPoti = nearPoti;
	}

	@Override
	public String toString() {
		return "CPMark [headTime=" + headTime + ", tailTime=" + tailTime + ", headCurr=" + headCurr + ", tailCurr="
				+ tailCurr + ", nearPoti=" + nearPoti + "]";
	}

}

class CPExcel {

	public static File createWorkbook(ArrayList<String> names, ArrayList<CPMark> marks, String rootDir)
			throws IOException {
		// Set up output file name.
		Date date = new Date();
		String dateTrim = date.toString().replaceAll("\\s", "_");
		dateTrim = dateTrim.toString().replaceAll(":", "-");
		File output = new File(rootDir + "/" + dateTrim + ".xlsx");
		FileOutputStream outputStream = new FileOutputStream(output);

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
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
		
		return output;
	}

	private static void createChart(XSSFSheet sheet, int endRowNum, String name, int cellOffset) {
		int readingCount = cellOffset % 15;
		while (cellOffset > 0) {
			cellOffset -= 15;
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, cellOffset + 7, endRowNum - 7, cellOffset + 15,
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
					new CellRangeAddress(endRowNum - 7, endRowNum, cellOffset + 5, cellOffset + 5));
			ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet,
					new CellRangeAddress(endRowNum - 7, endRowNum, cellOffset + 6, cellOffset + 6));

			LineChartSeries dataSeries = data.addSeries(xs, ys);
			dataSeries.setTitle(name + " NP" + readingCount);
			chart.plot(data, bottomAxis, leftAxis);
		}
	}

	/**
	 * This method sets an excel row for a CPMark reading.
	 */
	private static int setRow(XSSFRow row, CPMark mark, String name) {
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

	private static void createFormatedCell(XSSFRow row, int offset) {
		int rowNum = row.getRowNum() + 1;
		Character first = (char) (offset + 2 + 'A');
		Character second = (char) (offset + 4 + 'A');

		String difFormula = first + "" + rowNum + "-" + second + "" + rowNum;
		XSSFCell cell = row.createCell(offset + 6);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula(difFormula);
	}
}


class CPGUIAction {

	static File lastOutput = null;
	
	public void doRun(String rootDir, ArrayList<File> myFiles, Pattern p) throws Exception {
		System.out.println("hello");
		ArrayList<CPMark> marks = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();
		for (File file : myFiles) {
			BufferedReader readBuffer = FileManager.getReader(file);
			String line = "";
			CPMark mark = new CPMark();
			while ((line = readBuffer.readLine()) != null) {
				Matcher rMatcher = p.matcher(line);
				while (rMatcher.find()) {
					CPMark.checkInterest(mark, rMatcher);
					if (mark.ready()) {
						marks.add(mark);
						mark = new CPMark();
					}
				}
			}
			if (marks.size() > 0) {
				names.add(file.getName());
				System.out.println("File scan complete: " + file.getName());
			}
		}
		lastOutput = CPMark.createWorkbook(names, marks, rootDir);
	}
	
	public void doView() throws IOException{
		System.out.println(lastOutput);
		Desktop.getDesktop().open(lastOutput);
	}
}

