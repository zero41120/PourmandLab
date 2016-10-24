package edu.pourmand.soe.ucsc.BioGrapher;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import static java.lang.System.out;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import edu.pourmand.soe.ucsc.BioGrapher.StateMachine.States;
import static edu.pourmand.soe.ucsc.BioGrapher.DataProvider.dP;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.sM;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.msg;

public class GUIController implements Initializable {
	// @formatter:off
	@FXML Menu menFile, menEdit, menHelp, menLanguage;
	@FXML MenuItem meitBrowse, meitClearData, meitExportData; 
	@FXML MenuItem meitPlot1, meitPlot2, meitPlot3, meitPlot4; 
	@FXML MenuItem meitEdit, meitAbout;
	@FXML Button btnEdit, btnBrowse; 
	@FXML Button btnClearData, btnReloadStatus;
	@FXML Button btnCalibrationType1, btnCalibrationType2; 
	@FXML Button btnComparisonPlot, btnPeakCurrentPlot;
	@FXML Button btnEstimateConcentration;
	@FXML TextField txfCInputCharge; 
	@FXML TextFlow txfwReport; 
	@FXML Label labEstimateConcentration, labInputCharge; 
	@FXML TitledPane tipaConcentration, tipaDataProviderStatus;
	@FXML NumberAxis charMainxAxis, charMainyAxis;
	@FXML ProgressBar pbMainProgressBar; 
	@FXML LineChart<Number, Number> chartMainChart;
	// @formatter:on

