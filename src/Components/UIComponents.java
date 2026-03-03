package Components;

import RecordClasses.PasswordBundle;
import RecordClasses.RectangleButtonBundle;
import RecordClasses.TextAreaBundle;
import RecordClasses.TextBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


import java.util.Objects;
import java.util.function.Predicate;

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
        passwordShow.setOnMouseClicked(_ -> showPassword(passwordHide, passwordShow, copyPw, password));
        passwordHide.setOnMouseClicked(_ -> hidePassword(passwordHide, passwordShow, copyPw, password));

        Group inputs = new Group(passwordInpRect, password, copyPw);

        return new PasswordBundle(inputs, password, copyPw, passwordShow, passwordHide, lock);
    }

    // Creates a text bundle with an icon
    public TextBundle buildTextFieldInput(int rectX, int rectY, int rectW, int rectH, int fieldX, int fieldY,
                                          String promptText, String iconURL, int iconX, int iconY, int iconW, int iconH){
        // Create background rectangle for text input
        Rectangle inputRect = new Rectangle(rectX, rectY, rectW, rectH);
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
        Group tfInputs = new Group(inputRect, tf);
        return new TextBundle(tfInputs, tf, icon);
    }

    // Creates a rectangular button with text
    public RectangleButtonBundle buildRectangularButtonBundle(int rectW, int rectH, String text, String typeOfText, int StackPaneX, int StackPaneY) {
        Rectangle buttonRect = new Rectangle(rectW, rectH);
        Text buttonText = new Text(text);
        buttonText.getStyleClass().add(typeOfText);
        StackPane button = new StackPane(buttonRect, buttonText);
        button.getStyleClass().add("StackPane-Button");
        button.setLayoutX(StackPaneX);
        button.setLayoutY(StackPaneY);
        return new RectangleButtonBundle(buttonRect, buttonText, button);
    }

    // Creates a text area with prompt text and a character counter
    public TextAreaBundle createTextArea(String promptText, int charLimit, int lowerLimit) {
        TextArea textArea = new TextArea();
        textArea.getStyleClass().add("textArea");
        textArea.setPromptText(promptText);
        Label characterCounter = new Label("0 / " + charLimit);
        characterCounter.getStyleClass().add("counter");
        // Initial Colour
        characterCounter.setStyle("-fx-text-fill: red;");
        textArea.textProperty().addListener((_, _, newValue) -> {
            // Length when white space is removed
            int trimmedLength = newValue.trim().length();
            // Change the number
            characterCounter.setText(trimmedLength + " / " + charLimit);
            // Change label colours depending on limits
            if (trimmedLength < lowerLimit || trimmedLength > charLimit) {
                characterCounter.setStyle("-fx-text-fill: red;");
            }else{
                characterCounter.setStyle("-fx-text-fill: green;");
            }
        });

        return new TextAreaBundle(textArea, characterCounter, charLimit, lowerLimit);
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
        cut.setOnAction(_ -> tf.cut());

        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(_ -> tf.copy());

        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(_ -> tf.paste());

        ContextMenu cm = new ContextMenu(cut, copy, paste);
        cm.getStyleClass().add("context-menu");

        return cm;
    }

    // Adds a real-time validator to input fields and error styling validation fields.
    public static void attachValidator(TextInputControl field, Predicate<String> rule, String errorClass, TextInputControl... linkedField) {
        field.textProperty().addListener((_, _, newText) -> {
            boolean invalid = !rule.test(newText);
            updateStyle(field, errorClass, invalid);
            // Apply same styling to linked fields (such as visible copy of password field)
            for(TextInputControl f : linkedField){
                updateStyle(f, errorClass, invalid);
            }
        });
    }
    // Updates CSS styling of field depending on validation state
    public static void updateStyle(TextInputControl field, String errorClass, boolean invalid) {
        field.getStyleClass().remove(errorClass);
        if (invalid && !field.getText().isEmpty()) {
            field.getStyleClass().add(errorClass);
        }
    }

    // Validates email format via regex
    public static boolean isValidEmail(String email){
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    // Validates strength of password
    public static boolean isValidPassword(String pw){
        if(pw == null) return false;
        return pw.length()>=6 && pw.matches(".*[^a-zA-Z0-9].*");
    }
    // Makes sure recovery is something rememberable
    public static boolean isValidRecoveryW(String recoveryWord){
        return recoveryWord != null && recoveryWord.length() >= 3;
    }

    // Bind Enter Button to submit in Text Fields
    public void bindEnter(EventHandler<ActionEvent> handler, Node... fields) {
        for (Node node : fields) {
            if(node instanceof TextField tf){
                tf.setOnAction(handler);
            }else if(node instanceof PasswordField pf){
                pf.setOnAction(handler);
            }
        }
    }
    // Function to set position of a node
    public void setPosition(Node node, int x, int y){
        node.setLayoutX(x);
        node.setLayoutY(y);
    }

    // Function to return a question mark image view to be used for information
    public ImageView getHintView(){
        ImageView hint = new ImageView("/images/Constant/question.png");
        hint.setCursor(Cursor.HAND);
        hint.setLayoutX(1820);
        hint.setFitHeight(95);
        hint.setFitWidth(95);
        return hint;
    }
}
