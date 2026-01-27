import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Main extends Application {
    public Font Xirod = Font.loadFont("file:src/resources/fonts/Xirod.otf", 30);
    public Font Vipnagorgialla = Font.loadFont("file:src/resources/fonts/Vipnagorgialla.otf", 30);
    public final AuthenticationController ac = new AuthenticationController();
    private Stack<List<Node>> history = new Stack<>();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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

    private ImageView createLogoView() {
        ImageView logoView = new ImageView("file:src/resources/images/logo.png");
        logoView.getStyleClass().add("constantImages");
        logoView.setLayoutX(0);
        logoView.setLayoutY(0);
        return logoView;
    }
    private ImageView createBackView(){
        ImageView backView = new ImageView("file:src/resources/images/back.png");
        backView.getStyleClass().add("constantImages");
        backView.setLayoutX(1776);
        backView.setLayoutY(916);
        backView.setCursor(Cursor.HAND);
        return backView;
    }

    private Scene createLoginScreen() {
        Pane loginPane = new Pane();
        loginPane.getStyleClass().add("screens");
        ImageView logo = createLogoView();

        Rectangle background = new Rectangle(357, 187, 1206,683);
        background.getStyleClass().add("backgroundRect");

        RectangleButtonBundle selectLogin = buildRectangularButtonBundle(334, 98, "Login", "smallButtonText", 578, 215);
        RectangleButtonBundle selectRegister = buildRectangularButtonBundle(334,98, "Register", "smallButtonText", 972, 215);

        Group contentLayer = new Group(selectLogin.getButton(), selectRegister.getButton());
        buildLoginContent(contentLayer, selectRegister, selectLogin);
        loginPane.getChildren().addAll(logo, background, contentLayer);
        return new Scene(loginPane, 1920, 1057);
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

    private void buildLoginContent(Group contentLayer, RectangleButtonBundle selectRegister, RectangleButtonBundle selectLogin) {
        selectRegister.getButtonRect().setFill(Color.GREY);
        selectLogin.getButtonRect().setFill(Color.BLACK);
        selectRegister.getButton().setOnMouseClicked(event -> {
            buildRegistrationContent(contentLayer, selectRegister, selectLogin);
        });
        String iconURL = "file:src/resources/images/user.png";
        TextBundle emailInput = buildTextFieldInput(695, 372, 530, 790, 382, "Enter Email", iconURL, 694, 376, 95, 90);
        emailInput.getTf().getStyleClass().add("loginTextField");

        PasswordBundle loginPasswordInput = buildPasswordInput(695, 510, 530, 790, 520, "Enter Password", 694, 513,1166, 534);
        loginPasswordInput.getCopyPw().getStyleClass().add("loginTextField");
        loginPasswordInput.getPassword().getStyleClass().add("loginTextField");
        Group inputs = new Group(emailInput.getGroup(), loginPasswordInput.getPasswordGroup());

        Group icons = new Group(emailInput.getIcon(), loginPasswordInput.getIconGroup());

        RectangleButtonBundle login = buildRectangularButtonBundle(1083, 98, "Login", "buttonText", 430, 684);
        login.getButton().setOnMouseClicked(event -> {
            int option = 0;
            if(!emailInput.getTf().getText().isEmpty() && !loginPasswordInput.getPassword().getText().isEmpty()) {
                option = userLogin(emailInput.getTf().getText(), loginPasswordInput.getPassword().getText());
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
        RectangleButtonBundle forgotPassword = buildRectangularButtonBundle(700, 60, "Forgot Password", "smallerButtonText", 600, 790);

        forgotPassword.getButton().setOnMouseClicked(event -> {
            try {
                forgotPassword(contentLayer);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
        switchContent(contentLayer, selectLogin.getButton(), selectRegister.getButton(), inputs, login.getButton(), icons, forgotPassword.getButton());
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

    private void forgotPassword(Group contentLayer) throws IOException, NoSuchAlgorithmException {
        String userURL = "file:src/resources/images/user.png";
        TextBundle emailInput = buildTextFieldInput(531, 248, 815, 633, 258, "Enter Email", userURL, 531, 251, 95, 90);
        String recoveryURL = "file:src/resources/images/recover.png";
        TextBundle recoveryWord = buildTextFieldInput(531, 370, 815 ,633, 380, "Enter Recovery Word", recoveryURL, 534, 373 ,95, 90);
        PasswordBundle resetPassword = buildPasswordInput(531, 489, 815, 633, 499, "Enter Password", 531, 495 ,1280, 515);
        PasswordBundle confirmResetPassword = buildPasswordInput(531, 609, 815, 633, 619, "Confirm password", 531, 615, 1280, 635);
        RectangleButtonBundle resetPasswordRectangle = buildRectangularButtonBundle(1083,98,"Reset Password", "buttonText", 418, 729);

        resetPasswordRectangle.getButton().setOnMouseClicked(event -> {
            int option = -1;
            if(emailInput.getTf().getText().isEmpty() && recoveryWord.getTf().getText().isEmpty() && resetPassword.getPassword().getText().isEmpty() && confirmResetPassword.getPassword().getText().isEmpty()) {
                option = 0; //empty
            }else {
                User temp = null;
                try {
                    temp = ac.findUserByEmail(emailInput.getTf().getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (temp == null) {
                    option = 1; //email not found
                } else {
                    if (!temp.getRecoveryWord().equals(recoveryWord.getTf().getText())) {
                        option = 2; // recovery word does not match
                    } else {
                        if (!resetPassword.getPassword().getText().equals(confirmResetPassword.getPassword().getText())) {
                            option = 3; // passwords do not match
                        } else {
                            try {
                                if (ac.resetPassword(temp, resetPassword.getPassword().getText())) {
                                    option = 4; // success
                                }
                            } catch (NoSuchAlgorithmException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
            switch(option){
                case 0:
                    Alert emptyError = new Alert(Alert.AlertType.ERROR, "Cannot be empty");
                    emptyError.show();
                    break;
                case 1:
                    Alert emailError = new Alert(Alert.AlertType.ERROR, "Email not found");
                    emailError.show();
                    break;
                case 2:
                    Alert rwError = new Alert(Alert.AlertType.ERROR, "Recovery word does not match");
                    rwError.show();
                    break;
                case 3:
                    Alert pwError = new Alert(Alert.AlertType.ERROR, "Passwords do not match");
                    pwError.show();
                    break;
                case 4:
                    Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Password reset successfully, you will be redirected to the login screen!");
                    success.show();
                    success.setOnCloseRequest(e -> goBack(contentLayer));
                    break;
            }
        });

        ImageView back = createBackView();
        back.setOnMouseClicked(event -> {goBack(contentLayer);});
        Group icons  = new Group(emailInput.getIcon(), recoveryWord.getIcon(), resetPassword.getIconGroup(), confirmResetPassword.getIconGroup(), back);
        switchContent(contentLayer, emailInput.getGroup(), recoveryWord.getGroup(), resetPassword.getPasswordGroup(), confirmResetPassword.getPasswordGroup(), resetPasswordRectangle.getButton(), icons);
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

    private void buildRegistrationContent(Group contentLayer, RectangleButtonBundle selectRegister, RectangleButtonBundle selectLogin) {
        selectRegister.getButtonRect().setFill(Color.BLACK);
        selectLogin.getButtonRect().setFill(Color.GREY);
        selectLogin.getButton().setOnMouseClicked(event -> buildLoginContent(contentLayer, selectRegister, selectLogin));

        String iconURL = "file:src/resources/images/user.png";
        TextBundle emailInput = buildTextFieldInput(531, 345, 815, 630, 355, "Enter Email", iconURL, 531, 345, 95, 90);
        PasswordBundle registrationPassword = buildPasswordInput(531, 458, 815, 630, 468, "Enter Password", 531, 463 ,1280, 483);
        PasswordBundle confirmRegistrationPassword = buildPasswordInput(531, 572, 815, 630, 582, "Confirm password", 531, 576, 1280, 598);

        Group inputs = new Group(emailInput.getGroup(), registrationPassword.getPasswordGroup(), confirmRegistrationPassword.getPasswordGroup());
        Group icons = new Group(emailInput.getIcon(), registrationPassword.getIconGroup(), confirmRegistrationPassword.getIconGroup());

        RectangleButtonBundle register = buildRectangularButtonBundle(1083, 98, "Register", "buttonText", 430, 699);

        register.getButton().setOnMouseClicked(event ->{
            if (!emailInput.getTf().getText().isEmpty() && !registrationPassword.getPassword().getText().isEmpty() && !confirmRegistrationPassword.getPassword().getText().isEmpty()) {
                if (!registrationPassword.getPassword().getText().equals(confirmRegistrationPassword.getPassword().getText())) {
                    Alert passwordDoNotMatch = new Alert(Alert.AlertType.ERROR, "Passwords do not match");
                    passwordDoNotMatch.show();
                }else {
                    try {
                        if (ac.doesEmailExist(emailInput.getTf().getText())) {
                            Alert error = new Alert(Alert.AlertType.ERROR, "Email already exists.");
                            error.show();
                        } else {
                            buildRecoveryWordEntrance(contentLayer, emailInput.getTf().getText(), registrationPassword.getPassword().getText());
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
        RectangleButtonBundle alreadyHaveAccount = buildRectangularButtonBundle(700,60, "Already have an account", "smallerButtonText", 600, 805);
        alreadyHaveAccount.getButton().setOnMouseClicked(event -> buildLoginContent(contentLayer, selectRegister, selectLogin));
        switchContent(contentLayer, selectLogin.getButton(), selectRegister.getButton(), inputs, register.getButton(), icons, alreadyHaveAccount.getButton());
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
            int option;
            if(recoveryWord.getTf().getText().isEmpty()){
                option = 0;
            }else {
                option = userRegistration(email, password, recoveryWord.getTf().getText());
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
        Group input = new Group(recoveryWord.getGroup(), recoveryWord.getIcon(), enter);
        ImageView back  = createBackView();
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