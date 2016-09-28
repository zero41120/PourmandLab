

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class GUIController implements Initializable {
	// @formatter:off
	@FXML	Button	buttonBrowse;
	@FXML	Button 	buttonRun;
	@FXML	Button	buttonView;
	static Stage refStage = null;
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
		showAlertInfo("Browse", "Works");
		buttonRun.setDisable(false);
		buttonView.setDisable(true);
	}

	public void actionRun() {
		showAlertInfo("Run", "Works");
		buttonRun.setDisable(true);
		buttonView.setDisable(false);
	}

	public void actionView() {
		showAlertInfo("View", "Works");

	}

}
