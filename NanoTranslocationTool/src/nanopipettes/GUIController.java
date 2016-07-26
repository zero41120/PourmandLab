package nanopipettes;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
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
	public static DataProvider dP = null;
	
	public void initialize(URL location, ResourceBundle resources) {
		removeGraph();
	}

	public void TNActionClickLoadText() {
		// Prompts the user to select files
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select machine readings");
		List<File> fileList = chooser.showOpenMultipleDialog(refStage);
		
		// Execute the when the program receives files
		if (fileList != null) {
			// For each file, check the header.
			String ignoreFileTitles = "\n";
			for (File myFile : fileList) {
				try {
					FileManager.checkHeader(myFile);
				} catch (RuntimeException e) {
					ignoreFileTitles += myFile.getName() + "\n";
				}
			}
			
			// Show alert for non-compatible files.
			if (!ignoreFileTitles.equals("\n")) {
				new GUIAlertException("Unrecognized files").showAlert();
			}

			// For each file, scan the data and insert to the database.
			for (File myFile : fileList) {
				try {
					dP = new SQLDatabase();
					dP.scanData(0.0, 0.0, myFile);
				} catch (Exception e) {
					e.printStackTrace();
					new GUIAlertErrorException(e.getMessage()).showAlert();
					dP = null;
				}

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
