package edu.pourmand.soe.ucsc.BioGrapher;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.swing.SwingWorker;

import apple.laf.JRSUIConstants.Size;
import edu.pourmand.soe.ucsc.BioGrapher.StateMachine.States;

import static edu.pourmand.soe.ucsc.BioGrapher.DataProvider.dP;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.sM;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.msg;
import static edu.pourmand.soe.ucsc.BioGrapher.Main.globalException;
import static edu.pourmand.soe.ucsc.BioGrapher.Main.refStage;
import static edu.pourmand.soe.ucsc.BioGrapher.Main.main;
import static java.lang.System.out;

public class GUIController implements Initializable {
	// @formatter:off
	@FXML Button btnBrowse;
	@FXML Button btnClearData;
	@FXML Button btnCalibrationPlot;
	@FXML Button btnComparisonPlot;
	@FXML Button btnCalculateConcentration;
	@FXML Button btnReloadStatus;
	@FXML TextField txfCVVoltageUsed;
	@FXML TextField txfTCVVoltageUsed;
	@FXML TextField txfTCVConcentration;
	@FXML TextField txfConcentrationInput;
	@FXML TextFlow txfwReport;
	@FXML NumberAxis charMainxAxis;
	@FXML NumberAxis charMainyAxis;
	@FXML ProgressBar pbMainProgressBar;
	@FXML LineChart<Number, Number> chartMainChart;
	// @formatter:on

	static double progressSize = 0;
	static double progressCounter = 0;


	private void createSeries_Type_1(DataList refDataList) {
		if (refDataList.getListType_1() == null) {
			return;
		}
		final XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		progressSize += refDataList.getListType_1().size();
		series.setName(refDataList.getFileTitle());
		for (DataType_1 refType_1 : refDataList.getListType_1()) {
			series.getData().add(new XYChart.Data<Number, Number>(refType_1.getVoltage(), refType_1.getCurrnet()));
			pbMainProgressBar.setProgress(progressCounter++ / progressSize);
		}
		Runnable addData = () -> {
			chartMainChart.getData().add(series);
		};
		Platform.runLater(addData);
	}
	
	private void createSeries_Type_2(DataList refDataList) {
		if (refDataList.getListType_2() == null) {
			return;
		}
		final XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		progressSize += refDataList.getListType_2().size();
		series.setName(refDataList.getFileTitle());
		for (DataType_2 refType_2 : refDataList.getListType_2()) {
			series.getData().add(new XYChart.Data<Number, Number>(refType_2.getTime(), refType_2.getAverageVol()));
			pbMainProgressBar.setProgress(progressCounter++ / progressSize);
		}
		Runnable addData = () -> {
			chartMainChart.getData().add(series);
		};
		Platform.runLater(addData);
	}

