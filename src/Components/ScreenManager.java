package Components;

import Controllers.SettingsController;
import Models.Settings;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Stack;

// Utility class for managing scene navigation
public class ScreenManager {
    // Primary stage of application, allows access from anywhere in application
    private static Stage stage;
    // Stack to maintain navigation of history opf scenes.
    private static final Stack<Scene> history = new Stack<>();
    //  sets the primary stage reference for application, called only at startup
    private static SettingsController sC;

    public static void setSC(SettingsController sC) {
        ScreenManager.sC = sC;
    }

    public static void setStage(Stage primaryStage){
        stage = primaryStage;
    }
    // Switches current scene to a new scene and saves current seen to history
    public static void switchScene(Scene root){
        // Check if there's currently a scene being displayed
        if(stage.getScene() != null){
            // Save current scene to stack before switching
            history.push(stage.getScene());
        }
        root.getStylesheets().add(Objects.requireNonNull(ScreenManager.class.getResource("/styles/styles.css")).toExternalForm());
        root.getStylesheets().add(Objects.requireNonNull(ScreenManager.class.getResource("/styles/dark.css")).toExternalForm());
        // Switch scene to new scene
        stage.setScene(root);
    }
    public static void switchScene(Scene root, String email) throws IOException {
        // Check if there's currently a scene being displayed
        if(stage.getScene() != null){
            // Save current scene to stack before switching
            history.push(stage.getScene());
        }
//        Settings userSettings = sC.loadSettings(email);
//        root.getStylesheets().clear();
//        root.getStylesheets().add(Objects.requireNonNull(ScreenManager.class.getResource("/styles/styles.css")).toExternalForm());
//        root.getStylesheets().add(Objects.requireNonNull(ScreenManager.class.getResource("/styles/" + userSettings.getTheme() + ".css")).toExternalForm());
        changeStyleSheet(root, email);
//        Parent originalRoot = root.getRoot();
//        StackPane wrapper = new StackPane(originalRoot);
//        wrapper.getChildren().add(addBrightnessOverlay(email));
//        root.setRoot(wrapper);
//        root = sceneWithOverlay(root, email);
        // Switch scene to new scene
        stage.setScene(sceneWithOverlay(root, email));
    }
    // Navigate back to previous scene in history stack
    public static void goBack(){
        // Check if there's any scene to go back to
        if(!history.isEmpty()){
            // Remove and fetch most recent scene from history, setting it as the current scene.
            stage.setScene(history.pop());
        }
    }
    public static void goBack(String email) throws IOException {
        // Check if there's any scene to go back to
        if(!history.isEmpty()){
            // Remove and fetch most recent scene from history, setting it as the current scene.
            Scene nextScene = history.pop();
            changeStyleSheet(nextScene, email);
            if(nextScene.getRoot() instanceof StackPane wrapper){
                for(var node : wrapper.getChildren()){
                    if(node instanceof Rectangle rect && rect.getWidth() == 1920 && rect.getHeight() == 1057){
                        Settings cS = getUserSettings(email);
                        rect.setOpacity(cS.getDarkness());
                        break;
                    }
                }
            }
            stage.setScene(nextScene);
        }
    }
    // Clears all navigation history (You can't navigate back post logout).
    public static void clearHistory(){
        history.clear();
    }
    private static Settings getUserSettings(String email) throws IOException {
        if (sC != null && sC.getCurrentSettings() != null && sC.getCurrentSettings().getEmail().equals(email)) {
            return sC.getCurrentSettings();
        }
        return sC.loadSettings(email);
    }
    // Procedure for applying user settings to each screen (stylesheet specific as of) 08/02/2025
    private static void changeStyleSheet(Scene root, String email) throws IOException {
        Settings userSettings = getUserSettings(email);
        root.getStylesheets().clear();
        root.getStylesheets().add(Objects.requireNonNull(ScreenManager.class.getResource("/styles/styles.css")).toExternalForm());
        root.getStylesheets().add(Objects.requireNonNull(ScreenManager.class.getResource("/styles/" + userSettings.getTheme() + ".css")).toExternalForm());
    }
    private static Scene sceneWithOverlay(Scene scene, String email) throws IOException {
        Parent originalRoot = scene.getRoot();
        StackPane wrapper = new StackPane(originalRoot);
        wrapper.getChildren().add(getBrightnessOverlay(email));
        scene.setRoot(wrapper);
        return scene;
    }

    public static Rectangle getBrightnessOverlay(String email) throws IOException {
        Settings userSettings = getUserSettings(email);
        Rectangle brightnessOverlay = new Rectangle(1920, 1057);
        brightnessOverlay.getStyleClass().add("brightnessOverlay");
        brightnessOverlay.setOpacity(userSettings.getDarkness());
        brightnessOverlay.setMouseTransparent(true);
        return brightnessOverlay;
    }
}
