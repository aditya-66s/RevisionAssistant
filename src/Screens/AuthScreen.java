package Screens;

import Components.ContentNavigator;
import Components.CustomAlert;
import Components.ScreenManager;
import Components.UIComponents;
import Controllers.AuthenticationController;
import Controllers.SettingsController;
import Models.User;
import RecordClasses.PasswordBundle;
import RecordClasses.RectangleButtonBundle;
import RecordClasses.TextBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class AuthScreen {
    // Authentication Controller for account-related operations
    private final AuthenticationController ac;
    private final UIComponents ui = new UIComponents();
    private final ContentNavigator nav = new ContentNavigator();
    private final SettingsController sc;

    // Validation Requirements used across multiple subroutines
    private final String emailReq = "Email should follow the standard format";
    private final String passwordReq = "Password should have a minimum of 6 characters, including a special character";
    private final String rwReq = "Recovery Word must be a minimum of 3 characters";

    public AuthScreen(AuthenticationController ac, SettingsController sc) {
        this.ac = ac;
        this.sc = sc;
    }

    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        ImageView logo = ui.createLogoView();
        // Create consistent background rectangle for content
        Rectangle background = new Rectangle(357, 187, 1206,683);
        background.getStyleClass().add("backgroundRect");

        // Create tab buttons for switching between login and registration
        RectangleButtonBundle selectLogin = ui.buildRectangularButtonBundle(334, 98, "Login", "smallButtonText", 578, 215);
        RectangleButtonBundle selectRegister = ui.buildRectangularButtonBundle(334,98, "Register", "smallButtonText", 972, 215);
        // Group content that changes based on selected tab
        Group contentLayer = new Group(selectLogin.button(), selectRegister.button());
        // Initialise first with login content
        buildLoginContent(contentLayer, selectLogin, selectRegister);

        root.getChildren().addAll(logo, background, contentLayer);
        
        Scene scene = new Scene(root, 1920, 1057);
//        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        return scene;
    }

    // Displays log in content when login is selected
    private void buildLoginContent(Group contentLayer, RectangleButtonBundle selectLogin, RectangleButtonBundle selectRegister) {
        // Style the tab buttons to indicate which one is selected
        selectRegister.buttonRect().setStyle("-fx-fill: grey");
        selectLogin.buttonRect().setStyle("-fx-fill: black");

        // Set up button to switch content when clicked
        selectRegister.button().setOnMouseClicked(_ -> {
            buildRegistrationContent(contentLayer, selectRegister, selectLogin);
            selectRegister.buttonRect().setStyle("-fx-fill: black");
            selectLogin.buttonRect().setStyle("-fx-fill: grey");
        });

        // Create input field with user icon
        String iconURL = "/images/AuthScreen/user.png";
        TextBundle emailInput = ui.buildTextFieldInput(695, 372, 530, 98,790, 382, "Enter Email", iconURL, 694, 376, 95, 90);
        emailInput.tf().getStyleClass().add("loginTextField");
        // Create password input field with show and hide functionality
        PasswordBundle loginPasswordInput = ui.buildPasswordInput(695, 510, 530, 790, 520, "Enter Password", 694, 513,1166, 534);
        loginPasswordInput.copyPw().getStyleClass().add("loginTextField");
        loginPasswordInput.password().getStyleClass().add("loginTextField");
        // Add enter to submit
        EventHandler<ActionEvent> loginHandler = event -> {
            try {
                loginSubmit(event, emailInput, loginPasswordInput);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        ui.bindEnter(loginHandler, emailInput.tf(), loginPasswordInput.password(), loginPasswordInput.copyPw());
        // Groups inputs together
        Group inputs = new Group(emailInput.group(), loginPasswordInput.passwordGroup());
        // Group icons together
        Group icons = new Group(emailInput.icon(), loginPasswordInput.getIconGroup());

        // Create a login button
        RectangleButtonBundle login = ui.buildRectangularButtonBundle(1083, 98, "Login", "buttonText", 430, 684);
        login.button().setOnMouseClicked(event -> {
            try {
                loginSubmit(event, emailInput, loginPasswordInput);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Create a "Forgot Password" button
        RectangleButtonBundle forgotPassword = ui.buildRectangularButtonBundle(700, 60, "Forgot Password", "smallerButtonText", 600, 790);
        forgotPassword.button().setOnMouseClicked(_ -> {
            // Change content to display forgot password options
            forgotPassword(contentLayer);
        });

        // Replace content layer with login elements
        nav.switchContent(contentLayer, selectLogin.button(), selectRegister.button(), inputs, login.button(), icons, forgotPassword.button());
    }
    // Displays correct Alert depending on scenario, on enter or login click.
    private void loginSubmit(Event event, TextBundle emailInput, PasswordBundle loginPasswordInput) throws IOException {
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
                ScreenManager.switchScene(new MainMenuScreen(ac, sc).create(), emailInput.tf().getText());
                break;
            case 2:
                CustomAlert.error(alertWindow, "Invalid email or password, or possible problems with files.");
                break;
        }
    }
    // Displays forgotPassword form, when option is selected
    private void forgotPassword(Group contentLayer) {
        // Create email input field
        String userURL = "/images/AuthScreen/user.png";
        TextBundle emailInput = ui.buildTextFieldInput(531, 248, 815, 98, 633, 258, "Enter Email", userURL, 531, 251, 95, 90);
        // Create recovery word input field
        String recoveryURL = "/images/AuthScreen/recover.png";
        TextBundle recoveryWord = ui.buildTextFieldInput(531, 370, 815 , 98,633, 380, "Enter Recovery Word", recoveryURL, 534, 373 ,95, 90);
        // Create password input fields
        PasswordBundle resetPassword = ui.buildPasswordInput(531, 489, 815, 633, 499, "Enter Password", 531, 495 ,1280, 515);
        PasswordBundle confirmResetPassword = ui.buildPasswordInput(531, 609, 815, 633, 619, "Confirm password", 531, 615, 1280, 635);
        // Attach real time validators to tell user when input is valid
        // :: method reference, allowing it's specified method to be passed as a function
        UIComponents.attachValidator(emailInput.tf(), UIComponents::isValidEmail, "error");
        UIComponents.attachValidator(resetPassword.password(), UIComponents::isValidPassword, "error", resetPassword.copyPw());
        UIComponents.attachValidator(confirmResetPassword.password(), UIComponents::isValidPassword, "error", confirmResetPassword.copyPw());
        UIComponents.attachValidator(recoveryWord.tf(), UIComponents::isValidRecoveryW, "error");
        // Can be reset on enter for convenience
        EventHandler<ActionEvent> resetHandler = e -> resetSubmit(e, recoveryWord, emailInput, resetPassword, confirmResetPassword, contentLayer);
        ui.bindEnter(resetHandler, emailInput.tf(), resetPassword.password(), resetPassword.copyPw(), confirmResetPassword.password(), confirmResetPassword.copyPw(), recoveryWord.tf());
        // Create password reset button
        RectangleButtonBundle resetPasswordRectangle = ui.buildRectangularButtonBundle(1083,98,"Reset Password", "buttonText", 418, 729);
        resetPasswordRectangle.button().setOnMouseClicked(event -> resetSubmit(event, recoveryWord, emailInput, resetPassword, confirmResetPassword, contentLayer));
        // Create a back button to return to login screen
        ImageView back = ui.createBRImageView("back");
        back.setOnMouseClicked(_ -> nav.goBack(contentLayer));
        Group icons  = new Group(emailInput.icon(), recoveryWord.icon(), resetPassword.getIconGroup(), confirmResetPassword.getIconGroup(), back);
        // Replace content with password reset content
        nav.switchContent(contentLayer, emailInput.group(), recoveryWord.group(), resetPassword.passwordGroup(), confirmResetPassword.passwordGroup(), resetPasswordRectangle.button(), icons);
    }
    // Displays correct alert depending on scenario, or enter or reset password button click
    private void resetSubmit(Event event, TextBundle recoveryWord, TextBundle emailInput, PasswordBundle resetPassword, PasswordBundle confirmResetPassword, Group contentLayer) {
        int option = -1;
        // Validate input
        boolean validInputs = UIComponents.isValidEmail(emailInput.tf().getText()) && UIComponents.isValidPassword(resetPassword.password().getText()) && UIComponents.isValidPassword(confirmResetPassword.password().getText()) && UIComponents.isValidRecoveryW(recoveryWord.tf().getText());
        if (!validInputs) {
            option = 0; // invalid input
        } else {
            // Find user by email
            User temp;
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
                CustomAlert.error(alertWindow, "No field can be empty or \n" + emailReq + "\n" + passwordReq + "\n" + rwReq);
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
                nav.goBack(contentLayer);
                break;
        }
    }
    // Attempts to log in the user
    private int userLogin(String email, String password) {
        boolean valid;
        try{
            valid = ac.loginUser(email, password, sc);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        if(valid) {
            return 1; // Login Successful
        }else{
            return 2; // Login failed
        }
    }

    // Displays registration content when register is selected
    private void buildRegistrationContent(Group contentLayer, RectangleButtonBundle selectRegister, RectangleButtonBundle selectLogin) {
        // Style the tab buttons to indicate which one is selected
        selectRegister.buttonRect().setStyle("-fx-fill: black");
        selectLogin.buttonRect().setStyle("-fx-fill: grey");
        // Set up button to switch content when clicked
        selectLogin.button().setOnMouseClicked(_ -> {
                buildLoginContent(contentLayer, selectRegister, selectLogin);
                selectRegister.buttonRect().setStyle("-fx-fill: grey");
                selectLogin.buttonRect().setStyle("-fx-fill: black");
        });

        // Create input field
        String iconURL = "/images/AuthScreen/user.png";
        TextBundle emailInput = ui.buildTextFieldInput(531, 345, 815, 98, 630, 355, "Enter Email", iconURL, 531, 345, 95, 90);
        // Create password input fields
        PasswordBundle registrationPassword = ui.buildPasswordInput(531, 458, 815, 630, 468, "Enter Password", 531, 463 ,1280, 483);
        PasswordBundle confirmRegistrationPassword = ui.buildPasswordInput(531, 572, 815, 630, 582, "Confirm password", 531, 576, 1280, 598);
        // attach real-time validators to input fields
        UIComponents.attachValidator(registrationPassword.password(), UIComponents::isValidPassword, "error", registrationPassword.copyPw());
        UIComponents.attachValidator(confirmRegistrationPassword.password(), UIComponents::isValidPassword, "error", confirmRegistrationPassword.copyPw());
        UIComponents.attachValidator(emailInput.tf(), UIComponents::isValidEmail,  "error");

        // Create a question mark icon for displaying input requirements
        ImageView question = ui.createBRImageView("question");

        // Enter to submit for convenience
        EventHandler<ActionEvent> registerHandler = e -> registerSubmit(e, emailInput, registrationPassword, confirmRegistrationPassword, contentLayer);
        ui.bindEnter(registerHandler, emailInput.tf(), registrationPassword.password(), registrationPassword.copyPw(), confirmRegistrationPassword.password(), confirmRegistrationPassword.copyPw());
        // Group inputs fields and icons
        Group inputs = new Group(emailInput.group(), registrationPassword.passwordGroup(), confirmRegistrationPassword.passwordGroup());
        Group icons = new Group(emailInput.icon(), registrationPassword.getIconGroup(), confirmRegistrationPassword.getIconGroup(), question);

        // Create register button
        RectangleButtonBundle register = ui.buildRectangularButtonBundle(1083, 98, "Register", "buttonText", 430, 699);
        // Show requirements when question mark is clicked
        question.setOnMouseClicked(event -> {
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            CustomAlert.info(alertWindow, (emailReq + "\n" + passwordReq + "\n" + rwReq));
        });
        // Handle registration button click
        register.button().setOnMouseClicked(event -> registerSubmit(event, emailInput, registrationPassword, confirmRegistrationPassword, contentLayer));
        // Create "Already have an account" button
        RectangleButtonBundle alreadyHaveAccount = ui.buildRectangularButtonBundle(700,60, "Already have an account", "smallerButtonText", 600, 805);
        alreadyHaveAccount.button().setOnMouseClicked(_ -> buildLoginContent(contentLayer, selectRegister, selectLogin));
        // Replace content with registration form
        nav.switchContent(contentLayer, selectLogin.button(), selectRegister.button(), inputs, register.button(), icons, alreadyHaveAccount.button());
    }
    // Enter or Click Register logic
    public void registerSubmit(Event event, TextBundle emailInput, PasswordBundle registrationPassword, PasswordBundle confirmRegistrationPassword, Group contentLayer){
        Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
        // Check if inputs are valid
        boolean validInputs = UIComponents.isValidEmail(emailInput.tf().getText()) && UIComponents.isValidPassword(registrationPassword.password().getText()) && UIComponents.isValidPassword(confirmRegistrationPassword.password().getText());
        if(!validInputs) {
            CustomAlert.error(alertWindow, "No field can be empty or \n" + emailReq + "\n" + passwordReq + "\n" + rwReq);
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
    }
    // Builds recovery word input screen when all inputs are valid
    public void buildRecoveryWordEntrance(Group contentLayer, String email, String password){
        // Create informational text
        Text infoText = new Text("Enter a recovery word to use \n when resetting password \n Minimum 3 Characters");
        infoText.getStyleClass().add("infoText");
        infoText.setTextAlignment(TextAlignment.CENTER);
        infoText.setLayoutX(530);
        infoText.setLayoutY(300);
        String iconURL = "/images/AuthScreen/recover.png";
        // Create recovery word input field
        TextBundle recoveryWord = ui.buildTextFieldInput(531, 460, 815 , 98,640, 470, "Enter Recovery Word", iconURL, 534, 463 ,95, 90);
        UIComponents.attachValidator(recoveryWord.tf(), UIComponents::isValidRecoveryW, "error");
        recoveryWord.tf().setOnAction(e -> {
            try {
                rwSubmit(e, recoveryWord, email, password);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        // Create enter button (as an icon)
        ImageView enter = new ImageView(Objects.requireNonNull(getClass().getResource("/images/AuthScreen/enter.png")).toExternalForm());
        enter.setFitHeight(90);
        enter.setFitWidth(95);
        enter.setLayoutX(1245);
        enter.setLayoutY(463);
        enter.setCursor(Cursor.HAND);
        // Handle registration submission
        enter.setOnMouseClicked(event -> {
            try {
                rwSubmit(event, recoveryWord, email, password);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Group recovery word input and entry button
        Group input = new Group(recoveryWord.group(), recoveryWord.icon(), enter);
        // Create back button if user wants to cancel
        ImageView back  = ui.createBRImageView("back");
        back.setOnMouseClicked(_ -> nav.goBack(contentLayer));
        // replace content with recovery word entry screen
        nav.switchContent(contentLayer, input, infoText, back);
    }
    // Enter / onClick, submitting recovery word
    public void rwSubmit(Event event, TextBundle recoveryWord, String email, String password) throws IOException {
        Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
        int option;
        if(!UIComponents.isValidRecoveryW(recoveryWord.tf().getText())) {
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
                ScreenManager.switchScene(new MainMenuScreen(ac, sc).create(), email);
                break;
            case 2:
                CustomAlert.error(alertWindow, "Possible problem with files.");
                break;
        }
    }
    // Attempts to register the user
    public int userRegistration(String email, String password, String recoveryWord){
        boolean added;
        try {
            added = ac.registerUser(email, password, recoveryWord, sc);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        if(added){
            return  1; // Registration Success
        }else{
            return 2; // Registration failed
        }
    }

}
