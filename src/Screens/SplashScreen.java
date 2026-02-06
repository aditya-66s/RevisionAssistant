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
        topText.setLayoutX(420);
        topText.setLayoutY(100);

        // Create the rotating loading indicator
        Image loading = new Image(Objects.requireNonNull(getClass().getResource("/images/SplashScreen/loading.png")).toExternalForm());
        ImageView loadingView = new ImageView(loading);
        loadingView.setFitWidth(100);
        loadingView.setFitHeight(100);
        loadingView.setLayoutX(880);
        loadingView.setLayoutY(600);

        // Animate the loading indicator with continuous rotation
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

        // Add all components to root Pane and create a new scene, containing the root
        root.getChildren().addAll(logo, loadingView, texts);
        Scene scene = new Scene(root, 1920, 1057);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

        // Event Handlers, will execute the code that was passed in, when user interacts with splash screen
        scene.setOnKeyPressed(_ -> onContinue.run());
        scene.setOnMouseClicked(_ -> onContinue.run());
        return scene;
    }
}
