import Components.ScreenManager;
import Controllers.AuthenticationController;
import Controllers.SettingsController;
import Screens.AuthScreen;
import Screens.MainMenuScreen;
import Screens.SplashScreen;
import Screens.SystemScreens;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {
    // Custom Fonts Xirod and Vipnagorgialla loaded from local files
    public Font Xirod = Font.loadFont(getClass().getResourceAsStream("/fonts/Xirod.otf"), 30);
    public Font Vipnagorgialla = Font.loadFont(getClass().getResourceAsStream("/fonts/Vipnagorgialla.otf"), 30);
    private final AuthenticationController universalAuth = new AuthenticationController();
    public static void main(String[] args){
//        conversion();
        launch(args);
    }
    public static void conversion(){
        Scanner sc = new Scanner(System.in);
        boolean yes = true;
        do{
            System.out.println("Width: ");
            double w = sc.nextDouble();
            if(w == 0){
                yes = false;
            }else {
                System.out.println(1920 * (w / 33.867));
                System.out.println("Height: ");
                double h = sc.nextDouble();
                System.out.println(1057 * (h / 19.05));
            }
        }while (yes);
    }
    @Override
    public void start(Stage primaryStage) {
        // Set stage reference for scene management
        ScreenManager.setStage(primaryStage);
        // Creates and configures the splash screen, linking the stylesheet to it.
        SplashScreen splashScreen = new SplashScreen();
        AuthScreen auth = new AuthScreen(universalAuth);
        Scene splashScene = splashScreen.create(() -> ScreenManager.switchScene(auth.create()));
        primaryStage.setTitle("Revision Assistant");
        primaryStage.setScene(splashScene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
//        MainMenuScreen mMS = new MainMenuScreen(universalAuth);
//        Scene mainMenuTest = mMS.create();
//        primaryStage.setScene(mainMenuTest);
//        primaryStage.show();
    }
}