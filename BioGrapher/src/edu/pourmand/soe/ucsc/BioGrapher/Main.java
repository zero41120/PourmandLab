/**
* <h1>BioGrapher</h1> 
* The BioGrapher program implements an application that
* analysis and graphs data from .txt files of the CHI1030C and the GFP machine.
* 
*
* @author  Tz-Shiuan Lin, Thomas Boser.
* @version 4
* @since   2016-01-20 
*/

package edu.pourmand.soe.ucsc.BioGrapher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static java.lang.System.out;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.sM;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.msg;

public class Main extends Application {

	/**
	 * JavaFX is activated via Application.launch()
	 * launch() will prepare the GUI and call start()
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
	/**
	 * This method makes starts the GUI and execute the state machine.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		sM.running = createMainScreen(primaryStage);
		GUIController.refStage = primaryStage;
		checkPreviousStage();
		GUIController.executeStateMachine();
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
			GUIController.globalException = e;
			e.printStackTrace();
			out.println("<Error>Failed to create main screen");
			return false;
		}
	}
	
	/**
	 * This method checks .bgt file to restore previous stage.
	 */
	private void checkPreviousStage() {
		if (GUIController.fM.checkPathFile()) {
			// Alerts the user if path file exists.
			sM.isAlertLoadPathConfirmed = GUIController.showAlertConfrimation( //
					msg.getString("<GUITEXT>TitleConfirm"), //
					msg.getString("<GUITEXT>HeaderConfirm_LoadPathFile"), //
					msg.getString("<GUITEXT>ContentConfirm_LoadPathFile"));
		}
	}

	

}
