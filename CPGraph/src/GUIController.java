
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GUIController implements Initializable {
	// @formatter:off
	@FXML	Button	buttonBrowse;
	@FXML	Button 	buttonRun;
	@FXML	Button	buttonView;
	static Stage refStage = null;
	static ArrayList<File> myFiles = new ArrayList<>();
	final static Pattern format = Pattern.compile("^([0-9]+.?[0-9]+)\\t([0-9\\-]+.?[0-9]+)\\t([0-9\\-]+.?[0-9]+)$");
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
			primaryStage.setTitle("Cool program");
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
			myFiles = getATFFiles(atfFiles);
			for (File file : myFiles) {
				System.out.println(file.getName());
			}
			return;
		}
	}

	public void actionRun() {
		// for each file, scan the target time
		buttonRun.setDisable(true);
		buttonView.setDisable(false);
		ArrayList<CPMark> marks = new ArrayList<>();
		
		for (File file : myFiles) {	
			try (FileInputStream readFile = new FileInputStream(file);
				InputStreamReader readIn = new InputStreamReader(readFile, "UTF8");
				BufferedReader readBuffer = new BufferedReader(readIn);) {
				String line = "";
				CPMark mark = new CPMark();
				while ((line = readBuffer.readLine()) != null) {
					Matcher matcher = format.matcher(line);
					while (matcher.find()) {
						setInterest(mark, matcher);
						if (mark.ready()) {
							marks.add(mark);
							mark = new CPMark();
						}
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
			System.out.println(file.getName());
			System.out.println(marks);
			marks = new ArrayList<>();
		}
		
	}

	public void actionView() {
		showAlertInfo("View", "Works");

	}

	private String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	private ArrayList<File> getATFFiles(List<File> targetFiles) {
		ArrayList<File> toReturn = new ArrayList<>();
		for (int i = 0; i < targetFiles.size(); i++) {
			if (getFileExtension(targetFiles.get(i)).equals("atf")) {
				toReturn.add(targetFiles.get(i));
			}
		}
		return toReturn;
	}
	
	/**
	 * This method set the point of interest.
	 * When the matcher's first group(time) is the interest point, 
	 * return second group 
	 * @return
	 */
	private void setInterest(CPMark mark, Matcher matcher) {
		
		String[] interestHead = {"17.03", "27.2", "37.01", "47.35", "57.34", "67.25", "77.15", "87.06"};
		String[] interestTail = {"18.68", "28.41", "38.23", "48.66", "58.73", "68.47", "78.63", "88.45"};
		
		if(Arrays.asList(interestHead).contains(matcher.group(1))){
			mark.setHeadTime(Double.parseDouble(matcher.group(1)));
			mark.setHeadCurr(Double.parseDouble(matcher.group(2)));
		} else if(Arrays.asList(interestTail).contains(matcher.group(1))){
			int num = (int) Double.parseDouble(matcher.group(3));
			int rounded = ((( num + 99) / 100 ) * 100);
			mark.setTailTime(Double.parseDouble(matcher.group(1)));
			mark.setTailCurr(Double.parseDouble(matcher.group(2)));
			mark.setNearPoti(rounded+0.0);
		}			
	}
}
