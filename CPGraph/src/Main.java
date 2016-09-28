import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

	public static void main(String[] args){
		Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		if (GUIController.createMainScreen(primaryStage)) {
			GUIController.refStage = primaryStage;
		} else {
			System.exit(0);
		}
	}

	@Override
	public void stop() {
		// Nothing to close as now.
	}
}
