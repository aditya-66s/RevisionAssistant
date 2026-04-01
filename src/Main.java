import Components.ScreenManager;
import Controllers.AuthenticationController;
import Controllers.SettingsController;
import Screens.AuthScreen;
import Screens.SplashScreen;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    // Constants
    private static final int fontSize = 30;
    // Custom Fonts Xirod and Vipnagorgialla loaded from local files
    public Font Xirod = Font.loadFont(getClass().getResourceAsStream("/fonts/Xirod.otf"), fontSize);
    public Font Vipnagorgialla = Font.loadFont(getClass().getResourceAsStream("/fonts/Vipnagorgialla.otf"), fontSize);
    private final AuthenticationController universalAuth = new AuthenticationController();
    private final SettingsController universalSettings = universalAuth.getSettingsController();
    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        // Set stage reference for scene management
        ScreenManager.setStage(primaryStage);
        // Creates and configures the splash screen, linking the stylesheet to it.
        SplashScreen splashScreen = new SplashScreen();
        AuthScreen auth = new AuthScreen(universalAuth, universalSettings);
        Scene splashScene = splashScreen.create(() -> ScreenManager.switchScene(auth.create()));
        primaryStage.setTitle("Revision Assistant");
        primaryStage.setScene(splashScene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
    }
}