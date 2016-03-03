/**
* <h1>BioGrapher</h1> 
* The BioGrapher program implements an application that
* analysis and graphs data from .txt files of the xxx and the yyy machine.
* 
*
* @author  Tz-Shiuan Lin, Thomas Boser.
* @version 1.2
* @since   2016-01-20 
*/

package edu.pourmand.soe.ucsc.BioGrapher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;
import edu.pourmand.soe.ucsc.BioGrapher.StateMachine.States;
import static java.lang.System.out;
import static edu.pourmand.soe.ucsc.BioGrapher.DataProvider.dP;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.sM;
import static edu.pourmand.soe.ucsc.BioGrapher.StateMachine.msg;

public class Main extends Application {

	static DataFileManager fM = new DataFileManager();
	static File myFile = null;
	static Stage refStage = null;
	static Exception globalException = new Exception("Default Error");
	static Main main = new Main();

	public static void main(String[] args) {
		Application.launch(args);
	}

	private void checkPreviousStage() {
		if (fM.checkPathFile()) {
			// Alerts the user if path file exists.
			sM.isAlertLoadPathConfirmed = GUIController.showAlertConfrimation( //
					msg.getString("<GUITEXT>TitleConfirm"), //
					msg.getString("<GUITEXT>HeaderConfirmLoadPathFile"), //
					msg.getString("<GUITEXT>ContentConfirmLoadPathFile"));
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		sM.running = GUIController.createMainScreen(primaryStage);
		refStage = primaryStage;
		checkPreviousStage();
		main.executeStateMachine();
	}

	public void executeStateMachine() {

		// State Machine not running.
		if (!sM.running) {
			GUIController.showAlertError(//
					msg.getString("<GUITEXT>TitleError"), //
					msg.getString("<GUITEXT>HeaderError"), //
					msg.getString("<GUITEXT>ContentError"), //
					globalException);
			return;
		}
		try {

			switch (sM.currentState) {
			case BEGINNING:
			/*
			 * If user selects to load path file, 
			 * state machine starts to calibrate BioTemp.bgt, 
			 * otherwise redirect to main screen state.
			 */
			{
				out.print("\n---Beginning---\n");
				if (sM.isAlertLoadPathConfirmed) {
					myFile = fM.getFilePath();
					out.print(msg.getString("<Notice>DataLoad"));
					sM.currentState = States.CALIBRATING;
				} else {
					fM.deletePathFile();
					dP.resetProviderAndKeepData(false);
					sM.currentState = States.DISPLAYING;
				}
				main.executeStateMachine();
				break;
			}

			case CALIBRATING:
			/*
			 * CALIBRATING
			 * A five-stage process.
			 * Refer to each stage in the code.
			 */
			{
				/*
				 * Stage 1:
				 * Check the existence of the inputting file.
				 * If it exists, process CalibatingSignature.
				 */
				out.print("\n---InputtingFile---\n");
				if (sM.isDataInputted) {
					myFile = dP.getNextWorkingFile();
					if (myFile != null) {
						out.print(msg.getString("<Notice>DataLoad"));
						out.println(myFile.getPath());
					} else {
						out.println(msg.getString("<Error>DataLoad"));
						sM.isDataInputted = false;
						sM.currentState = States.SAVING;
						//sM.currentState = States.DISPLAYING;
						dP.resetProviderAndKeepData(true);
						main.executeStateMachine();
						break;
					}
				}
				/*
				 * Stage 2:
				 * Check the signature of the inputting file.
				 * If it exists, process CalibatingVariable.
				 */
				out.print("\n---CalibatingSignature---\n");
				dP.setCurrentType(myFile);
				if (dP.isValidType()) {
					// Good signature.
					out.print(msg.getString("<Notice>DataCalibate"));
					out.println("(" + dP.getCurrentType() + ")");
				} else {
					// Bad signature.
					out.println(msg.getString("<Error>DataCalibate"));
					main.executeStateMachine();
					break;
				}
				/*
				 * Stage 3:
				 * Check the variable of the inputting file.
				 * If it all data are valid, process CalibatingRemaining.
				 */
				out.print("\n---CalibatingVariable---\n");
				dP.extractVariable(myFile);
				if (dP.isValidVariable()) {
					// Good signature, good data.
					out.print(msg.getString("<Notice>DataVariable"));
					out.println("(" + dP.getCurrentType() + ")");
					out.print(msg.getString("<Notice>FileRemaining"));
					out.println(dP.getRemainingFileSize());
				} else {
					// Good signature, bad data.
					out.println(msg.getString("<Error>DataVariable"));
					main.executeStateMachine();
					break;
				}

				/*
				 * Stage 4:
				 * Check the remaining files.
				 * If remaining >0, process InputtingFile again.
				 */
				out.print("\n---CalibatingRemaining---\n");
				out.println("Remaining:"+dP.getRemainingFileSize());
				if (dP.getRemainingFileSize() > 0) {
					sM.currentState = States.CALIBRATING;
					sM.isDataInputted = true;
					main.executeStateMachine();
					break;
				} else {
					sM.currentState = States.SAVING;
					sM.isDataInputted = false;
					main.executeStateMachine();
					break;
				}
			}
			
			case SAVING:
				/*
				 * Stage 5:
				 * All files are loaded. Prompt the user to enter concentration for each file.
				 * If we load from previous stage, 
				 * we will graph them automatically.
				 * Goto DISPLAYING state.
				 */

				GUIController.showAlertConcentration(//
						msg.getString("<GUITEXT>TitleConcentration"), //
						msg.getString("<GUITEXT>HeaderConcentration"), //
						msg.getString("<GUITEXT>ContentConcentration"));
				if (sM.isAlertLoadPathConfirmed) {
					sM.isAlertClearDataComirmed = false;
				}
   				fM.savePath(dP); 
				sM.currentState = States.DISPLAYING;
				dP.resetProviderAndKeepData(true);
				main.executeStateMachine();
				break;
			

			case DISPLAYING:
			/*
			* MainScreen 
			* 1. Activate buttons 
			* 2. Calculate expected concentration 
			* 3. Display graph Goto clear layout when graph button is clicked
			*/
			{
				out.print("\n---MainScreen---\n");
				out.println(dP.getReport());
				List<String> someString = new ArrayList<>();
				for (Integer i = 0; i < 5; i++) {
					someString.add(i.toString());
				}

				if (sM.isAlertClearDataComirmed) {
					dP.resetProviderAndKeepData(false);
					fM.deletePathFile();
					sM.isAlertClearDataComirmed = false;
					main.executeStateMachine();
				}

				break;
			}

			default:
				out.println("Something Wrong");
				break;

			} // switch

		} catch (Exception e) {
			globalException = e;
			e.printStackTrace();
			GUIController.showAlertError(//
					msg.getString("<GUITEXT>TitleError"), //
					msg.getString("<GUITEXT>HeaderError"), //
					msg.getString("<GUITEXT>ContentError"), //
					globalException);
		}
	}
}
