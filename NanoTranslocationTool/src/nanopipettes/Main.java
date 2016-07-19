package nanopipettes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	public static final String VERSION = "0.01";

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(Main.class.getResource("TNGUI.fxml"));
		primaryStage.setTitle("Nano Translocation Tool " + VERSION);
		primaryStage.setScene(new Scene(root));
		nanopipettes.GUIController.refStage = primaryStage;
		primaryStage.show();
	}
}
