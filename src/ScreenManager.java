import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

public class ScreenManager {
    private static Stage stage;
    private static final Stack<Scene> history = new Stack<Scene>();

    public static void setStage(Stage primaryStage){
        stage = primaryStage;
    }
    public static void switchScene(Scene root){
        if(stage.getScene() != null){
            history.push(stage.getScene());
        }
        stage.setScene(root);
    }
    public static void goBack(){
        if(!history.isEmpty()){
            stage.setScene(history.pop());
        }
    }
    public static void clearHistory(){
        history.clear();
    }
}
