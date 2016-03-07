package edu.pourmand.soe.ucsc.BioGrapher;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.text.DecimalFormat;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import edu.pourmand.soe.ucsc.BioGrapher.StateMachine.States;
import static edu.pourmand.soe.ucsc.BioGrapher.DataProvider.dP;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.sM;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.msg;
import static edu.pourmand.soe.ucsc.BioGrapher.Main.refStage;
import static edu.pourmand.soe.ucsc.BioGrapher.Main.main;

public class GUIController implements Initializable {
	// @formatter:off
	@FXML Button btnEdit;
	@FXML Button btnBrowse;
	@FXML Button btnClearData;
	@FXML Button btnCalibrationPlot;
	@FXML Button btnComparisonPlot;
	@FXML Button btnEstimateConcentration;
	@FXML Button btnReloadStatus;
	@FXML TextField txfCInputCharge;
	@FXML TextFlow txfwReport;
	@FXML Label labEstimateConcentration;
	@FXML NumberAxis charMainxAxis;
	@FXML NumberAxis charMainyAxis;
	@FXML ProgressBar pbMainProgressBar;
	@FXML LineChart<Number, Number> chartMainChart;
	// @formatter:on

	static Double progressCounter = 0.0;
	static Boolean calculatedLinearRegression = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Loads all GUI texts into the GUI elements.
		btnCalibrationPlot.setText(msg.getString("<GUITEXT>ButtonCalibration"));
		btnComparisonPlot.setText(msg.getString("<GUITEXT>ButtonComparision"));
		btnClearData.setText(msg.getString("<GUITEXT>ButtonClearData"));
		btnBrowse.setText(msg.getString("<GUITEXT>ButtonBrowse"));
		btnEdit.setText(msg.getString("<GUITEXT>ButtonEdit"));

