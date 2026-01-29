import RecordClasses.PasswordBundle;
import RecordClasses.RectangleButtonBundle;
import RecordClasses.TextBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

public class Main extends Application {
    // Custom Fonts Xirod and Vipnagorgialla loaded from local files
    public Font Xirod = Font.loadFont("file:src/resources/fonts/Xirod.otf", 30);
    public Font Vipnagorgialla = Font.loadFont("file:src/resources/fonts/Vipnagorgialla.otf", 30);

    // Authentication Controller for account-related operations
    public final AuthenticationController ac = new AuthenticationController();

    // Validation Requirements used across multiple subroutines
    public String emailReq = "Email should follow the standard format";
    public String passwordReq = "Password should have a minimum of 6 characters, including a special character";

    // Stack maintains navigation history for backtracking
    private Stack<List<Node>> history = new Stack<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set stage reference for scene management
        ScreenManager.setStage(primaryStage);

        //Creates and configures the splash screen, linking the stylesheet to it.
        Scene splashScreen = createSplashScreen();
        splashScreen.getStylesheets().add("file:src/resources/styles/styles.css");
        primaryStage.setTitle("Revision Assistant");
        primaryStage.setScene(splashScreen);
        primaryStage.show();

        // Create login screen for a quick transition
        Scene loginScreen = createLoginScreen();
        loginScreen.getStylesheets().add("file:src/resources/styles/styles.css");

