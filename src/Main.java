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
        // Set global stage
        ScreenManager.setStage(primaryStage);

        Scene splashScreen = createSplashScreen();
        splashScreen.getStylesheets().add("file:src/resources/styles/styles.css");
        primaryStage.setTitle("Revision Assistant");
        primaryStage.setScene(splashScreen);
        primaryStage.show();

        Scene loginScreen = createLoginScreen();
        loginScreen.getStylesheets().add("file:src/resources/styles/styles.css");
        splashScreen.setOnKeyPressed(event -> primaryStage.setScene(loginScreen));
        splashScreen.setOnMouseClicked(event -> primaryStage.setScene(loginScreen));
    }

    private Scene createSplashScreen() {
        Pane splashPane = new Pane();
        splashPane.getStyleClass().add("screens");
        ImageView logo = createLogoView();

        Text titleText1 = new Text("Revision");
        titleText1.getStyleClass().add("title");
        Text titleText2 = new Text("Assistant");
        titleText2.getStyleClass().add("title");
        VBox topText = new VBox(titleText1, titleText2);
        topText.setAlignment(Pos.CENTER);
        topText.setLayoutX(420);
        topText.setLayoutY(100);

        Image loading = new Image("file:src/resources/images/loading.png");
        ImageView loadingView = new ImageView(loading);
        loadingView.setFitWidth(100);
        loadingView.setFitHeight(100);
        loadingView.setLayoutX(880);
        loadingView.setLayoutY(600);

        RotateTransition loadingTransition = new RotateTransition(Duration.seconds(8), loadingView);
        loadingTransition.setFromAngle(0);
        loadingTransition.setToAngle(360);
        loadingTransition.setInterpolator(Interpolator.EASE_IN);
        loadingTransition.setCycleCount(Animation.INDEFINITE);
        loadingTransition.play();

        Text infoText = new Text("Press any key to continue!");
        infoText.getStyleClass().add("text");
        VBox bottomText = new VBox(infoText);
        bottomText.setAlignment(Pos.CENTER);
        bottomText.setLayoutX(225);
        bottomText.setLayoutY(825);

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

        Rectangle background = new Rectangle(357, 187, 1206,683);
        background.getStyleClass().add("backgroundRect");

        RectangleButtonBundle selectLogin = buildRectangularButtonBundle(334, 98, "Login", "smallButtonText", 578, 215);
        RectangleButtonBundle selectRegister = buildRectangularButtonBundle(334,98, "Register", "smallButtonText", 972, 215);

        Group contentLayer = new Group(selectLogin.button(), selectRegister.button());
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
        selectRegister.buttonRect().setFill(Color.GREY);
        selectLogin.buttonRect().setFill(Color.BLACK);
        selectRegister.button().setOnMouseClicked(event -> {
            buildRegistrationContent(contentLayer, selectRegister, selectLogin);
        });
        String iconURL = "file:src/resources/images/user.png";
        TextBundle emailInput = buildTextFieldInput(695, 372, 530, 790, 382, "Enter Email", iconURL, 694, 376, 95, 90);
        emailInput.tf().getStyleClass().add("loginTextField");

        PasswordBundle loginPasswordInput = buildPasswordInput(695, 510, 530, 790, 520, "Enter Password", 694, 513,1166, 534);
        loginPasswordInput.copyPw().getStyleClass().add("loginTextField");
        loginPasswordInput.password().getStyleClass().add("loginTextField");
        Group inputs = new Group(emailInput.group(), loginPasswordInput.passwordGroup());

        Group icons = new Group(emailInput.icon(), loginPasswordInput.getIconGroup());

        RectangleButtonBundle login = buildRectangularButtonBundle(1083, 98, "Login", "buttonText", 430, 684);
        login.button().setOnMouseClicked(event -> {
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            int option = 0;
            if(!emailInput.tf().getText().isEmpty() && !loginPasswordInput.password().getText().isEmpty()) {
                option = userLogin(emailInput.tf().getText(), loginPasswordInput.password().getText());
            }
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
        RectangleButtonBundle forgotPassword = buildRectangularButtonBundle(700, 60, "Forgot Password", "smallerButtonText", 600, 790);

        forgotPassword.button().setOnMouseClicked(event -> {
            try {
                forgotPassword(contentLayer);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
        switchContent(contentLayer, selectLogin.button(), selectRegister.button(), inputs, login.button(), icons, forgotPassword.button());
    }
    private void forgotPassword(Group contentLayer) throws IOException, NoSuchAlgorithmException {
        String userURL = "file:src/resources/images/user.png";
        TextBundle emailInput = buildTextFieldInput(531, 248, 815, 633, 258, "Enter Email", userURL, 531, 251, 95, 90);
        String recoveryURL = "file:src/resources/images/recover.png";
        TextBundle recoveryWord = buildTextFieldInput(531, 370, 815 ,633, 380, "Enter Recovery Word", recoveryURL, 534, 373 ,95, 90);
        PasswordBundle resetPassword = buildPasswordInput(531, 489, 815, 633, 499, "Enter Password", 531, 495 ,1280, 515);
        PasswordBundle confirmResetPassword = buildPasswordInput(531, 609, 815, 633, 619, "Confirm password", 531, 615, 1280, 635);
        attachValidator(emailInput.tf(), this::isValidEmail, "error");
        attachValidator(resetPassword.password(), this::isValidPassword, "error", resetPassword.copyPw());
        attachValidator(confirmResetPassword.password(), this::isValidPassword, "error", confirmResetPassword.copyPw());
        RectangleButtonBundle resetPasswordRectangle = buildRectangularButtonBundle(1083,98,"Reset Password", "buttonText", 418, 729);
        resetPasswordRectangle.button().setOnMouseClicked(event -> {
            int option = -1;
            boolean validInputs = isValidEmail(emailInput.tf().getText()) && isValidPassword(resetPassword.password().getText()) && isValidPassword(confirmResetPassword.password().getText());
            if(!validInputs) {
                option = 0; //empty
            }else {
                User temp = null;
                try {
                    temp = ac.findUserByEmail(emailInput.tf().getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (temp == null) {
                    option = 1; //email not found
                } else {
                    if (!temp.getRecoveryWord().equals(recoveryWord.tf().getText())) {
                        option = 2; // recovery word does not match
                    } else {
                        if (!resetPassword.password().getText().equals(confirmResetPassword.password().getText())) {
                            option = 3; // passwords do not match
                        } else {
                            try {
                                if (ac.resetPassword(temp, resetPassword.password().getText())) {
                                    option = 4; // success
                                }
                            } catch (NoSuchAlgorithmException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
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
        ImageView back = createBRImageView("back");
        back.setOnMouseClicked(event -> {goBack(contentLayer);});
        Group icons  = new Group(emailInput.icon(), recoveryWord.icon(), resetPassword.getIconGroup(), confirmResetPassword.getIconGroup(), back);
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
            return 1;
        }else{
            return 2;
        }
    }

    private PasswordBundle buildPasswordInput(int rectX, int rectY, int rectW, int fieldX, int fieldY, String promptText, int lIconX, int lIconY, int rIconX, int rIconY){
        Rectangle passwordInpRect = new Rectangle(rectX, rectY, rectW, 98);
        passwordInpRect.getStyleClass().add("inputRect");

        PasswordField password = new PasswordField();
        password.setPromptText(promptText);
        password.setLayoutX(fieldX);
        password.setLayoutY(fieldY);

        TextField copyPw = new TextField();
        copyPw.setLayoutX(fieldX);
        copyPw.setLayoutY(fieldY);
        copyPw.setVisible(false);
        copyPw.setManaged(false);

        ImageView lock = new ImageView("file:src/resources/images/lock.png");
        lock.setLayoutX(lIconX);
        lock.setLayoutY(lIconY);
        lock.setFitHeight(88);
        lock.setFitWidth(95);

        copyPw.textProperty().bindBidirectional(password.textProperty());
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

        passwordShow.setOnMouseClicked(e -> showPassword(passwordHide, passwordShow, copyPw, password));
        passwordHide.setOnMouseClicked(e -> hidePassword(passwordHide, passwordShow, copyPw, password));

        Group inputs = new Group(passwordInpRect, password, copyPw);

        return new PasswordBundle(inputs, password, copyPw, passwordShow, passwordHide, lock);
    }
    private TextBundle buildTextFieldInput(int rectX, int rectY, int rectW, int fieldX, int fieldY, String promptText, String iconURL, int iconX, int iconY, int iconW, int iconH){
        Rectangle inputRect = new Rectangle(rectX, rectY, rectW, 98);
        inputRect.getStyleClass().add("inputRect");

        TextField tf = new TextField();
        tf.setPromptText(promptText);
        tf.setLayoutX(fieldX);
        tf.setLayoutY(fieldY);

        ImageView icon = new ImageView(iconURL);
        icon.setLayoutX(iconX);
        icon.setLayoutY(iconY);
        icon.setFitHeight(iconH);
        icon.setFitWidth(iconW);
        Group tfInps = new Group(inputRect, tf);
        return new TextBundle(tfInps, tf, icon);
    }

    private void buildRegistrationContent(Group contentLayer, RectangleButtonBundle selectRegister, RectangleButtonBundle selectLogin) {
        selectRegister.buttonRect().setFill(Color.BLACK);
        selectLogin.buttonRect().setFill(Color.GREY);
        selectLogin.button().setOnMouseClicked(event -> buildLoginContent(contentLayer, selectRegister, selectLogin));

        String iconURL = "file:src/resources/images/user.png";
        TextBundle emailInput = buildTextFieldInput(531, 345, 815, 630, 355, "Enter Email", iconURL, 531, 345, 95, 90);
        PasswordBundle registrationPassword = buildPasswordInput(531, 458, 815, 630, 468, "Enter Password", 531, 463 ,1280, 483);
        PasswordBundle confirmRegistrationPassword = buildPasswordInput(531, 572, 815, 630, 582, "Confirm password", 531, 576, 1280, 598);
        attachValidator(registrationPassword.password(), this::isValidPassword, "error", registrationPassword.copyPw());
        attachValidator(confirmRegistrationPassword.password(), this::isValidPassword, "error", confirmRegistrationPassword.copyPw());
        attachValidator(emailInput.tf(), this::isValidEmail,  "error");
        ImageView question = createBRImageView("question");
        Group inputs = new Group(emailInput.group(), registrationPassword.passwordGroup(), confirmRegistrationPassword.passwordGroup());
        Group icons = new Group(emailInput.icon(), registrationPassword.getIconGroup(), confirmRegistrationPassword.getIconGroup(), question);
        RectangleButtonBundle register = buildRectangularButtonBundle(1083, 98, "Register", "buttonText", 430, 699);
        question.setOnMouseClicked(event -> {
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            CustomAlert.info(alertWindow, (emailReq + "\n" + passwordReq));
        });
        register.button().setOnMouseClicked(event ->{
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            boolean validInputs = isValidEmail(emailInput.tf().getText()) && isValidPassword(registrationPassword.password().getText()) && isValidPassword(confirmRegistrationPassword.password().getText());
            if(!validInputs) {
                CustomAlert.error(alertWindow, "No field can be empty or \n" + emailReq + "\n" + passwordReq);
            }else if(!registrationPassword.password().getText().equals(confirmRegistrationPassword.password().getText())) {
                CustomAlert.error(alertWindow, "Passwords do not match");
            }else{
                try {
                    if (ac.doesEmailExist(emailInput.tf().getText())) {
                        CustomAlert.error(alertWindow, "Email already exists.");
                        } else {
                            buildRecoveryWordEntrance(contentLayer, emailInput.tf().getText(), registrationPassword.password().getText());
                        }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        RectangleButtonBundle alreadyHaveAccount = buildRectangularButtonBundle(700,60, "Already have an account", "smallerButtonText", 600, 805);
        alreadyHaveAccount.button().setOnMouseClicked(event -> buildLoginContent(contentLayer, selectRegister, selectLogin));
        switchContent(contentLayer, selectLogin.button(), selectRegister.button(), inputs, register.button(), icons, alreadyHaveAccount.button());
    }
    public void buildRecoveryWordEntrance(Group contentLayer, String email, String password){
        Text infoText = new Text("Enter a recovery word to use \n when resetting password");
        infoText.getStyleClass().add("infoText");
        infoText.setTextAlignment(TextAlignment.CENTER);
        infoText.setLayoutX(430);
        infoText.setLayoutY(300);
        String iconURL = "file:src/resources/images/recover.png";
        TextBundle recoveryWord = buildTextFieldInput(531, 460, 815 ,640, 470, "Enter Recovery Word", iconURL, 534, 463 ,95, 90);

        ImageView enter = new ImageView("file:src/resources/images/enter.png");
        enter.setFitHeight(90);
        enter.setFitWidth(95);
        enter.setLayoutX(1245);
        enter.setLayoutY(463);
        enter.setCursor(Cursor.HAND);
        enter.setOnMouseClicked(event -> {
            Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
            int option;
            if(recoveryWord.tf().getText().isEmpty()){
                option = 0;
            }else {
                option = userRegistration(email, password, recoveryWord.tf().getText());
            }
            switch(option){
                case 0:
                    CustomAlert.error(alertWindow, "Cannot be empty");
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
        Group input = new Group(recoveryWord.group(), recoveryWord.icon(), enter);
        ImageView back  = createBRImageView("back");
        back.setOnMouseClicked(event -> {goBack(contentLayer);});
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
            return  1;
        }else{
            return 2;
        }
    }
    private void hidePassword(ImageView passwordHide, ImageView passwordShow, TextField copyPw, PasswordField password) {
        passwordHide.setVisible(false);
        passwordShow.setVisible(true);
        copyPw.setManaged(false);
        copyPw.setVisible(false);
        password.setVisible(true);
        password.setManaged(true);
    }
    private void showPassword(ImageView passwordHide, ImageView passwordShow, TextField copyPw, PasswordField password) {
        passwordHide.setVisible(true);
        passwordShow.setVisible(false);
        copyPw.setManaged(true);
        copyPw.setVisible(true);
        password.setVisible(false);
        password.setManaged(false);
    }

    private void attachValidator(TextInputControl field, Predicate<String> rule, String errorClass, TextInputControl... linkedField) {
        field.textProperty().addListener((obs, oldText, newText) -> {
            boolean invalid = !rule.test(newText);
            updateStyle(field, errorClass, invalid);
            for(TextInputControl f : linkedField){
                updateStyle(f, errorClass, invalid);
            }
        });
    }
    private void updateStyle(TextInputControl field, String errorClass, boolean invalid) {
        field.getStyleClass().remove(errorClass);
        if (invalid && !field.getText().isEmpty()) {
            field.getStyleClass().add(errorClass);
        }
    }
    private boolean isValidEmail(String email){
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    private boolean isValidPassword(String pw){
        if(pw == null) return false;
        return pw.length()>=6 && pw.matches(".*[^a-zA-Z0-9].*");
    }

    private RectangleButtonBundle buildRectangularButtonBundle(int rectW, int rectH, String text, String typeOfText, int StackPaneX, int StackPaneY) {
        Rectangle buttonRect = new Rectangle(rectW, rectH);
        Text buttonText = new Text(text);
        buttonText.getStyleClass().add(typeOfText);
        StackPane button = new StackPane(buttonRect, buttonText);
        button.setLayoutX(StackPaneX);
        button.setLayoutY(StackPaneY);
        return new RectangleButtonBundle(buttonRect, buttonText, button);
    }

    private ImageView createLogoView() {
        ImageView logoView = new ImageView("file:src/resources/images/logo.png");
        logoView.getStyleClass().add("constantImages");
        logoView.setLayoutX(0);
        logoView.setLayoutY(0);
        return logoView;
    }
    private ImageView createBRImageView(String name){
        ImageView iv = new ImageView("file:src/resources/images/" + name + ".png");
        iv.getStyleClass().add("constantImages");
        iv.setLayoutX(1776);
        iv.setLayoutY(916);
        iv.setCursor(Cursor.HAND);
        return iv;
    }

    private void switchContent(Group contentLayer, Node... newContent){
        history.push(new ArrayList<>(contentLayer.getChildren()));
        contentLayer.getChildren().setAll(newContent);
    }
    private void goBack(Group contentLayer){
        if(!history.isEmpty()){
            contentLayer.getChildren().setAll(history.pop());
        }
    }
}