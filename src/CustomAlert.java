import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Objects;


public class CustomAlert {
    public CustomAlert(){}
    public static void error(Window alertWindow, String message){
        show(alertWindow, "Error", message, Alert.AlertType.ERROR, ButtonType.OK);
    }

    public static void info(Window alertWindow, String message){
        show(alertWindow, "Info", message, Alert.AlertType.INFORMATION, ButtonType.OK);
    }
    public static void success(Window alertWindow, String message){
        show(alertWindow, "Success", message, Alert.AlertType.CONFIRMATION, ButtonType.OK);
    }
    public static boolean confirm(Window alertWindow, String title, String message){
        Alert a = build(alertWindow, title, message, Alert.AlertType.CONFIRMATION, ButtonType.CANCEL, ButtonType.OK);
        // showAndWait() pauses code until user responds
        // filter checks if OK was clicked
        // isPresent() returns true if OK was chosen
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    private static Alert build(Window alertWindow, String title, String message, Alert.AlertType alertType, ButtonType... buttons) {
        Alert a = new Alert(alertType);
        // Place alert on main application window, blocking interaction with background
        if(alertWindow!=null){
            a.initOwner(alertWindow);
        }
        a.setTitle(title);
        a.setHeaderText(null);
        Label label = new Label(message);
        label.setWrapText(true);
        a.getDialogPane().setContent(new VBox(10, label));
        a.getDialogPane().getButtonTypes().setAll(buttons);
        a.getDialogPane().setMinHeight(Control.USE_PREF_SIZE);
        a.getDialogPane().setMinWidth(420);
        a.getDialogPane().getStyleClass()
                .add(alertType.name().toLowerCase());
        a.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(CustomAlert.class
                                .getResource("/resources/styles/alert.css"))
                        .toExternalForm()
        );
        return a;
    }

    private static void show(Window alertWindow, String title, String message, Alert.AlertType alertType, ButtonType... buttons) {
        build(alertWindow, title, message, alertType, buttons).showAndWait();
    }
}
