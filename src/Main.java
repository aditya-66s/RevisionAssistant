import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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

        Rectangle selectRegister = new Rectangle(334,98);
        selectRegister.setLayoutX(972);
        selectRegister.setLayoutY(215);
        selectRegister.setArcHeight(50);
        selectRegister.setArcWidth(50);

        Group contentLayer = new Group();
        contentLayer.getChildren().addAll(selectLogin, selectRegister);
        buildLoginContent(contentLayer, selectLogin, selectRegister);
        loginPane.getChildren().addAll(logo, background, contentLayer);
        return new Scene(loginPane, 1920, 1057);
    }

    private void buildLoginContent(Group contentLayer, Rectangle selectLogin, Rectangle selectRegister) {
        selectRegister.setFill(Color.GREY);
        selectLogin.setFill(Color.BLACK);
        selectRegister.setCursor(Cursor.HAND);
        selectRegister.setOnMouseClicked(event -> {
            buildRegistrationContent(contentLayer, selectLogin, selectRegister);
        });

        Rectangle emailInput = new Rectangle(530,98);
        emailInput.setFill(Color.WHITE);
        emailInput.setStroke(Color.BLACK);
        emailInput.setLayoutX(695);
        emailInput.setLayoutY(372);
        emailInput.setArcHeight(50);
        emailInput.setArcWidth(50);
        Rectangle passwordInput = new Rectangle(530, 98);
        passwordInput.setFill(Color.WHITE);
        passwordInput.setStroke(Color.BLACK);
        passwordInput.setLayoutX(695);
        passwordInput.setLayoutY(510);
        passwordInput.setArcHeight(50);
        passwordInput.setArcWidth(50);

        Rectangle loginButton = new Rectangle(1083,98, Color.BLACK);
        loginButton.setLayoutX(430);
        loginButton.setLayoutY(684);
        loginButton.setArcHeight(50);
        loginButton.setArcWidth(50);

        contentLayer.getChildren().setAll(selectLogin, selectRegister, emailInput, passwordInput, loginButton);

    }

    private void buildRegistrationContent(Group contentLayer, Rectangle selectLogin, Rectangle selectRegister) {
        selectRegister.setFill(Color.BLACK);
        selectLogin.setFill(Color.GREY);
        selectLogin.setCursor(Cursor.HAND);
        selectLogin.setOnMouseClicked(event -> {
            buildLoginContent(contentLayer, selectLogin, selectRegister);
        });

        Rectangle emailInput = new Rectangle(815,98);
        emailInput.setFill(Color.WHITE);
        emailInput.setStroke(Color.BLACK);
        emailInput.setLayoutX(531);
        emailInput.setLayoutY(345);
        emailInput.setArcHeight(50);
        emailInput.setArcWidth(50);
        Rectangle passwordInput = new Rectangle(815, 98);
        passwordInput.setFill(Color.WHITE);
        passwordInput.setStroke(Color.BLACK);
        passwordInput.setLayoutX(531);
        passwordInput.setLayoutY(458);
        passwordInput.setArcHeight(50);
        passwordInput.setArcWidth(50);
        Rectangle confirmPasswordInput = new Rectangle(815, 98);
        confirmPasswordInput.setFill(Color.WHITE);
        confirmPasswordInput.setStroke(Color.BLACK);
        confirmPasswordInput.setLayoutX(531);
        confirmPasswordInput.setLayoutY(572);
        confirmPasswordInput.setArcHeight(50);
        confirmPasswordInput.setArcWidth(50);

        Rectangle registerButton = new Rectangle(1083,98, Color.BLACK);
        registerButton.setLayoutX(430);
        registerButton.setLayoutY(699);
        registerButton.setArcHeight(50);
        registerButton.setArcWidth(50);

        contentLayer.getChildren().setAll(selectLogin, selectRegister, emailInput, passwordInput, confirmPasswordInput, registerButton);
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