		// Disable the buttons.
		btnEstimateConcentration.setDisable(true);
		btnCalibrationPlot.setDisable(true);
		btnComparisonPlot.setDisable(true);
		btnClearData.setDisable(true);
		btnEdit.setDisable(true);
	}

	/* ------------------------------------------
	 * Methods below are button action methods. 
	 * ------------------------------------------ */

	/**
	 * This is the method that reloads the program when the reload button is
	 * clicked. This is a temporary solution to the issue of the state machine
	 * not able to call any method in the GUI controller.
	 * 
	 * TODO make the main program automatically reload.
	 */
	public void actionReloadStatus() {
		// Enables buttons if data provider has data
		this.removeGraph();
		pbMainProgressBar.setProgress(0);
		txfCInputCharge.setText("");
		labEstimateConcentration.setText("");
		btnEstimateConcentration.setDisable(true);
		calculatedLinearRegression = false;
		if (dP.getDataCollection() != null) {
			pbMainProgressBar.setProgress(progressCounter = 0.0);
			btnClearData.setDisable(false);
			btnEdit.setDisable(false);
			for (DataListCollection refDataCollection : dP.getDataCollection()) {
				// Buttons are enabled, no longer need to check the provider.
				if (!btnCalibrationPlot.isDisable() && !btnComparisonPlot.isDisable())
					break;
				// Collection has type1, then enables the Comparison button
				if (btnComparisonPlot.isDisable() && refDataCollection.getListType_1() != null)
					btnComparisonPlot.setDisable(false);
				// Collection has type1, then enables the Calibration button
				if (btnCalibrationPlot.isDisable() && refDataCollection.getListType_2() != null)
					btnCalibrationPlot.setDisable(false);
			}

		}
		// Update the report on screen
		printReport();
	}

	/**
	 * This is the button action method which graphs the calibration plot
	 */
	public void actionCalibrationPlot(ActionEvent event) throws InterruptedException {
		// Removes any graph on the screen and checks if data exists.
		removeGraph();
		printReport();
		if (!isDataExists()) {
			return;
		}
		// Disables this button.
		btnCalibrationPlot.setDisable(true);
		btnEstimateConcentration.setDisable(false);
		// Initializes some default value for the progress counter.
		pbMainProgressBar.setProgress(0);
		progressCounter = 0.0;
		// Initializes some default value for the graph.
		chartMainChart.setCreateSymbols(true);
		/* AutoRanging will handle this.
		Double totalGap = 0.0, avarageGap = 0.0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
		for (int i = 1; i < dP.getDataCollection().size(); i++) {
			// Gets the good range for the graph.
			Double temp = dP.getDataCollection().get(i).getConcentration();
			max = temp > max ? temp : max;
			min = temp < min ? temp : min;
			Double step = Math.abs(temp - dP.getDataCollection().get(i - 1).getConcentration());
			totalGap += step;
		}
		avarageGap = totalGap / dP.getDataCollection().size();
		charMainxAxis.setLowerBound(min - avarageGap / 2);
		charMainxAxis.setUpperBound(max + avarageGap / 2);
		 */
		charMainxAxis.setAutoRanging(true);
		charMainyAxis.setForceZeroInRange(false);

		// Creates multiple threads to get the data from collection.
		for (DataListCollection refDataList : dP.getDataCollection()) {
			Thread seriesThreads = new Thread(() -> createSeries_Type_2(refDataList));
			seriesThreads.start();
			seriesThreads.join();
		}

	}

	/**
	 * This is the button action method which graphs the comparison plot
	 */
	public void actionComparisonPlot() {
		// Removes any graph on the screen and checks if data exists.
		removeGraph();
		printReport();
		if (!isDataExists()) {
			return;
		}
		// Disables this button to prevent garbage tasks.
		btnComparisonPlot.setDisable(true);
		btnEstimateConcentration.setDisable(true);
		// Initializes some default value for the progress counter.
		pbMainProgressBar.setProgress(0);
		progressCounter = 0.0;
		// Initializes some default value for the graph.
		chartMainChart.setCreateSymbols(false);
		charMainxAxis.setAutoRanging(false);
		charMainxAxis.setLowerBound(-1.25);
		charMainxAxis.setUpperBound(1.25);

		// Creates multiple threads to get the data from collection.
		for (DataListCollection refDataList : dP.getDataCollection()) {
			Thread seriesThreads = new Thread(() -> createSeries_Type_1(refDataList));
			seriesThreads.start();
		}
	}

	/**
	 * This is the method which allows the user to edit the concentration for
	 * each file.
	 */
	public void actionEdit(ActionEvent event) {
		GUIController.showAlertConcentration(//
				msg.getString("<GUITEXT>TitleConcentration"), //
				msg.getString("<GUITEXT>HeaderConcentration"), //
				msg.getString("<GUITEXT>ContentConcentration"));
	}

	/**
	 * This is the method which prompts the browsing window to the user. Files
	 * chosen by the user will be stored in the DataProvider, and the method
	 * will call the state machine to calibrate the files into useful data.
	 */
	public void actionBrowse(ActionEvent event) {
		// Prompts the user to select files
		FileChooser chooser = new FileChooser();
		chooser.setTitle(msg.getString("<GUITEXT>TitleOpenFile"));
		List<File> fileList = chooser.showOpenMultipleDialog(refStage);

		// Execute the state machine when the program receives files
		if (fileList != null) {
			removeGraph();
			btnClearData.setDisable(false);
			btnEdit.setDisable(false);
			dP.setWorkingFiles(fileList);
			sM.isDataInputted = true;
			sM.currentState = States.CALIBRATING;
			main.executeStateMachine();
		}

		if (dP.getDataCollection() != null) {
			// Enable the buttons if read the corresponding files
			for (DataListCollection refDataList : dP.getDataCollection()) {
				if (refDataList.getListType_1() != null) {
					btnComparisonPlot.setDisable(false);
				}
				if (refDataList.getListType_2() != null) {
					btnCalibrationPlot.setDisable(false);
				}
			}
		}

		// Print report on the screen
		printReport();
	}

	/**
	 * This is the method which shows the alert box to confirm data deletion. If
	 * the user clicks OK, the data in the state machine will be deleted.
	 */
	public void actionClearData(ActionEvent event) {
		// Prompts the user to confirm to clear data
		boolean isConfirmed = showAlertConfrimation(//
				msg.getString("<GUITEXT>TitleConfirm"), //
				msg.getString("<GUITEXT>HeaderConfirmLoadPathFile"), //
				msg.getString("<GUITEXT>ContentConfirmClearData"));
		sM.isAlertClearDataComirmed = isConfirmed;

		// Disables the button and clears all data in the provider.
		if (isConfirmed) {
			this.removeGraph();
			pbMainProgressBar.setProgress(0);
			txfCInputCharge.setText("");
			labEstimateConcentration.setText("");
			calculatedLinearRegression = false;

			btnEstimateConcentration.setDisable(true);
			btnCalibrationPlot.setDisable(true);
			btnComparisonPlot.setDisable(true);
			btnClearData.setDisable(true);
			btnEdit.setDisable(true);
			calculatedLinearRegression = false;
			main.executeStateMachine();
			printReport();
		}
	}

	/**
	 * This is the method which calculates the linear regression and estimates
	 * the concentration.
	 */
	public void actionEstimateConcentration() {
		// Calculates linear regression
		if (!calculatedLinearRegression) {
			chartMainChart.getData().add(calculateLinearRegression());
		}
		// Adds the estimate value on the graph and label
		if (!txfCInputCharge.getText().isEmpty()) {
			System.out.println(txfCInputCharge.getText().toString());
			try {
				Double xValue = Double.parseDouble(txfCInputCharge.getText().toString());
				XYChart.Series<Number, Number> estimatedSeries = new XYChart.Series<Number, Number>();
				XYChart.Data<Number, Number> estimatedData = new XYChart.Data<Number, Number>(xValue,
						getEstimatedYvalue(xValue));
				String estimatedString = new DecimalFormat("##.##").format(getEstimatedYvalue(xValue));
				Text text = new Text(estimatedString);
				text.setFill(Color.RED);
				text.setTranslateY(text.getLayoutBounds().getHeight() / 2);
				estimatedData.setNode(text);
				estimatedSeries.setName("Est:" + txfCInputCharge.getText() + " : " + estimatedString);
				estimatedSeries.getData().add(estimatedData);
				chartMainChart.getData().add(estimatedSeries);
				labEstimateConcentration.setText(estimatedString);
				txfCInputCharge.setText("");
			} catch (Exception e) {
				// Double.pareseDouble() might fail if user enters a invalid
				// text string.
				// Reset the text box.
				txfCInputCharge.setText("");
				labEstimateConcentration.setText("");
			}

		}
	}

	/* ------------------------------------------
	 * Methods below are alert box methods. 
	 * ------------------------------------------ */

	/**
	 * This is the method which generates a general alert message.
	 * 
	 * @param aTitle
	 *            String of the title
	 * @param aHeader
	 *            String of the header
	 * @param aContent
	 *            String of the content
	 * @return returns true if OK is selected, false otherwise.
	 */
	public static boolean showAlertConfrimation(String aTitle, String aHeader, String aContent) {
		try {
			// Creates a custom alert box.
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle(aTitle);
			alert.setHeaderText(aHeader);
			alert.setContentText(aContent);
			Optional<ButtonType> result = alert.showAndWait();
			// Returns true if OK is clicked.
			return result.get() == ButtonType.OK ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(msg.getString("<Error>AlertBoxLoadPath"));
		}

		return false;
	}

	/**
	 * This is the method which prompts the user to enter the concentration for
	 * each files. The user can only click the assign button when all field are
	 * filled. If the user clicks the cancel button, the program will try to get
	 * the concentration from data provider or assigns a zero.
	 * 
	 * When creating the text field, this method will check concentration data
	 * from last stage if we are restoring the last stage, and this method also
	 * checks the data provider when user is editing the concentration.
	 * 
	 * @param aTitle
	 * @param aHeader
	 * @param aContent
	 */
	public static void showAlertConcentration(String aTitle, String aHeader, String aContent) {
		// Creates a custom dialog and the fields.
		Dialog<List<Double>> dialog = new Dialog<>();
		dialog.setTitle(aTitle);
		dialog.setHeaderText(aHeader);
		dialog.setContentText(aContent);
		HBox rows; // Will be created in the loop.
		VBox cols = new VBox();
		NumberTextField[] concentrationInputField = new NumberTextField[dP.getDataCollection().size()];

		// Sets the custom button types and adds to the dialog.
		ButtonType assignType = new ButtonType("Assign", ButtonData.OK_DONE);
		ButtonType cancelType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(assignType, cancelType);

		// Gets the reference of the buttons.
		Node btnAssign = dialog.getDialogPane().lookupButton(assignType);
		// Node btnCancel = dialog.getDialogPane().lookupButton(cancelType);

		// Initializes the boolean array for button assign.
		Boolean[] agreement = new Boolean[dP.getDataCollection().size()];
		Arrays.fill(agreement, Boolean.FALSE);
		btnAssign.setDisable(true);

		// Sets the text fields and labels with names from DataProvider
		for (int i = 0; i < dP.getDataCollection().size(); i++) {
			// Initializes this text field.
			final int copy = i;
			concentrationInputField[i] = new NumberTextField();
			concentrationInputField[i].setPromptText(dP.getDataCollection().get(i).getFileTitle());

			// Creates a listener to enable/disable the assign button.
			concentrationInputField[i].textProperty().addListener((observable, oldValue, newValue) -> {
				agreement[copy] = !newValue.trim().isEmpty();
				btnAssign.setDisable(Arrays.asList(agreement).contains(false));
			});

			// Gets the concentration from the last stage or the data provider.
			if (dP.getWorkingConcentration() != null) {
				concentrationInputField[i].setText(dP.getWorkingConcentration().get(i).toString());
				agreement[i] = true;
			} else if (dP.getDataCollection().get(i).getConcentration() != null) {
				concentrationInputField[i].setText(dP.getDataCollection().get(i).getConcentration().toString());
				agreement[i] = true;
			}

			// Adds the label and the text field into the row,
			// then insert the row into the column
			rows = new HBox();
			rows.getChildren().add(concentrationInputField[i]);
			rows.getChildren().add(new Label(dP.getDataCollection().get(i).getFileTitle()));
			cols.getChildren().add(rows);
		} // Text fields are created.

		// Enables/Disables the assign depending on the text fields.
		btnAssign.setDisable(Arrays.asList(agreement).contains(false));

		// Loads everything into the dialog.
		dialog.getDialogPane().setContent(cols);

		// When a button is clicked
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == assignType) {
				// Inserts the concentration into the data provider.
				for (int i = 0; i < dP.getDataCollection().size(); i++) {
					Double concentration = Double.parseDouble(concentrationInputField[i].getText());
					dP.getDataCollection().get(i).setConcentration(concentration);
				}

				// Converts the result to a list isPresent().
				List<Double> returnConcentration = new ArrayList<>();
				for (TextField textField : concentrationInputField) {
					returnConcentration.add(Double.parseDouble(textField.getText()));
				}
				return returnConcentration;

			} else if (dialogButton == cancelType) {

				// Restores the old concentration or assigns 0 if missing.
				for (int i = 0; i < dP.getDataCollection().size(); i++) {
					Double concentration = dP.getDataCollection().get(i).getConcentration();
					concentration = concentration == null ? 0.0 : concentration;
					dP.getDataCollection().get(i).setConcentration(concentration);
				}
			}
			return null;
		});

		// Displays the dialog.
		Optional<List<Double>> result = dialog.showAndWait();

		// Sysouts the numbers in the text fields.
		// We can further implement new function to this.
		result.ifPresent(refList -> {
			for (Double myDouble : refList) {
				System.out.println(myDouble);
			}
		});
	}

	/**
	 * This is the method which shows the error box.
	 * 
	 * @param aTitle
	 *            String for the error title.
	 * @param aHeader
	 *            String for the error header.
	 * @param aContent
	 *            String for content which describes what happen.
	 * @param ex
	 *            Exception, usually the global exception, which will be print
	 *            on the error box.
	 */
	public static void showAlertError(String aTitle, String aHeader, String aContent, Exception ex) {
		// Creates a custom alert box.
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(aTitle);
		alert.setHeaderText(aHeader);
		alert.setContentText(aContent);

		// Gets the exception and loads it into the writer.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();
		Label label = new Label("The exception stacktrace was:");

		// Adds the exception the expandable text area.
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
	}

	/* ------------------------------------------
	 * Methods below are private helper methods.
	 * ------------------------------------------ */

	/**
	 * This is a private method that will be ran by multiple tread. This method
	 * gets data from the reference data collection and adds it into the series.
	 * The program will add the series into the chart using runLater to run it
	 * on the main thread.
	 * 
	 * @param refCollection
	 *            DataListCollection witch contains the data.
	 */
	private void createSeries_Type_1(DataListCollection refCollection) {
		// Rejects type 2.
		if (refCollection.getListType_1() == null) {
			return;
		}

		// Creates the series for the plots on the chart.
		final XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		series.setName(refCollection.getConcentration() + " : " + refCollection.getFileTitle());
		XYChart.Data<Number, Number> myData = new XYChart.Data<>();

		// Gets data from the reference file and updates the progress bar.
		for (DataType_1 refType_1 : refCollection.getListType_1()) {
			myData = new XYChart.Data<Number, Number>(refType_1.getVoltage(), refType_1.getCurrnet());
			series.getData().add(myData);
			synchronized (progressCounter) {
				pbMainProgressBar.setProgress(progressCounter++ / dP.getFileSizeType1());
			}
		}

		// Creates the label on the plots
		String value = "      (" + refCollection.getConcentration().toString() + ")";
		Text text = new Text(value);
		text.setTranslateY(text.getLayoutBounds().getHeight() / 2);
		myData.setNode(text);

		// Add the series in correct thread when done.
		Platform.runLater(() -> {
			chartMainChart.getData().add(series);
		});
	}

	/**
	 * This is a private method that will be ran by multiple tread. This method
	 * gets data from the reference data collection and adds it into the series.
	 * The program will add the series into the chart using runLater to run it
	 * on the main thread.
	 * 
	 * @param refCollection
	 */
	private void createSeries_Type_2(DataListCollection refCollection) {
		// Rejects type 1;
		if (refCollection.getListType_2() == null) {
			return;
		}

		// Gets data from time ranged from 40~45.
		Double sampleSize = 0.0, sampleTotal = 0.0;
		for (DataType_2 refType_2 : refCollection.getListType_2()) {
			if (refType_2.getTime() > 40.0 && refType_2.getTime() < 45) {
				sampleSize++;
				sampleTotal += refType_2.getAverageVol();
			}
			synchronized (progressCounter) {
				pbMainProgressBar.setProgress(progressCounter++ / dP.getFileSizeType2());
			}
		}
		Double averageVoltage = sampleTotal / sampleSize;
		System.out.println(averageVoltage + " for " + refCollection.getFileTitle());

		// Creates the series for the plots on the chart.
		final XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		XYChart.Data<Number, Number> myData = new XYChart.Data<Number, Number>(refCollection.getConcentration(),
				averageVoltage);
		series.getData().add(myData);
		series.setName(refCollection.getConcentration() + " : " + new DecimalFormat("##.##").format(averageVoltage));

		// Creates the label on the plots
		String value = "(" + refCollection.getConcentration().toString() + " : ";
		value += new DecimalFormat("##.##").format(averageVoltage) + ")";
		Text text = new Text(value);
		text.setTranslateY(-10 + text.getLayoutBounds().getHeight() / 2);
		text.setTranslateX(45);
		myData.setNode(text);

		// Add the series in correct thread when done.
		Platform.runLater(() -> {
			chartMainChart.getData().add(series);
		});

	}

	/**
	 * This is a private method that checks the existence of the data. By the
	 * design of the program, this method should always return true, otherwise
	 * there is a serious issue.
	 * 
	 * @return true if data exists.
	 */
	private boolean isDataExists() {
		if (dP.getDataCollection() == null) {
			showAlertError(msg.getString("<GUITEXT>TitleError"), //
					msg.getString("<GUITEXT>HeaderError"), //
					msg.getString("<GUITEXT>ContentError_NoDataFound"), //
					new Exception("No data found"));
			btnComparisonPlot.setDisable(true);
			btnCalibrationPlot.setDisable(true);
			return false;
		}
		return true;
	}

	/**
	 * This is a private method that removes all data on the chart.
	 */
	private void removeGraph() {
		while (!chartMainChart.getData().isEmpty()) {
			chartMainChart.getData().remove(chartMainChart.getData().size() - 1);
		}
	}

	/**
	 * This is a private method that updates the information on the main screen.
	 */
	private void printReport() {
		while (!txfwReport.getChildren().isEmpty()) {
			txfwReport.getChildren().remove(txfwReport.getChildren().size() - 1);
		}
		Text message = new Text(dP.getReport());
		message.setFont(Font.font("System", 13));
		txfwReport.getChildren().add(message);
	}

	/**
	 * This is the method what calculates the linear regression of the data.
	 * Estimated Y = Y intersect + Slope * X. \\
	 * Slope = Variance of X / Product of the average distance to the mean of X and Y \\
	 * Y intersect = averageY - averageX * slope \\
	 * 
	 * @return the XYChart Series with (0,Y intersect), (Mean of X, Mean of Y),
	 *         and (Max X, Estimated Y).
	 */
	private XYChart.Series<Number, Number> calculateLinearRegression() {

		ObservableList<Series<Number, Number>> series = chartMainChart.getData();
		List<Double> xValue = new ArrayList<Double>();
		List<Double> xNormal = new ArrayList<Double>();
		List<Double> xSquare = new ArrayList<Double>();
		List<Double> yValue = new ArrayList<Double>();
		List<Double> yNormal = new ArrayList<Double>();
		List<Double> xyNormalProduct = new ArrayList<Double>();
		Double yIntersect = 0.0;
		Double xMax = 0.0;

		for (Series<Number, Number> s : series) {
			xValue.add(Double.parseDouble(s.getData().get(0).getXValue().toString()));
			yValue.add(Double.parseDouble(s.getData().get(0).getYValue().toString()));
		}
		Double averageX = xValue.stream().mapToDouble(a -> a).average().getAsDouble();
		Double averageY = yValue.stream().mapToDouble(a -> a).average().getAsDouble();
		System.out.println(averageX);
		System.out.println(averageY);

		for (int i = 0; i < xValue.size(); i++) {
			xNormal.add(xValue.get(i) / averageX);
			xSquare.add(xNormal.get(i) * xNormal.get(i));
			yNormal.add(yValue.get(i) / averageY);
			xyNormalProduct.add(xNormal.get(i) * yNormal.get(i));
		}

		Double slope = xSquare.stream().mapToDouble(a -> a).average().getAsDouble()
				/ xyNormalProduct.stream().mapToDouble(a -> a).average().getAsDouble();
		yIntersect = averageY - averageX * slope;

		dP.setYIntersect(yIntersect);
		dP.setSlope(slope);
		xMax = Collections.max(xValue);
		XYChart.Series<Number, Number> regression = new Series<>();
		regression.getData().add(new XYChart.Data<Number, Number>(0, yIntersect));
		regression.getData().add(new XYChart.Data<Number, Number>(averageX, averageY));
		regression.getData().add(new XYChart.Data<Number, Number>(xMax, yIntersect + slope * xMax));
		regression.setName("Linear regression");

		calculatedLinearRegression = true;
		return regression;
	}

	private Double getEstimatedYvalue(Double xValue) {
		return dP.getYIntersect() + dP.getSlope() * xValue;
	}

}

class NumberTextField extends TextField {
	// Source from: http://goo.gl/FKZKKr

	@Override
	public void replaceText(int start, int end, String text) {
		if (validate(text)) {
			super.replaceText(start, end, text);
		}
	}

	@Override
	public void replaceSelection(String text) {
		if (validate(text)) {
			super.replaceSelection(text);
		}
	}

	private boolean validate(String text) {
		// Modify by Tz-Shiuan Lin
		if (text.equals(".")) {
			int counter = 0;
			for (int i = 0; i < this.getText().length(); i++) {
				counter += this.getText().charAt(i) == '.' ? 1 : 0;
				if (counter == 1) {
					return false;
				}
			}
		}
		return text.matches("[0-9.]*");

	}
}
