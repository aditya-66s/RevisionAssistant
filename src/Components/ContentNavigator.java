package Components;

import javafx.scene.Group;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ContentNavigator {
    private final Stack<List<Node>> history = new Stack<>();

    // Switches content of a layer to new nodes, saving current state to history for backtracking
    public void switchContent(Group contentLayer, Node... newContent){
        // Save current state to history stack
        history.push(new ArrayList<>(contentLayer.getChildren()));
        // Replace all children with new content
        contentLayer.getChildren().setAll(newContent);
    }
    // Navigates back to previous content state
    public void goBack(Group contentLayer){
        if(!history.isEmpty()){
            contentLayer.getChildren().setAll(history.pop());
        }
    }
}
