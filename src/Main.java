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

        Rectangle background = new Rectangle(1206,683, Color.WHITE);
        background.setLayoutX(357);
        background.setLayoutY(187);
        background.setArcHeight(100);
        background.setArcWidth(100);

        Rectangle selectLoginRect = new Rectangle(334,98);
        selectLoginRect.setArcHeight(50);
        selectLoginRect.setArcWidth(50);
        Text selectLoginText = new Text("Login");
        selectLoginText.getStyleClass().add("smallButtonText");
        StackPane selectLogin = new StackPane(selectLoginRect, selectLoginText);
        selectLogin.setCursor(Cursor.HAND);
        selectLogin.setLayoutX(578);
        selectLogin.setLayoutY(215);

        Rectangle selectRegisterRect = new Rectangle(334,98);
        selectRegisterRect.setArcHeight(50);
        selectRegisterRect.setArcWidth(50);
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
        ImageView passwordShow = new ImageView("file:src/resources/images/passwordShow.png");
        passwordShow.setFitHeight(47);
        passwordShow.setFitWidth(48);
        ImageView passwordHide = new ImageView("file:src/resources/images/passwordHide.png");
        passwordHide.setFitHeight(47);
        passwordHide.setFitWidth(48);

        Group contentLayer = new Group();
        contentLayer.getChildren().addAll(selectLogin, selectRegister);
        buildLoginContent(contentLayer, selectLoginRect, selectRegisterRect, user, lock, passwordShow, passwordHide, selectRegister, selectLogin);
        loginPane.getChildren().addAll(logo, background, contentLayer);
        return new Scene(loginPane, 1920, 1057);
    }

    private void buildLoginContent(Group contentLayer, Rectangle selectLoginRect, Rectangle selectRegisterRect, ImageView user, ImageView lock, ImageView passwordShow, ImageView passwordHide, StackPane selectRegister, StackPane selectLogin) {
        selectRegisterRect.setFill(Color.GREY);
        selectLoginRect.setFill(Color.BLACK);
        selectRegister.setOnMouseClicked(event -> {
            buildRegistrationContent(contentLayer, selectLoginRect, selectRegisterRect, user, lock, passwordShow, passwordHide, selectRegister, selectLogin);
        });

        Rectangle emailInput = new Rectangle(695, 372, 530,98);
        emailInput.setFill(Color.WHITE);
        emailInput.setStroke(Color.BLACK);
        emailInput.setArcHeight(50);
        emailInput.setArcWidth(50);
        TextField email = new TextField();
        email.setPromptText("Enter Email");
        email.getStyleClass().add("loginTextField");
        email.setLayoutX(790);
        email.setLayoutY(382);

        Rectangle passwordInput = new Rectangle(695, 510, 530, 98);
        passwordInput.setFill(Color.WHITE);
        passwordInput.setStroke(Color.BLACK);
        passwordInput.setArcHeight(50);
        passwordInput.setArcWidth(50);
        PasswordField password = new PasswordField();
        password.getStyleClass().add("loginTextField");
        TextField copyPw = new TextField();
        copyPw.getStyleClass().add("loginTextField");
        copyPw.setLayoutX(790);
        copyPw.setLayoutY(520);
        copyPw.setVisible(false);
        copyPw.setManaged(false);
        copyPw.textProperty().bindBidirectional(password.textProperty());
        password.setPromptText("Enter Password");
        password.setLayoutX(790);
        password.setLayoutY(520);

        Group inputs = new Group(emailInput, email, passwordInput, password, copyPw);
        user.setLayoutX(694);
        user.setLayoutY(376);
        lock.setLayoutX(694);
        lock.setLayoutY(513);
        passwordShow.setLayoutX(1166);
        passwordShow.setLayoutY(534);
        passwordShow.setVisible(true);
        passwordHide.setLayoutX(1166);
        passwordHide.setLayoutY(534);
        passwordHide.setVisible(false);
        passwordShow.setOnMouseClicked(event -> {
            showPassword(passwordHide, passwordShow, copyPw, password);
        });
        passwordHide.setOnMouseClicked(event -> {
            hidePassword(passwordHide, passwordShow, copyPw, password);
        });
        Group icons = new Group(user, lock, passwordShow, passwordHide);

        Rectangle loginRect = new Rectangle(1083,98, Color.BLACK);
        loginRect.setArcHeight(50);
        loginRect.setArcWidth(50);
        Text loginText = new Text("Login");
        loginText.getStyleClass().add("buttonText");
        StackPane loginButton = new StackPane(loginRect, loginText);
        loginButton.setLayoutX(430);
        loginButton.setLayoutY(684);
        loginButton.setCursor(Cursor.HAND);
        loginButton.setOnMouseClicked(event -> {
            int option = 0;
            if(!email.getText().isEmpty() && !password.getText().isEmpty()) {
                option = userLogin(email.getText(), password.getText());
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
        forgetPasswordRect.setArcHeight(50);
        forgetPasswordRect.setArcWidth(50);
        Text forgotPasswordText = new Text("Forgot Password?");
        forgotPasswordText.getStyleClass().add("smallerButtonText");
        StackPane forgotPassword = new StackPane(forgetPasswordRect, forgotPasswordText);
        forgotPassword.setLayoutX(600);
        forgotPassword.setLayoutY(790);
        forgotPassword.setCursor(Cursor.HAND);
        contentLayer.getChildren().setAll(selectLogin, selectRegister, inputs, loginButton, icons, forgotPassword);

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

    private void buildRegistrationContent(Group contentLayer, Rectangle selectLoginRect, Rectangle selectRegisterRect, ImageView user, ImageView lock, ImageView passwordShow, ImageView passwordHide, StackPane selectRegister, StackPane selectLogin) {
        selectRegisterRect.setFill(Color.BLACK);
        selectLoginRect.setFill(Color.GREY);
        selectLogin.setOnMouseClicked(event -> buildLoginContent(contentLayer, selectLoginRect, selectRegisterRect, user, lock, passwordShow, passwordHide, selectRegister, selectLogin));

        Rectangle emailInput = new Rectangle(531, 345, 815,98);
        emailInput.setFill(Color.WHITE);
        emailInput.setStroke(Color.BLACK);
        emailInput.setArcHeight(50);
        emailInput.setArcWidth(50);
        TextField email = new TextField();
        email.setPromptText("Enter Email");
        email.setLayoutX(630);
        email.setLayoutY(355);

        Rectangle passwordInput = new Rectangle(531,458, 815, 98);
        passwordInput.setFill(Color.WHITE);
        passwordInput.setStroke(Color.BLACK);
        passwordInput.setArcHeight(50);
        passwordInput.setArcWidth(50);
        PasswordField password = new PasswordField();
        TextField copyPw = new TextField();
        copyPw.setLayoutX(630);
        copyPw.setLayoutY(468);
        copyPw.setVisible(false);
        copyPw.setManaged(false);
        copyPw.textProperty().bindBidirectional(password.textProperty());
        password.setPromptText("Enter Password");
        password.setLayoutX(630);
        password.setLayoutY(468);

        Rectangle confirmPasswordInput = new Rectangle(531, 572, 815, 98);
        confirmPasswordInput.setFill(Color.WHITE);
        confirmPasswordInput.setStroke(Color.BLACK);
        confirmPasswordInput.setArcHeight(50);
        confirmPasswordInput.setArcWidth(50);
        PasswordField confirmPassword = new PasswordField();
        TextField copyCPw = new TextField();
        copyCPw.setLayoutX(630);
        copyCPw.setLayoutY(582);
        copyCPw.setVisible(false);
        copyCPw.setManaged(false);
        copyCPw.textProperty().bindBidirectional(confirmPassword.textProperty());
        confirmPassword.setPromptText("Confirm Password");
        confirmPassword.setLayoutX(630);
        confirmPassword.setLayoutY(582);

        ImageView lock2 = new ImageView("file:src/resources/images/lock.png");
        lock2.setFitHeight(88);
        lock2.setFitWidth(95);
        ImageView passwordShow2 = new ImageView("file:src/resources/images/passwordShow.png");
        passwordShow2.setFitHeight(47);
        passwordShow2.setFitWidth(48);
        ImageView passwordHide2 = new ImageView("file:src/resources/images/passwordHide.png");
        passwordHide2.setFitHeight(47);
        passwordHide2.setFitWidth(48);

        user.setLayoutX(531);
        user.setLayoutY(348);

        lock.setLayoutX(531);
        lock.setLayoutY(463);
        passwordShow.setLayoutX(1280);
        passwordShow.setLayoutY(483);
        passwordShow.setVisible(true);
        passwordHide.setLayoutX(1280);
        passwordHide.setLayoutY(483);
        passwordHide.setVisible(false);
        passwordShow.setOnMouseClicked(event -> {
            showPassword(passwordHide, passwordShow, copyPw, password);
        });
        passwordHide.setOnMouseClicked(event -> {
            hidePassword(passwordHide, passwordShow, copyPw, password);
        });
        Group inputs = new Group(emailInput, email, passwordInput, copyPw, password, confirmPasswordInput, copyCPw, confirmPassword);

        lock2.setLayoutX(531);
        lock2.setLayoutY(576);
        passwordShow2.setLayoutX(1280);
        passwordShow2.setLayoutY(598);
        passwordShow2.setVisible(true);
        passwordHide2.setLayoutX(1280);
        passwordHide2.setLayoutY(598);
        passwordHide2.setVisible(false);
        passwordShow2.setOnMouseClicked(event -> {
            passwordHide2.setVisible(true);
            passwordShow2.setVisible(false);
            copyCPw.setManaged(true);
            copyCPw.setVisible(true);
            confirmPassword.setVisible(false);
            confirmPassword.setManaged(false);
        });
        passwordHide2.setOnMouseClicked(event -> {
            passwordHide2.setVisible(false);
            passwordShow2.setVisible(true);
            copyCPw.setManaged(false);
            copyCPw.setVisible(false);
            confirmPassword.setVisible(true);
            confirmPassword.setManaged(true);
        });

        Group icons = new Group(user, lock, passwordShow, passwordHide, lock2, passwordShow2, passwordHide2);

        Rectangle registerRect = new Rectangle(1083,98, Color.BLACK);
        registerRect.setArcHeight(50);
        registerRect.setArcWidth(50);
        Text registerText = new Text("Register");
        registerText.getStyleClass().add("buttonText");
        StackPane registerButton = new StackPane(registerRect, registerText);
        registerButton.setLayoutX(430);
        registerButton.setLayoutY(699);
        registerButton.setCursor(Cursor.HAND);
        registerButton.setOnMouseClicked(event ->{
            if (!email.getText().isEmpty() && !password.getText().isEmpty() && !confirmPassword.getText().isEmpty()) {
                if (!password.getText().equals(confirmPassword.getText())) {
                    Alert passwordDoNotMatch = new Alert(Alert.AlertType.ERROR, "Passwords do not match");
                    passwordDoNotMatch.show();
                }else {
                    try {
                        if (ac.doesEmailExist(email.getText())) {
                            Alert error = new Alert(Alert.AlertType.ERROR, "Email already exists.");
                            error.show();
                        } else {
                            buildRecoveryWordEntrance(contentLayer, email.getText(), password.getText());
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
        alreadyHaveAccountRect.setArcHeight(50);
        alreadyHaveAccountRect.setArcWidth(50);
        Text alreadyHaveAccountText = new Text("Already have an account?");
        alreadyHaveAccountText.getStyleClass().add("smallerButtonText");
        StackPane alreadyHaveAccount = new StackPane(alreadyHaveAccountRect, alreadyHaveAccountText);
        alreadyHaveAccount.setOnMouseClicked(event -> buildLoginContent(contentLayer, selectLoginRect, selectRegisterRect, user, lock, passwordShow, passwordHide, selectRegister, selectLogin) );
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
        recoveryWordRect.setFill(Color.WHITE);
        recoveryWordRect.setStroke(Color.BLACK);
        recoveryWordRect.setArcHeight(50);
        recoveryWordRect.setArcWidth(50);
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
        logoView.setFitWidth(100);
        logoView.setFitHeight(100);
        logoView.setLayoutX(0);
        logoView.setLayoutY(0);
        return logoView;
    }

}