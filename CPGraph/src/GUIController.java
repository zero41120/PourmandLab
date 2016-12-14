
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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

interface GUIActions{
	void doRun();
}
public class GUIController implements Initializable {
	// @formatter:off
	@FXML	Button	buttonBrowse, buttonRun, buttonView, buttonTarget;
	static Stage refStage = null;
	static ArrayList<File> myFiles = new ArrayList<>();
	static File targetFile = null;
	final static Pattern ramizFormat = 
			Pattern.compile("^([0-9]+.?[0-9]*)((\\t[0-9\\-]+.?[0-9]*){2,})$");
	static String rootDir = null;
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
			primaryStage.setTitle("CP Grapher 2.0 (Mutiple reading supported)");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method lets the user select files and scan for text files.
	 */
	public void actionBrowse() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose Files");
		List<File> targetFiles = chooser.showOpenMultipleDialog(refStage);
		if (targetFiles != null) {
			System.out.println("Scan for atf files: ");
			if ((myFiles = FileManager.getTextFiles(targetFiles)) != null) {
				for (File file : myFiles) {
					System.out.println("\t+" + file.getName());
				}
			}
			if(targetFile != null && myFiles.size() > 0) disalbeButtons(false, true);
		}
	}
	
	public void actionTarget(){
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose target file");
		File tFile = chooser.showOpenDialog(refStage);
		if(tFile != null) {
			targetFile = tFile;
		}
		if(targetFile != null && myFiles != null) disalbeButtons(false, true);
	}

	/**
	 * This method lets the user select location for output and call doRun
	 */
	public void actionRun() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select location to save output");
		File selectedDirectory = directoryChooser.showDialog(refStage);
		if (selectedDirectory == null)
			return;

		rootDir = selectedDirectory.getAbsolutePath();
		System.out.println("Location to save: " + rootDir);
		
		try {
			CPMark.findTarget(targetFile);
			new CPGUIAction().doRun(rootDir, myFiles, ramizFormat);
			disalbeButtons(true, false);
		} catch (Exception e){
			new AlertController(e);
		}
	}

	public void actionView() {
		try {
			new OSDetector().openDirectory(new File(rootDir));
			new CPGUIAction().doView();
		} catch (Exception e) {
			new AlertController(e);
		}
	}
}

class AlertController {

	Alert dialog;
	AlertType type;
	String header, message;

	public AlertController(String header, String message, AlertType type) {
		dialog = new Alert(type);
		dialog.setTitle(type.toString());
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

		this.showAndWait();
	}

	public void showAndWait() {
		try {
			dialog.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
