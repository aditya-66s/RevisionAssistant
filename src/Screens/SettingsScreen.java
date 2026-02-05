package Screens;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class SettingsScreen extends SystemScreens{

    public Scene create() {
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Group background = createBackground("System Settings");
        getTitle().setLayoutX(70);
        root.getChildren().addAll(background);
        Scene scene = new Scene(root, 1920, 1057);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        return scene;
    }
}
