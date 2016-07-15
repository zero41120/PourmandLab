package nanopipettes;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class GUIController implements Initializable {
	// @formatter:off
	@FXML Button TNButtonLoadText;
	// TODO add all fxml element
	// @formatter:on
	
	public static void main(String[] args) {
		try {
			System.out.println("start");
			DataProvider dP = new SQLDatabase();
			dP.scanData(0.0, 0.0025, "Gold100");
			System.out.println(dP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub	
	}
	
	public void TNActionClickLoadText(){
		
	}
	
	public void TNActionClickScanMachine(){
		
	}
	
	public void TNActionClickOutputExcel(){
		
	}
	
	public void TNActionClickOutputText(){
		
	}

	// TODO add all fxml action

}