	static Exception globalException = new Exception("Default Error");
	static Double progressCounter = 0.0;
	static Boolean displayingLinearRegression = false;
	static Boolean displayingCalibration_Type2 = false;
	static Boolean displayingCalibration_Type1 = false;
	static Boolean displayingComparision = false;
	static Boolean displayingPeakCurrent = false;
	static DataFileManager fM = new DataFileManager();
	static File myFile = null;
	static Stage refStage = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Loads all GUI texts into the GUI elements.
		changeLangauge(StateMachine.l);
		disableFunctionButtons();

	}

	/* ------------------------------------------
	 * Methods below are button action methods. 
	 * ------------------------------------------ */

	/**
	 * This is the method which changes the language of the program.
	 */
	public void actionLanguage(ActionEvent event) {
		if (event.getSource() instanceof MenuItem) {
			MenuItem temp = (MenuItem) event.getSource();
			java.util.Locale l = new java.util.Locale("en");
			System.out.println(temp.getText());
			switch (temp.getText()) {
			case "Traditional Chinese":
				l = new java.util.Locale("zh", "TW");
				break;
			case "Spanish":
				l = new java.util.Locale("es");
				break;
			case "Hindi":
				l = new java.util.Locale("hi", "IN");
				break;
			case "Russian":
				l = new java.util.Locale("ru", "RU");
				break;
			case "Turkish":
				l = new java.util.Locale("tr", "TR");
				break;
			case "Arabic":
				l = new java.util.Locale("ar");
				break;
			default:
				l = new java.util.Locale("en");
				break;
			}
			changeLangauge(l);
		}

	}

	/**
	 * This is the method which exports the data on the graph to a .txt file.
	 */
	public void actionExportCalibration() {

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select Location");
		File selectedDirectory = chooser.showDialog(refStage);
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = "Biographer_Export_" + dateFormat.format(new Date()) + ".txt";
			FileWriter writeFile = new FileWriter(new File(selectedDirectory, fileName));
			PrintWriter writerPrint = new PrintWriter(writeFile);
			writerPrint.println("Lengend Title\t" + charMainxAxis.getLabel() + "\t" + charMainyAxis.getLabel());
			for (Series<Number, Number> series : chartMainChart.getData()) {
				for (Data<Number, Number> data : series.getData()) {
					writerPrint.println(series.getName() + "\t" + new DecimalFormat("##.##").format(data.getXValue())
							+ "\t" + new DecimalFormat("##.##").format(data.getYValue()));
				}
			}
			writerPrint.flush();
			showAlertConfrimation("Export", "Success", "File exported to: \n" + selectedDirectory);
			System.out.println("Created Export"); // TODO message bundles
			writerPrint.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

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
		enableFunctionButtons();
		// Update the report on screen
		printReport();
	}

	/**
	 * This is the method which graphs the Peak Current Plot.
	 */
	public void actionPeakCurrentPlot() throws Exception {
		// Removes any graph on the screen and checks if data exists.
		removeGraph();
		printReport();
		if (!isDataExists())
			return;

		// Initializes some default value for the progress.
		displayingPeakCurrent = true;
		btnEstimateConcentration.setDisable(true);
		pbMainProgressBar.setProgress(0);
		progressCounter = 0.0;
		// Initializes some default value for the graph.
		chartMainChart.setCreateSymbols(false);
		charMainxAxis.setAutoRanging(true);
		charMainyAxis.setForceZeroInRange(false);
		charMainxAxis.setLabel("Voltage (mV)");
		charMainyAxis.setLabel("Current (nA)");

		// Creates multiple threads to get the data from collection.
		for (DataListCollection refDataList : dP.getDataCollection()) {
			Thread seriesThreads = new Thread(() -> createSeries_Type_3(refDataList));
			seriesThreads.start();
			seriesThreads.join();
		}
	}

	public void actionCalibrationPlot_Type1() throws InterruptedException {
		// Removes any graph on the screen and checks if data exists.
		removeGraph();
		printReport();
		if (!isDataExists()) {
			return;
		}
		showAlertVoltage("Estimate", "Enter target voltage to get reference data.", "413");
		displayingCalibration_Type1 = true;
		// Enables Estimate button.
		btnEstimateConcentration.setDisable(false);
		// Initializes some default value for the progress counter.
		pbMainProgressBar.setProgress(0);
		progressCounter = 0.0;
		// Initializes some default value for the graph.
		chartMainChart.setCreateSymbols(false);
		charMainxAxis.setAutoRanging(true);
		charMainyAxis.setForceZeroInRange(false);
		charMainxAxis.setLabel("Concentration");
		charMainyAxis.setLabel("Current (nA)");

		// Creates multiple threads to get the data from collection.
		if (dP.getType1Voltage() != null) {
			for (DataListCollection refDataList : dP.getDataCollection()) {
				Thread seriesThreads = new Thread(() -> createCalibrationSeries(refDataList, 1));
				seriesThreads.start();
			}
		}
	}

	/**
	 * This is the button action method which graphs the calibration plot
	 */
	public void actionCalibrationPlot_Type2() throws InterruptedException {
		// Removes any graph on the screen and checks if data exists.
		removeGraph();
		printReport();
		if (!isDataExists()) {
			return;
		}
		displayingCalibration_Type2 = true;
		// Enables Estimate button.
		btnEstimateConcentration.setDisable(false);
		// Initializes some default value for the progress counter.
		pbMainProgressBar.setProgress(0);
		progressCounter = 0.0;
		// Initializes some default value for the graph.
		chartMainChart.setCreateSymbols(true);
		charMainxAxis.setAutoRanging(true);
		charMainyAxis.setForceZeroInRange(false);
		charMainxAxis.setLabel("Concentration");
		charMainyAxis.setLabel("Average Voltage (nA)");

		// Creates multiple threads to get the data from collection.
		for (DataListCollection refDataList : dP.getDataCollection()) {
			Thread seriesThreads = new Thread(() -> createCalibrationSeries(refDataList, 2));
			seriesThreads.start();
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
		displayingComparision = true;
		// Disables this button to prevent garbage tasks.
		btnEstimateConcentration.setDisable(true);
		// Initializes some default value for the progress counter.
		pbMainProgressBar.setProgress(0);
		progressCounter = 0.0;
		// Initializes some default value for the graph.
		chartMainChart.setCreateSymbols(false);
		charMainxAxis.setAutoRanging(false);
		charMainxAxis.setLowerBound(-1.25);
		charMainxAxis.setUpperBound(1.25);
		charMainxAxis.setLabel("Voltage (mV)");
		charMainyAxis.setLabel("Current (nA)");

		// Creates multiple threads to get the data from collection.
		for (DataListCollection refDataList : dP.getDataCollection()) {
			Thread seriesThreads = new Thread(() -> createSeries_Type_1_Comparison(refDataList));
			seriesThreads.start();
		}
	}

	/**
	 * This is the method which allows the user to edit the concentration for
	 * each file.
	 */
	public void actionEdit(ActionEvent event) {

		// TODO time range
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
		disableFunctionButtons();

		// Execute the state machine when the program receives files
		if (fileList != null) {
			removeGraph();
			btnClearData.setDisable(false);
			btnEdit.setDisable(false);
			dP.setWorkingFiles(fileList);
			sM.isDataInputted = true;
			sM.currentState = States.CALIBRATING;
			executeStateMachine();
		}

		if (dP.getDataCollection() != null) {
			// Enable the buttons if read the corresponding files
			enableFunctionButtons();
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
				msg.getString("<GUITEXT>HeaderConfirm_LoadPathFile"), //
				msg.getString("<GUITEXT>ContentConfirm_ClearData"));
		sM.isAlertClearDataComirmed = isConfirmed;

		// Disables the button and clears all data in the provider.
		if (isConfirmed) {
			this.removeGraph();
			pbMainProgressBar.setProgress(0);
			txfCInputCharge.setText("");
			disableFunctionButtons();
			executeStateMachine();
			printReport();
		}
	}

	/**
	 * This is the method which calculates the linear regression and estimates
	 * the concentration.
	 */
	public void actionEstimateConcentration() {
		// Calculates linear regression
		if (!displayingLinearRegression) {
			chartMainChart.getData().add(calculateLinearRegression());
			displayingLinearRegression = true;
		}

		// Adds the estimate value on the graph and label
		if (!txfCInputCharge.getText().isEmpty()) {
			System.out.println(txfCInputCharge.getText().toString());
			try {
				Double yValue = Double.parseDouble(txfCInputCharge.getText().toString());
				XYChart.Series<Number, Number> estimatedSeries = new XYChart.Series<Number, Number>();
				XYChart.Data<Number, Number> estimatedData = new XYChart.Data<Number, Number>(
						getEstimatedXvalue(yValue), yValue);
				String estimatedString = "(" + new DecimalFormat("##.##").format(getEstimatedXvalue(yValue)) + " : "
						+ yValue + ")";
				Text text = new Text(estimatedString);
				text.setFill(Color.RED);
				text.setTranslateY(text.getLayoutBounds().getHeight() / 2);
				estimatedData.setNode(text);
				estimatedSeries.setName("Est:" + new DecimalFormat("##.##").format(getEstimatedXvalue(yValue)) + " : "
						+ txfCInputCharge.getText());
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

	public static void showAlertVoltage(String aTitle, String aHeader, String aContent) {
		// Creates a custom dialog and the fields.
		Dialog<List<Double>> dialog = new Dialog<>();
		dialog.setTitle(aTitle);
		dialog.setHeaderText(aHeader);
		dialog.setContentText(aContent);
		HBox rows = new HBox();
		Boolean[] agreement = new Boolean[1];
		agreement[0] = false;
		NumberTextField concentrationInputField = new NumberTextField();
		concentrationInputField.setPromptText("-1< voltage < 1");

		// Sets the custom button types and adds to the dialog.
		ButtonType assignType = new ButtonType("Assign", ButtonData.OK_DONE);
		ButtonType cancelType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(assignType, cancelType);

		// Gets the reference of the buttons.
		Node btnAssign = dialog.getDialogPane().lookupButton(assignType);

		concentrationInputField.textProperty().addListener((observable, oldValue, newValue) -> {
			agreement[0] = new Boolean(!newValue.trim().isEmpty());
			btnAssign.setDisable(!agreement[0]);
		});

		btnAssign.setDisable(!agreement[0]);

		// Loads everything into the dialog.
		rows.getChildren().add(concentrationInputField);
		dialog.getDialogPane().setContent(rows);

		// When a button is clicked
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == assignType) {

				Double concentration = Double.parseDouble(concentrationInputField.getText());
				dP.setType1Concetration(concentration);
				// Converts the result to a list isPresent().
				List<Double> returnConcentration = new ArrayList<>();
				returnConcentration.add(Double.parseDouble(concentrationInputField.getText()));
				return returnConcentration;

			} else if (dialogButton == cancelType) {
				dP.setType1Concetration(null);
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
		NumberTextField[] concentrationInputField;
		try {
			concentrationInputField = new NumberTextField[dP.getDataCollection().size()];

		} catch (Exception e) {
			return;
		}

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

	private void changeLangauge(Locale l) {
		msg = ResourceBundle.getBundle("edu.pourmand.soe.ucsc.BioGrapher/StringBundles/SBundle", l);
		// Upper 5 buttons
		btnBrowse.setText(msg.getString("<GUITEXT>ButtonBrowse"));
		btnComparisonPlot.setText(msg.getString("<GUITEXT>ButtonComparision"));
		btnCalibrationType1.setText(msg.getString("<GUITEXT>ButtonCalibration_Type1"));
		btnCalibrationType2.setText(msg.getString("<GUITEXT>ButtonCalibration_Type2"));
		btnPeakCurrentPlot.setText(msg.getString("<GUITEXT>ButtonPeakCurrentPlot"));

		// Lower 4 buttons
		btnEstimateConcentration.setText(msg.getString("<GUITEXT>ButtonEstimateConcentration"));
		btnReloadStatus.setText(msg.getString("<GUITEXT>ButtonReload"));
		btnEdit.setText(msg.getString("<GUITEXT>ButtonEdit"));
		btnClearData.setText(msg.getString("<GUITEXT>ButtonClearData"));

		// Menu bar
		menFile.setText(msg.getString("<GUITEXT>MenuFile"));
		menEdit.setText(msg.getString("<GUITEXT>MenuEdit"));
		menHelp.setText(msg.getString("<GUITEXT>MenuHelp"));
		menLanguage.setText(msg.getString("<GUITEXT>MenuLanguage"));

		// Menu items
		meitBrowse.setText(btnBrowse.getText());
		meitClearData.setText(btnClearData.getText());
		meitExportData.setText(msg.getString("<GUITEXT>MenuItemExportData"));
		meitPlot1.setText(btnComparisonPlot.getText());
		meitPlot2.setText(btnCalibrationType1.getText());
		meitPlot3.setText(btnCalibrationType2.getText());
		meitPlot4.setText(btnPeakCurrentPlot.getText());
		meitEdit.setText(btnEdit.getText());
		meitAbout.setText(msg.getString("<GUITEXT>MenuItemAbout"));

		// Other
		labEstimateConcentration.setText(msg.getString("<GUITEXT>LabelEstimateConcentration"));
		labInputCharge.setText(msg.getString("<GUITEXT>LabelInputCharge"));
		tipaConcentration.setText(msg.getString("<GUITEXT>TitledPaneConcentration"));
		tipaDataProviderStatus.setText(msg.getString("<GUITEXT>TitledPaneDataProviderStatus"));
	}

	/**
	 * This is a private method that will be ran by multiple tread. This method
	 * gets data from the reference data collection and adds it into the series.
	 * The program will add the series into the chart using runLater to run it
	 * on the main thread.
	 */
	private void createCalibrationSeries(DataListCollection refCollection, Integer whichType) {
		// Reject other type
		if (!refCollection.getType().equals(whichType))
			return;

		// Creates the series for the plots on the chart.
		SeriesEssential mySeries = new SeriesEssential(refCollection);

		// Gets data from the reference file and updates the progress bar.
		switch (whichType) {
		case 1:
			seriesSetCalibrationType_1(refCollection, mySeries);
			break;
		case 2:
			seriesSetCalibrationType_2(refCollection, mySeries);
			break;
		default:
			throw new RuntimeException("Calibration unknow type: " + whichType);
		}

		// Add the series in correct thread when done.
		Platform.runLater(() -> {
			chartMainChart.getData().add(mySeries.series);
		});

	}

	private void seriesSetCalibrationType_2(DataListCollection refCollection, SeriesEssential mySeries) {

		// Gets data from time ranged from 40~45.
		Double sampleSize = 0.0, sampleTotal = 0.0, fileSize = dP.getFileSize(2);

		for (AutoDataType autoData : refCollection.getListAuto()) {
			if (!(autoData instanceof DataType_2)) {
				throw new RuntimeException("Fail to convert autotype");
			}
			DataType_2 refType_2 = (DataType_2) autoData;
			if (refType_2.getTime() > 40.0 && refType_2.getTime() < 45) {
				sampleSize++;
				sampleTotal += refType_2.getAverageVol();
			}
			synchronized (progressCounter) {
				pbMainProgressBar.setProgress(progressCounter++ / fileSize);
			}
		}
		NumberFormat f = new DecimalFormat("##.##");
		Double xValue = refCollection.getConcentration();
		Double yValue = sampleTotal / sampleSize;
		;
		String nameString = xValue + " : " + f.format(yValue);

		System.out.println(refCollection.getFileTitle() + "(" + nameString + ")");
		mySeries.setData(xValue, yValue);
		mySeries.setName(nameString);
		mySeries.setTag("(" + nameString + ")");
		// text.setTranslateY(-10 + text.getLayoutBounds().getHeight() / 2);
		// text.setTranslateX(45);
	}

	private void seriesSetCalibrationType_1(DataListCollection refCollection, SeriesEssential mySeries) {
		// Gets data from the reference file and updates the progress bar.
		Double filesize = dP.getFileSize(1);
		NumberFormat f = new DecimalFormat("##.##");

		for (AutoDataType autoType : refCollection.getListAuto()) {
			if (!(autoType instanceof DataType_1)) {
				throw new RuntimeException("Fail to convert to type 1");
			}
			DataType_1 dataType_1 = (DataType_1) autoType;
			if (dataType_1.getVoltage() == dP.getType1Voltage()) {
				Double xValue = refCollection.getConcentration();
				Double yValue = dataType_1.getCurrnet();
				mySeries.setData(xValue, yValue);
				mySeries.setTag("(" + xValue + " : " + f.format(yValue) + ")");
				mySeries.setName(mySeries.valueTag);
				System.out.println(mySeries.valueTag);
			}
			synchronized (progressCounter) {
				pbMainProgressBar.setProgress(progressCounter++ / filesize);
			}
		}
	}

	/**
	 * This is a private method that will be ran by multiple tread. This method
	 * gets data from the reference data collection and adds it into the series.
	 * The program will add the series into the chart using runLater to run it
	 * on the main thread.
	 * 
	 * @param refCollection
	 *            DataListCollection witch contains the data.
	 */
	private void createSeries_Type_1_Comparison(DataListCollection refCollection) {
		// Rejects type other than 1
		if (!refCollection.getType().equals(1)) {
			return;
		}

		// Creates the series for the plots on the chart.
		final XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		series.setName(refCollection.getConcentration() + " : " + refCollection.getFileTitle());
		XYChart.Data<Number, Number> myData = new XYChart.Data<>();
		Double filesize = dP.getFileSize(1);

		// Gets data from the reference file and updates the progress bar.
		for (AutoDataType auto : refCollection.getListAuto()) {
			DataType_1 refType_1 = (DataType_1) auto;
			myData = new XYChart.Data<Number, Number>(refType_1.getVoltage(), refType_1.getCurrnet());
			series.getData().add(myData);
			synchronized (progressCounter) {
				pbMainProgressBar.setProgress(progressCounter++ / filesize);
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
	 * TODO
	 * 
	 * @param refCollection
	 */
	private void createSeries_Type_3(DataListCollection refCollection) {
		// Rejects type;
		if (refCollection.getType() != 3) {
			return;
		}

		// Creates the series for the plots on the chart.
		final XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		XYChart.Data<Number, Number> myData = new XYChart.Data<Number, Number>();
		Double newCurrent = -Double.MAX_VALUE, oldCurrent = 0.0, omnCurrent = 0.0, nmoCurrent, lastCurrentChange = 0.0;
		Double xEP = -1000.0, de = 1.0, nu = 0.0, average = 0.0;
		Double filesize = dP.getFileSize(3);
		for (AutoDataType auto : refCollection.getListAuto()) {
			DataType_3 refType_3 = (DataType_3) auto;
			oldCurrent = newCurrent;
			newCurrent = refType_3.getCurrent();
			omnCurrent = oldCurrent - newCurrent;
			nmoCurrent = newCurrent - oldCurrent;
			lastCurrentChange = nmoCurrent > 15000 ? nmoCurrent : 0.0;
			if (omnCurrent > 1000) { // small gap
				nu += omnCurrent;
				de++;
			}
			if (lastCurrentChange != 0.0) { // big gap
				nu = 0.0;
				xEP += 200;
			}

			if (de == 4.0) {
				if (omnCurrent > 60000.0) {
					break;
				}
				average = nu / de;
				myData = new XYChart.Data<Number, Number>(xEP, average);
				String value = "(" + xEP + " : ";
				value += new DecimalFormat("##.##").format(average) + ")";
				Text text = new Text(value);
				text.setTranslateY(-10 + text.getLayoutBounds().getHeight() / 2);
				text.setTranslateX(45);
				myData.setNode(text);
				series.getData().add(myData);
				de = 0.0;
			}

			synchronized (progressCounter) {
				pbMainProgressBar.setProgress(progressCounter++ / filesize);
			}
		}

		series.setName(refCollection.getConcentration() + " : " + refCollection.getFileTitle());

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
			btnCalibrationType2.setDisable(true);
			return false;
		}
		return true;
	}

	private void enableFunctionButtons() {
		if (dP.getDataCollection() != null) {
			pbMainProgressBar.setProgress(progressCounter = 0.0);
			btnClearData.setDisable(false);
			btnEdit.setDisable(false);
			for (DataListCollection refDataCollection : dP.getDataCollection()) {
				// Collection has type1, then enables the Comparison button
				if (btnComparisonPlot.isDisable() && refDataCollection.getType() == 1) {
					btnComparisonPlot.setDisable(false);
					btnCalibrationType1.setDisable(false);
				}
				// Collection has type2, then enables the Calibration button
				if (btnCalibrationType2.isDisable() && refDataCollection.getType() == 2) {
					btnCalibrationType2.setDisable(false);
				}
				// Collection has type3, then enables the Peak Current button
				if (btnPeakCurrentPlot.isDisabled() && refDataCollection.getType() == 3) {
					btnPeakCurrentPlot.setDisable(false);
				}
			}

		}
	}

	private void disableFunctionButtons() {
		removeGraph();
		btnEstimateConcentration.setDisable(true);
		btnCalibrationType1.setDisable(true);
		btnCalibrationType2.setDisable(true);
		btnComparisonPlot.setDisable(true);
		btnPeakCurrentPlot.setDisable(true);
		btnClearData.setDisable(true);
		btnEdit.setDisable(true);
	}

	/**
	 * This is a private method that removes all data on the chart.
	 */
	private void removeGraph() {
		displayingCalibration_Type2 = false;
		displayingCalibration_Type1 = false;
		displayingPeakCurrent = false;
		displayingComparision = false;
		displayingLinearRegression = false;
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
	 * 
	 * @return the XYChart Series with linear regression.
	 */
	private XYChart.Series<Number, Number> calculateLinearRegression() {

		ObservableList<Series<Number, Number>> series = chartMainChart.getData();
		List<Double> xValue = new ArrayList<Double>();
		List<Double> yValue = new ArrayList<Double>();
		for (Series<Number, Number> s : series) {
			xValue.add(Double.parseDouble(s.getData().get(0).getXValue().toString()));
			yValue.add(Double.parseDouble(s.getData().get(0).getYValue().toString()));
		}

		Integer elementCount = xValue.size();
		Double xMax = Collections.max(xValue), xMin = Collections.min(xValue);

		Double ySum = 0.0, xSum = 0.0, xSquSum = 0.0, ySquSum = 0.0;
		Double yIntersect = 0.0, slope = 0.0, correlation = 0.0;
		Double a = 0.0, b = 0.0, c = 0.0, d = 0.0, e = 0.0, f = 0.0, n = 0.0, m = 0.0;

		for (int i = 0; i < elementCount; i++) {
			a += (xValue.get(i) * yValue.get(i));
			ySum += yValue.get(i);
			xSum += xValue.get(i);
			xSquSum += xValue.get(i) * xValue.get(i);
			ySquSum += yValue.get(i) * yValue.get(i);
		}
		a *= elementCount;
		b = ySum * xSum;
		c = elementCount * xSquSum;
		d = xSum * xSum;
		slope = (a - b) / (c - d);

		e = ySum;
		f = slope * xSum;
		yIntersect = (e - f) / elementCount;

		n = a - b;
		n *= n;
		m = (elementCount * xSquSum - d) * (elementCount * ySquSum - e * e);
		correlation = n / m;
		// Line equation y = slope * x + yIntersect;
		// Correlation R^2 = [(count * xySum - xSumySum)^2]/[(count * xSqlSum -
		// xSumxSum)(count * y^2sum - ySUmySum)]

		dP.setYIntersect(yIntersect);
		dP.setSlope(slope);
		XYChart.Series<Number, Number> line = new Series<>();

		line.getData().add(new XYChart.Data<Number, Number>(xMin, slope * xMin + yIntersect));
		line.getData().add(new XYChart.Data<Number, Number>(xMax, slope * xMax + yIntersect));

		String slopeString = String.format("y = %.2fx + ", slope);
		String yIntersectString = String.format("%.2f", yIntersect);
		String correlationString = String.format("\nR² = %.6f", correlation);
		line.setName(slopeString + yIntersectString + correlationString);
		return line;
	}

	private Double getEstimatedXvalue(Double yValue) {
		return (yValue - dP.getYIntersect()) / dP.getSlope();
	}

	public static void executeStateMachine() {

		// State Machine not running.
		if (!sM.running) {
			GUIController.showAlertError(//
					msg.getString("<GUITEXT>TitleError"), //
					msg.getString("<GUITEXT>HeaderError"), //
					msg.getString("<GUITEXT>ContentError"), //
					globalException);
			return;
		}
		try {

			switch (sM.currentState) {
			case BEGINNING:
			/*
			 * If user selects to load path file, 
			 * state machine starts to calibrate BioTemp.bgt, 
			 * otherwise redirect to main screen state.
			 */
			{
				out.print("\n---Beginning---\n");
				if (sM.isAlertLoadPathConfirmed) {
					myFile = fM.getFilePath();
					out.print("<Notice>Load successed");
					sM.currentState = States.CALIBRATING;
				} else {
					fM.deletePathFile();
					dP.resetProviderAndKeepData(false);
					sM.currentState = States.DISPLAYING;
				}
				executeStateMachine();
				break;
			}

			case CALIBRATING:
			/*
			 * CALIBRATING
			 * A five-stage process.
			 * Refer to each stage in the code.
			 */
			{
				/*
				 * Stage 1:
				 * Check the existence of the inputting file.
				 * If it exists, process CalibatingSignature.
				 */
				out.print("\n---InputtingFile---\n");
				if (sM.isDataInputted) {
					myFile = dP.getNextWorkingFile();
					if (myFile != null) {
						out.print("<Notice>Load successed: ");
						out.println(myFile.getPath());
					} else {
						out.println("<Notice>Loading failed or reach the end of file queue.");
						sM.isDataInputted = false;
						sM.currentState = States.SAVING;
						dP.resetProviderAndKeepData(true);
						executeStateMachine();
						break;
					}
				}
				/*
				 * Stage 2:
				 * Check the signature of the inputting file.
				 * If it exists, process CalibatingVariable.
				 */
				out.print("\n---CalibatingSignature---\n");
				dP.setCurrentType(myFile);
				if (dP.isValidType()) {
					// Good signature.
					out.print("<Notice>Data type calibration successed.");
					out.println("(" + dP.getCurrentType() + ")");
				} else {
					// Bad signature.
					out.println("<Error>Data does not contain a correct signature.");
					executeStateMachine();
					break;
				}
				/*
				 * Stage 3:
				 * Check the variable of the inputting file.
				 * If it all data are valid, process CalibatingRemaining.
				 */
				out.print("\n---CalibatingVariable---\n");
				dP.extractVariable(myFile);
				if (dP.isValidVariable()) {
					// Good signature, good data.
					out.print("<Notice>Extracted Variable type: ");
					out.print("(" + dP.getCurrentType() + ")\t\t");
					out.print("Reamining files:");
					out.println(dP.getRemainingFileSize());
				} else {
					// Good signature, bad data.
					out.println("<Error>Data is damaged");
					executeStateMachine();
					break;
				}

				/*
				 * Stage 4:
				 * Check the remaining files.
				 * If remaining >0, process InputtingFile again.
				 */
				out.print("\n---CalibatingRemaining---\n");
				out.println("Remaining:" + dP.getRemainingFileSize());
				if (dP.getRemainingFileSize() > 0) {
					sM.currentState = States.CALIBRATING;
					sM.isDataInputted = true;
					executeStateMachine();
					break;
				} else {
					sM.currentState = States.SAVING;
					sM.isDataInputted = false;
					executeStateMachine();
					break;
				}
			}

			case SAVING:
				/*
				 * Stage 5:
				 * All files are loaded. Prompt the user to enter concentration for each file.
				 * If we load from previous stage, 
				 * we will graph them automatically.
				 * Goto DISPLAYING state.
				 */

				GUIController.showAlertConcentration(//
						msg.getString("<GUITEXT>TitleConcentration"), //
						msg.getString("<GUITEXT>HeaderConcentration"), //
						msg.getString("<GUITEXT>ContentConcentration"));
				if (sM.isAlertLoadPathConfirmed) {
					sM.isAlertClearDataComirmed = false;
				}
				fM.savePath(dP);
				sM.currentState = States.DISPLAYING;
				dP.resetProviderAndKeepData(true);
				executeStateMachine();
				break;

			case DISPLAYING:
			/*
			* MainScreen 
			* 1. Activate buttons 
			* 2. Calculate expected concentration 
			* 3. Display graph Goto clear layout when graph button is clicked
			*/
			{
				out.print("\n---MainScreen---\n");
				out.println(dP.getReport());
				if (sM.isAlertClearDataComirmed) {
					dP.resetProviderAndKeepData(false);
					fM.deletePathFile();
					sM.isAlertClearDataComirmed = false;
					executeStateMachine();
				}
				break;
			}

			default:
				out.println("Something Wrong");
				break;

			} // switch

		} catch (Exception e) {
			globalException = e;
			e.printStackTrace();
			GUIController.showAlertError(//
					msg.getString("<GUITEXT>TitleError"), //
					msg.getString("<GUITEXT>HeaderError"), //
					msg.getString("<GUITEXT>ContentError"), //
					globalException);
		}
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
		if (text.equals("-")) {
			return this.getText().length() > 0 ? false : true;
		}
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

class SeriesEssential {

	final XYChart.Series<Number, Number> series;
	XYChart.Data<Number, Number> myData;
	String valueTag = "No value";
	String name = "No name";

	public SeriesEssential(DataListCollection refCollection) {
		series = new XYChart.Series<Number, Number>();
		myData = new XYChart.Data<>();
	}

	public void setName(String toSet) {
		name = toSet;
		series.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setTag(String toSet) {
		valueTag = toSet;
		Text text = new Text(valueTag);
		text.setTranslateY(text.getLayoutBounds().getHeight() / 2);
		myData.setNode(text);
	}

	public void setData(Number x, Number y) {
		myData = new XYChart.Data<Number, Number>(x, y);
		series.getData().add(myData);
	}

}
