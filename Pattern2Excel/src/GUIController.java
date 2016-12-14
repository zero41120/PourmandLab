
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GUIController implements Initializable {
	// @formatter:off
	@FXML	Button buttonBrowse, buttonRun, buttonView;
	static	Stage refStage = null;
	static	String rootDir = null;
	static	ArrayList<File> myFiles = new ArrayList<>();
	static 	File targetNumberFile = null;
	final static Pattern ramizFormat = 
			Pattern.compile("^([0-9]+.?[0-9]+)\\t([0-9\\-]+.?[0-9]+)\\t([0-9\\-]+.?[0-9]+)$");
	final static Pattern geoFormat = 
			Pattern.compile("^(-?[0-9]+.[0-9]*e[-+][0-9]*), (-?[0-9].[0-9]*e[-+][0-9]*)$");
	// @formatter:on

	/**
	 * Disables the buttons.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		disalbeButtons(true, true);
	}

	/**
	 * This method disable Generate and View button.
	 */
	private void disalbeButtons(Boolean disableRun, Boolean disableView) {
		buttonRun.setDisable(disableRun);
		buttonView.setDisable(disableView);
	}

	/**
	 * This method load the xml file and shows it.
	 */
	public static boolean createMainScreen(Stage primaryStage) {
		try {
			System.out.println("Program starts");
			Parent root = FXMLLoader.load(Main.class.getResource("main.fxml"));
			primaryStage.setTitle("Pattern alpha");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method lets the user select text files.
	 * 
	 * @see FileManager
	 */
	public void actionBrowse() {
		System.out.println("Action Browse");
		myFiles = null;
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose Files");
		List<File> textFiles = chooser.showOpenMultipleDialog(refStage);
		myFiles = FileManager.getTextFiles(textFiles);
		if (myFiles != null) {
			disalbeButtons(false, true);
		}
	}

	/**
	 * This method lets the user choose the directory to save the processed
	 * files
	 * 
	 * @throws IOException
	 */
	public void actionRun() throws IOException {
		System.out.println("Action Run");
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select location to save output");
		File selectedDirectory = directoryChooser.showDialog(refStage);
		if (selectedDirectory == null) {
			rootDir = null;
			disalbeButtons(false, true);
		} else {
			rootDir = selectedDirectory.getAbsolutePath();
			for (File file : myFiles) {
				Platform.runLater(() -> {
					try {
						ArrayList<CapturedGroup> myRows = scanLines(file, geoFormat);
						String outputName = rootDir + "/" + FileManager.getName(file) + ".xlsx";
						FileOutputStream fileOut = new FileOutputStream(outputName);
						Nano.createWorkbook(myRows, fileOut);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
			disalbeButtons(true, false);
		}
	}

	/**
	 * This method scans each line and insert matched group into a CapturedGroup
	 * instance. CapturedGroup is then insert into an array list for reference.
	 * 
	 * @param file
	 *            The file to scan lines
	 * @return Array of capturedGroups
	 */
	private ArrayList<CapturedGroup> scanLines(File file, Pattern pattern) {
		ArrayList<CapturedGroup> myRows = new ArrayList<>();
		try (BufferedReader reader = FileManager.getReader(file)) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				Matcher m = pattern.matcher(line);
				while (m.find()) {
					CapturedGroup myColumn = new CapturedGroup(m);
					myRows.add(myColumn);
				}
			}
			return myRows;

		} catch (Exception e) {
			AlertController alert = new AlertController(e);
			alert.showAndWait();
			return null;
		}
	}

	/**
	 * This method open the root directory.
	 * 
	 * @see OSDetector
	 */
	public void actionView() {
		File file = new File(rootDir);
		try {
			OSDetector.openDirectory(file);
		} catch (Exception e) {
			AlertController alert = new AlertController(e);
			alert.showAndWait();
		}
	}
}

class AlertController {

	Alert dialog;
	AlertType type;
	String header, message;

	public AlertController(String header, String message, AlertType type) {
		dialog = new Alert(type);
		dialog.setTitle("Information");
		dialog.setHeaderText(header);
		dialog.setContentText(message);
	}

	public AlertController(Exception e) {
		e.printStackTrace();
		dialog = new Alert(AlertType.ERROR);
		dialog.setTitle("Error");
		dialog.setHeaderText("Java exception");
		dialog.setContentText("Something wrong");

		// Gets the exception and loads it into the writer.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		Label label = new Label("The exception stacktrace was:");

		// Adds the exception the expandable text area.
		TextArea textArea = new TextArea(sw.toString());
		textArea.setEditable(true);
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
		dialog.getDialogPane().setExpandableContent(expContent);
	}

	public void showAndWait() {
		try {
			dialog.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}