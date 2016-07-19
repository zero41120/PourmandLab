package nanopipettes;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GUIController implements Initializable {

	// @formatter:off
	@FXML Button TNButtonLoadText;
	@FXML Button TNButtonScanMachine;
	@FXML Button TNButtonOutputText;
	@FXML Button TNButtonOutputExcel;
	@FXML ChoiceBox<String> TNChoiceBox;
	@FXML LineChart<Double, Double> TNGraph;
	@FXML ProgressBar tnProgressBar;
	@FXML Slider	 TNSliderVoltage;
	// @formatter:on

	public static Stage refStage = null;

	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}

	public void TNActionClickLoadText() {
		// Prompts the user to select files
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select machine readings");
		List<File> fileList = chooser.showOpenMultipleDialog(refStage);
		String ignoreFileTitles = "\n";

		// Execute the state machine when the program receives files
		if (fileList != null) {
			for (File myFile : fileList) {
				try {
					FileManager.checkHeader(myFile);
				} catch (RuntimeException e) {
					ignoreFileTitles += myFile.getName() + "\n";
				}
			}
			if (!ignoreFileTitles.isEmpty()) {
				String alertMessage = "System will ignore the following files:\n";
				RuntimeException toPrint = new RuntimeException(ignoreFileTitles);
				showAlertError("Error File", "Unreconized file(s)", alertMessage, toPrint);
			}
		}

	}

	public void TNActionClickScanMachine() {

	}

	public void TNActionClickOutputExcel() {

	}

	public void TNActionClickOutputText() {

	}

	public void TNActionOutputText() {

	}

	public void TNActionOutputFile() {

	}

	// ALERT METHODS
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
	 *            An exception, which will be printed on the error box.
	 */
	public static void showAlertError(String aTitle, String aHeader, String aContent, Exception ex) {
		// Creates a custom alert box.
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(aTitle);
		alert.setHeaderText(aHeader);
		alert.setContentText(aContent);

		if (ex != null) {
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
			alert.getDialogPane().setExpandableContent(expContent);
			alert.getDialogPane().setExpanded(true);
		}

		// Set expandable Exception into the dialog pane.
		alert.showAndWait();
	}

	// HELPER METHODS

	/**
	 * This is a private method that removes all data on the chart.
	 */
	private void removeGraph() {
		while (!TNGraph.getData().isEmpty()) {
			TNGraph.getData().remove(TNGraph.getData().size() - 1);
		}
	}
}
