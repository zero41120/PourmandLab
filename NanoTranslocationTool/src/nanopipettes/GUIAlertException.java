package nanopipettes;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * This class lets the GUI catch and present message alert.
 */
public class GUIAlertException extends RuntimeException {

	protected GUIAlertException(String message) {
		super(message);
	}

	/**
	 * This is the method which shows the message box.
	 * 
	 * @param aTitle
	 *            String for the error title.
	 * @param aHeader
	 *            String for the error header.
	 */
	protected void showAlert() {
		// Creates a custom alert box.
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText("Message");
		alert.setContentText(getMessage());
		// Set expandable Exception into the dialog pane.
		alert.showAndWait();
	}
}

/**
 * This class lets the GUI catch and present error message.
 */
class GUIAlertErrorException extends GUIAlertException {
	
	protected GUIAlertErrorException(String message) {
		super(message);
	}
	
	/**
	 * This is the method which shows the error box.
	 */
	protected void showAlert() {
		// Creates a custom alert box.
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("System report:");
		alert.setContentText(getMessage());

		// Gets the exception and loads it into the writer.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		printStackTrace(pw);
		String exceptionText = sw.toString();
		Label label = new Label("The exception stacktrace was:");

		// Adds the exception the expandable text area.
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		alert.getDialogPane().setExpandableContent(expContent);
		alert.getDialogPane().setExpanded(true);

		// Set expandable Exception into the dialog pane.
		alert.showAndWait();
	}
}