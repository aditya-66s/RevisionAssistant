package Screens;

import Components.UIComponents;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class MainMenuScreen {
    private final UIComponents ui = new UIComponents();

    public Scene create(){
        Pane root = new Pane(ui.createLogoView());
        root.getStyleClass().add("screens");

        Scene scene = new Scene(root, 1920, 1057);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        return scene;
    }
}
