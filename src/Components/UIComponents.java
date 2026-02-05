package Components;

import RecordClasses.PasswordBundle;
import RecordClasses.RectangleButtonBundle;
import RecordClasses.TextBundle;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Objects;

public class UIComponents {
    // Creates a password bundle with show/hide functionality
    public PasswordBundle buildPasswordInput(int rectX, int rectY, int rectW, int fieldX, int fieldY, String promptText, int lIconX, int lIconY, int rIconX, int rIconY){
        // Create background rectangle for password input
        Rectangle passwordInpRect = new Rectangle(rectX, rectY, rectW, 98);
        passwordInpRect.getStyleClass().add("inputRect");
        // Create hidden password field
        PasswordField password = new PasswordField();
        password.setPromptText(promptText);
        password.setLayoutX(fieldX);
        password.setLayoutY(fieldY);
        // Create visible text field that mirrors password field (for show/hide functionality)
        TextField copyPw = new TextField();
        copyPw.setLayoutX(fieldX);
        copyPw.setLayoutY(fieldY);
        copyPw.setVisible(false);
        copyPw.setManaged(false);
        // Create lock icon
        ImageView lock = new ImageView(Objects.requireNonNull(getClass().getResource("/images/AuthScreen/lock.png")).toExternalForm());
        lock.setLayoutX(lIconX);
        lock.setLayoutY(lIconY);
        lock.setFitHeight(88);
        lock.setFitWidth(95);
        // Bind text fields together so they stay synchronized
        copyPw.textProperty().bindBidirectional(password.textProperty());
        // Create show/hide password icons
        ImageView passwordShow = new ImageView(Objects.requireNonNull(getClass().getResource("/images/AuthScreen/passwordShow.png")).toExternalForm());
        ImageView passwordHide = new ImageView(Objects.requireNonNull(getClass().getResource("/images/AuthScreen/passwordHide.png")).toExternalForm());

        passwordShow.setLayoutX(rIconX);
        passwordShow.setLayoutY(rIconY);
        passwordShow.setVisible(true);
        passwordShow.getStyleClass().add("passwordImage");

        passwordHide.setLayoutX(rIconX);
        passwordHide.setLayoutY(rIconY);
        passwordHide.setVisible(false);
        passwordHide.getStyleClass().add("passwordImage");
        // Set up click handlers to toggle password visibility
        passwordShow.setOnMouseClicked(e -> showPassword(passwordHide, passwordShow, copyPw, password));
        passwordHide.setOnMouseClicked(e -> hidePassword(passwordHide, passwordShow, copyPw, password));

        Group inputs = new Group(passwordInpRect, password, copyPw);

        return new PasswordBundle(inputs, password, copyPw, passwordShow, passwordHide, lock);
    }

    // Creates a text bundle with an icon
    public TextBundle buildTextFieldInput(int rectX, int rectY, int rectW, int fieldX, int fieldY, String promptText, String iconURL, int iconX, int iconY, int iconW, int iconH){
        // Create background rectangle for text input
        Rectangle inputRect = new Rectangle(rectX, rectY, rectW, 98);
        inputRect.getStyleClass().add("inputRect");
        // Create text field
        TextField tf = new TextField();
        tf.setPromptText(promptText);
        tf.setLayoutX(fieldX);
        tf.setLayoutY(fieldY);
        tf.setContextMenu(createContextMenu(tf));
        // Create icon
        ImageView icon = new ImageView(iconURL);
        icon.setLayoutX(iconX);
        icon.setLayoutY(iconY);
        icon.setFitHeight(iconH);
        icon.setFitWidth(iconW);
        Group tfInps = new Group(inputRect, tf);
        return new TextBundle(tfInps, tf, icon);
    }

    // Creates a rectangular button with text
    public RectangleButtonBundle buildRectangularButtonBundle(int rectW, int rectH, String text, String typeOfText, int StackPaneX, int StackPaneY) {
        Rectangle buttonRect = new Rectangle(rectW, rectH);
        Text buttonText = new Text(text);
        buttonText.getStyleClass().add(typeOfText);
        StackPane button = new StackPane(buttonRect, buttonText);
        button.setLayoutX(StackPaneX);
        button.setLayoutY(StackPaneY);
        return new RectangleButtonBundle(buttonRect, buttonText, button);
    }

    // Creates logo image view for easy reuse
    public ImageView createLogoView() {
        ImageView logoView = new ImageView(Objects.requireNonNull(getClass().getResource("/images/Constant/logo.png")).toExternalForm());
        logoView.getStyleClass().add("constantImages");
        logoView.setLayoutX(0);
        logoView.setLayoutY(0);
        return logoView;
    }

    // Creates an image view positioned in the bottom right of the page
    public ImageView createBRImageView(String name){
        ImageView iv = new ImageView(Objects.requireNonNull(getClass().getResource("/images/Constant/" + name + ".png")).toExternalForm());
        iv.getStyleClass().add("constantImages");
        iv.setLayoutX(1820);
        iv.setLayoutY(957);
        iv.setCursor(Cursor.HAND);
        return iv;
    }

    // Hides the password
    private void hidePassword(ImageView passwordHide, ImageView passwordShow, TextField copyPw, PasswordField password) {
        passwordHide.setVisible(false);
        passwordShow.setVisible(true);
        copyPw.setManaged(false);
        copyPw.setVisible(false);
        password.setVisible(true);
        password.setManaged(true);
    }
    // Shows the password
    private void showPassword(ImageView passwordHide, ImageView passwordShow, TextField copyPw, PasswordField password) {
        passwordHide.setVisible(true);
        passwordShow.setVisible(false);
        copyPw.setManaged(true);
        copyPw.setVisible(true);
        password.setVisible(false);
        password.setManaged(false);
    }
    private ContextMenu createContextMenu(TextField tf){
        MenuItem cut = new MenuItem("Cut");
        cut.setOnAction(e -> tf.cut());

        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(e -> tf.copy());

        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(e -> tf.paste());

        ContextMenu cm = new ContextMenu(cut, copy, paste);
        cm.getStyleClass().add("context-menu");

        return cm;
    }
}
