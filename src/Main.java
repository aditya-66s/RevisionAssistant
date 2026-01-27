import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class Main extends Application {
    public Font Xirod = Font.loadFont("file:src/resources/fonts/Xirod.otf", 30);
    public Font Vipnagorgialla = Font.loadFont("file:src/resources/fonts/Vipnagorgialla.otf", 30);
    public final AuthenticationController ac = new AuthenticationController();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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

    private Scene createLoginScreen() {
        Pane loginPane = new Pane();
        loginPane.getStyleClass().add("screens");
        ImageView logo = createLogoView();

        Rectangle background = new Rectangle(357, 187, 1206,683);
        background.getStyleClass().add("backgroundRect");
        Rectangle selectLoginRect = new Rectangle(334,98);
        Text selectLoginText = new Text("Login");
        selectLoginText.getStyleClass().add("smallButtonText");
        StackPane selectLogin = new StackPane(selectLoginRect, selectLoginText);
        selectLogin.setCursor(Cursor.HAND);
        selectLogin.setLayoutX(578);
        selectLogin.setLayoutY(215);

        Rectangle selectRegisterRect = new Rectangle(334,98);
        Text selectRegisterText = new Text("Register");
        selectRegisterText.getStyleClass().add("smallButtonText");
        StackPane selectRegister = new StackPane(selectRegisterRect, selectRegisterText);
        selectRegister.setCursor(Cursor.HAND);
        selectRegister.setLayoutX(972);
        selectRegister.setLayoutY(215);

        ImageView user = new ImageView("file:src/resources/images/user.png");
        user.setFitHeight(90);
        user.setFitWidth(95);
        ImageView lock = new ImageView("file:src/resources/images/lock.png");
        lock.setFitHeight(88);
        lock.setFitWidth(95);

        Group contentLayer = new Group();
        contentLayer.getChildren().addAll(selectLogin, selectRegister);
        buildLoginContent(contentLayer, selectLoginRect, selectRegisterRect, user, lock, selectRegister, selectLogin);
        loginPane.getChildren().addAll(logo, background, contentLayer);
        return new Scene(loginPane, 1920, 1057);
    }

    private void buildLoginContent(Group contentLayer, Rectangle selectLoginRect, Rectangle selectRegisterRect, ImageView user, ImageView lock, StackPane selectRegister, StackPane selectLogin) {
        selectRegisterRect.setFill(Color.GREY);
        selectLoginRect.setFill(Color.BLACK);
        selectRegister.setOnMouseClicked(event -> {
            buildRegistrationContent(contentLayer, selectLoginRect, selectRegisterRect, user, lock, selectRegister, selectLogin);
        });

        Rectangle emailInput = new Rectangle(695, 372, 530,98);
        emailInput.getStyleClass().add("inputRect");
        TextField email = new TextField();
        email.setPromptText("Enter Email");
        email.getStyleClass().add("loginTextField");
        email.setLayoutX(790);
        email.setLayoutY(382);

        PasswordBundle loginPasswordInput = buildPasswordInput(695, 510, 530, 790, 520, "Enter Password", 1166, 534);
        loginPasswordInput.getCopyPw().getStyleClass().add("loginTextField");
        loginPasswordInput.getPassword().getStyleClass().add("loginTextField");
        Group inputs = new Group(emailInput, email, loginPasswordInput.getInputs());
        user.setLayoutX(694);
        user.setLayoutY(376);
        lock.setLayoutX(694);
        lock.setLayoutY(513);
        Group icons = new Group(user, lock, loginPasswordInput.getShowIcon(), loginPasswordInput.getHideIcon());

        Rectangle loginRect = new Rectangle(1083,98, Color.BLACK);
        Text loginText = new Text("Login");
        loginText.getStyleClass().add("buttonText");
        StackPane loginButton = new StackPane(loginRect, loginText);
        loginButton.setLayoutX(430);
        loginButton.setLayoutY(684);
        loginButton.setCursor(Cursor.HAND);
        loginButton.setOnMouseClicked(event -> {
            int option = 0;
            if(!email.getText().isEmpty() && !loginPasswordInput.getPassword().getText().isEmpty()) {
                option = userLogin(email.getText(), loginPasswordInput.getPassword().getText());
            }
            switch(option) {
                case 0:
                    Alert emptyError = new Alert(Alert.AlertType.ERROR, "Cannot be empty");
                    emptyError.show();
                    break;
                case 1:
                    Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Logged in successfully");
                    success.show();
                    break;
                case 2:
                    Alert error = new Alert(Alert.AlertType.ERROR, "Invalid email or password, or possible problems with files.");
                    error.show();
                    break;
            }
        });

        Rectangle forgetPasswordRect = new Rectangle(700,60, Color.BLACK);
        Text forgotPasswordText = new Text("Forgot Password?");
        forgotPasswordText.getStyleClass().add("smallerButtonText");
        StackPane forgotPassword = new StackPane(forgetPasswordRect, forgotPasswordText);
        forgotPassword.setLayoutX(600);
        forgotPassword.setLayoutY(790);
        forgotPassword.setCursor(Cursor.HAND);
        forgotPassword.setOnMouseClicked(event -> {
            forgotPassword(contentLayer);
        });
        contentLayer.getChildren().setAll(selectLogin, selectRegister, inputs, loginButton, icons, forgotPassword);
    }

    private PasswordBundle buildPasswordInput(int rectX, int rectY, int rectW, int fieldX, int fieldY, String promptText, int iconX, int iconY){
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

        copyPw.textProperty().bindBidirectional(password.textProperty());
        ImageView passwordShow = new ImageView("file:src/resources/images/passwordShow.png");
        ImageView passwordHide = new ImageView("file:src/resources/images/passwordHide.png");

        passwordShow.setLayoutX(iconX);
        passwordShow.setLayoutY(iconY);
        passwordShow.setVisible(true);
        passwordShow.getStyleClass().add("passwordImage");

        passwordHide.setLayoutX(iconX);
        passwordHide.setLayoutY(iconY);
        passwordHide.setVisible(false);
        passwordHide.getStyleClass().add("passwordImage");

        passwordShow.setOnMouseClicked(e -> showPassword(passwordHide, passwordShow, copyPw, password));
        passwordHide.setOnMouseClicked(e -> hidePassword(passwordHide, passwordShow, copyPw, password));

        Group inputs = new Group(passwordInpRect, password, copyPw);

        return new PasswordBundle(inputs, password, copyPw, passwordShow, passwordHide);
    }

    private void forgotPassword(Group contentLayer) {

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

    private void buildRegistrationContent(Group contentLayer, Rectangle selectLoginRect, Rectangle selectRegisterRect, ImageView user, ImageView lock, StackPane selectRegister, StackPane selectLogin) {
        selectRegisterRect.setFill(Color.BLACK);
        selectLoginRect.setFill(Color.GREY);
        selectLogin.setOnMouseClicked(event -> buildLoginContent(contentLayer, selectLoginRect, selectRegisterRect, user, lock, selectRegister, selectLogin));

        Rectangle emailInput = new Rectangle(531, 345, 815,98);
        emailInput.getStyleClass().add("inputRect");
        TextField email = new TextField();
        email.setPromptText("Enter Email");
        email.setLayoutX(630);
        email.setLayoutY(355);

        PasswordBundle registrationPassword = buildPasswordInput(531, 458, 815, 630, 468, "Enter Password", 1280, 483);
        PasswordBundle confirmRegistrationPassword = buildPasswordInput(531, 572, 815, 630, 582, "Confirm password", 1280, 598);

        ImageView lock2 = new ImageView("file:src/resources/images/lock.png");
        lock2.setFitHeight(88);
        lock2.setFitWidth(95);
        user.setLayoutX(531);
        user.setLayoutY(348);
        lock.setLayoutX(531);
        lock.setLayoutY(463);
        Group inputs = new Group(emailInput, email, registrationPassword.getInputs(), confirmRegistrationPassword.getInputs());
        lock2.setLayoutX(531);
        lock2.setLayoutY(576);
        Group icons = new Group(user, lock, registrationPassword.getShowIcon(), registrationPassword.getHideIcon(), lock2, confirmRegistrationPassword.getShowIcon(), confirmRegistrationPassword.getHideIcon());

        Rectangle registerRect = new Rectangle(1083,98, Color.BLACK);
        Text registerText = new Text("Register");
        registerText.getStyleClass().add("buttonText");
        StackPane registerButton = new StackPane(registerRect, registerText);
        registerButton.setLayoutX(430);
        registerButton.setLayoutY(699);
        registerButton.setCursor(Cursor.HAND);
        registerButton.setOnMouseClicked(event ->{
            if (!email.getText().isEmpty() && !registrationPassword.getPassword().getText().isEmpty() && !confirmRegistrationPassword.getPassword().getText().isEmpty()) {
                if (!registrationPassword.getPassword().getText().equals(confirmRegistrationPassword.getPassword().getText())) {
                    Alert passwordDoNotMatch = new Alert(Alert.AlertType.ERROR, "Passwords do not match");
                    passwordDoNotMatch.show();
                }else {
                    try {
                        if (ac.doesEmailExist(email.getText())) {
                            Alert error = new Alert(Alert.AlertType.ERROR, "Email already exists.");
                            error.show();
                        } else {
                            buildRecoveryWordEntrance(contentLayer, email.getText(), registrationPassword.getPassword().getText());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                Alert emptyError = new Alert(Alert.AlertType.ERROR, "Cannot be empty");
                emptyError.show();
            }
        });

        Rectangle alreadyHaveAccountRect = new Rectangle(700,60, Color.BLACK);
        Text alreadyHaveAccountText = new Text("Already have an account?");
        alreadyHaveAccountText.getStyleClass().add("smallerButtonText");
        StackPane alreadyHaveAccount = new StackPane(alreadyHaveAccountRect, alreadyHaveAccountText);
        alreadyHaveAccount.setOnMouseClicked(event -> buildLoginContent(contentLayer, selectLoginRect, selectRegisterRect, user, lock, selectRegister, selectLogin) );
        alreadyHaveAccount.setLayoutX(600);
        alreadyHaveAccount.setLayoutY(805);

        contentLayer.getChildren().setAll(selectLogin, selectRegister, inputs, registerButton, icons, alreadyHaveAccount);
    }

    public void buildRecoveryWordEntrance(Group contentLayer, String email, String password){
        Text infoText = new Text("Enter a recovery word to use \n when resetting password");
        infoText.getStyleClass().add("infoText");
        infoText.setTextAlignment(TextAlignment.CENTER);
        infoText.setLayoutX(430);
        infoText.setLayoutY(300);
        Rectangle recoveryWordRect = new Rectangle(531, 460, 815, 98);
        recoveryWordRect.getStyleClass().add("inputRect");
        TextField recoveryWordInput = new TextField();
        recoveryWordInput.setLayoutX(640);
        recoveryWordInput.setLayoutY(470);
        recoveryWordInput.setPromptText("Enter Recovery Word");
        ImageView recovery = new ImageView("file:src/resources/images/recover.png");
        recovery.setFitHeight(90);
        recovery.setFitWidth(95);
        recovery.setLayoutX(534);
        recovery.setLayoutY(463);
        ImageView enter = new ImageView("file:src/resources/images/enter.png");
        enter.setFitHeight(90);
        enter.setFitWidth(95);
        enter.setLayoutX(1245);
        enter.setLayoutY(463);
        enter.setCursor(Cursor.HAND);
        enter.setOnMouseClicked(event -> {
            int option;
            if(recoveryWordInput.getText().isEmpty()){
                option = 0;
            }else {
                option = userRegistration(email, password, recoveryWordInput.getText());
            }
            switch(option){
                case 0:
                    Alert emptyError = new Alert(Alert.AlertType.ERROR, "Cannot be empty");
                    emptyError.show();
                    break;
                case 1:
                    Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Account successfully registered");
                    success.show();
                    break;
                case 2:
                    Alert error = new Alert(Alert.AlertType.ERROR, "Possible problem with files.");
                    error.show();
                    break;
            }
        });
        Group input = new Group(recoveryWordRect, recoveryWordInput, recovery, enter);
        contentLayer.getChildren().setAll(input, infoText);
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

    private ImageView createLogoView() {
        Image logo = new Image("file:src/resources/images/logo.png");
        ImageView logoView = new ImageView(logo);
        logoView.getStyleClass().add("logo");
        logoView.setLayoutX(0);
        logoView.setLayoutY(0);
        return logoView;
    }

}