        // Set up event handlers to transition from splash screen to login on a mouse click or a keypress.
        splashScreen.setOnKeyPressed(event -> primaryStage.setScene(loginScreen));
        splashScreen.setOnMouseClicked(event -> primaryStage.setScene(loginScreen));
    }

    private Scene createSplashScreen(){
        Pane splashPane = new Pane();
        splashPane.getStyleClass().add("screens");
        ImageView logo = createLogoView();
        // Creating title text split into two lines.
        Text titleText1 = new Text("Revision");
        titleText1.getStyleClass().add("title");
        Text titleText2 = new Text("Assistant");
        titleText2.getStyleClass().add("title");
        VBox topText = new VBox(titleText1, titleText2);
        topText.setAlignment(Pos.CENTER);
        topText.setLayoutX(420);
        topText.setLayoutY(100);

        // Create the rotating loading indicator
        Image loading = new Image("file:src/resources/images/loading.png");
        ImageView loadingView = new ImageView(loading);
        loadingView.setFitWidth(100);
        loadingView.setFitHeight(100);
        loadingView.setLayoutX(880);
        loadingView.setLayoutY(600);

        // Animate the loading indicator with continuos rotation
        RotateTransition loadingTransition = new RotateTransition(Duration.seconds(8), loadingView);
        loadingTransition.setFromAngle(0);
        loadingTransition.setToAngle(360);
        loadingTransition.setInterpolator(Interpolator.EASE_IN);
        loadingTransition.setCycleCount(Animation.INDEFINITE);
        loadingTransition.play();

        // Instruction for the user at bottom of the screen
        Text infoText = new Text("Press any key to continue!");
        infoText.getStyleClass().add("text");
        VBox bottomText = new VBox(infoText);
        bottomText.setAlignment(Pos.CENTER);
        bottomText.setLayoutX(225);
        bottomText.setLayoutY(825);

        // Group text elements to create a pulsing fade effect for text
        Group texts = new Group(topText, bottomText);
        FadeTransition varyingOpacity = new FadeTransition(Duration.seconds(2.5), texts);
        varyingOpacity.setFromValue(1.0);
        varyingOpacity.setToValue(0.25);
        varyingOpacity.setCycleCount(FadeTransition.INDEFINITE);
        varyingOpacity.setAutoReverse(true);
        varyingOpacity.play();

        splashPane.getChildren().addAll(logo, loadingView, texts);
        return new Scene(splashPane, 1920, 1057);
    }
    private Scene createLoginScreen() {
        Pane loginPane = new Pane();
        loginPane.getStyleClass().add("screens");
        ImageView logo = createLogoView();

        // Create consistent background rectangle for content
        Rectangle background = new Rectangle(357, 187, 1206,683);
        background.getStyleClass().add("backgroundRect");

        // Create tab buttons for switching between login and registration
        RectangleButtonBundle selectLogin = buildRectangularButtonBundle(334, 98, "Login", "smallButtonText", 578, 215);
        RectangleButtonBundle selectRegister = buildRectangularButtonBundle(334,98, "Register", "smallButtonText", 972, 215);

        // Group content that changes based on selected tab
        Group contentLayer = new Group(selectLogin.button(), selectRegister.button());
        // Initialise first with login content
        buildLoginContent(contentLayer, selectRegister, selectLogin);
        loginPane.getChildren().addAll(logo, background, contentLayer);
        return new Scene(loginPane, 1920, 1057);
    }
    private Scene createMainMenuScreen(){
        Pane mainMenu = new Pane(createLogoView());
        mainMenu.getStyleClass().add("screens");
        Scene mainMenuScene = new Scene(mainMenu, 1920, 1057);
        mainMenuScene.getStylesheets().add("file:src/resources/styles/styles.css");
        return mainMenuScene;
    }

    private void buildLoginContent(Group contentLayer, RectangleButtonBundle selectRegister, RectangleButtonBundle selectLogin) {
        // Style the tab buttons to indicate which one is selected
        selectRegister.buttonRect().setFill(Color.GREY);
        selectLogin.buttonRect().setFill(Color.BLACK);
        // Set up button to switch content when clicked
        selectRegister.button().setOnMouseClicked(event -> {
            buildRegistrationContent(contentLayer, selectRegister, selectLogin);
        });

        // Create input field with user icon
        String iconURL = "file:src/resources/images/user.png";
        TextBundle emailInput = buildTextFieldInput(695, 372, 530, 790, 382, "Enter Email", iconURL, 694, 376, 95, 90);
        emailInput.tf().getStyleClass().add("loginTextField");

        // Create password input field with show and hide functionality
        PasswordBundle loginPasswordInput = buildPasswordInput(695, 510, 530, 790, 520, "Enter Password", 694, 513,1166, 534);
        loginPasswordInput.copyPw().getStyleClass().add("loginTextField");
        loginPasswordInput.password().getStyleClass().add("loginTextField");

        // Groups inputs together
        Group inputs = new Group(emailInput.group(), loginPasswordInput.passwordGroup());
        // Group icons together
        Group icons = new Group(emailInput.icon(), loginPasswordInput.getIconGroup());

        // Create a login utton
        RectangleButtonBundle login = buildRectangularButtonBundle(1083, 98, "Login", "buttonText", 430, 684);
        login.button().setOnMouseClicked(event -> {
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            int option = 0;

            // Validate that fields are not empty before attempting to login
            if(!emailInput.tf().getText().isEmpty() && !loginPasswordInput.password().getText().isEmpty()) {
                option = userLogin(emailInput.tf().getText(), loginPasswordInput.password().getText());
            }
            // Handle different login outcomes displaying alerts depending outcome
            switch(option) {
                case 0:
                    CustomAlert.error(alertWindow, "Cannot be empty");
                    break;
                case 1:
                    CustomAlert.success(alertWindow, "Logged in successfully");
                    ScreenManager.switchScene(createMainMenuScreen());
                    break;
                case 2:
                    CustomAlert.error(alertWindow, "Invalid email or password, or possible problems with files.");
                    break;
            }
        });

        // Create a "Forgot Password" button
        RectangleButtonBundle forgotPassword = buildRectangularButtonBundle(700, 60, "Forgot Password", "smallerButtonText", 600, 790);
        forgotPassword.button().setOnMouseClicked(event -> {
            try {
                // Change content to display forgot password options
                forgotPassword(contentLayer);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
        // Replace content layer with login elements
        switchContent(contentLayer, selectLogin.button(), selectRegister.button(), inputs, login.button(), icons, forgotPassword.button());
    }
    private void forgotPassword(Group contentLayer) throws IOException, NoSuchAlgorithmException {
        // Create email input field
        String userURL = "file:src/resources/images/user.png";
        TextBundle emailInput = buildTextFieldInput(531, 248, 815, 633, 258, "Enter Email", userURL, 531, 251, 95, 90);
        // Create recovery word input field
        String recoveryURL = "file:src/resources/images/recover.png";
        TextBundle recoveryWord = buildTextFieldInput(531, 370, 815 ,633, 380, "Enter Recovery Word", recoveryURL, 534, 373 ,95, 90);
        // Create password input fields
        PasswordBundle resetPassword = buildPasswordInput(531, 489, 815, 633, 499, "Enter Password", 531, 495 ,1280, 515);
        PasswordBundle confirmResetPassword = buildPasswordInput(531, 609, 815, 633, 619, "Confirm password", 531, 615, 1280, 635);
        // Attach real time validators to tell user when input is valid
        // :: method reference, allowing it's specified method to be passed as a function
        attachValidator(emailInput.tf(), this::isValidEmail, "error");
        attachValidator(resetPassword.password(), this::isValidPassword, "error", resetPassword.copyPw());
        attachValidator(confirmResetPassword.password(), this::isValidPassword, "error", confirmResetPassword.copyPw());
        // Create password reset button
        RectangleButtonBundle resetPasswordRectangle = buildRectangularButtonBundle(1083,98,"Reset Password", "buttonText", 418, 729);
        resetPasswordRectangle.button().setOnMouseClicked(event -> {
            int option = -1;
            // Validate input
            boolean validInputs = isValidEmail(emailInput.tf().getText()) && isValidPassword(resetPassword.password().getText()) && isValidPassword(confirmResetPassword.password().getText());
            if(!validInputs) {
                option = 0; // invalid input
            }else {
                // Find user by email
                User temp = null;
                try {
                    temp = ac.findUserByEmail(emailInput.tf().getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (temp == null) {
                    option = 1; // Email not found
                } else {
                    if (!temp.getRecoveryWord().equals(recoveryWord.tf().getText())) {
                        option = 2; // Recovery word does not match
                    } else {
                        if (!resetPassword.password().getText().equals(confirmResetPassword.password().getText())) {
                            option = 3; // Passwords do not match
                        } else {
                            try {
                                if (ac.resetPassword(temp, resetPassword.password().getText())) {
                                    option = 4; // Success
                                }
                            } catch (NoSuchAlgorithmException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
            // Display appropriate alert based on outcome
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            switch(option){
                case 0:
                    CustomAlert.error(alertWindow, "No field can be empty or \n" + emailReq + "\n" + passwordReq);
                    break;
                case 1:
                    CustomAlert.error(alertWindow, "Email not found");
                    break;
                case 2:
                    CustomAlert.error(alertWindow, "Recovery word does not match");
                    break;
                case 3:
                    CustomAlert.error(alertWindow, "Passwords do not match");
                    break;
                case 4:
                    CustomAlert.success(alertWindow, "Password reset successfully, you will be redirected to the login screen!" );
                    goBack(contentLayer);
                    break;
            }
        });
        // Create a back button to return to login screen
        ImageView back = createBRImageView("back");
        back.setOnMouseClicked(event -> {goBack(contentLayer);});
        Group icons  = new Group(emailInput.icon(), recoveryWord.icon(), resetPassword.getIconGroup(), confirmResetPassword.getIconGroup(), back);
        // Replace content with password reset content
        switchContent(contentLayer, emailInput.group(), recoveryWord.group(), resetPassword.passwordGroup(), confirmResetPassword.passwordGroup(), resetPasswordRectangle.button(), icons);
    }
    private int userLogin(String email, String password) {
        boolean valid;
        try{
            valid = ac.loginUser(email, password);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        if(valid) {
            return 1; // Login Successful
        }else{
            return 2; // Login failed
        }
    }

    // Creates a password bundle with show/hide functionality
    private PasswordBundle buildPasswordInput(int rectX, int rectY, int rectW, int fieldX, int fieldY, String promptText, int lIconX, int lIconY, int rIconX, int rIconY){
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
        ImageView lock = new ImageView("file:src/resources/images/lock.png");
        lock.setLayoutX(lIconX);
        lock.setLayoutY(lIconY);
        lock.setFitHeight(88);
        lock.setFitWidth(95);
        // Bind text fields together so they stay synchronized
        copyPw.textProperty().bindBidirectional(password.textProperty());
        // Create show/hide password icons
        ImageView passwordShow = new ImageView("file:src/resources/images/passwordShow.png");
        ImageView passwordHide = new ImageView("file:src/resources/images/passwordHide.png");

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
    private TextBundle buildTextFieldInput(int rectX, int rectY, int rectW, int fieldX, int fieldY, String promptText, String iconURL, int iconX, int iconY, int iconW, int iconH){
        // Create background rectangle for text input
        Rectangle inputRect = new Rectangle(rectX, rectY, rectW, 98);
        inputRect.getStyleClass().add("inputRect");
        // Create text field
        TextField tf = new TextField();
        tf.setPromptText(promptText);
        tf.setLayoutX(fieldX);
        tf.setLayoutY(fieldY);
        // Create icon
        ImageView icon = new ImageView(iconURL);
        icon.setLayoutX(iconX);
        icon.setLayoutY(iconY);
        icon.setFitHeight(iconH);
        icon.setFitWidth(iconW);
        Group tfInps = new Group(inputRect, tf);
        return new TextBundle(tfInps, tf, icon);
    }

    private void buildRegistrationContent(Group contentLayer, RectangleButtonBundle selectRegister, RectangleButtonBundle selectLogin) {
        // Style the tab buttons to indicate which one is selected
        selectRegister.buttonRect().setFill(Color.BLACK);
        selectLogin.buttonRect().setFill(Color.GREY);
        // Set up button to switch content when clicked
        selectLogin.button().setOnMouseClicked(event -> buildLoginContent(contentLayer, selectRegister, selectLogin));

        // Create input field
        String iconURL = "file:src/resources/images/user.png";
        TextBundle emailInput = buildTextFieldInput(531, 345, 815, 630, 355, "Enter Email", iconURL, 531, 345, 95, 90);
        // Create password input fields
        PasswordBundle registrationPassword = buildPasswordInput(531, 458, 815, 630, 468, "Enter Password", 531, 463 ,1280, 483);
        PasswordBundle confirmRegistrationPassword = buildPasswordInput(531, 572, 815, 630, 582, "Confirm password", 531, 576, 1280, 598);
        // attach real-time validators to input fields
        attachValidator(registrationPassword.password(), this::isValidPassword, "error", registrationPassword.copyPw());
        attachValidator(confirmRegistrationPassword.password(), this::isValidPassword, "error", confirmRegistrationPassword.copyPw());
        attachValidator(emailInput.tf(), this::isValidEmail,  "error");
        // Create a question mark icon for displaying input requirements
        ImageView question = createBRImageView("question");

        // Group inputs fields and icons
        Group inputs = new Group(emailInput.group(), registrationPassword.passwordGroup(), confirmRegistrationPassword.passwordGroup());
        Group icons = new Group(emailInput.icon(), registrationPassword.getIconGroup(), confirmRegistrationPassword.getIconGroup(), question);

        // Create register button
        RectangleButtonBundle register = buildRectangularButtonBundle(1083, 98, "Register", "buttonText", 430, 699);
        // Show requirements when question mark is clicked
        question.setOnMouseClicked(event -> {
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            CustomAlert.info(alertWindow, (emailReq + "\n" + passwordReq));
        });
        // Handle registration button click
        register.button().setOnMouseClicked(event ->{
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            // Check if inputs are valid
            boolean validInputs = isValidEmail(emailInput.tf().getText()) && isValidPassword(registrationPassword.password().getText()) && isValidPassword(confirmRegistrationPassword.password().getText());
            if(!validInputs) {
                CustomAlert.error(alertWindow, "No field can be empty or \n" + emailReq + "\n" + passwordReq);
            }else if(!registrationPassword.password().getText().equals(confirmRegistrationPassword.password().getText())) {
                CustomAlert.error(alertWindow, "Passwords do not match");
            }else{
                try {
                    // Check if email already exists
                    if (ac.doesEmailExist(emailInput.tf().getText())) {
                        CustomAlert.error(alertWindow, "Email already exists.");
                        } else {
                            // Proceed to Recovery word setup
                            buildRecoveryWordEntrance(contentLayer, emailInput.tf().getText(), registrationPassword.password().getText());
                        }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // Create "Already have an account" button
        RectangleButtonBundle alreadyHaveAccount = buildRectangularButtonBundle(700,60, "Already have an account", "smallerButtonText", 600, 805);
        alreadyHaveAccount.button().setOnMouseClicked(event -> buildLoginContent(contentLayer, selectRegister, selectLogin));
        // Replace content with registration form
        switchContent(contentLayer, selectLogin.button(), selectRegister.button(), inputs, register.button(), icons, alreadyHaveAccount.button());
    }
    public void buildRecoveryWordEntrance(Group contentLayer, String email, String password){
        // Create informational text
        Text infoText = new Text("Enter a recovery word to use \n when resetting password \n Minimum 3 Characters");
        infoText.getStyleClass().add("infoText");
        infoText.setTextAlignment(TextAlignment.CENTER);
        infoText.setLayoutX(430);
        infoText.setLayoutY(300);
        String iconURL = "file:src/resources/images/recover.png";
        // Create recovery word input field
        TextBundle recoveryWord = buildTextFieldInput(531, 460, 815 ,640, 470, "Enter Recovery Word", iconURL, 534, 463 ,95, 90);
        attachValidator(recoveryWord.tf(), this::isValidRecoveryW, "error");
        // Create enter button (as an icon)
        ImageView enter = new ImageView("file:src/resources/images/enter.png");
        enter.setFitHeight(90);
        enter.setFitWidth(95);
        enter.setLayoutX(1245);
        enter.setLayoutY(463);
        enter.setCursor(Cursor.HAND);
        // Handle registration submission
        enter.setOnMouseClicked(event -> {
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            int option;
            if(!isValidRecoveryW(recoveryWord.tf().getText())) {
                option = 0; // Empty recovery word
            }else {
                option = userRegistration(email, password, recoveryWord.tf().getText());
            }
            // Display appropriate alert based on outcome
            switch(option){
                case 0:
                    CustomAlert.error(alertWindow, "Cannot be empty and must be greater than 3 characters");
                    break;
                case 1:
                    CustomAlert.success(alertWindow, "Account successfully registered");
                    ScreenManager.switchScene(createMainMenuScreen());
                    break;
                case 2:
                    CustomAlert.error(alertWindow, "Possible problem with files.");
                    break;
            }
        });
        // Group recovery word input and entry button
        Group input = new Group(recoveryWord.group(), recoveryWord.icon(), enter);
        // Create back button if user wants to cancel
        ImageView back  = createBRImageView("back");
        back.setOnMouseClicked(event -> {goBack(contentLayer);});
        // replace content with recovery word entry screen
        switchContent(contentLayer, input, infoText, back);
    }
    public int userRegistration(String email, String password, String recoveryWord){
        boolean added;
        try {
            added = ac.registerUser(email, password, recoveryWord);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        if(added){
            return  1; // Registration Success
        }else{
            return 2; // Registration failed
        }
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

    // Adds a real-time validator to input fields and error styling validation fields.
    private void attachValidator(TextInputControl field, Predicate<String> rule, String errorClass, TextInputControl... linkedField) {
        field.textProperty().addListener((obs, oldText, newText) -> {
            boolean invalid = !rule.test(newText);
            updateStyle(field, errorClass, invalid);
            // Apply same styling to linked fields (such as visible copy of password field)
            for(TextInputControl f : linkedField){
                updateStyle(f, errorClass, invalid);
            }
        });
    }
    // Updates CSS styling of field depending on validation state
    private void updateStyle(TextInputControl field, String errorClass, boolean invalid) {
        field.getStyleClass().remove(errorClass);
        if (invalid && !field.getText().isEmpty()) {
            field.getStyleClass().add(errorClass);
        }
    }
    // Validates email format via regex
    private boolean isValidEmail(String email){
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    // Validates strength of password
    private boolean isValidPassword(String pw){
        if(pw == null) return false;
        return pw.length()>=6 && pw.matches(".*[^a-zA-Z0-9].*");
    }
    // Makes sure recovery is something rememberable
    private boolean isValidRecoveryW(String recoveryWord){
        return recoveryWord != null && recoveryWord.length() >= 3;
    }

    // Creates a rectangular button with text
    private RectangleButtonBundle buildRectangularButtonBundle(int rectW, int rectH, String text, String typeOfText, int StackPaneX, int StackPaneY) {
        Rectangle buttonRect = new Rectangle(rectW, rectH);
        Text buttonText = new Text(text);
        buttonText.getStyleClass().add(typeOfText);
        StackPane button = new StackPane(buttonRect, buttonText);
        button.setLayoutX(StackPaneX);
        button.setLayoutY(StackPaneY);
        return new RectangleButtonBundle(buttonRect, buttonText, button);
    }

    // Creates logo image view for easy reuse
    private ImageView createLogoView() {
        ImageView logoView = new ImageView("file:src/resources/images/logo.png");
        logoView.getStyleClass().add("constantImages");
        logoView.setLayoutX(0);
        logoView.setLayoutY(0);
        return logoView;
    }
    // Creates an image view positioned in the bottom right of the page
    private ImageView createBRImageView(String name){
        ImageView iv = new ImageView("file:src/resources/images/" + name + ".png");
        iv.getStyleClass().add("constantImages");
        iv.setLayoutX(1776);
        iv.setLayoutY(916);
        iv.setCursor(Cursor.HAND);
        return iv;
    }

    // Switches content of a layer to new nodes, saving current state to history for backtracking
    private void switchContent(Group contentLayer, Node... newContent){
        // Save current state to history stack
        history.push(new ArrayList<>(contentLayer.getChildren()));
        // Replace all children with new content
        contentLayer.getChildren().setAll(newContent);
    }
    // Navigates back to previous content state
    private void goBack(Group contentLayer){
        if(!history.isEmpty()){
            contentLayer.getChildren().setAll(history.pop());
        }
    }
}