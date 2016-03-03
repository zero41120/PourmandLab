package edu.pourmand.soe.ucsc.BioGrapher;

import java.util.Locale;
import java.util.ResourceBundle;

public class StateMachine {
	public enum States
	{
		BEGINNING, CALIBRATING, SAVING, DISPLAYING;
	}

	States	currentState;
	boolean	running;
	boolean alertRequest_LoadPreviousPaths;
	boolean	isAlertLoadPathConfirmed;
	boolean isAlertClearDataComirmed;
	boolean isDataInputted;
	boolean	graphBTN1, graphBTN2, graphBTN3;
	boolean	doneDrawing;
	
	// static Locale l = new Locale(System.getProperty("user.country"), System.getProperty("user.language"));
	static Locale l = new Locale("zh", "TW");
	static ResourceBundle msg = ResourceBundle.getBundle("edu.pourmand.soe.ucsc.BioGrapher/StringBundles/SBundle", l);
	
	static StateMachine sM = new StateMachine(States.BEGINNING);

	private StateMachine(States currentState) {
		this.currentState = currentState;
		this.running = true;
		this.alertRequest_LoadPreviousPaths = false;
		this.isAlertLoadPathConfirmed = false;
		this.isDataInputted = false;
		this.isAlertClearDataComirmed = false;
		this.graphBTN1 = false;
		this.graphBTN2 = false;
		this.graphBTN3 = false;
		this.doneDrawing = false;
	}

	@Override
	public String toString() {
		String report = "State Machine current status: ";
		report += "\nCurrent state: " + sM.currentState;
		return report;
	}
	
	

}