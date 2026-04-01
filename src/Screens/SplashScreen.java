package Screens;

import Components.UIComponents;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Objects;

public class SplashScreen {
    // Constants
    // Scene Dimensions
    private static final int sceneW = 1920;
    private static final int sceneH = 1057;
    // Splash Screen
    private static final int topX = 420;
    private static final int topY = 100;
    private static final int loadingW = 100;
    private static final int loadingH = 100;
    private static final int loadingX = 880;
    private static final int loadingY = 600;
    private static final int startAngle = 0;
    private static final int endAngle = 360;
    private static final int bottomX = 225;
    private static final int bottomY = 825;
    private static final int duration1 = 8;
    private static final double duration2 = 2.5;
    private static final double startOpacity = 1.0;
    private static final double endOpacity = 0.25;
    private final UIComponents ui = new UIComponents();
    // Code will be executed when onContinue.run() runs
    public Scene create(Runnable onContinue) {
        Pane root = new Pane();
        root.getStyleClass().add("screens");

        ImageView logo = ui.createLogoView();

        // Creating title text split into two lines.
        Text titleText1 = new Text("Revision");
        titleText1.getStyleClass().add("title");
        Text titleText2 = new Text("Assistant");
        titleText2.getStyleClass().add("title");
        VBox topText = new VBox(titleText1, titleText2);
        topText.setAlignment(Pos.CENTER);
        topText.setLayoutX(topX);
        topText.setLayoutY(topY);

        // Create the rotating loading indicator
        Image loading = new Image(Objects.requireNonNull(getClass().getResource("/images/SplashScreen/loading.png")).toExternalForm());
        ImageView loadingView = new ImageView(loading);
        loadingView.setFitWidth(loadingW);
        loadingView.setFitHeight(loadingH);
        loadingView.setLayoutX(loadingX);
        loadingView.setLayoutY(loadingY);

        // Animate the loading indicator with continuous rotation
        RotateTransition loadingTransition = new RotateTransition(Duration.seconds(duration1), loadingView);
        loadingTransition.setFromAngle(startAngle);
        loadingTransition.setToAngle(endAngle);
        loadingTransition.setInterpolator(Interpolator.EASE_IN);
        loadingTransition.setCycleCount(Animation.INDEFINITE);
        loadingTransition.play();

        // Instruction for the user at bottom of the screen
        Text infoText = new Text("Press any key to continue!");
        infoText.getStyleClass().add("text");
        VBox bottomText = new VBox(infoText);
        bottomText.setAlignment(Pos.CENTER);
        bottomText.setLayoutX(bottomX);
        bottomText.setLayoutY(bottomY);

        // Group text elements to create a pulsing fade effect for text
        Group texts = new Group(topText, bottomText);
        FadeTransition varyingOpacity = new FadeTransition(Duration.seconds(duration2), texts);
        varyingOpacity.setFromValue(startOpacity);
        varyingOpacity.setToValue(endOpacity);
        varyingOpacity.setCycleCount(FadeTransition.INDEFINITE);
        varyingOpacity.setAutoReverse(true);
        varyingOpacity.play();

        // Add all components to root Pane and create a new scene, containing the root
        root.getChildren().addAll(logo, loadingView, texts);
        Scene scene = new Scene(root, sceneW, sceneH);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/dark.css")).toExternalForm());
        // Event Handlers, will execute the code that was passed in, when user interacts with splash screen
        scene.setOnKeyPressed(_ -> onContinue.run());
        scene.setOnMouseClicked(_ -> onContinue.run());
        return scene;
    }
}
