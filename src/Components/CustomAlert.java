package Components;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Objects;

// Utility class for creating and displaying custom alerts, proving convenient static methods for common alert types
public class CustomAlert {
    // Constructor not used, all methods are static
    public CustomAlert(){}
    // Displays an error alert dialog
    public static void error(Window alertWindow, String message){
        show(alertWindow, "Error", message, Alert.AlertType.ERROR, ButtonType.OK);
    }
    // Displays an informational alert dialog
    public static void info(Window alertWindow, String message){
        show(alertWindow, "Info", message, Alert.AlertType.INFORMATION, ButtonType.OK);
    }
    // Displays a success confirmation alert dialog
    public static void success(Window alertWindow, String message){
        show(alertWindow, "Success", message, Alert.AlertType.CONFIRMATION, ButtonType.OK);
    }
    // displays a confirmational dialog with OK and Cancel buttons
    public static boolean confirm(Window alertWindow, String title, String message){
        Alert a = build(alertWindow, title, message, Alert.AlertType.CONFIRMATION, ButtonType.CANCEL, ButtonType.OK);
        // showAndWait() pauses code until user responds
        // filter checks if OK was clicked
        // isPresent() returns true if OK was chosen
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }
    // builds custom styled alert dialog
    private static Alert build(Window alertWindow, String title, String message, Alert.AlertType alertType, ButtonType... buttons) {
        // create a new alert of specified type
        Alert a = new Alert(alertType);
        // Place alert on main application window, blocking interaction with background
        // If alert window is null, then alert will be independent
        if(alertWindow!=null){
            a.initOwner(alertWindow);
        }
        // Set title of alert
        a.setTitle(title);
        // Removes default header text
        a.setHeaderText(null);
        // Create a label for the message with text wrapping enabled
        // Allows longer messages across multiple lines
        Label label = new Label(message);
        label.setWrapText(true);
        // Set the label as the content, wrapped in a VBox with 10px spacing
        a.getDialogPane().setContent(new VBox(10, label));
        // Set the buttons that will appear at the bottom of the alert
        a.getDialogPane().getButtonTypes().setAll(buttons);
        // Configure minimum size to use preferred size (auto-sizing based on content)
        a.getDialogPane().setMinHeight(Control.USE_PREF_SIZE);
        // Set minimum width to ensure the alert is not too narrow
        a.getDialogPane().setMinWidth(420);
        // Add a CSS class based on the alert type (such as: "error", "information", "confirmation")
        a.getDialogPane().getStyleClass().add(alertType.name().toLowerCase());
        // Apply custom stylesheet for alert styling
        a.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(CustomAlert.class.getResource("/styles/alert.css")).toExternalForm()
        );
        return a;
    }
    // Builds alert and shows it immediately
    private static void show(Window alertWindow, String title, String message, Alert.AlertType alertType, ButtonType... buttons) {
        // Build the alert and show it, waiting for user response before continuing
        build(alertWindow, title, message, alertType, buttons).showAndWait();
    }
}
