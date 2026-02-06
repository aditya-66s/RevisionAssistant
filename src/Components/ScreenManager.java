package Components;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Stack;

// Utility class for managing scene navigation
public class ScreenManager {
    // Primary stage of application, allows access from anywhere in application
    private static Stage stage;
    // Stack to maintain navigation of history opf scenes.
    private static final Stack<Scene> history = new Stack<>();
    //  sets the primary stage reference for application, called only at startup
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
        // Switch scene to new scene
        stage.setScene(root);
    }
    // Navigate back to previous scene in history stack
    public static void goBack(){
        // Check if there's any scene to go back to
        if(!history.isEmpty()){
            // Remove and fetch most recent scene from history, setting it as the current scene.
            stage.setScene(history.pop());
        }
    }
    // Clears all navigation history (You can't navigate back post logout).
    public static void clearHistory(){
        history.clear();
    }
}