	private boolean isDataExists() {
		if (dP.getMainList() == null) {
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

	private void removeGraph() {
		while (!chartMainChart.getData().isEmpty()) {
			chartMainChart.getData().remove(chartMainChart.getData().size() - 1);
		}
	}

	public void actionReloadStatus() {
		printReport();
		if (dP.getMainList() != null) {
			if (btnClearData.isDisable() && // ) {
					btnCalibrationPlot.isDisable() && //
					btnComparisonPlot.isDisable()) {
				btnClearData.setDisable(false);
				for (DataList refDataList : dP.getMainList()) {
					if (refDataList.getListType_1() != null) {
						btnComparisonPlot.setDisable(false);
					}
					if (refDataList.getListType_2() != null) {
						btnCalibrationPlot.setDisable(false);
					}
				}
			}
		}
	}

	public void actionCalibrationPlot(ActionEvent event) throws InterruptedException {
		removeGraph();
		if (!isDataExists()) {
			return;
		}
		this.printReport();
		btnCalibrationPlot.setDisable(true);
		chartMainChart.setCreateSymbols(false);
		pbMainProgressBar.setProgress(0);
		progressCounter = 0;
		progressSize = 0;
		if (!dP.getMainList().isEmpty()) {
			for (DataList refDataList : dP.getMainList()) {
				Runnable task = () -> {
					createSeries_Type_2(refDataList);
				};
				Thread seriesThreads = new Thread(task);
				seriesThreads.start();
			}
		}
	}

	public void actionComparisonPlot() {
		removeGraph();
		if (!isDataExists()) {
			return;
		}
		this.printReport();
		btnComparisonPlot.setDisable(true); // Disable this button
		chartMainChart.setCreateSymbols(false); // Hide dots
		pbMainProgressBar.setProgress(0);
		progressCounter = 0;
		progressSize = 0;
		charMainxAxis.setLowerBound(-1);
		charMainxAxis.setUpperBound(1);
		if (!dP.getMainList().isEmpty()) {
			for (DataList refDataList : dP.getMainList()) {
				createSeries_Type_1(refDataList);
				/*
				series = new XYChart.Series<Number, Number>();
				for (DataType_1 refType_1 : refDataList.getListType_1()) {
					series.getData()
							.add(new XYChart.Data<Number, Number>(//
									refType_1.getVoltage(), //
									refType_1.getCurrnet()));
				}
				series.setName(refDataList.getFileTitle());
				chartMainChart.getData().add(series);
				*/
			} 

		}
	}

	public void actionCalculateConcentration() {
		// TODO
	}

	private void printReport() {
		while (!txfwReport.getChildren().isEmpty()) {
			txfwReport.getChildren().remove(txfwReport.getChildren().size() - 1);
		}
		Text message = new Text(dP.getReport());
		message.setFont(Font.font("System", 13));
		txfwReport.getChildren().add(message);
	}

	/**
	 * This is the method which prompts the browsing window to the user. Files
	 * chosen by the user will be stored in the DataProvider, and the method
	 * will call the state machine to calibrate the files into useful data.
	 */
	public void actionBrowse(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open File");
		List<File> fileList = chooser.showOpenMultipleDialog(refStage);
		if (fileList != null) {
			removeGraph();
			btnClearData.setDisable(false);
			dP.setWorkingFiles(fileList);
			sM.isDataInputted = true;
			sM.currentState = States.CALIBRATING;
			main.executeStateMachine();
		}
		for (DataList refDataList : dP.getMainList()) {
			if (refDataList.getListType_1() != null) {
				btnComparisonPlot.setDisable(false);
			}
			if (refDataList.getListType_2() != null) {
				btnCalibrationPlot.setDisable(false);
			}
		}
		printReport();
	}

	/**
	 * This is the method which shows the alert box to confirm data deletion. If
	 * the user clicks OK, the data in the state machine will be deleted.
	 */
	public void actionClearData(ActionEvent event) {
		boolean isConfirmed = showAlertConfrimation(//
				msg.getString("<GUITEXT>TitleConfirm"), //
				msg.getString("<GUITEXT>HeaderConfirmLoadPathFile"), //
				msg.getString("<GUITEXT>ContentConfirmClearData"));
		sM.isAlertClearDataComirmed = isConfirmed;
		if (isConfirmed) {
			this.removeGraph();
			btnCalculateConcentration.setDisable(true);
			btnCalibrationPlot.setDisable(true);
			btnComparisonPlot.setDisable(true);
			btnClearData.setDisable(true);
			main.executeStateMachine();
			printReport();
		}
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
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(aTitle);
		alert.setHeaderText(aHeader);
		alert.setContentText(aContent);

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

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

	/**
	 * This is the method which generates a main window.
	 * 
	 * @param primaryStage
	 *            Stage to refer.
	 * @return True is created successfully, false otherwise.
	 */
	public static boolean createMainScreen(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource("BioGrapherUI.fxml"));
			primaryStage.setTitle(msg.getString("<GUITEXT>TitleProgram"));
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
			out.println(msg.getString("<Notice>Greeting"));
			return true;
		} catch (Exception e) {
			globalException = e;
			e.printStackTrace();
			out.println(msg.getString("<Error>LayoutCreate"));
			return false;
		}
	}

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
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle(aTitle);
			alert.setHeaderText(aHeader);
			alert.setContentText(aContent);
			Optional<ButtonType> result = alert.showAndWait();
			return result.get() == ButtonType.OK ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
			out.println(msg.getString("<Error>AlertBoxLoadPath"));
		}

		return false;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnCalibrationPlot.setDisable(true);
		btnComparisonPlot.setDisable(true);
		btnClearData.setDisable(true);
	}

}
