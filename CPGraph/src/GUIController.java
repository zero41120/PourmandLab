
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GUIController implements Initializable {
	// @formatter:off
	@FXML	Button	buttonBrowse, buttonRun, buttonView;
	static Stage refStage = null;
	static ArrayList<File> myFiles = new ArrayList<>();
	// Number.Number \t Number.Number \t Number.Number Ramiz
	final static Pattern ramizFormat = 
			Pattern.compile("^([0-9]+.?[0-9]*)((\\t[0-9\\-]+.?[0-9]*){2,})$");
	static String rootDir = null;
	// @formatter:on

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		disableElements();
	}

	/**
	 * This method disable Generate and View button.
	 */
	private void disableElements() {
		buttonRun.setDisable(true);
		buttonView.setDisable(true);
	}

	/**
	 * This method load the xml file and shows it.
	 */
	public static boolean createMainScreen(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource("main.fxml"));
			primaryStage.setTitle("CPGrapher 1.3");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
			System.out.println("Program starts");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method creates a message box that show message to the user.
	 */
	public static void showAlertInfo(String header, String message) {
		try {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Info");
			alert.setHeaderText(header);
			alert.setContentText(message);
			alert.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Alert fail.");
		}
	}

	public void actionBrowse() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose Files");
		List<File> atfFiles = chooser.showOpenMultipleDialog(refStage);
		if (atfFiles != null) {
			System.out.println("User selected some files.");
			buttonRun.setDisable(false);
			buttonView.setDisable(true);
			myFiles = FileManager.getATFFiles(atfFiles);
			for (File file : myFiles) {
				System.out.println(file.getName());
			}
			return;
		}
	}

	public void actionRun() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select location to save output");
		File selectedDirectory = directoryChooser.showDialog(refStage);
		if (selectedDirectory == null) {
			return;
		}

		rootDir = selectedDirectory.getAbsolutePath();
		System.out.println(rootDir);
		buttonRun.setDisable(true);
		buttonView.setDisable(false);

		ArrayList<CPMark> marks = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();

		for (File file : myFiles) {
			try (FileInputStream readFile = new FileInputStream(file);
					InputStreamReader readIn = new InputStreamReader(readFile, "UTF8");
					BufferedReader readBuffer = new BufferedReader(readIn);) {
				String line = "";
				CPMark mark = new CPMark();
				while ((line = readBuffer.readLine()) != null) {
					Matcher rMatcher = ramizFormat.matcher(line);
					while (rMatcher.find()) {
						setInterest(mark, rMatcher);
						if (mark.ready()) {
							System.out.println(mark);
							marks.add(mark);
							mark = new CPMark();
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (marks.size() > 0) {
				names.add(file.getName());
				System.out.println("File scan complete: " + file.getName());
			}
		}
		
		ExcelOutput out = new ExcelOutput();
		try{
		out.createWorkbook(names, marks, rootDir);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void actionView() {
		// Credit: https://goo.gl/Vtfvd3
		File file = new File(rootDir);
		try {
			if (OSDetector.isWindows()) {
				Runtime.getRuntime()
						.exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath() });
			} else if (OSDetector.isLinux() || OSDetector.isMac()) {
				Runtime.getRuntime().exec(new String[] { "/usr/bin/open", file.getAbsolutePath() });
			} else {
				// Unknown OS, try with desktop
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(file);
				} else {
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	

	

	/**
	 * This method set the point of interest. When the matcher's first
	 * group(time) is the interest point, return second group
	 * 
	 * @return
	 */
	private void setInterest(CPMark mark, Matcher matcher) {

		String[] interestHead = { "17.03", "27.2", "37.01", "47.35", "57.34", "67.25", "77.15", "87.06" };
		String[] interestTail = { "18.68", "28.41", "38.23", "48.66", "58.73", "68.47", "78.63", "88.45" };

		if (Arrays.asList(interestHead).contains(matcher.group(1))) {
			mark.setHeadTime(Double.parseDouble(matcher.group(1)));
			String dataStr = matcher.group(2);
			StringTokenizer stk = new StringTokenizer(dataStr, "\t");
			for(int i = 0; i < stk.countTokens(); i+=2){
				mark.setHeadCurr(Double.parseDouble(stk.nextToken()));
			}
		} else if (Arrays.asList(interestTail).contains(matcher.group(1))) {
			mark.setTailTime(Double.parseDouble(matcher.group(1)));
			String dataStr = matcher.group(2);
			StringTokenizer stk = new StringTokenizer(dataStr, "\t");
			int rawPoti = 0;
			for(int i = 0; i < stk.countTokens(); i++){
				mark.setTailCurr(Double.parseDouble(stk.nextToken()));
				rawPoti = (int) Double.parseDouble(stk.nextToken());
			}
			int rounded = (((rawPoti + 99) / 100) * 100) - 200;
			mark.setNearPoti(rounded + 0.0);
		}
	}
}
