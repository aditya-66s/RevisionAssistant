import Components.ScreenManager;
import Screens.AuthScreen;
import Screens.SplashScreen;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    // Custom Fonts Xirod and Vipnagorgialla loaded from local files
    public Font Xirod = Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/Xirod.otf")).toExternalForm(), 30);
    public Font Vipnagorgialla = Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/Vipnagorgialla.otf")).toExternalForm(), 30);
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set stage reference for scene management
        ScreenManager.setStage(primaryStage);

        //Creates and configures the splash screen, linking the stylesheet to it.
        SplashScreen splashScreen = new SplashScreen();
        AuthScreen auth = new AuthScreen();

        Scene splashScene = splashScreen.create(() -> ScreenManager.switchScene(auth.create()));
        primaryStage.setTitle("Revision Assistant");
        primaryStage.setScene(splashScene);
        primaryStage.show();
    }
}