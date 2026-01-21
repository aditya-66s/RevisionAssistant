import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;

public class Main extends Application {
    public Font Xirod = Font.loadFont("file:src/resources/fonts/Xirod.otf", 30);
    public Font Vipnagorgialla = Font.loadFont("file:src/resources/fonts/Vipnagorgialla.otf", 30);
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

        Rectangle selectLogin = new Rectangle(334,98);
        selectLogin.setLayoutX(578);
        selectLogin.setLayoutY(215);
        selectLogin.setArcHeight(50);
        selectLogin.setArcWidth(50);
        selectLogin.setCursor(Cursor.HAND);

        Rectangle selectRegister = new Rectangle(334,98);
        selectRegister.setLayoutX(972);
        selectRegister.setLayoutY(215);
        selectRegister.setArcHeight(50);
        selectRegister.setArcWidth(50);
        selectRegister.setCursor(Cursor.HAND);

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
        buildLoginContent(contentLayer, selectLogin, selectRegister, user, lock, passwordShow, passwordHide);
        loginPane.getChildren().addAll(logo, background, contentLayer);
        return new Scene(loginPane, 1920, 1057);
    }

    private void buildLoginContent(Group contentLayer, Rectangle selectLogin, Rectangle selectRegister, ImageView user, ImageView lock, ImageView passwordShow, ImageView passwordHide) {
        selectRegister.setFill(Color.GREY);
        selectLogin.setFill(Color.BLACK);
        selectRegister.setOnMouseClicked(event -> {
            buildRegistrationContent(contentLayer, selectLogin, selectRegister, user, lock, passwordShow, passwordHide);
        });

        Rectangle emailInput = new Rectangle(530,98);
        emailInput.setFill(Color.WHITE);
        emailInput.setStroke(Color.BLACK);
        emailInput.setLayoutX(695);
        emailInput.setLayoutY(372);
        emailInput.setArcHeight(50);
        emailInput.setArcWidth(50);
        TextField email = new TextField();
        email.setPromptText("Enter Email");
        email.getStyleClass().add("loginTextField");
        email.setLayoutX(785);
        email.setLayoutY(382);

        Rectangle passwordInput = new Rectangle(530, 98);
        passwordInput.setFill(Color.WHITE);
        passwordInput.setStroke(Color.BLACK);
        passwordInput.setLayoutX(695);
        passwordInput.setLayoutY(510);
        passwordInput.setArcHeight(50);
        passwordInput.setArcWidth(50);

        Group inputs = new Group(emailInput, email, passwordInput);
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
            passwordHide.setVisible(true);
            passwordShow.setVisible(false);
        });
        passwordHide.setOnMouseClicked(event -> {
            passwordHide.setVisible(false);
            passwordShow.setVisible(true);
        });
        Group icons = new Group(user, lock, passwordShow, passwordHide);

        Rectangle loginButton = new Rectangle(1083,98, Color.BLACK);
        loginButton.setLayoutX(430);
        loginButton.setLayoutY(684);
        loginButton.setArcHeight(50);
        loginButton.setArcWidth(50);
        loginButton.setCursor(Cursor.HAND);

        contentLayer.getChildren().setAll(selectLogin, selectRegister, inputs, loginButton, icons);

    }

    private void buildRegistrationContent(Group contentLayer, Rectangle selectLogin, Rectangle selectRegister, ImageView user, ImageView lock, ImageView passwordShow, ImageView passwordHide) {
        selectRegister.setFill(Color.BLACK);
        selectLogin.setFill(Color.GREY);
        selectLogin.setOnMouseClicked(event -> {
            buildLoginContent(contentLayer, selectLogin, selectRegister, user, lock, passwordShow, passwordHide);
        });

        Rectangle emailInput = new Rectangle(815,98);
        emailInput.setFill(Color.WHITE);
        emailInput.setStroke(Color.BLACK);
        emailInput.setLayoutX(531);
        emailInput.setLayoutY(345);
        emailInput.setArcHeight(50);
        emailInput.setArcWidth(50);
        TextField email = new TextField();
        email.setPromptText("Enter Email");
        email.setLayoutX(630);
        email.setLayoutY(355);

        Rectangle passwordInput = new Rectangle(815, 98);
        passwordInput.setFill(Color.WHITE);
        passwordInput.setStroke(Color.BLACK);
        passwordInput.setLayoutX(531);
        passwordInput.setLayoutY(458);
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

        Rectangle confirmPasswordInput = new Rectangle(815, 98);
        confirmPasswordInput.setFill(Color.WHITE);
        confirmPasswordInput.setStroke(Color.BLACK);
        confirmPasswordInput.setLayoutX(531);
        confirmPasswordInput.setLayoutY(572);
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
        passwordShow.setLayoutX(1290);
        passwordShow.setLayoutY(483);
        passwordShow.setVisible(true);
        passwordHide.setLayoutX(1290);
        passwordHide.setLayoutY(483);
        passwordHide.setVisible(false);
        passwordShow.setOnMouseClicked(event -> {
            passwordHide.setVisible(true);
            passwordShow.setVisible(false);
            copyPw.setManaged(true);
            copyPw.setVisible(true);
            password.setVisible(false);
            password.setManaged(false);
        });
        passwordHide.setOnMouseClicked(event -> {
            passwordHide.setVisible(false);
            passwordShow.setVisible(true);
            password.setVisible(false);
            copyPw.setManaged(false);
            copyPw.setVisible(false);
            password.setVisible(true);
            password.setManaged(true);
        });
        Group inputs = new Group(emailInput, email, passwordInput, copyPw, password, confirmPasswordInput, copyCPw, confirmPassword);

        lock2.setLayoutX(531);
        lock2.setLayoutY(576);
        passwordShow2.setLayoutX(1290);
        passwordShow2.setLayoutY(598);
        passwordShow2.setVisible(true);
        passwordHide2.setLayoutX(1290);
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

        Rectangle registerButton = new Rectangle(1083,98, Color.BLACK);
        registerButton.setLayoutX(430);
        registerButton.setLayoutY(699);
        registerButton.setArcHeight(50);
        registerButton.setArcWidth(50);
        registerButton.setCursor(Cursor.HAND);

        contentLayer.getChildren().setAll(selectLogin, selectRegister, inputs, registerButton, icons);
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